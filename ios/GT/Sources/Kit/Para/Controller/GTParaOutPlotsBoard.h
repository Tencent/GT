//
//  GTParaOutPlotsBoard.h
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

#import "GTParaOutDetailBoard.h"
#import "GTPlotsView.h"

@interface GTParaOutPlotsBoard : GTParaOutCommonBoard <GTPlotsViewDataSource, GTOutShowDelegate>
{
    UIView          *_backgroundView;
    UIView          *_summaryView;
    UILabel         *_count;
    UILabel         *_countValue;
    
    GTPlotsView     *_plotView;
    NSArray         *_history;
    GTPlotsData     *_plotDataBuf;      //数据展示缓冲区，避免频繁的获取数据
    NSThread        *_fileOpThread;     //线程操作：加载数据
    NSMutableDictionary *_lastGetDict;  //防止循环读取同样的内容
}

@property (nonatomic, copy) NSArray *history;
@property (nonatomic, retain) NSMutableDictionary *lastGetDict;

- (void)initHistoryUI;
- (void)viewLayout;

#pragma mark - 数据缓冲自定义曲线展示扩展
- (void)plotDataInitWithMemory:(NSArray *)array;
- (void)plotDataInitWithDisk:(NSArray *)array;
- (void)plotDataUpdateWithMemory:(NSArray *)array fromIndex:(NSInteger)index;

- (void)resetPlotDataBuf;
@end
