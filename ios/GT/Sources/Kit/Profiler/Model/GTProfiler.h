//
//  GTProfiler.h
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
#import <Foundation/Foundation.h>
#import "GTList.h"
#import "GTLogBuffer.h"


@interface GTProfilerItem : NSObject
{
    NSString       *_key;
    NSTimeInterval _date;
    NSTimeInterval _timeValue;
}

@property (nonatomic, assign) NSTimeInterval timeValue;
@property (nonatomic, assign) NSTimeInterval date;
@property (nonatomic, retain) NSString *key;

@end

@interface GTProfilerDetail : NSObject
{
    NSString       *_key;
    NSString       *_fileName;
    NSUInteger      _count;
    NSTimeInterval  _totalTime;
    NSTimeInterval  _avgTime;
    NSTimeInterval  _maxTime;
    NSTimeInterval  _minTime;
    
    NSMutableArray *_timeArray;
}

@property (nonatomic, retain) NSString *key;
@property (nonatomic, retain) NSString *fileName;
@property (nonatomic, assign) NSUInteger count;
@property (nonatomic, assign) NSTimeInterval totalTime;
@property (nonatomic, assign) NSTimeInterval avgTime;
@property (nonatomic, assign) NSTimeInterval maxTime;
@property (nonatomic, assign) NSTimeInterval minTime;
@property (nonatomic, retain) NSMutableArray *timeArray;

- (void)addTime:(NSTimeInterval)time withDate:(NSTimeInterval)date;
- (void)saveAll:(NSString *)fileName;

@end


@interface GTProfiler : NSObject
{
    NSString * _fileName;
    GTList   * _perfTmp;
    
    GTList   * _analyseList;
    GTLogBuffer *_timeBuf;
    NSTimer     *_timer;
}

M_GT_AS_SINGLETION( GTProfiler );

@property (nonatomic, retain) NSString * fileName;
@property (nonatomic, retain) GTList * perfTmp;
@property (nonatomic, retain) GTList * analyseList;


- (void)startRecTime:(NSString *)tagStr forGroup:(NSString *)groupKey inThread:(BOOL)inThread;
- (NSTimeInterval)endRecTime:(NSTimeInterval)date forKey:(NSString *)tagStr forGroup:(NSString *)groupKey inThread:(BOOL)inThread;

- (void)clearAll;
- (void)saveAll:(NSString *)fileName;

- (GTProfilerDetail *)getLogAnalyse:(NSString *)logContent forKey:(NSString *)groupKey;
- (NSTimeInterval)getRecTime:(NSString *)key forKey:(NSString *)groupKey;


@end
#endif
