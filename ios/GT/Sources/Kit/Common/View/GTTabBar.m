//
//  GTTabBar.m
//  GTKit
//
//  Created   on 13-3-18.
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
#import "GTTabBar.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>

@implementation GTTabBar

@synthesize backgroundView  = _backgroundView;
@synthesize buttons         = _buttons;
@synthesize delegate        = _delegate;

#define M_GT_TAR_INTERVAL 2.0f

- (id)initWithFrame:(CGRect)frame buttonTitles:(NSArray *)titleArray
{
    self = [super initWithFrame:frame];
    if (self)
	{
		self.backgroundColor = [UIColor clearColor];
		_backgroundView = [[UIImageView alloc] initWithFrame:self.bounds];
        //底色 #3c3c42
        [_backgroundView setBackgroundColor:[UIColor colorWithRed:0.235 green:0.235 blue:0.259 alpha:1.000]];
		[self addSubview:_backgroundView];
		
		self.buttons = [NSMutableArray arrayWithCapacity:[titleArray count]];
		UIButton *btn;
		CGFloat width = self.frame.size.width / [titleArray count];
		for (int i = 0; i < [titleArray count]; i++)
		{
			btn = [UIButton buttonWithType:UIButtonTypeCustom];
			btn.showsTouchWhenHighlighted = NO;
			btn.tag = i;
			btn.frame = CGRectMake(width * i + M_GT_TAR_INTERVAL, 0 + M_GT_TAR_INTERVAL, width - 2*M_GT_TAR_INTERVAL, frame.size.height - 2*M_GT_TAR_INTERVAL);
            
			[btn setTitle:[titleArray objectAtIndex:i]  forState:UIControlStateNormal];
            [btn.titleLabel setFont:[UIFont systemFontOfSize:16.0f]];
            [btn.titleLabel setTextAlignment:NSTextAlignmentCenter];
			[btn addTarget:self action:@selector(tabBarButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
			[self.buttons addObject:btn];
			[self addSubview:btn];
		}
    }
    return self;
}

- (void)tabBarButtonClicked:(id)sender
{
	UIButton *btn = sender;
    
    [self selectTabAtIndex:btn.tag];
    if ([_delegate respondsToSelector:@selector(tabBar:didSelectIndex:)])
    {
        [_delegate tabBar:self didSelectIndex:btn.tag];
    }
}

- (void)selectTabAtIndex:(NSInteger)index
{
	for (int i = 0; i < [self.buttons count]; i++)
	{
		UIButton *b = [self.buttons objectAtIndex:i];
		b.selected = NO;
		b.userInteractionEnabled = YES;
        //按钮正常态颜色#35353b  边框#1c1c21
        [b setBackgroundColor:[UIColor colorWithRed:0.208 green:0.208 blue:0.231 alpha:1.000]];
        b.layer.borderColor = [UIColor colorWithRed:0.110 green:0.110 blue:0.129 alpha:1.000].CGColor;
        b.layer.borderWidth = 1.0f;
	}
    
	UIButton *btn = [self.buttons objectAtIndex:index];
	btn.selected = YES;
	btn.userInteractionEnabled = NO;
    //按钮选中颜色#3c4a76  边框2px #22293f
    [btn setBackgroundColor:M_GT_SELECTED_COLOR];
    btn.layer.borderColor = [UIColor colorWithRed:0.133 green:0.161 blue:0.247 alpha:1.000].CGColor;
    btn.layer.borderWidth = 1.0f;
}


- (void)setBackgroundImage:(UIImage *)img
{
    [_backgroundView setImage:img];
}


- (void)dealloc
{
    self.backgroundView = nil;
    self.buttons = nil;
    [super dealloc];
}

 
@end


#endif
