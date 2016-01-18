//
//  GTLogBuffer.m
//  GTKit
//
//  Created   on 13-3-7.
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
#import "GTLogBuffer.h"
#import "GTConfig.h"
#import "GTLogConfig.h"

typedef enum {
	GTLogBufferClean = 0,
    GTLogBufferCleanStart,
    GTLogBufferCleanEnd,
    GTLogBufferStart,
    GTLogBufferEnd
} GTLogBufferFileOp;

@implementation NSString (FileSort)

//按文件修改时间排序，最新的时间放在最前面
- (NSComparisonResult)fileTimeCompare:(NSString *)other {
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSDictionary *fileAttributes; 
    
    fileAttributes = [fileManager attributesOfItemAtPath:self error:nil];
    NSDate *date1 = [fileAttributes fileModificationDate];
    
    fileAttributes = [fileManager attributesOfItemAtPath:other error:nil];
    NSDate *date2 = [fileAttributes fileModificationDate];
    
    int result = [date2 compare:date1];
    return result;
}

@end

@implementation GTLogBufferSaveFile

@synthesize name = _name;
@synthesize buffer = _buffer;
@synthesize tmpBuf = _tmpBuf;
@synthesize fileOpType = _fileOpType;

- (id)initWithName:(NSString *)name
{
	self = [super init];
    
	if(self)
	{
        [self setName:name];
        _buffer = [[NSMutableString alloc] initWithCapacity:M_GT_KB];
	}
	
	return self;
}

- (void)dealloc
{
    [_name release];
    [_buffer release];
    [super dealloc];
}

- (void)addBuffer:(NSString *)buffer
{
    if ((_fileOpType == GTLogBufferCleanEnd) || (_fileOpType == GTLogBufferClean) || (_fileOpType == GTLogBufferEnd)) {
        return;
    }
    
    @synchronized (_buffer) {
        [_buffer appendString:@"\n"];
        [_buffer appendString:buffer];
    }
    
}

- (void)clearBuffer
{
    [_buffer setString:@""];
}

- (void)newBuffer
{
    if (_buffer) {
        [_buffer release];
        _buffer = nil;
    }
    
    _buffer = [[NSMutableString alloc] initWithCapacity:M_GT_KB];
}

@end


#define M_GT_INDEX_INVALID 0xffff

@implementation GTLogBuffer


@synthesize name = _name;

- (id)initWithName:(NSString *)dirName
{
	self = [super init];
    
	if(self)
	{
        [self setName:dirName];
        _buffer = [[NSMutableString alloc] initWithCapacity:M_GT_KB];
        _fileIndex = M_GT_INDEX_INVALID;
        _isSaving = NO;
        _timeCnt = 0;
        _timer = [NSTimer scheduledTimerWithTimeInterval:10 target:self selector:@selector(timerCheck:) userInfo:nil repeats:YES];
        [_timer retain];
        _fileList = [[GTList alloc] init];
	}
	
	return self;
}

- (void)dealloc
{
    [self stopTimer];
    [_name release];
    [_buffer release];
    [_tmpBuf release];
    [_fileList release];
    [super dealloc];
}

- (void)stopTimer
{
    if (_timer) {
        [_timer invalidate];
        [_timer release];
        _timer = nil;
    }
    
}

- (BOOL)needSave:(NSMutableString *)buffer
{
    if ([buffer length] > 16 * M_GT_KB) {
        return YES;
    }
    
    return NO;
}

- (void)addBuffer:(NSString *)buffer
{
    BOOL needSave = NO;

    M_GT_LOG_SWITCH_CHECK;
    
    if ([[GTLogConfig sharedInstance] bufferAutoSave]) {
        @synchronized (_buffer) {
            [_buffer appendString:@"\n"];
            [_buffer appendString:buffer];
        }
        
    }
    
    
    for (int i = 0; i < [[_fileList keys] count]; i++) {
        NSString *key = [[_fileList keys] objectAtIndex:i];
        GTLogBufferSaveFile *obj = [_fileList objectForKey:key];
        [obj addBuffer:buffer];
        needSave = (needSave == YES)? YES : [self needSave:[obj buffer]];
    }
    
    needSave = (needSave == YES)? YES : [self needSave:_buffer];
    
    if (needSave == YES) {
        [self saveBuffer];
    }
}

- (void)cleanLog:(NSString *)fileName
{
    @synchronized (self) {
        GTLogBufferSaveFile *obj = [_fileList objectForKey:fileName];
        if (obj == nil) {
            obj = [[[GTLogBufferSaveFile alloc] initWithName:fileName] autorelease];
        }
        
        [obj setFileOpType:GTLogBufferClean];
        [obj clearBuffer];
        [_fileList setObject:obj forKey:fileName];
    }
    
}

- (void)startLog:(NSString *)fileName
{
    @synchronized (self) {
        GTLogBufferSaveFile *obj = [_fileList objectForKey:fileName];
        if (obj == nil) {
            obj = [[[GTLogBufferSaveFile alloc] initWithName:fileName] autorelease];
            [obj setFileOpType:GTLogBufferStart];
            [_fileList setObject:obj forKey:fileName];
        } else {
            
            if ([obj fileOpType] == GTLogBufferClean) {
                [obj setFileOpType:GTLogBufferCleanStart];
            } else {
                [obj setFileOpType:GTLogBufferStart];
            }
            
        }
    }
}

- (void)endLog:(NSString *)fileName
{
    @synchronized (self) {
        GTLogBufferSaveFile *obj = [_fileList objectForKey:fileName];
        if (obj != nil) {
            
            if ([obj fileOpType] == GTLogBufferClean) {
                [obj setFileOpType:GTLogBufferCleanEnd];
            } else if ([obj fileOpType] == GTLogBufferCleanStart) {
                [obj setFileOpType:GTLogBufferCleanEnd];
            } else {
                [obj setFileOpType:GTLogBufferEnd];
            }
            [_fileList setObject:obj forKey:fileName];
        }
    }
    
}

#pragma mark -

- (void)timerCheck:(id)sender
{
    _timeCnt++;
    if (_timeCnt > 1) {
        _timeCnt = 0;
        [self saveBuffer];
    }
    
}


- (void)updateFileList
{
    for (int i = 0; i < [[_fileList keys] count]; i++) {
        NSString *key = [[_fileList keys] objectAtIndex:i];
        GTLogBufferSaveFile *obj = [_fileList objectForKey:key];
        switch ([obj fileOpType]) {
            case GTLogBufferClean:
            case GTLogBufferEnd:
            case GTLogBufferCleanEnd:
                if (([[obj buffer] length] == 0) && ([[obj tmpBuf] length] == 0)) {
                    [_fileList removeObjectForKey:key];
                }
                
                break;
            case GTLogBufferStart:
            case GTLogBufferCleanStart:
                break;
                
            default:
                break;
        }
        
    }
}

- (void)fileListSwitchBuffer
{
    for (int i = 0; i < [[_fileList keys] count]; i++) {
        NSString *key = [[_fileList keys] objectAtIndex:i];
        GTLogBufferSaveFile *obj = [_fileList objectForKey:key];
        [obj setTmpBuf:[obj buffer]];
        [obj newBuffer];
    }
}

- (void)saveBuffer
{
    if (_isSaving) {
        return;
    }
    
    BOOL hasContent = NO;
    for (int i = 0; i < [[_fileList keys] count]; i++) {
        NSString *key = [[_fileList keys] objectAtIndex:i];
        GTLogBufferSaveFile *obj = [_fileList objectForKey:key];
        if ([[obj buffer] length] != 0) {
            hasContent = YES;
            break;
        }
    }
    
    if (hasContent == NO) {
        if ([_buffer length] == 0) {
            return;
        }
    }
    
    
    _isSaving = YES;
    _tmpBuf = _buffer;
    _buffer = [[NSMutableString alloc] initWithCapacity:M_GT_KB];
    
    [self fileListSwitchBuffer];
    [self updateFileList];
    NSThread *thread = [[[NSThread alloc] initWithTarget:self selector:@selector(autoSave:) object:nil] autorelease];
    thread.name = [NSString stringWithFormat:@"%@_GTLogBufferAutoSave", _name];
    [thread start];
}

- (NSArray *)getFilenamelistOfType:(NSString *)type fromDirPath:(NSString *)dirPath
{
    NSMutableArray *filenamelist = [NSMutableArray arrayWithCapacity:10];
    NSArray *tmplist = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:dirPath error:nil];
    
    for (NSString *filename in tmplist) {
        NSString *fullpath = [dirPath stringByAppendingPathComponent:filename];
        if ([self isFileExistAtPath:fullpath]) {
            if ([[filename pathExtension] isEqualToString:type]) {
                [filenamelist addObject:fullpath];
            }
        }
    }
    
    return filenamelist;
}

- (BOOL)isFileExistAtPath:(NSString*)fileFullPath {
    BOOL isExist = NO;
    isExist = [[NSFileManager defaultManager] fileExistsAtPath:fileFullPath];
    return isExist;
}

- (NSString *)getBufferDir
{
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_SYS_DIR fileName:_name ofType:M_GT_FILE_TYPE_LOG];
    
    return filePath;
}

-(NSDate *)getFileTime:(NSString *)filePath
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSDictionary *fileAttributes = [fileManager attributesOfItemAtPath:filePath error:nil];
    if (fileAttributes != nil) {
        
        //文件修改日期
        return [fileAttributes objectForKey:NSFileModificationDate];
        
    }
    return nil;
}

- (unsigned long long)getFileSize:(NSString *)filePath
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSDictionary *fileAttributes = [fileManager attributesOfItemAtPath:filePath error:nil];
    if (fileAttributes != nil) {
        //文件大小
        return [fileAttributes fileSize];
    }
    
    return 0;
}

- (NSUInteger )getFileIndex
{
    NSUInteger index = 0;
    NSString *logDir = [self getBufferDir];
    NSArray *fileList = [self getFilenamelistOfType:@"log"
                                        fromDirPath:logDir];
    
    if ([fileList count] == 0) {
        return index;
    }
    NSArray*sortedArray = [fileList sortedArrayUsingSelector:@selector(fileTimeCompare:)];

    NSString *fileName = [[[sortedArray objectAtIndex:0] lastPathComponent] stringByDeletingPathExtension];
    index = [fileName integerValue];
    return index;
}

- (NSString *)getFilePath
{
    // 获取最新的文件名，若没有则直接从0开始
    if (_fileIndex == M_GT_INDEX_INVALID) {
        _fileIndex = [self getFileIndex];
    }
    
    NSString *sysDir = [[GTConfig sharedInstance] sysDirByCreated];
    
    NSString *filePath = [NSString stringWithFormat:@"%@%lu.log", sysDir, (unsigned long)_fileIndex];
    
    // 判断文件是否大于设定阈值
    unsigned long long fileSize = [self getFileSize:filePath];
    if (fileSize > M_GT_MB) {
        _fileIndex++;
        if (_fileIndex >= 10) {
            _fileIndex = 0;
        }
        
        filePath = [NSString stringWithFormat:@"%@%lu.log", sysDir, (unsigned long)_fileIndex];
        // 判断文件是否存在，存在则删除
        [self delFile:filePath];
    }

    return filePath;
}

- (NSString *)getFilePathByName:(NSString *)fileName;
{
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_LOG_COMMON_DIR fileName:fileName ofType:M_GT_FILE_TYPE_LOG];
    
    return filePath;
}

#pragma mark - File

- (void)delFile:(NSString *)filePath
{
    if ([self isFileExistAtPath:filePath]) {
        NSError *error;
        if ([[NSFileManager defaultManager] removeItemAtPath:filePath error:&error] != YES) {
            NSLog(@"GT Unable to delete file:%@ error:%@", filePath, [error localizedDescription]);
        }
    }
}

- (void)saveFile:(NSString *)filePath buffer:(NSMutableString *)buffer
{
    FILE *file = fopen([filePath UTF8String], "a+");
    
	if (file) {
        fprintf(file, "%s", [buffer UTF8String]);
		fflush(file);
        fclose(file);
	}
}

- (void)autoSave:(id)sender
{
    @autoreleasepool {
        NSString *filePath = [self getFilePath];
        [self saveFile:filePath buffer:_tmpBuf];
        
        for (int i = 0; i < [[_fileList keys] count]; i++) {
            NSString *key = [[_fileList keys] objectAtIndex:i];
            filePath = [self getFilePathByName:key];
            GTLogBufferSaveFile *obj = [_fileList objectForKey:key];
            switch ([obj fileOpType]) {
                case GTLogBufferClean:
                {
                    [self delFile:filePath];
                    break;
                }
                case GTLogBufferCleanStart:
                case GTLogBufferCleanEnd:
                {
                    [self delFile:filePath];
                    [self saveFile:filePath buffer:[obj tmpBuf]];
                    break;
                }
                case GTLogBufferStart:
                {
                    [self saveFile:filePath buffer:[obj tmpBuf]];
                    break;
                }
                case GTLogBufferEnd:
                {
                    [self saveFile:filePath buffer:[obj tmpBuf]];
                    break;
                }
                default:
                    break;
            }
            
        }
        
        _isSaving = NO;
        [_tmpBuf release];
        [_tmpFileList release];
    }
    
}

@end
#endif
