//
//  GTCrashFileHandler.m
//  GTKit
//
//  Created   on 13-6-27.
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

#import "GTCrashFileHandler.h"
#include <libkern/OSAtomic.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include <execinfo.h>
#import "GTConfig.h"

int backtrace(void **buffer, int size);
char **backtrace_symbols(void *const *buffer, int size);
void backtrace_symbols_fd(void *const *buffer, int size, int fd);

NSString * const M_GT_EXCEPTION_SIGNAL_NAME = @"EXCEPTION_SIGNAL";
NSString * const M_GT_EXCEPTION_SIGNAL_KEY = @"EXCEPTION_SIGNAL_KEY";
NSString * const M_GT_EXCEPTION_ADDRESS_KEY = @"EXCEPTION_ADDRESS_KEY";

volatile int32_t g_gtUncaughtExceptionCount = 0;
const int32_t g_gtUncaughtExceptionMaximum = 10;

static NSUncaughtExceptionHandler* g_old_ExceptionHandler = NULL;


void exceptionHandlerForGT(NSException *exception) {
    
    if (g_old_ExceptionHandler != NULL) {
        g_old_ExceptionHandler(exception);
    }
    
    NSArray *arr = [exception callStackSymbols];
    NSString *reason = [exception reason];
    NSString *name = [exception name];
    
    NSString *crashStr = [NSString stringWithFormat:@"NAME : %@\rREASON : %@\r%@\rCALL STACK:\r%@\r\r\r", name,reason, [GTCrashFileHandler getAppInfo], [arr componentsJoinedByString:@"\r"]];
    
    
    //保存信息到文件中
    [GTCrashFileHandler saveDataToLocal:crashStr];
}

void crashSignalHandlerForGT(int signal) {
    NSArray *callStack = [GTCrashFileHandler backtrace];
    
    [[[[GTCrashFileHandler alloc] init] autorelease]
     performSelectorOnMainThread:@selector(handleException:)
     withObject:[NSException
                 exceptionWithName:M_GT_EXCEPTION_SIGNAL_NAME
                 reason:[NSString stringWithFormat:NSLocalizedString(@"Signal %@(%d) was raised.\n"
                                                                     @"%@", nil), [GTCrashFileHandler getSignalInfo:signal], signal, [GTCrashFileHandler getAppInfo]]
                 userInfo:[NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:[NSNumber numberWithInt:signal], callStack, nil] forKeys:[NSArray arrayWithObjects:M_GT_EXCEPTION_SIGNAL_KEY, M_GT_EXCEPTION_ADDRESS_KEY, nil]]]
     waitUntilDone:YES];
    
}

@implementation GTCrashFileHandler

M_GT_DEF_SINGLETION(GTCrashFileHandler)

- (id)init
{
    self = [super init];
    if (self) {
    
        // 记录之前已经注册的回调
        g_old_ExceptionHandler = NSGetUncaughtExceptionHandler();
        
        // 注册异常回调
        NSSetUncaughtExceptionHandler(&exceptionHandlerForGT);
        
        // 注册异常信号回调
        [self installUncaughtExceptionHandler];
    }
    
    return self;
}

- (void)installUncaughtExceptionHandler
{
    signal(SIGABRT, crashSignalHandlerForGT);
    signal(SIGILL, crashSignalHandlerForGT);
    signal(SIGSEGV, crashSignalHandlerForGT);
    signal(SIGFPE, crashSignalHandlerForGT);
    signal(SIGBUS, crashSignalHandlerForGT);
    signal(SIGPIPE, crashSignalHandlerForGT);
}

- (void)handleException:(NSException *)exception
{
    NSArray *arr = [[exception userInfo] objectForKey:M_GT_EXCEPTION_ADDRESS_KEY];
    NSString *reason = [exception reason];
    NSString *name = [exception name];
    
    NSString *crashStr = [NSString stringWithFormat:@"NAME : %@\rREASON : %@\rCALL STACK:\r%@\r\r\r", name, reason, arr];
    
    
    //保存信息到文件中
    [GTCrashFileHandler saveDataToLocal:crashStr];
    
    NSSetUncaughtExceptionHandler(NULL);
    
    signal(SIGABRT, SIG_DFL);
    signal(SIGILL, SIG_DFL);
    signal(SIGSEGV, SIG_DFL);
    signal(SIGFPE, SIG_DFL);
    signal(SIGBUS, SIG_DFL);
    signal(SIGPIPE, SIG_DFL);
    
    if ([[exception name] isEqual:M_GT_EXCEPTION_SIGNAL_NAME])
    {
        kill(getpid(), [[[exception userInfo] objectForKey:M_GT_EXCEPTION_SIGNAL_KEY] intValue]);
    }
    else
    {
        [exception raise];
    }
    
}

+ (NSString*)getSignalInfo:(int)signal
{
    switch (signal) {
        case SIGABRT:
            return @"SIGABRT";
            
        case SIGILL:
            return @"SIGILL";
            
        case SIGSEGV:
            return @"SIGSEGV";
            
        case SIGFPE:
            return @"SIGFPE";
            
        case SIGBUS:
            return @"SIGBUS";
            
        case SIGPIPE:
            return @"SIGPIPE";
            
        default:
            return @"OTHER";
    }
}

+ (NSString*)getAppInfo
{
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    NSString *appInfo = [NSString stringWithFormat:@"App : %@ %@(%@)\nDevice : %@\nOS Version : %@ %@\n", [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"], [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"], [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"], [UIDevice currentDevice].model, [UIDevice currentDevice].systemName, [UIDevice currentDevice].systemVersion];
#pragma clang diagnostic pop
    return appInfo;
}



+ (NSArray *)backtrace
{
    void* callstack[128];
    int frames = backtrace(callstack, 128);
    char **strs = backtrace_symbols(callstack, frames);
    
    int i;
    NSMutableArray *backtrace = [NSMutableArray arrayWithCapacity:frames];
    
    for (i = 2;//skip address
         i < frames;
         i++)
    {
        [backtrace addObject:[NSString stringWithUTF8String:strs[i]]];
    }
    
    free(strs);    
    
    return backtrace;
    
}

+ (void)saveDataToLocal:(NSString*)crashInfo
{
    // 获取到document下面的文件：
    NSString *crashDirPath = [NSString stringWithFormat:@"%@/%@", [[GTConfig sharedInstance] usrDir], M_GT_CRASH_DIR];
    // 如果文件夹不存在，创建一个
    if (![[NSFileManager defaultManager] fileExistsAtPath:crashDirPath])
    {
        [[NSFileManager defaultManager] createDirectoryAtPath:crashDirPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat : @"yyyy年M月d日 H点m分ss秒"];
    NSString* str = [formatter stringFromDate:[NSDate date]];
    [formatter release];
    
    NSString* filePath =[str stringByAppendingFormat:@"%@", @".log"];
    
    FILE *file = fopen([[crashDirPath stringByAppendingPathComponent:filePath] UTF8String], "a+");
    
	if (file) {
        fprintf(file, "%s", [crashInfo UTF8String]);
		fflush(file);
        fclose(file);
	}
    
    NSLog(@"%@", crashInfo);
}

+ (NSArray*) getCrashFileList
{
    //获取到document下面的文件：
    NSString *crashDirPath = [NSString stringWithFormat:@"%@/%@", [[GTConfig sharedInstance] usrDir], M_GT_CRASH_DIR];
    
    NSArray *filePathsArray = [self filesByModDate:crashDirPath];
    return filePathsArray;
}

//按最后更新时间排序
+ (NSArray *)filesByModDate: (NSString *)fullPath
{
    NSError* error = nil;
    NSArray* files = [[NSFileManager defaultManager] subpathsOfDirectoryAtPath:fullPath
                                                                         error:&error];
    if(error == nil)
    {
        NSMutableDictionary* filesAndProperties = [NSMutableDictionary	dictionaryWithCapacity:[files count]];
        for(NSString* path in files)
        {
            NSDictionary* properties = [[NSFileManager defaultManager]
                                        attributesOfItemAtPath:[fullPath stringByAppendingPathComponent:path]
                                        error:&error];
            NSDate* modDate = [properties objectForKey:NSFileModificationDate];
            
            if(error == nil)
            {
                [filesAndProperties setValue:modDate forKey:path];
            }
        }
        return [filesAndProperties keysSortedByValueUsingSelector:@selector(compare:)];
    }
    
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wnonnull"
	return [NSArray arrayWithObjects:nil];
#pragma clang diagnostic pop
}


+ (NSString*)readCrashDetail:(NSString*) aPath
{
    NSError* err = nil;
    NSString* data = [NSString stringWithContentsOfFile:aPath encoding:NSUTF8StringEncoding error:&err];
    return data;
}

+(BOOL)removeFileInPath:(NSString*) aPath{
    return [[NSFileManager defaultManager] removeItemAtPath:aPath error:nil];
}



@end
#endif
