//
//  GTLog.h
//  GTKit
//
//  Created by  on 12-10-10.
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
#import "GTDebugDef.h"
#import "GTList.h"
#import "GTProfiler.h"
#import "GTLog.h"
#import "GTLogBuffer.h"
#import "GTLogConfig.h"


typedef enum {
	GT_LOG_DEBUG = 0,
	GT_LOG_INFO,
    GT_LOG_WARNING,
    GT_LOG_ERROR,
    GT_LOG_INVALID
} GTLogLevel;

@interface GTLogRecord : NSObject
{
    NSTimeInterval   _date;
    NSString        *_tag;
    GTLogLevel       _level;
    NSString        *_levelStr;
    NSString        *_thread;
    NSString        *_content;
}

@property (nonatomic, assign) NSTimeInterval date;
@property (nonatomic, assign) GTLogLevel level;
@property (nonatomic, retain) NSString *levelStr;
@property (nonatomic, retain) NSString *tag;
@property (nonatomic, retain) NSString *thread;
@property (nonatomic, retain) NSString *content;


@end


@interface GTLog : NSObject
{
    NSString        * _fileName;
    NSMutableArray  *_logs;
    NSArray         *_levels;
    GTList          *_tags;
    GTLogBuffer     *_commonBuf;
    
    BOOL            _isModified;
    NSTimer         *_timer;
}

M_GT_AS_SINGLETION( GTLog );


@property (nonatomic, retain) NSString * fileName;
@property (nonatomic, retain) NSMutableArray *logs;
@property (nonatomic, retain) NSArray *levels;
@property (nonatomic, retain) GTList *tags;
@property (nonatomic) BOOL isModified;

- (void)addLog:(NSString *)content tag:(NSString *)tag forLevel:(GTLogLevel)level;

- (void)clearAll;
- (void)saveAll:(NSString *)fileName;
- (void)saveLogs:(NSMutableArray *)array fileName:(NSString *)fileName;

- (void)cleanLog:(NSString *)fileName;
- (void)startLog:(NSString *)fileName;
- (void)endLog:(NSString *)fileName;

- (NSMutableArray *)searchContent:(NSString *)content withTag:(NSString *)tag withLevel:(GTLogLevel)level inArray:(NSMutableArray *)logArray;

@end

#endif
