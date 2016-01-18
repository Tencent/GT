//
//  GTParaBar.m
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

#import "GTParaBar.h"
#import "GTStyleDef.h"

@implementation GTParaBar

#define M_GT_PARA_WIDTH  62.0f
#define M_GT_PARA_HEIGHT 26.0f

- (id)initWithFrame:(CGRect)frame buttonTitles:(NSArray *)titleArray
{
    self = [super initWithFrame:frame];
    if (self)
	{
		self.backgroundColor = [UIColor clearColor];
		_backgroundView = [[UIImageView alloc] initWithFrame:self.bounds];
        [_backgroundView setBackgroundColor:M_GT_COLOR_WITH_HEX(0x26262D)];
        _backgroundView.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
        _backgroundView.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
		[self addSubview:_backgroundView];
        
		self.buttons = [NSMutableArray arrayWithCapacity:[titleArray count]];
		CGFloat xOffset = (self.frame.size.width - [titleArray count]*M_GT_PARA_WIDTH)/2;
        CGFloat yOffset = (self.frame.size.height - M_GT_PARA_HEIGHT)/2;
        
        for (int i = 0; i < [titleArray count]; i++)
		{
            UIButton *btn;
            btn = [UIButton buttonWithType:UIButtonTypeCustom];
            btn.frame = CGRectMake(M_GT_PARA_WIDTH * i + xOffset, yOffset, M_GT_PARA_WIDTH, M_GT_PARA_HEIGHT);
            btn.tag = i;
            btn.showsTouchWhenHighlighted = NO;
			[btn setTitle:[titleArray objectAtIndex:i] forState:UIControlStateNormal];
            [btn.titleLabel setFont:[UIFont systemFontOfSize:18]];
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
        //按钮正常态无颜色
        [b setBackgroundColor:[UIColor clearColor]];
        b.layer.borderColor = [UIColor clearColor].CGColor;
        b.layer.borderWidth = 0.0f;
        [b.titleLabel setTextColor:M_GT_LABEL_COLOR];
        
	}
    
	UIButton *btn = [self.buttons objectAtIndex:index];
	btn.selected = YES;
	btn.userInteractionEnabled = NO;
    //按钮选中颜色
    [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x3C3C42)];
    btn.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    btn.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [btn.titleLabel setTextColor:M_GT_COLOR_WITH_HEX(0xFFFFFF)];
}

- (void)dealloc
{
    [super dealloc];
}


@end


#endif
