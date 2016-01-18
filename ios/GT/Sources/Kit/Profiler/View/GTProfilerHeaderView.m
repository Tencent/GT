//
//  GTProfilerHeaderView.m
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
#import "GTProfilerHeaderView.h"
#import "GTProfilerSummaryView.h"
#import "GTLang.h"
#import "GTLangDef.h"



//#define M_GT_TIME_AVERAGE   @"Average"

@implementation GTProfilerHeaderView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setBackgroundColor:[UIColor clearColor]];
        
        UIView *backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height - 4)];
        [backgroundView setBackgroundColor:M_GT_COLOR_WITH_HEX(0x29292D)];
        [self addSubview:backgroundView];
        [backgroundView release];
        
        [self load];
        [self viewLayout:frame];
        [self showData];
    }
    
    return self;
}

- (void)dealloc
{
    [super dealloc];
}



- (void)load
{
    UIColor *color = M_GT_COLOR_WITH_HEX(0xCB7418);
    _count = [[UILabel alloc] init];
    _count.font = [UIFont systemFontOfSize:12.0];
    _count.textColor = color;
    _count.textAlignment = NSTextAlignmentRight;
    _count.backgroundColor = [UIColor clearColor];
    [self addSubview:_count];
    [_count release];
    
    _totalTime = [[UILabel alloc] init];
    _totalTime.font = [UIFont systemFontOfSize:12.0];
    _totalTime.textColor = color;
    _totalTime.textAlignment = NSTextAlignmentRight;
    _totalTime.backgroundColor = [UIColor clearColor];
    [self addSubview:_totalTime];
    [_totalTime release];
    
    _maxTime = [[UILabel alloc] init];
    _maxTime.font = [UIFont systemFontOfSize:12.0];
    _maxTime.textColor = color;
    _maxTime.textAlignment = NSTextAlignmentRight;
    _maxTime.backgroundColor = [UIColor clearColor];
    [self addSubview:_maxTime];
    [_maxTime release];
    
    _avgTime = [[UILabel alloc] init];
    _avgTime.font = [UIFont systemFontOfSize:12.0];
    _avgTime.textColor = color;
    _avgTime.textAlignment = NSTextAlignmentRight;
    _avgTime.backgroundColor = [UIColor clearColor];
    [self addSubview:_avgTime];
    [_avgTime release];
    
    
}

- (void)viewLayout:(CGRect)frame
{
    CGFloat x = frame.origin.x;
    CGFloat y = 0;
    CGFloat width = frame.size.width - 40 - 20 - 10;
    CGFloat height = frame.size.height;
    
    _count.frame = CGRectMake( 10 + x, y, 40, height );
    _totalTime.frame = CGRectMake( 50 + x + 5, y, width/3 - 5, height );
    _maxTime.frame = CGRectMake( 50 + x + width/3 + 5, y, width/3 - 5, height );
    _avgTime.frame = CGRectMake( 50 + x + 2*width/3 + 5, y, width/3 - 5, height );
}

- (void)showData
{
    [_count setText:M_GT_LOCALSTRING(M_GT_TIME_COUNT_KEY)];
    [_totalTime setText:M_GT_LOCALSTRING(M_GT_TIME_TOTAL_KEY)];
    [_maxTime setText:M_GT_LOCALSTRING(M_GT_TIME_MAX_KEY)];
    [_avgTime setText:M_GT_LOCALSTRING(M_GT_TIME_AVG_KEY)];
}


@end

#endif
