//
//  GTNetDetailBoard.m
//  GTKit
//
//  Created   on 13-7-5.
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
#import "GTNetDetailBoard.h"
#import <QuartzCore/QuartzCore.h>
#import "GTNetModel.h"
#import "GTUIAlertView.h"
#import "GTOutputList.h"
#import "GTConfig.h"

@interface GTNetDetailBoard ()

@end

@implementation GTNetDetailBoard

- (void)load
{
    [super load];
    
}

- (void)unload
{
    M_GT_SAFE_FREE( _btnReset);
    M_GT_SAFE_FREE( _detailDesc);
    M_GT_SAFE_FREE( _detailContent);
    [super unload];
}

- (void)initHistoryUI
{
    [super initHistoryUI];
    [_plotView setYDesc:@"KB"];
    
    _btnReset = [[UIButton alloc] initWithFrame:CGRectMake(M_GT_SCREEN_WIDTH - 80, 5, 60, 30)];
    [_btnReset addTarget:self action:@selector(onReset:) forControlEvents:UIControlEventTouchUpInside];
    [_btnReset addTarget:self action:@selector(onSetColorIn:) forControlEvents:UIControlEventTouchDown];
    [_btnReset addTarget:self action:@selector(onSetColorOut:) forControlEvents:UIControlEventTouchDragExit];
    [_btnReset setTitle:@"reset" forState:UIControlStateNormal];
    [_btnReset.titleLabel setFont:[UIFont systemFontOfSize:12]];
    [_btnReset setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    
    _btnReset.layer.borderColor = [UIColor blackColor].CGColor;
    _btnReset.layer.borderWidth = 1.0f;
    [_summaryView addSubview:_btnReset];
    
    _detailDesc = [[UILabel alloc] init];
    _detailDesc.font = [UIFont systemFontOfSize:10.0];
    _detailDesc.textColor = M_GT_LABEL_COLOR;
    _detailDesc.textAlignment = NSTextAlignmentLeft;
    _detailDesc.backgroundColor = [UIColor clearColor];
    _detailDesc.lineBreakMode = NSLineBreakByCharWrapping;
    _detailDesc.numberOfLines = 0;
    [_summaryView addSubview:_detailDesc];
    
    _detailContent = [[UILabel alloc] init];
    _detailContent.font = [UIFont systemFontOfSize:12.0];
    _detailContent.textColor = M_GT_LABEL_COLOR;
    _detailContent.textAlignment = NSTextAlignmentLeft;
    _detailContent.backgroundColor = [UIColor clearColor];
    _detailContent.lineBreakMode = NSLineBreakByCharWrapping;
    _detailContent.numberOfLines = 0;
    [_summaryView addSubview:_detailContent];
}

- (void)viewLayout
{
    [super viewLayout];
    
    CGFloat width = [self widthForOutDetail];
    CGRect frame;
    
    frame.origin.x = 0;
    frame.origin.y = 5;
    frame.size.height = 100;
    frame.size.width = width;
    
    //设置summary页的frame
    [_summaryView setFrame:frame];
    
    frame.origin.y += frame.size.height + 5;
    frame.size.height = _backgroundView.frame.size.height - frame.origin.y - 10;
    
    //设置曲线页的frame
    [_plotView setFrame:frame];
    
    width = 45;
    CGFloat x = [self widthForOutDetail] - 10 - 50;
    CGFloat y = 10;
    CGFloat height = 40;
    
    _btnReset.frame = CGRectMake( x, y, width, height );
    
    x = 5;
    y += 5;
    height = 40;
    width = [self widthForOutDetail] - 10 - 50;
    _content.frame = CGRectMake( x, y, width, height );
    
    y = 55;
    height = 10;
    width = [self widthForOutDetail] - 10;
    _detailDesc.frame = CGRectMake( x, y, width, height );
    
    y = 60;
    height = 40;
    width = [self widthForOutDetail] - 10;
    _detailContent.frame = CGRectMake( x, y, width, height );
}

#pragma mark - 扩展HeaderView
- (CGFloat)heightForHeader
{
    return 55.0f;
}

- (UIView *)viewForHeader
{
    CGFloat width = [self widthForOutDetail];
    CGFloat height = [self heightForHeader];
    UIView *container = [[[UIView alloc] initWithFrame:CGRectMake(0,0,width,height)] autorelease];
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(M_GT_SCREEN_WIDTH - 80, 5, 60, 30)];
    [btn addTarget:self action:@selector(onReset:) forControlEvents:UIControlEventTouchUpInside];
    [btn addTarget:self action:@selector(onSetColorIn:) forControlEvents:UIControlEventTouchDown];
    [btn addTarget:self action:@selector(onSetColorOut:) forControlEvents:UIControlEventTouchDragExit];
    [btn setTitle:@"reset" forState:UIControlStateNormal];
    [btn.titleLabel setFont:[UIFont systemFontOfSize:12]];
    [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    
    btn.layer.borderColor = [UIColor blackColor].CGColor;
    btn.layer.borderWidth = 1.0f;
    [container addSubview:btn];
    [btn release];
    
    return container;
}


- (void)onSetColorIn:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    [btn setBackgroundColor:[UIColor grayColor]];
}

- (void)onSetColorOut:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
}

- (void)onReset:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    
    [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    //清除累计流量
    [[GTNetModel sharedInstance] resetData];
    [self updateData];
}

- (void)updateData
{
    [super updateData];
    
    [_detailDesc setText:@"since the mobile phone boot:"];
    
    NSString *str = nil;
    GTNetModel *net = [GTNetModel sharedInstance];
    
    str = [NSString stringWithFormat:@"Wifi T:%.3fKB R:%.3fKB\r\nWWAN T:%.3fKB R:%.3fKB", ([net WiFiSent] + [net prevWiFiSent])/M_GT_K_B, ([net WiFiReceived] + [net prevWiFiReceived])/M_GT_K_B, ([net WWANSent] + [net prevWWANSent])/M_GT_K_B, ([net WWANReceived] + [net prevWWANReceived])/M_GT_K_B];
    [_detailContent setText:str];
}

#pragma mark - GTMultiPlotsViewDataSource
- (NSArray*)multiChartDatas
{
    NSArray * result = nil;
    NSMutableArray *histroy = [[[_data history] copy] autorelease];
    
    NSUInteger count = [histroy count];
    if (count == 0) {
        return nil;
    }
    
    NSMutableArray * totals = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTNetData *obj = nil;

    for (int i = 0; i < count; i++) {
        obj = [histroy objectAtIndex:i];
        [totals addObject:[NSNumber numberWithFloat:MAX(0, ([obj wifiSent] + [obj WWANSent] + [obj wifiRev] + [obj WWANRev])/M_GT_K_B)]];
    }
    
    result = [[NSArray alloc] initWithObjects:totals, nil];
    [totals release];
    
    return [result autorelease];
}

- (NSArray*)multiChartDates
{
    NSMutableArray *histroy = [[[_data history] copy] autorelease];
    
    NSUInteger count = [histroy count];
    if (count == 0) {
        return nil;
    }
    
    NSMutableArray * dates = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTNetData *obj = nil;
    
    for (int i = 0; i < count; i++) {
        obj = [histroy objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
    }
    
    
    return [dates autorelease];
}

#pragma mark - 数据缓冲自定义曲线展示扩展
- (void)plotDataInitWithMemory:(NSArray *)array
{
    NSUInteger count = [array count];
    
    NSMutableArray *dates = [[NSMutableArray alloc] initWithCapacity:count];
    NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTNetData *obj = nil;
    
    for (int i = 0; i < count; i++) {
        obj = [array objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
        [values addObject:[NSNumber numberWithFloat:MAX(0, ([obj wifiSent] + [obj WWANSent] + [obj wifiRev] + [obj WWANRev])/M_GT_K_B)]];
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
        if ([itemArray count] != 5) {
            continue;
        }
        
        //日期格式转换为秒数
        [dates addObject:[NSNumber numberWithDouble:[(NSString *)[itemArray objectAtIndex:0] timeValue]]];
        //保存已经除了M_GT_K_B，这里直接获取
        double value = ([[itemArray objectAtIndex:1] doubleValue] + [[itemArray objectAtIndex:2] doubleValue] + [[itemArray objectAtIndex:3] doubleValue] + [[itemArray objectAtIndex:4] doubleValue]);
        [values addObject:[NSNumber numberWithDouble:value]];
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
    
    GTNetData *obj = nil;
    
    for (NSUInteger i = index; i < [array count]; i++) {
        obj = [array objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
        [values addObject:[NSNumber numberWithDouble:MAX(0, ([obj wifiSent] + [obj WWANSent] + [obj wifiRev] + [obj WWANRev])/M_GT_K_B)]];
    }
    
}

#pragma mark - GTPlotsViewDataSource

- (NSArray *)popValueStrs1:(NSInteger)index
{
    NSMutableArray *histroy = [_data history];
    NSUInteger count = [histroy count];
    
    if (index >= count) {
        return nil;
    }
    
    GTNetData *obj = [histroy objectAtIndex:index];
    
    NSMutableArray *array = [[[NSMutableArray alloc] initWithCapacity:3] autorelease];
    NSString *str = nil;
    str = [NSString stringWithFormat:@"%@,Total(T,R): %0.1fK, %0.1fK",
           [NSString stringWithTimeEx:[obj date]],
           MAX(0, ([obj wifiSent] + [obj WWANSent]))/M_GT_K_B,
           ([obj wifiRev] + [obj WWANRev])/M_GT_K_B];
    [array addObject:str];
    
    if ((index >= 1) && ((index - 1) < [histroy count])) {
        GTNetData *obj1 = [histroy objectAtIndex:(index - 1)];
        
        str = [NSString stringWithFormat:@"T(wifi,wwan): %0.1fK/S, %0.1fK/S",
               MAX(0, ([obj wifiSent] - [obj1 wifiSent]))/M_GT_K_B,
               ([obj WWANSent] - [obj1 WWANSent])/M_GT_K_B];
        [array addObject:str];
        
        str = [NSString stringWithFormat:@"R(wifi,wwan): %0.1fK/S, %0.1fK/S",
               MAX(0, ([obj wifiRev] - [obj1 wifiRev]))/M_GT_K_B,
               MAX(0, ([obj WWANRev] - [obj1 WWANRev]))/M_GT_K_B];
        [array addObject:str];
    } else {
        str = [NSString stringWithFormat:@"T(wifi,wwan): 0.0K/S, 0.0K/S"];
        [array addObject:str];
        
        str = [NSString stringWithFormat:@"R(wifi,wwan): 0.0K/S, 0.0K/S"];
        [array addObject:str];
    }
    
    return array;
}


#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    [super alertView:alertView clickedButtonAtIndex:buttonIndex];
    //clear
    if ([alertView tag] == M_GT_ALERT_TAG_CLEAR) {
        if (buttonIndex == 1) {
            //清除累计流量
            [[GTNetModel sharedInstance] resetData];
            [self updateData];
        }
    }
}

@end
#endif
