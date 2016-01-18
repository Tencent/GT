//
//  GTOutput.h
//  GTKit
//
//  Created   on 12-5-29.
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
#import "GTOutputObject.h"
#import "GTList.h"
#import "GTDebugDef.h"


@interface GTOutputList : GTList
{
    NSMutableArray  *_acArray;      //展示在AC上的队列
    NSMutableArray  *_normalArray;  //展示在Normal区的队列
    NSMutableArray  *_disabledArray;//展示在Disabled区的队列
    
    BOOL             _showWarning;  //整个列表是否存在告警并提示用户（震动+发音）
    NSString        *_dirName;      //一键保存对应的目录名称
    
    NSUInteger       _recordCnt;    //当前所有出参对象中的历史记录数，用于判断是否要自动保存数据
    NSThread        *_fileOpThread; //线程操作：自动保存，删除记录
    BOOL             _threadReady;  //防止多线程同时启动问题，用于启动线程前先置位
    
    NSMutableDictionary *_dicAll;   //记录待保存的数据信息，便于保存后及时的释放
}

M_GT_AS_SINGLETION(GTOutputList);

@property (nonatomic, assign) NSMutableArray *acArray;
@property (nonatomic, assign) NSMutableArray *normalArray;
@property (nonatomic, assign) NSMutableArray *disabledArray;
@property (nonatomic, retain) NSString *dirName;

- (GTOutputValue *)dataValueForKey:(NSString *)key;
- (BOOL)isEnableForKey:(NSString *)key;
- (void)defaultOnAC:(NSString*)key1 key2:(NSString*)key2 key3:(NSString*)key3;
- (void)defaultOnDisabled:(NSArray *)array;
- (void)setStatus:(NSUInteger)status forKey:(NSString *)key;
- (void)insertKey:(NSString *)key2 atKey:(NSString *)key1;

- (void)setParaDelegate:(id <GTParaDelegate>)delegate forKey:(NSString*)key;

- (void)clearHistroyForKey:(NSString*)key;
- (void)saveHistroyForKey:(NSString*)key fileName:(NSString *)fileName inThread:(BOOL)inThread;

- (void)clearAllHistroy;
- (void)saveHistroyForDirName:(NSString *)dirName;


- (BOOL)hasItemHistoryOn;
- (BOOL)itemsAllHistoryOn;
- (void)setAllHistoryOn;
- (void)setAllHistoryOff;

@end


#endif

