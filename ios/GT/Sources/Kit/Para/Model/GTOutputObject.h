//
//  GTOutputObject.h
//  GTKit
//
//  Created   on 12-5-30.
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

#import <UIKit/UIKit.h>
#import "GTOutputDataInfo.h"
#import "GTParaDef.h"
#import "GTList.h"
#import "GTParaOutDef.h"
#import "GTOutputValue.h"

#define M_GT_PARA_AUTOSAVE_RECORD_CNT    1000           //历史总记录数达到该值时写磁盘
#define M_GT_PARA_AUTOSAVE_FILE_RECORD_CNT_MIN    100   //触发写文件要求的最低文件个数
#define M_GT_PARA_AUTOSAVE_FILE_RECORD_CNT_MAX    2000  //每个文件的最多历史记录数
#define M_GT_PARA_OUT_DISK_PROMPT_SIZE   (160 * M_GT_MB) //磁盘占用达到该值提醒用户及早清理磁盘
#define M_GT_PARA_OUT_DISK_MAX_SIZE      (200 * M_GT_MB) //磁盘占用达到该值自动关闭采集功能


@protocol GTOutShowDelegate <NSObject>

- (void)willSaveDisk:(NSDictionary *)dict;

@optional


@end

@interface GTOutputObject : NSObject
{
    GTOutputDataInfo *_dataInfo;
    NSUInteger       _status;               //枚举定义类型:GTParaStatus
    BOOL             _writeToLog;
    
    NSInteger        _switchForHistory;     //枚举定义类型:GTParaHistoryStatus
    BOOL             _switchForWarning;
    NSMutableArray  *_history;
    NSUInteger       _historyCnt;           //当前内存和磁盘总记录数
    NSString        *_recordClassStr;       //历史记录对应的类名，用于写入文件时调用rowTitle
    NSString        *_fileName;
    NSThread        *_fileOpThread;         //线程操作：删除记录
    
    NSTimeInterval  _totalTime;
    NSTimeInterval  _avgTime;
    NSTimeInterval  _maxTime;
    NSTimeInterval  _minTime;
    
    BOOL            _showWarning;           //UI是否显示告警标志，产生告警时置YES，待用户查看后置NO
    NSTimeInterval  _thresholdInterval;     //告警持续时间
    
    double          _lowerThresholdValue;   //下限告警阈值
    NSTimeInterval  _lowerStartWarning;
    NSString        *_lowerStartWarningStr;
    GTList          *_lowerWarningList;
    
    double          _upperThresholdValue;   //上限告警阈值
    NSTimeInterval  _upperStartWarning;
    NSString        *_upperStartWarningStr;
    GTList          *_upperWarningList;
    
    
    NSString        *_vcForDetail;
    id<GTParaDelegate> _paraDelegate;
    id<GTOutShowDelegate> _showDelegate;
}

@property (nonatomic, retain) GTOutputDataInfo *dataInfo;
@property (nonatomic, retain) NSString *vcForDetail;
@property (nonatomic, retain) id<GTParaDelegate> paraDelegate;
@property (nonatomic, assign) id<GTOutShowDelegate> showDelegate;
@property (nonatomic, assign) SEL  selFloatingDesc;
@property (nonatomic, assign) NSUInteger status;
@property (nonatomic, assign) BOOL writeToLog;

@property (nonatomic, assign) NSInteger switchForHistory;
@property (nonatomic, assign) BOOL switchForWarning;
@property (nonatomic, retain) NSMutableArray *history;
@property (nonatomic, retain) NSString *recordClassStr;
@property (nonatomic, retain) NSString *fileName;
@property (nonatomic, assign) NSUInteger historyCnt;
@property (nonatomic, assign) NSTimeInterval totalTime;
@property (nonatomic, assign) NSTimeInterval avgTime;
@property (nonatomic, assign) NSTimeInterval maxTime;
@property (nonatomic, assign) NSTimeInterval minTime;

@property (nonatomic, assign) BOOL showWarning;
@property (nonatomic, assign) NSTimeInterval thresholdInterval;

@property (nonatomic, assign) double lowerThresholdValue;
@property (nonatomic, assign) NSTimeInterval lowerStartWarning;
@property (nonatomic, retain) NSString *lowerStartWarningStr;
@property (nonatomic, retain) GTList *lowerWarningList;

@property (nonatomic, assign) double upperThresholdValue;
@property (nonatomic, assign) NSTimeInterval upperStartWarning;
@property (nonatomic, retain) NSString *upperStartWarningStr;
@property (nonatomic, retain) GTList *upperWarningList;


- (id)initWithKey:(NSString*)key alias:(NSString*)alias value:(GTOutputValue *)value;

- (BOOL)setDataValue:(GTOutputValue *)value;
- (void)clearHistroy;
- (void)updateWarningList;
- (void)notifyParaDelegate;

- (NSMutableDictionary *)dictionaryForSave;
- (void)exportCSV:(NSString *)filePath param:(NSDictionary *)dic;
- (void)saveFile:(NSString *)fileName inThread:(BOOL)inThread;

//保存到系统文件
+ (void)exportDisk:(NSDictionary *)dic;
- (void)notifyUI;

@end

#endif
