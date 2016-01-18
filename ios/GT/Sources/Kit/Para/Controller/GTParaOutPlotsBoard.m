//
//  GTParaOutPlotsBoard.m
//  GTKit
//
//  Created   on 13-11-23.
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

#import "GTParaOutPlotsBoard.h"
#import "GTProfilerValue.h"
#import "GTConfig.h"

@interface GTParaOutPlotsBoard ()

@end

@implementation GTParaOutPlotsBoard

@synthesize history = _history;
@synthesize lastGetDict = _lastGetDict;

- (void)load
{
    [super load];
    _plotDataBuf = [[GTPlotsData alloc] init];
    self.lastGetDict = nil;
}


- (void)unload
{
    M_GT_SAFE_FREE( _backgroundView );
    M_GT_SAFE_FREE( _summaryView );
    M_GT_SAFE_FREE( _plotView );
    M_GT_SAFE_FREE( _count );
    M_GT_SAFE_FREE( _countValue );
    
    [_plotDataBuf release];
    [_data setShowDelegate:nil];
    
    self.history = nil;
    self.lastGetDict = nil;
    [super unload];
}

- (void)bindData:(GTOutputObject *)data
{
	[super bindData:data];
    
    [_data setShowDelegate:self];
    
    [self resetPlotDataBuf];
}


- (void)initHistoryUI
{
    _backgroundView = [[UIView alloc] init];
    _backgroundView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:_backgroundView];
    
    _summaryView = [[UIView alloc] init];
    _summaryView.backgroundColor = M_GT_CELL_BKGD_COLOR;
    _summaryView.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _summaryView.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [_backgroundView addSubview:_summaryView];
    
    _count = [[UILabel alloc] init];
    _count.font = [UIFont systemFontOfSize:12.0];
    _count.textColor = M_GT_LABEL_COLOR;
    _count.textAlignment = NSTextAlignmentRight;
    _count.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_count];
    
    _countValue = [[UILabel alloc] init];
    _countValue.font = [UIFont systemFontOfSize:12.0];
    _countValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _countValue.textAlignment = NSTextAlignmentLeft;
    _countValue.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_countValue];
    
    _plotView = [[GTPlotsView alloc] init];
    
    if ([_data paraDelegate]) {
        if ([[_data paraDelegate] respondsToSelector:@selector(upperBound)] && [[_data paraDelegate] respondsToSelector:@selector(lowerBound)]) {
            _plotView.autoCalBound = NO;
            _plotView.lowerBound = (CGFloat)[[_data paraDelegate] lowerBound];
            _plotView.upperBound = (CGFloat)[[_data paraDelegate] upperBound];
        }
        
        if ([[_data paraDelegate] respondsToSelector:@selector(yDesc)]) {
            [_plotView setYDesc:[[_data paraDelegate] yDesc]];
        }
    }
    
    [_backgroundView addSubview:_plotView];
    _plotView.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _plotView.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    
    [_plotView setShowAvg:YES];
}


- (void)initDetailUI
{
    [self initHistoryUI];
    [self viewLayout];
    
    [_plotView setDataSource:self];
    [self updateData];
}

- (void)viewLayout
{
    CGRect rect = M_GT_BOARD_FRAME;
    //左右两边预留10像素
    CGFloat offset = 10;
    CGFloat width = [self widthForOutDetail];
    
    self.view.backgroundColor = M_GT_CELL_BKGD_COLOR;
    
    CGRect frame;
    frame.origin.x = offset;
    frame.origin.y = rect.origin.y + 5;
    frame.size.height = M_GT_BOARD_HEIGHT - 5;
    frame.size.width = width;
    
    [_backgroundView setFrame:frame];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [_data setShowDelegate:self];
    [_plotView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [_data setShowDelegate:nil];
    [super viewWillDisappear:animated];
}


#pragma mark - 定时更新
- (void)resetData
{
    [self resetPlotDataBuf];
    [_plotView reloadData];
}

- (void)updateData
{
    if ([self loadMemoryData:[_data history] historyCnt:[_data historyCnt]]) {
        [_plotView reloadData];
    }
}

#pragma mark - GTOutShowDelegate

- (void)willSaveDisk:(NSDictionary *)dict
{
    NSArray *history = [dict objectForKey:@"history"];
    NSInteger historyCnt = [[dict objectForKey:@"historyCnt"] integerValue];
    
    //加载数据
    if ([self loadMemoryData:history historyCnt:historyCnt]) {
        [_plotView reloadData];
    }
    
    return;
}

#pragma mark - 数据缓冲自定义曲线展示扩展

- (void)plotDataInitWithMemory:(NSArray *)array
{
    NSUInteger count = [array count];
    
    NSMutableArray *dates = [[NSMutableArray alloc] initWithCapacity:count];
    NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTProfilerValue *obj = nil;
    
    for (int i = 0; i < count; i++) {
        obj = [array objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
        [values addObject:[NSNumber numberWithDouble:[obj time]]];
    }
    
    [_plotDataBuf setDates:dates];
    [dates release];
    
    //这里支持多曲线，所以输入对应为二维数组
    [_plotDataBuf setCurves:[NSMutableArray arrayWithObjects:values, nil]];
    [values release];
}

- (void)plotDataInitWithDisk:(NSArray *)array
{
    NSUInteger count = [array count];
    
    NSMutableArray *dates = [[NSMutableArray alloc] initWithCapacity:count];
    NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:count];
    
    //更新数据
    for (int i = 0; i < count; i++) {
        NSString *item = [array objectAtIndex:i];
        NSArray *itemArray = [item componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@","]];
        if ([itemArray count] != 2) {
            continue;
        }
        
        //日期格式转换为秒数
        [dates addObject:[NSNumber numberWithDouble:[(NSString *)[itemArray objectAtIndex:0] timeValue]]];
        [values addObject:[NSNumber numberWithDouble:[[itemArray objectAtIndex:1] doubleValue]]];
    }
    
    [_plotDataBuf setDates:dates];
    [dates release];
    
    //这里支持多曲线，所以输入对应为二维数组
    [_plotDataBuf setCurves:[NSMutableArray arrayWithObjects:values, nil]];
    [values release];
}


- (void)plotDataUpdateWithMemory:(NSArray *)array fromIndex:(NSInteger)index
{
    if ([[_plotDataBuf curves] count] == 0) {
        return;
    }
    
    NSMutableArray *dates = [_plotDataBuf dates];
    NSMutableArray *values = [[_plotDataBuf curves] objectAtIndex:0];
    
    GTProfilerValue *obj = nil;
    
    for (NSUInteger i = index; i < [array count]; i++) {
        obj = [array objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
        [values addObject:[NSNumber numberWithDouble:[obj time]]];
    }

}

#pragma mark - 数据缓冲区相关逻辑

- (void)resetPlotDataBuf
{
    //指定对象后，初始化展示缓冲区数据
    NSInteger histroyIndex = [_data historyCnt] - [[_data history] count];
    if (histroyIndex <= 0) {
        [self initPlotDataBuf];
    } else {
        //加载disk数据
        NSInteger preOffset = _plotView.capacity * 5;
        NSInteger preStartIndex = MAX(0, histroyIndex - preOffset);
        NSInteger preEndIndex = MIN(histroyIndex + preOffset, [_data historyCnt]);
        
        [self fileGetThreadStart:preStartIndex endIndex:preEndIndex];
    }
}

//初始化缓冲区，使用内存中的历史数据信息
- (void)initPlotDataBuf
{
    self.history = [_data history];
    NSUInteger count = [_history count];
    
    [self plotDataInitWithMemory:_history];
    
    if ([_data historyCnt] > count) {
        [_plotDataBuf setHistoryIndex:([_data historyCnt] - count)];
    } else {
        [_plotDataBuf setHistoryIndex:0];
    }
    
    [_plotDataBuf setHistoryCnt:[_data historyCnt]];
    
}



//磁盘数据读取后的数据更新
- (void)updateDiskData:(NSDictionary *)dic
{
    NSArray *array = [dic objectForKey:@"result"];
    if ([array count] == 0) {
        return;
    }
    
    [self plotDataInitWithDisk:array];
    [_plotDataBuf setHistoryIndex:[[dic objectForKey:@"resultIndex"] integerValue]];
    
    
    [self loadMemoryData:[_data history] historyCnt:[_data historyCnt]];
    
    [_plotView setStatus:GTPlotsStatusNormal];
    [_plotView reloadData];
}


//当前缓存区的数据和现有内存数据对比更新
- (BOOL)loadMemoryData:(NSArray *)history historyCnt:(NSInteger)historyCnt
{
    BOOL load = NO;
    
    self.history = history;
    
    //若disk数据是否和内存数据相连，则加载内存数据
    NSInteger plotLastIndex = [_plotDataBuf historyIndex] + [[[_plotDataBuf curves] objectAtIndex:0] count];
    NSInteger memFirstIndex = historyCnt - [history count];
    
    NSInteger startIndex = plotLastIndex - memFirstIndex;
    
    if (startIndex >= 0) {
        if (startIndex < [history count]) {
            [self plotDataUpdateWithMemory:history fromIndex:startIndex];
            load = YES;
        }
    }
    
    if ([_plotDataBuf historyCnt] != historyCnt) {
        load = YES;
        [_plotDataBuf setHistoryCnt:historyCnt];
        
        //对应没有数据的情况，设置起始为0
        if ([_plotDataBuf historyCnt] == 0) {
            [_plotDataBuf setHistoryIndex:0];
        }
    }
    
    
    
    return load;
}


#pragma mark - 线程保存文件相关操作

- (void)threadEnd:(NSDictionary *)dic
{
    if (_fileOpThread != nil) {
        [_fileOpThread cancel];
        [_fileOpThread release];
        _fileOpThread = nil;
    }
    
    [self updateDiskData:dic];
}

- (BOOL)fileGetThreadStart:(NSInteger)startIndex endIndex:(NSInteger)endIndex
{
    if (startIndex < 0) {
        return NO;
    }
    
    if (endIndex < startIndex) {
        return NO;
    }
    
    if (_fileOpThread == nil) {
        if (_lastGetDict != nil) {
            if (([[_lastGetDict objectForKey:@"key"] isEqualToString:[[_data dataInfo] key]])
             && ([[_lastGetDict objectForKey:@"startIndex"] integerValue] == startIndex)
             && ([[_lastGetDict objectForKey:@"endIndex"] integerValue] == endIndex))
            {
//                NSLog(@"fileGetThreadStart 重复 key = %@ startIndex = %u endIndex = %u", [[_data dataInfo] key], startIndex, endIndex);
                return NO;
            }
        }
        
        NSMutableDictionary* dic = [[[NSMutableDictionary alloc] init] autorelease];
        [dic setValue:[[_data dataInfo] key] forKey:@"key"];
        [dic setValue:[NSNumber numberWithInteger:startIndex] forKey:@"startIndex"];
        [dic setValue:[NSNumber numberWithInteger:endIndex] forKey:@"endIndex"];
        
        
        self.lastGetDict = dic;
        
        _fileOpThread = [[NSThread alloc] initWithTarget:self selector:@selector(getThreadProc:) object:dic];
        _fileOpThread.name = [NSString stringWithFormat:@"GTGet_%@", NSStringFromClass([self class])];
        [_fileOpThread start];
        return YES;
    }
    
    return NO;
}

- (void)getThreadProc:(NSDictionary *)dic
{
    @autoreleasepool {
        NSString *key = [dic objectForKey:@"key"];
//        NSUInteger fileMaxRecordCnt = [GT_OC_IN_GET(@"单个文件记录数", NO, 0) integerValue];
        NSUInteger fileMaxRecordCnt = M_GT_PARA_AUTOSAVE_FILE_RECORD_CNT_MAX;

        NSInteger startIndex = [[dic objectForKey:@"startIndex"] integerValue]/fileMaxRecordCnt;
        NSInteger endIndex = [[dic objectForKey:@"endIndex"] integerValue]/fileMaxRecordCnt;
        
        NSMutableArray *objArray = [NSMutableArray array];
        
        NSMutableDictionary* resultDic = [[[NSMutableDictionary alloc] init] autorelease];
        
        [resultDic setObject:[NSNumber numberWithInteger:(startIndex * fileMaxRecordCnt)] forKey:@"resultIndex"];
        NSString *dirName = [NSString stringWithFormat:@"%@/%@/", M_GT_SYS_PARA_DIR, key];
        
        //同时加载对应前后两个文件，共三个
        for (NSUInteger j = startIndex; j <= endIndex; j++) {
            //读取文件
            NSString *fileName = [NSString stringWithFormat:@"%@_%.3lu", key, (unsigned long)j];
            NSString *filePath = [[GTConfig sharedInstance] pathForDir:dirName fileName:fileName ofType:M_GT_FILE_TYPE_TXT];
            
//            NSUInteger count = [objArray count];
            if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
                NSArray *array = [[NSString stringWithContentsOfFile:filePath usedEncoding:nil error:nil] componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]];
                if ((array == nil) || ([array count] == 0)) {
                    continue;
                }
                
                for (int i = 0; i < [array count]; i++) {
//                    NSArray *rowArray = [[array objectAtIndex:i] componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:M_GT_HISTORY_ROW_HEADER]];
//                    if ((rowArray == nil) || ([rowArray count] == 0)) {
//                        continue;
//                    }
//                    
//                    for (int j = 0; j < [rowArray count]; j++) {
//                        [objArray addObject:[rowArray objectAtIndex:j]];
//                    }
                    
                    //去除M_GT_HISTORY_ROW_HEADER字符占用的长度
                    NSRange range = [[array objectAtIndex:i] rangeOfString:M_GT_HISTORY_ROW_HEADER];
                    if (range.location == NSNotFound) {
                        continue;
                    }
                    NSString *item = [[array objectAtIndex:i] substringFromIndex:(range.location + range.length)];
                    [objArray addObject:item];
                }
            }
            
            
        }
        
//        GT_OC_LOG_D(@"GTSys",@"[%@]共读取[%u-%u]%u条记录", key, startIndex * fileMaxRecordCnt, startIndex * fileMaxRecordCnt + [objArray count] - 1, [objArray count]);
//            NSLog(@"[%@]共读取[%u-%u]%u条记录", key, startIndex * fileMaxRecordCnt, startIndex * fileMaxRecordCnt + [objArray count] - 1, [objArray count]);
        
        [resultDic setObject:objArray forKey:@"result"];
        
        //获取完毕，通知主线程
        [self performSelectorOnMainThread:@selector(threadEnd:) withObject:resultDic waitUntilDone:NO];
        
        //    [NSThread exit];
    }

}

#pragma mark - GTPlotsViewDataSource

- (GTPlotsData *)chartDatas
{
    return _plotDataBuf;
}

- (void)loadHistroyDatas:(NSInteger)startIndex
{
    //预读数据，曲线前后预留5屏
    NSInteger preOffset = _plotView.capacity * 5;
    NSInteger preStartIndex = MAX(0, startIndex - preOffset);
    NSInteger preEndIndex = MIN(startIndex + preOffset, [_plotDataBuf historyCnt]);
    
    //左边界超出,预读
    if (preStartIndex < [_plotDataBuf historyIndex])
    {
        [self fileGetThreadStart:preStartIndex endIndex:preEndIndex];
        
//        NSLog(@"loadHistroyDatas startIndex:%u preStartIndex:%u [_plots historyIndex]:%u", startIndex, preStartIndex, [_plotDataBuf historyIndex]);
        return;
    }
    
    //右边界超出,预读
    if (preEndIndex > [_plotDataBuf historyIndex] + [[[_plotDataBuf curves] objectAtIndex:0] count]) {
        [self fileGetThreadStart:preStartIndex endIndex:preEndIndex];
        
//        NSLog(@"loadHistroyDatas _newStartIndex:%u preEndIndex:%u [_plots historyIndex]:%u [[[_plots curves] objectAtIndex:0] count]:%u", startIndex, preEndIndex, [_plotDataBuf historyIndex], [[[_plotDataBuf curves] objectAtIndex:0] count]);
        return;
    }
    
    
}

@end
