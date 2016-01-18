//
//  GTProfilerSummaryView.m
//  GTKit
//
//  Created   on 13-11-17.
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
#import "GTProfilerSummaryView.h"
#import "GTLang.h"
#import "GTLangDef.h"



@implementation GTProfilerSummaryView


- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setBackgroundColor:[UIColor clearColor]];
        
        [self load];
        [self viewLayout:frame];
    }
    
    return self;
}

- (void)load
{
    _summaryView = [[UIView alloc] init];
    _summaryView.backgroundColor = M_GT_CELL_BKGD_COLOR;
    _summaryView.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _summaryView.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [self addSubview:_summaryView];
    
    _contentValue = [[UILabel alloc] init];
    _contentValue.font = [UIFont systemFontOfSize:15.0];
    _contentValue.textColor = M_GT_LABEL_COLOR;
    _contentValue.textAlignment = NSTextAlignmentLeft;
    _contentValue.backgroundColor = [UIColor clearColor];
    [_summaryView addSubview:_contentValue];
    
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
    
    _panValue = [[UILabel alloc] init];
    _panValue.font = [UIFont systemFontOfSize:12.0];
    _panValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _panValue.textAlignment = NSTextAlignmentCenter;
    _panValue.backgroundColor = [UIColor clearColor];
    [self addSubview:_panValue];
    
#undef	GT_MAX_TIME_HISTORY
#define GT_MAX_TIME_HISTORY	(50)
    
    _plotView = [[GTPlotsView alloc] init];
    _plotView.capacity = GT_MAX_TIME_HISTORY;
    [_plotView setYDesc:@"s"];
    [self addSubview:_plotView];
    _plotView.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _plotView.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

- (void)viewLayout:(CGRect)frame
{
    CGFloat x = 10;
    CGFloat y = 15;
    CGFloat width = frame.size.width - 20;
    
    _summaryView.frame = CGRectMake( x, y, width, 90 );
    
    width = width - 20;
    _contentValue.frame = CGRectMake( x, 10, width, 20 );
    
    width = width/4;
    CGFloat height = 20;
    
    _count.frame = CGRectMake( x, 40, width, height );
    _countValue.frame = CGRectMake( x + width, 40, width, height );
    _avgTime.frame = CGRectMake( x + 2*width, 40, width, height );
    _avgTimeValue.frame = CGRectMake( x + 3*width, 40, width, height );
    
    _maxTime.frame = CGRectMake( x, 65, width, height );
    _maxTimeValue.frame = CGRectMake( x + width, 65, width, height );
    _minTime.frame = CGRectMake( x + 2*width, 65, width, height );
    _minTimeValue.frame = CGRectMake( x + 3*width, 65, width, height );
    
    width = frame.size.width - 20;
    
    _plotView.frame = CGRectMake( x, 115, width, M_GT_BOARD_HEIGHT - 115 - 5 );
}

- (void)bindData:(GTProfilerDetail *)data
{
    GTProfilerDetail *obj = (GTProfilerDetail *)data;
    NSString *str = nil;
    
    [_contentValue setText:[obj key]];
    
    [_count setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_TIME_COUNT_KEY)]];
    str = [NSString stringWithFormat:@"%lu", (unsigned long)[obj count]];
    [_countValue setText:str];
    
    [_maxTime setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_TIME_MAX_KEY)]];
    str = [NSString stringWithFormat:@"%.3f", [obj maxTime]];
    [_maxTimeValue setText:str];
    
    [_avgTime setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_TIME_AVG_KEY)]];
    str = [NSString stringWithFormat:@"%.3f", [obj avgTime]];
    [_avgTimeValue setText:str];
    
    [_minTime setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_TIME_MIN_KEY)]];
    str = [NSString stringWithFormat:@"%.3f", [obj minTime]];
    [_minTimeValue setText:str];
}


- (void)dealloc
{
    M_GT_SAFE_FREE( _summaryView );
    M_GT_SAFE_FREE( _content );
	M_GT_SAFE_FREE( _contentValue );
	
	M_GT_SAFE_FREE( _count );
	M_GT_SAFE_FREE( _countValue );
	M_GT_SAFE_FREE( _avgTime );
	M_GT_SAFE_FREE( _avgTimeValue );
    M_GT_SAFE_FREE( _maxTime );
    M_GT_SAFE_FREE( _maxTimeValue );
    M_GT_SAFE_FREE( _minTime );
    M_GT_SAFE_FREE( _minTimeValue );
    M_GT_SAFE_FREE( _panValue );
    M_GT_SAFE_FREE( _plotView );
    
    [super dealloc];
}

- (void)setPlotsDataSource:(id<GTPlotsViewDataSource>)delegate
{
    [_plotView setDataSource:delegate];
}

- (void)update
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.6f];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationRepeatAutoreverses:NO];
    [UIView setAnimationRepeatCount:1];
    
    [UIView commitAnimations];
    
	[_plotView reloadData];
}


@end

#endif
