//
//  GTNSLog.m
//  GTKit
//
//  Created   on 13-7-12.
// Tencent is pleased to support the open source community by making
// Tencent GT (Version 2.4 and subsequent versions) available.
//
// Notwithstanding anything to the contrary herein, any previous version
// of Tencent GT shall not be subject to the license hereunder.
// All right, title, and interest, including all intellectual property rights,
// in and to the previous version of Tencent GT (including any and all copies thereof)
// shall be owned and retained by Tencent and subject to the license under the
// Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
//
// Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the MIT License (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of the License at
//
// http://opensource.org/licenses/MIT
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.
//
//
#ifndef GT_DEBUG_DISABLE
#import "GTNSLog.h"
#import "GTConfig.h"


#define M_GT_NSLOG_MIN_COUNT 200
#define M_GT_NSLOG_MAX_COUNT 1000

@implementation GTNSLog

M_GT_DEF_SINGLETION(GTNSLog)

@synthesize redirectEnable = _redirectEnable;
@synthesize nslogSwitch = _nslogSwitch;
@synthesize logArray = _logArray;
@synthesize fileName = _fileName;

- (id)init
{
	self = [super init];
    
	if(self)
	{
        _stdoutPipe = [[NSPipe pipe] retain];
        _stderrPipe = [[NSPipe pipe] retain];
        _logArray = [[NSMutableArray alloc] init];
        _fileName = @"nslog";
        
        _redirectEnable = NO;
        _nslogSwitch = NO;
        _nslogCount = M_GT_NSLOG_MIN_COUNT;
        
        _bNewContent = NO;
        
        //重定向CPU消耗过高，脱机也关闭
        _isatty = YES;
        

	}
	
	return self;
}

- (void)dealloc
{
    [_stdoutPipe release];
    [_stderrPipe release];
    [_logArray release];
    
    [super dealloc];
}

- (void)setNslogSwitch:(BOOL)nslogSwitch
{
    _nslogSwitch = nslogSwitch;
    if (_nslogSwitch) {
        _nslogCount = M_GT_NSLOG_MAX_COUNT;
    } else {
        _nslogCount = M_GT_NSLOG_MIN_COUNT;
    }
    
    for (int i = (int)([_logArray count] - _nslogCount - 1); i >= 0; i--) {
        [_logArray removeObjectAtIndex:i];
    }
    
    if (!_isatty) {
        // Redirection
        [self handleLogRedirect:YES];
    } else {
        [self handleLogRedirect:nslogSwitch];
    }
}


- (void)handleLogRedirect:(BOOL)redirect
{
    if (_redirectEnable == redirect) {
        return;
    }
    
    _redirectEnable = redirect;
    if ( _redirectEnable ) {
        [self redirectLog];
    } else {
        [self unobserveTick];
        [self cancelRedirectLog];
    }
}

- (void)redirectLogToFile{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *fileName =[NSString stringWithFormat:@"%@.log",[NSDate date]];
    NSString *logFilePath = [documentsDirectory stringByAppendingPathComponent:fileName];
    freopen([logFilePath cStringUsingEncoding:NSASCIIStringEncoding],"a+",stderr);
}

- (void)redirectNotificationHandle:(NSNotification *)nf{
    NSData *data = [[nf userInfo] objectForKey:NSFileHandleNotificationDataItem];
    NSString *str = [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] autorelease];
    
    if ([str length] > 0) {
        _bNewContent = YES;
        
        NSArray * array = [str componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]];
        
        NSString *log = nil;
        for (int i = 0; i < [array count]; i++) {
            log = [array objectAtIndex:i];
            if ([log length] > 0) {
                [_logArray addObject:log];
            }
            
            if ([_logArray count] > _nslogCount) {
                [_logArray removeObjectAtIndex:0];
            }
        }

    }
    
    
    [[nf object] readInBackgroundAndNotify];
}

- (void)redirectSTD:(int)fd{
    GT_OC_LOG_D(@"Log Setting", @"redirectSTD %d", fd);
    
    NSPipe * pipe = nil;
    
    if (fd == STDOUT_FILENO ) {
        pipe = _stdoutPipe;
        _stdoutOrig = dup(fd);
    } else if (fd == STDERR_FILENO ) {
        pipe = _stderrPipe;
        _stderrOrig = dup(fd);
    } else {
        return;
    }
    
    NSFileHandle *pipeReadHandle = [pipe fileHandleForReading];
    dup2([[pipe fileHandleForWriting] fileDescriptor], fd);
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(redirectNotificationHandle:)
                                                 name:NSFileHandleReadCompletionNotification
                                               object:pipeReadHandle];
    [pipeReadHandle readInBackgroundAndNotify];
}

- (void)redirectSTD{
    _fileReadHandle = [NSFileHandle fileHandleForReadingAtPath:[self stderrPath]];
    [_fileReadHandle retain];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(redirectNotificationHandle:)
                                                 name:NSFileHandleReadCompletionNotification
                                               object:_fileReadHandle];
    [_fileReadHandle readInBackgroundAndNotify];
}

- (void)cancelRedirectSTD
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSFileHandleReadCompletionNotification object:_fileReadHandle];
    [_fileReadHandle closeFile];
    [_fileReadHandle release];
    _fileReadHandle = nil;
}

- (void)cancelRedirectSTD:(int)fd
{
    GT_OC_LOG_D(@"Log Setting", @"cancelRedirectSTD %d", fd);
    
    NSPipe * pipe = nil;
    if (fd == STDOUT_FILENO ) {
        pipe = _stdoutPipe;
        dup2(_stdoutOrig, fd);
    } else if (fd == STDERR_FILENO ) {
        pipe = _stderrPipe;
        dup2(_stderrOrig, fd);
    } else {
        return;
    }
    NSFileHandle *pipeReadHandle = [pipe fileHandleForReading];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSFileHandleReadCompletionNotification object:pipeReadHandle];
}

- (void)redirectLog
{
    [self startLog];
    [self redirectSTD];
}

- (void)cancelRedirectLog
{
    [self cancelRedirectSTD];
    [self finishLog];
}

- (void)finishLog {
    
    fflush(stderr);
    
    dup2(_stderrOrig, STDERR_FILENO);
}

- (NSString*)stderrPath {
    //对应目录不存在则创建一个新的目录
    return [[GTConfig sharedInstance] pathForDirByCreated:M_GT_SYS_DIR fileName:@"console" ofType:@"txt"];
}

- (void)startLog {
    _stderrOrig = dup(STDERR_FILENO);
    _fileSTD = freopen([[self stderrPath] UTF8String],"w",stderr);
}

#pragma mark - Timer
- (void)observeTick:(NSTimeInterval)interval
{
    _interval = interval;
    
    //如果进入plugin，则先flush
    if (_interval == M_GT_NSLOG_PLUGIN_TIME) {
        fflush(stderr);
    }
    
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:_interval
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:YES];
        [_timer retain];
    }
	
}

- (void)unobserveTick
{
    if (_timer) {
        [_timer invalidate];
        [_timer release];
        _timer = nil;
    }
}


- (void)handleTick
{
    if (_bNewContent) {
        fflush(stderr);
        [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_NSLOG_MOD object:nil];
        _bNewContent = NO;
    }

    return;
}

#pragma mark -


- (void)clearAll
{
    [_logArray removeAllObjects];
}


- (void)saveAll:(NSString *)fileName
{
    [self setFileName:fileName];
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_NSLOG_DIR fileName:fileName ofType:M_GT_FILE_TYPE_LOG];
    
    FILE *file = fopen([filePath UTF8String], "w");
    
	if (file) {
        for (int i = 0; i < [_logArray count]; i++) {
            fprintf(file, "%s\r\n", [[_logArray objectAtIndex:i] UTF8String]);
        }
        
        
		fflush(file);
        fclose(file);
	}
}
#pragma mark - C
void func_setNsLogSwitch(bool on)
{
    [[GTNSLog sharedInstance] setNslogSwitch:on];
}

@end


#endif
