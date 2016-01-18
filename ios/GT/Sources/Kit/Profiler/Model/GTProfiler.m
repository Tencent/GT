//
//  GTProfiler.m
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
#import "GTProfiler.h"
#import "GTLogBuffer.h"
#import "GTLogConfig.h"
#import "GTConfig.h"
#import "GTProfilerValue.h"


/* return the mach thread bound to the pthread */
mach_port_t pthread_mach_thread_np(pthread_t);
pthread_t pthread_self(void);

@implementation GTProfilerItem

@synthesize date = _date;
@synthesize timeValue = _timeValue;
@synthesize key = _key;

- (id)initWithKey:(NSString *)key date:(NSTimeInterval)date timeValue:(NSTimeInterval)timeValue
{
    self  = [super init];
    if (self) {
        [self setDate:date];
        [self setTimeValue:timeValue];
        [self setKey:key];
    }
    
    return self;
}

- (void) dealloc
{
    self.date = 0;
    self.key = nil;
    [super dealloc];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@ %@,%f", [NSString stringWithDateEx:_date], _key ,_timeValue];
}

@end


@implementation GTProfilerDetail

@synthesize key = _key;
@synthesize fileName = _fileName;
@synthesize count = _count;
@synthesize totalTime = _totalTime;
@synthesize avgTime = _avgTime;
@synthesize maxTime = _maxTime;
@synthesize minTime = _minTime;
@synthesize timeArray = _timeArray;

- (id) initWithContent:(NSString *)logContent
{
    self  = [super init];
    if (self) {
        [self setKey:logContent];
        [self setFileName:logContent];
        [self setCount:0];
        [self setAvgTime:0];
        [self setTotalTime:0];
        
        _timeArray = [[NSMutableArray alloc] initWithCapacity:1];
    }
    
    return self;
}

- (void) dealloc
{
    [_key release];
    [_fileName release];
    [_timeArray removeAllObjects];
    [_timeArray release];
    [super dealloc];
}

- (NSString *)description
{
    NSMutableString *str = [NSMutableString stringWithFormat:@"%@ = %lu, %.3f, %.3f, %.3f, %.3f", _key, (unsigned long)_count, _totalTime, _avgTime, _maxTime, _minTime];
    GTProfilerValue *value = nil;
    [str appendFormat:@"\r********\r"];
    for (int i = 0; i < [_timeArray count]; i++) {
        value = (GTProfilerValue *)[_timeArray objectAtIndex:i];
        [str appendFormat:@"%.3f,", [value time]];
    }
    [str appendFormat:@"\r********;\r"];
    
    return str;
}


- (void)addTime:(NSTimeInterval)time withDate:(NSTimeInterval)date
{
    _totalTime += time;
    _count++;
    if (_count) {
        _avgTime = _totalTime/_count;
    }
    
    if (_count == 1) {
        _maxTime = _minTime = time;
    } else if (time > _maxTime) {
        _maxTime = time;
    } else if (time < _minTime) {
        _minTime = time;
    }
    
    GTProfilerValue *value = [[GTProfilerValue alloc] initWithDate:date time:time];
    [_timeArray addObject:value];
    [value release];
}

- (void)decTime:(NSTimeInterval)time
{
    _totalTime -= time;
    _count--;
    if (_count) {
        _avgTime = _totalTime/_count;
    }
}

- (void)saveAll:(NSString *)fileName
{
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_LOG_TIME_DIR fileName:fileName ofType:M_GT_FILE_TYPE_LOG];
    
    FILE *file = fopen([filePath UTF8String], "w");
    
	if (file) {
        fprintf(file, "tagKey = count | totalTime | avgTime | maxTime | minTime | timeArray;\r");
        fprintf(file, "%s", [[self description] UTF8String]);
		fflush(file);
        fclose(file);
	}
}

@end


@implementation GTProfiler

M_GT_DEF_SINGLETION(GTProfiler)

@synthesize fileName = _fileName;
@synthesize perfTmp = _perfTmp;
@synthesize analyseList = _analyseList;


- (id)init
{
	self = [super init];
    
	if(self)
	{
        _fileName    = @"time";
        _perfTmp    = [[GTList alloc] init];
        _analyseList= [[GTList alloc] init];
	}
	
	return self;
}

- (void)dealloc
{
    [_perfTmp release];
    [_analyseList release];
    [super dealloc];
}


- (void)startRecTime:(NSString *)tagStr forGroup:(NSString *)groupKey inThread:(BOOL)inThread
{
    
    NSString *tagKey = tagStr;
    if (inThread) {
        mach_port_t tid = pthread_mach_thread_np(pthread_self());
        tagKey = [NSString stringWithFormat:@"%@_%u", tagStr, tid];
    }
    
    @synchronized (self) {
        NSMutableDictionary *tmpList = [_perfTmp objectForKey:groupKey];
        
        if(nil == tmpList)
        {
            tmpList = [[[NSMutableDictionary alloc] init] autorelease];
            [_perfTmp setObject:tmpList forKey:groupKey];
        }
        
        NSTimeInterval now = [GTUtility timeIntervalSince1970];
        [tmpList setObject:[NSNumber numberWithDouble:now] forKey:tagKey];
    }

}

- (NSTimeInterval)endRecTime:(NSTimeInterval)date forKey:(NSString *)tagStr forGroup:(NSString *)groupKey inThread:(BOOL)inThread
{
    NSTimeInterval now = date;
    NSTimeInterval old = 0;
    
    NSString *tagKey = tagStr;
    if (inThread) {
        mach_port_t tid = pthread_mach_thread_np(pthread_self());
        tagKey = [NSString stringWithFormat:@"%@_%u", tagStr, tid];
    }
    
    @synchronized (self) {
        NSMutableDictionary *tmpList = [_perfTmp objectForKey:groupKey];
        
        if(nil == tmpList)
        {
            return 0;
        }
        
        NSNumber *oldObj = [tmpList objectForKey:tagKey];
        if (nil == oldObj)
        {
            return 0;
        }
        
        old = [oldObj doubleValue];
        
        
        NSTimeInterval timeInterval = now - old;
        
        if (![[GTLogConfig sharedInstance] profilerSwitch]) {
            return timeInterval;
        }
        
        NSString *newTagKey = tagKey;
        if (inThread) {
            mach_port_t tid = pthread_mach_thread_np(pthread_self());
            NSString *tidStr = [NSString stringWithFormat:@"_%u", tid];
            NSRange range = [tagKey rangeOfString:tidStr];
            if (range.location != NSNotFound) {
                range.length = range.location;
                range.location = 0;
                newTagKey = [tagKey substringWithRange:range];
            }
            newTagKey = [NSString stringWithFormat:@"%@(T)", newTagKey];
        }
        
        GTProfilerItem *logRec = [[GTProfilerItem alloc] initWithKey:newTagKey date:old timeValue:timeInterval];
        [tmpList removeObjectForKey:tagKey];
                
        [self addAnalyseLog:logRec forKey:groupKey];
        [logRec release];
        [self observeTick];
        
        return timeInterval;
    }
}


- (NSTimeInterval)getRecTime:(NSString *)key forKey:(NSString *)groupKey
{
    
    GTProfilerDetail *detail = [self getLogAnalyse:key forKey:groupKey];
    if(nil == detail)
	{
        return 0;
	}
    
    NSUInteger count = [[detail timeArray] count];
    if (count > 0) {
        GTProfilerValue *value = (GTProfilerValue *)[[detail timeArray] objectAtIndex:(count-1)];
        return [value time];
    }
    
    return 0;
}


- (void)addAnalyseLog:(GTProfilerItem *)logPerf forKey:(NSString *)logKey
{
    GTList *list = [_analyseList objectForKey:logKey];
    if (!list) {
        list = [[GTList alloc] init];
        [_analyseList setObject:list forKey:logKey];
        [list release];
    }
    
    GTProfilerDetail *logAnalyse = [list objectForKey:[logPerf key]];
    if (logAnalyse == nil) {
        logAnalyse = [[GTProfilerDetail alloc] initWithContent:[logPerf key]];
        [list setObject:logAnalyse forKey:[logPerf key]];
        [logAnalyse release];
    }
    [logAnalyse addTime:[logPerf timeValue] withDate:[logPerf date]];
    
    return;
}

- (GTProfilerDetail *)getLogAnalyse:(NSString *)key forKey:(NSString *)groupKey
{
    GTList *analist = [_analyseList objectForKey:groupKey];
    if (analist == nil) {
        return nil;
    }
    
    return [analist objectForKey:key];
}

- (void)clearAll
{
    [_analyseList deleteAll];
}


- (void)saveAll:(NSString *)fileName
{
    [self setFileName:fileName];
    //对应目录不存在则创建一个新的目录
    NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_LOG_TIME_DIR fileName:fileName ofType:M_GT_FILE_TYPE_LOG];
    
    FILE *file = fopen([filePath UTF8String], "w");
    
	if (file) {
        fprintf(file, "{groupKey\r");
        fprintf(file, "tagKey = count | totalTime | avgTime | maxTime | minTime | timeArray;\r");
        fprintf(file, "}\r");
        for (int i = 0; i < [[_analyseList keys] count]; i++) {
            id groupKey = [[_analyseList keys] objectAtIndex:i];
            GTList *list = [_analyseList objectForKey:groupKey];
            
            fprintf(file, "{\"%s\"\r", [groupKey UTF8String]);
            for (int j = 0; j < [[list keys] count]; j++) {
                id tagKey = [[list keys] objectAtIndex:j];
                GTProfilerDetail *logRec = [list objectForKey:tagKey];
                fprintf(file, "%s", [[logRec description] UTF8String]);
            }
            fprintf(file, "}\r");
        }
        
		fflush(file);
        fclose(file);
	}
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
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_PROFILER_MOD object:nil];
    [self unobserveTick];
}

@end


#pragma mark - Profiler Interface Switch
void func_setTimeSwitch(bool on)
{
    @autoreleasepool {
        [[GTLogConfig sharedInstance] setProfilerSwitch:on];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    }
    
}

#pragma mark - Profiler Interface OC
void func_startRecTimeForOC(NSString * logKey, NSString * format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(logKey);
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        [[GTProfiler sharedInstance] startRecTime:str forGroup:logKey inThread:NO];
    }
    
}

NSTimeInterval func_endRecTimeForOC(NSString * logKey, NSString * format,...)
{
    NSTimeInterval now = [GTUtility timeIntervalSince1970];
    NSTimeInterval interval = 0;
    
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK_EX(logKey, 0);
        M_GT_PTR_NULL_CHECK_EX(format, 0);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        interval = [[GTProfiler sharedInstance]  endRecTime:now forKey:str forGroup:logKey inThread:NO];
        
    }
    
    return interval;
}


NSTimeInterval func_getRecTimeForOC(NSString* logKey, NSString* format,...)
{
    NSTimeInterval interval = 0;
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK_EX(logKey, 0);
        M_GT_PTR_NULL_CHECK_EX(format, 0);
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        interval = [[GTProfiler sharedInstance] getRecTime:str forKey:logKey];
    }

    
    return interval;
}

void func_startRecTimeInThreadForOC(NSString* logKey, NSString* format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(logKey);
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        [[GTProfiler sharedInstance] startRecTime:str forGroup:logKey inThread:YES];
        
    }

}
NSTimeInterval func_endRecTimeInThreadForOC(NSString* logKey, NSString* format,...)
{
    NSTimeInterval now = [GTUtility timeIntervalSince1970];
    NSTimeInterval interval = 0;
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK_EX(logKey, 0);
        M_GT_PTR_NULL_CHECK_EX(format, 0);
        
        M_GT_OC_FORMAT_INIT;
        NSString * str = M_GT_OC_FORMAT_STR;
        interval = [[GTProfiler sharedInstance] endRecTime:now forKey:str forGroup:logKey inThread:YES];
    }

    return interval;
}

#pragma mark - Time LOG Interface C

void func_startRecTime(const char * logKey, const char * format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(logKey);
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        NSString * logKeyStr = [NSString stringWithCString:logKey encoding:NSUTF8StringEncoding];
        [[GTProfiler sharedInstance] startRecTime:str forGroup:logKeyStr inThread:NO];
    }
    
}
double func_endRecTime(const char * logKey, const char * format,...)
{
    NSTimeInterval now = [GTUtility timeIntervalSince1970];
    double interval = 0;
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK_EX(logKey, 0);
        M_GT_PTR_NULL_CHECK_EX(format, 0);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        NSString * logKeyStr = [NSString stringWithCString:logKey encoding:NSUTF8StringEncoding];
        interval = [[GTProfiler sharedInstance] endRecTime:now forKey:str forGroup:logKeyStr inThread:NO];
        
    }

    return interval;
}


double func_getRecTime(const char * logKey, const char * format,...)
{
    double interval = 0;
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK_EX(logKey, 0);
        M_GT_PTR_NULL_CHECK_EX(format, 0);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        NSString * logKeyStr = [NSString stringWithCString:logKey encoding:NSUTF8StringEncoding];
        interval = [[GTProfiler sharedInstance] getRecTime:str forKey:logKeyStr];
    }
    
    return interval;
}

void func_startRecTimeInThread(const char * logKey, const char * format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(logKey);
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        NSString * logKeyStr = [NSString stringWithCString:logKey encoding:NSUTF8StringEncoding];
        
        [[GTProfiler sharedInstance] startRecTime:str forGroup:logKeyStr inThread:YES];
    }
    
}

double func_endRecTimeInThread(const char * logKey, const char * format,...)
{
    NSTimeInterval now = [GTUtility timeIntervalSince1970];
    double interval = 0;
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK_EX(logKey, 0);
        M_GT_PTR_NULL_CHECK_EX(format, 0);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        NSString * logKeyStr = [NSString stringWithCString:logKey encoding:NSUTF8StringEncoding];
        
        interval = [[GTProfiler sharedInstance] endRecTime:now forKey:str forGroup:logKeyStr inThread:YES];
    }
    
    return interval;
}

void func_saveRecTime(const char * format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * str = M_GT_FORMAT_STR;
        [[GTProfiler sharedInstance] saveAll:str];
    }
}

#endif
