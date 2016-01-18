//
//  GTSingleDetailBoard.m
//  GTKit
//
//  Created   on 13-9-3.
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

#import "GTSingleDetailBoard.h"
#import <QuartzCore/QuartzCore.h>
#import "GTProfilerValue.h"
#import "GTConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"


@interface GTSingleDetailBoard ()

@end

@implementation GTSingleDetailBoard

- (void)initHistoryUI
{
    [super initHistoryUI];
    _warningView = [[GTParaWarningView alloc] init];
    [_warningView bindData:_data];
    [_warningView setDelegate:self];
    [_backgroundView addSubview:_warningView];
    
    
    _maxTime = [[UILabel alloc] init];
    _maxTime.font = [UIFont systemFontOfSize:12.0];
    _maxTime.textColor = M_GT_LABEL_COLOR;
    _maxTime.textAlignment = NSTextAlignmentRight;
    _maxTime.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_maxTime];
    
    _maxTimeValue = [[UILabel alloc] init];
    _maxTimeValue.font = [UIFont systemFontOfSize:12.0];
    _maxTimeValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _maxTimeValue.textAlignment = NSTextAlignmentLeft;
    _maxTimeValue.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_maxTimeValue];
    
    _avgTime = [[UILabel alloc] init];
    _avgTime.font = [UIFont systemFontOfSize:12.0];
    _avgTime.textColor = M_GT_LABEL_COLOR;
    _avgTime.textAlignment = NSTextAlignmentRight;
    _avgTime.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_avgTime];
    
    _avgTimeValue = [[UILabel alloc] init];
    _avgTimeValue.font = [UIFont systemFontOfSize:12.0];
    _avgTimeValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _avgTimeValue.textAlignment = NSTextAlignmentLeft;
    _avgTimeValue.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_avgTimeValue];
    
    _minTime = [[UILabel alloc] init];
    _minTime.font = [UIFont systemFontOfSize:12.0];
    _minTime.textColor = M_GT_LABEL_COLOR;
    _minTime.textAlignment = NSTextAlignmentRight;
    _minTime.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_minTime];
    
    _minTimeValue = [[UILabel alloc] init];
    _minTimeValue.font = [UIFont systemFontOfSize:12.0];
    _minTimeValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _minTimeValue.textAlignment = NSTextAlignmentLeft;
    _minTimeValue.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_minTimeValue];
}

- (void)load
{
    [super load];
}

- (void)unload
{
    M_GT_SAFE_FREE( _warningView );
    
    M_GT_SAFE_FREE( _content );
	
	M_GT_SAFE_FREE( _count );
	M_GT_SAFE_FREE( _countValue );
	M_GT_SAFE_FREE( _avgTime );
	M_GT_SAFE_FREE( _avgTimeValue );
    M_GT_SAFE_FREE( _maxTime );
    M_GT_SAFE_FREE( _maxTimeValue );
    M_GT_SAFE_FREE( _minTime );
    M_GT_SAFE_FREE( _minTimeValue );
    
    [super unload];
}


#pragma mark -

- (void)viewLayout
{
    [super viewLayout];
    CGFloat width = [self widthForOutDetail];
    CGRect frame;
    
    CGFloat headerHeight = [self heightForHeader];
    
    frame.origin.x = 0;
    frame.origin.y = 5;
    frame.size.height = headerHeight;
    frame.size.width = width;
    [_warningView setFrame:frame];
    
    frame.origin.x = 0;
    frame.origin.y += frame.size.height + 5;
    frame.size.height = 50;
    frame.size.width = width;
    
    //设置summary页的frame
    [_summaryView setFrame:frame];
    
    frame.origin.y += frame.size.height + 5;
    frame.size.height = _backgroundView.frame.size.height - frame.origin.y - 10;
    
    //设置曲线页的frame
    [_plotView setFrame:frame];
    
    width = width/4;
    CGFloat x = frame.origin.x;
    CGFloat y = 5;
    CGFloat height = 20;
    
    _count.frame = CGRectMake( x, y, width, height );
    _countValue.frame = CGRectMake( x + width, y, width, height );
    _avgTime.frame = CGRectMake( x + 2*width, y, width, height );
    _avgTimeValue.frame = CGRectMake( x + 3*width, y, width, height );
    
    y = 25;
    _maxTime.frame = CGRectMake( x, y, width, height );
    _maxTimeValue.frame = CGRectMake( x + width, y, width, height );
    _minTime.frame = CGRectMake( x + 2*width, y, width, height );
    _minTimeValue.frame = CGRectMake( x + 3*width, y, width, height );
}


- (void)updateData
{
    [super updateData];
    GTOutputObject *obj = _data;
    NSString *str = nil;
    
    [_warningView updateData];
    
    [_count setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_CORE_COUNT_KEY)]];
    str = [NSString stringWithFormat:@"%lu", (unsigned long)[obj historyCnt]];
    [_countValue setText:str];
    
    [_maxTime setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_CORE_MAX_KEY)]];
    str = [NSString stringWithFormat:@"%.3f", [obj maxTime]];
    [_maxTimeValue setText:str];
    
    [_avgTime setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_CORE_AVG_KEY)]];
    str = [NSString stringWithFormat:@"%.3f", [obj avgTime]];
    [_avgTimeValue setText:str];
    
    [_minTime setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_CORE_MIN_KEY)]];
    str = [NSString stringWithFormat:@"%.3f", [obj minTime]];
    [_minTimeValue setText:str];
}

- (CGFloat)heightForHeader
{
    if ([_data switchForWarning]) {
        return 115.0f;
    } else {
        return 40.0f;
    }
}


- (GTList *)upperWarningList
{
    return [_data upperWarningList];
}

- (GTList *)lowerWarningList
{
    return [_data lowerWarningList];
}

#pragma mark - GTParaWarningDelegate

- (void)didClickExtend
{
    [self viewLayout];
    
    //收缩后坐标字体显示会有变形，这里重新绘图
    [_plotView setNeedsDisplay];
}

//对于返回页面补充清除告警操作
- (void)onBackClicked:(id)sender
{
    //返回
    [self.navigationController popViewControllerAnimated:YES];
    
    [_data setShowWarning:NO];
}

@end
#endif
