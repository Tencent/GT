//
//  GTLog.m
//  GTKit
//
//  Created   on 12-10-10.
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

#import "GTLog.h"
#import "GTDebugDef.h"
#import "GT.h"
#import "GTLogBuffer.h"
#import "GTConfig.h"

#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>

/* return the mach thread bound to the pthread */
mach_port_t pthread_mach_thread_np(pthread_t);
pthread_t pthread_self(void);


//每个log里面最大条数
#define GT_MAX_COUNT_IN_LOG 1000


@implementation GTLogRecord

@synthesize date = _date;
@synthesize tag = _tag;
@synthesize level = _level;
@synthesize levelStr = _levelStr;
@synthesize content = _content;
@synthesize thread = _thread;

- (id)initWithLevel:(GTLogLevel)logLevel tag:(NSString *)logTag content:(NSString *)logContent
{
    self  = [super init];
    if (self) {
        [self setDate:[GTUtility timeIntervalSince1970]];
        [self setTag:logTag];
        [self setLevel:logLevel];
        [self setLevelStr:[self logLevelStr]];
        [self setContent:logContent];
        mach_port_t tid = pthread_mach_thread_np(pthread_self());
        NSString *threadName = [NSString stringWithFormat:@"%u",tid];
        
        [self setThread:threadName];
    }
    
    return self;
}

- (void)dealloc
{
    M_GT_SAFE_FREE(_levelStr);
    M_GT_SAFE_FREE(_tag);
    M_GT_SAFE_FREE(_thread);
    M_GT_SAFE_FREE(_content);
    
    [super dealloc];
}

- (NSString *)logLevelStr
{
    NSString *level = nil;
    switch (_level) {
        case GT_LOG_INFO:
            level = [NSString stringWithFormat:@"I"];
            break;
            
        case GT_LOG_DEBUG:
            level = [NSString stringWithFormat:@"D"];
            break;
            
        case GT_LOG_WARNING:
            level = [NSString stringWithFormat:@"W"];
            break;
            
        case GT_LOG_ERROR:
            level = [NSString stringWithFormat:@"E"];
            break;
            
        default:
            level = [NSString stringWithFormat:@"A"];
            break;
    }
    return level;
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@ %@|%@|%@ %@", [NSString stringWithTimeEx:_date], _levelStr, _tag, _thread, _content];
}

@end


@implementation GTLog

M_GT_DEF_SINGLETION(GTLog)

@synthesize fileName = _fileName;
@synthesize logs = _logs;
@synthesize levels = _levels;
@synthesize tags = _tags;
@synthesize isModified = _isModified;


- (id)init
{
	self = [super init];
    
	if(self)
	{
        _fileName = @"common";
        _logs = [[NSMutableArray alloc] init];
        _levels = [[NSArray arrayWithObjects:@"ALL", @"DEBUG", @"INFO", @"WARNING", @"ERROR", nil] retain];
        _commonBuf = [[GTLogBuffer alloc] initWithName:@"common"];
        _tags = [[GTList alloc] init];
        [_tags setObject:[NSNumber numberWithInt:0xffff] forKey:@"TAG"];
        _isModified = NO;
	}
	
	return self;
}

- (void)dealloc
{
    [_commonBuf stopTimer];
    [_commonBuf release];
    [_logs removeAllObjects];
    [_logs release];
    [_levels release];
    [_tags release];
    [super dealloc];
}

- (void)addLog:(NSString *)content tag:(NSString *)tag forLevel:(GTLogLevel)level
{
    if(!content) {
        return;
    }
	
    
    GTLogRecord *logRec = nil;
    NSNumber *count = nil;
    
    @synchronized (self) {
        if([_logs count] > GT_MAX_COUNT_IN_LOG)
        {
            logRec = [_logs objectAtIndex:0];
            
            count = [_tags objectForKey:[logRec tag]];
            if (count != nil) {
                if ([count integerValue] == 1) {
                    [_tags removeObjectForKey:[logRec tag]];
                } else {
                    [_tags setObject:[NSNumber numberWithInteger:[count integerValue] - 1] forKey:[logRec tag]];
                }
                
            }
            [_logs removeObjectAtIndex:0];
        }
        
        logRec = [[GTLogRecord alloc] initWithLevel:level tag:tag content:content];
        [_logs addObject:logRec];
        
        count = [_tags objectForKey:tag];
        if (count == nil) {
            [_tags setObject:[NSNumber numberWithInt:1] forKey:tag];
        } else {
            [_tags setObject:[NSNumber numberWithInteger:[count integerValue] + 1] forKey:tag];
        }
        
        [_commonBuf addBuffer:[logRec description]];
        [logRec release];
        
        [self observeTick];
    }
	
    
	_isModified = YES;
}

- (void)clearAll
{
    @synchronized (self) {
        [_logs removeAllObjects];
        [_tags deleteAll];
        [_tags setObject:[NSNumber numberWithInt:0xffff] forKey:@"TAG"];
    }
    
}

- (void)saveAll:(NSString *)fileName
{
    [self setFileName:fileName];
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_LOG_COMMON_DIR fileName:fileName ofType:M_GT_FILE_TYPE_LOG];
    
    FILE *file = fopen([filePath UTF8String], "w");
    
	if (file) {
        for (int i = 0; i < [_logs count]; i++) {
            GTLogRecord *logRec = [_logs objectAtIndex:i];
            fprintf(file, "%s\r", [[logRec description] UTF8String]);
        }
        
		fflush(file);
        fclose(file);
	}
}

- (void)saveLogs:(NSMutableArray *)array fileName:(NSString *)fileName
{
    [self setFileName:fileName];
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_LOG_COMMON_DIR fileName:fileName ofType:M_GT_FILE_TYPE_LOG];
    
    FILE *file = fopen([filePath UTF8String], "w");
    
	if (file) {
        for (int i = 0; i < [array count]; i++) {
            GTLogRecord *logRec = [array objectAtIndex:i];
            fprintf(file, "%s\r", [[logRec description] UTF8String]);
        }
        
		fflush(file);
        fclose(file);
	}
}

- (void)cleanLog:(NSString *)fileName
{
    [_commonBuf cleanLog:fileName];
}

- (void)startLog:(NSString *)fileName
{
    [_commonBuf startLog:fileName];
}

- (void)endLog:(NSString *)fileName
{
    [_commonBuf endLog:fileName];
}

- (NSMutableArray *)searchContent:(NSString *)content withTag:(NSString *)tag withLevel:(GTLogLevel)level inArray:(NSMutableArray *)logArray
{
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:10];
    NSRange range;
    BOOL result;
    
    for (int i = 0; i < [_logs count]; i++) {
        GTLogRecord *logRec = [_logs objectAtIndex:i];
        if (content && ([content length] != 0)) {
            range = [[logRec content] rangeOfString:content options:NSCaseInsensitiveSearch];
            if (range.location == NSNotFound) {
                continue;
            }
        }
        
        if (tag && (![tag isEqualToString:@"TAG"])) {
            result = [[logRec tag] isEqualToString:tag];
            if (result == NO) {
                continue;
            }
        }
        
        if (level != GT_LOG_INVALID) {
            if (level > [logRec level]) {
                continue;
            }
        }
        
        // 匹配成功
        [array addObject:logRec];
    }
    
    _isModified = NO;
    return array;
}

#pragma mark - Timer
- (void)observeTick
{
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:NO];
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
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LOG_MOD object:nil];
    [self unobserveTick];
}

@end


#pragma mark - COMMON LOG Interface OC

void func_logDebugForOC( NSString* tag, NSString * format, ... )
{
    
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        
        [[GTLog sharedInstance] addLog:str tag:tag forLevel:GT_LOG_DEBUG];
    }
}

void func_logInfoForOC( NSString* tag, NSString * format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        
        [[GTLog sharedInstance] addLog:str tag:tag forLevel:GT_LOG_INFO];
    }
    
}

void func_logWarningForOC( NSString* tag, NSString * format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        
        [[GTLog sharedInstance] addLog:str tag:tag forLevel:GT_LOG_WARNING];
    }
    
}

void func_logErrorForOC( NSString* tag, NSString * format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        
        [[GTLog sharedInstance] addLog:str tag:tag forLevel:GT_LOG_ERROR];
    }
    
}

void func_logCleanForOC(NSString * format,...)
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        [[GTLog sharedInstance] cleanLog:str];
    }
}

void func_logStartForOC(NSString * format,...)
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        M_GT_PTR_NULL_CHECK(format);
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        
        [[GTLog sharedInstance] startLog:str];
    }
}

void func_logEndForOC(NSString * format,...)
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        M_GT_PTR_NULL_CHECK(format);
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        [[GTLog sharedInstance] endLog:str];
    }
}

#pragma mark - COMMON LOG Interface C

void func_logDebug( const char* tag, const char* format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        
        NSString * tagStr = [NSString stringWithCString:tag encoding:NSUTF8StringEncoding];
        [[GTLog sharedInstance] addLog:str tag:tagStr forLevel:GT_LOG_DEBUG];
    }
    
}

void func_logInfo( const char* tag, const char* format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        
        NSString * tagStr = [NSString stringWithCString:tag encoding:NSUTF8StringEncoding];
        [[GTLog sharedInstance] addLog:str tag:tagStr forLevel:GT_LOG_INFO];
    }
    
}

void func_logWarning( const char* tag, const char* format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        
        NSString * tagStr = [NSString stringWithCString:tag encoding:NSUTF8StringEncoding];
        [[GTLog sharedInstance] addLog:str tag:tagStr forLevel:GT_LOG_WARNING];
    }
    
}

void func_logError( const char* tag, const char* format, ... )
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        M_GT_PTR_NULL_CHECK(tag);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        
        NSString * tagStr = [NSString stringWithCString:tag encoding:NSUTF8StringEncoding];
        [[GTLog sharedInstance] addLog:str tag:tagStr forLevel:GT_LOG_ERROR];
    }
    
}

void func_logClean(const char * format,...)
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        [[GTLog sharedInstance] cleanLog:str];
    }
    
    
}

void func_logStart(const char * format,...)
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        [[GTLog sharedInstance] startLog:str];
    }
    
}

void func_logEnd(const char * format,...)
{
    @autoreleasepool {
        M_GT_LOG_SWITCH_CHECK;
        
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        [[GTLog sharedInstance] endLog:str];
    }
    
}

#endif
