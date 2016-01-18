//
//  GTUISwitch.m
//  GTKit
//
//  Created   on 13-4-5.
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
#import "GTUISwitch.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>

@interface GTUISwitch()
{
    UIColor *_tintColor;
	UIView  *_clippingView;
	UILabel *_rightLabel;
	UILabel *_leftLabel;
	
	// private member
	BOOL m_touchedSelf;
}


@property (nonatomic,retain) UIColor *tintColor;
@property (nonatomic,retain) UIView  *clippingView;
@property (nonatomic,retain) UILabel *rightLabel;
@property (nonatomic,retain) UILabel *leftLabel;
@end

@implementation GTUISwitch

@synthesize on;
@synthesize clippingView = _clippingView;
@synthesize tintColor = _tintColor;
@synthesize leftLabel = _leftLabel;
@synthesize rightLabel = _rightLabel;

+(GTUISwitch *)switchWithLeftText:(NSString *)leftText andRight:(NSString *)rightText
{
	GTUISwitch *switchView = [[GTUISwitch alloc] initWithFrame:CGRectZero];
	
	switchView.leftLabel.text = leftText;
	switchView.rightLabel.text = rightText;
	
	return [switchView autorelease];
}

-(id)initWithFrame:(CGRect)rect
{
    self = [super initWithFrame:rect];
    if (self)
	{
		[self initUI];
        
        //ios5上部分模块需要再设置一次才生效
		[self setFrame:rect];
	}
	return self;
}


-(void)dealloc
{
	[_tintColor release];
	[_clippingView release];
	[_rightLabel release];
	[_leftLabel release];
	
	[super dealloc];
}

-(void)initUI
{
	self.backgroundColor = [UIColor clearColor];
    self.layer.borderColor = [UIColor blackColor].CGColor;
    self.layer.borderWidth = 1;
    
    CGSize imageSize = CGSizeMake(2*self.bounds.size.width/5, self.bounds.size.height);
    UIGraphicsBeginImageContextWithOptions(imageSize, 0, [UIScreen mainScreen].scale);
    [[UIColor blackColor] set];
    UIRectFill(CGRectMake(0, 0, imageSize.width, imageSize.height));
    imageSize = CGSizeMake(self.bounds.size.width/2 - 2, self.bounds.size.height - 2);
    [M_GT_BTN_BKGD_COLOR set];
    UIRectFill(CGRectMake(1, 1, imageSize.width, imageSize.height));
    UIImage *thumbImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    imageSize = CGSizeMake(self.bounds.size.width, self.bounds.size.height);
    UIGraphicsBeginImageContextWithOptions(imageSize, 0, [UIScreen mainScreen].scale);
    [[UIColor clearColor] set];
    UIRectFill(CGRectMake(0, 0, imageSize.width, imageSize.height));
    UIImage *trackImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
	[self setThumbImage:thumbImage forState:UIControlStateNormal];
	[self setMinimumTrackImage:trackImage forState:UIControlStateNormal];
	[self setMaximumTrackImage:trackImage forState:UIControlStateNormal];
	
	self.minimumValue = 0;
	self.maximumValue = 1;
	self.continuous = NO;
	
	self.on = NO;
	self.value = 0.0;
    
    CGRect frame = self.bounds;
	frame.origin.y = 0.0f;
    frame.size.height = frame.size.height - 2*0.0f;
    
    _clippingView = [[UIView alloc] initWithFrame:frame];
	_clippingView.clipsToBounds = YES;
	_clippingView.userInteractionEnabled = NO;
	_clippingView.backgroundColor = [UIColor clearColor];
	[self addSubview:_clippingView];
	
	NSString *leftLabelText = @"ON";
		
    frame.size.width = frame.size.width/2;
	_leftLabel = [[UILabel alloc] init];
    _leftLabel.frame = frame;
	_leftLabel.text = leftLabelText;
	_leftLabel.textAlignment = NSTextAlignmentCenter;
	_leftLabel.font = [UIFont systemFontOfSize:15];
	_leftLabel.textColor = [UIColor whiteColor];
	_leftLabel.backgroundColor = M_GT_SELECTED_COLOR;
    _leftLabel.layer.borderColor = [UIColor blackColor].CGColor;
    _leftLabel.layer.borderWidth = 1;
	[_clippingView addSubview:_leftLabel];
	
	
	NSString *rightLabelText = @"OFF";
    
    frame.origin.x += frame.size.width;
	_rightLabel = [[UILabel alloc] init];
    _rightLabel.frame = frame;
	_rightLabel.text = rightLabelText;
	_rightLabel.textAlignment = NSTextAlignmentCenter;
	_rightLabel.font = [UIFont systemFontOfSize:15];
	_rightLabel.textColor = [UIColor grayColor];
	_rightLabel.backgroundColor = M_GT_COLOR_WITH_HEX(0x29292D);
    _rightLabel.layer.borderColor = [UIColor blackColor].CGColor;
    _rightLabel.layer.borderWidth = 1;
	[_clippingView addSubview:_rightLabel];
}

-(void)layoutSubviews
{
	//[super layoutSubviews];
	
	// move the labels to the front
	[_clippingView removeFromSuperview];
	[self addSubview:_clippingView];
	
	CGFloat thumbWidth = self.currentThumbImage.size.width;
	CGFloat switchWidth = self.bounds.size.width;
	CGFloat labelWidth = switchWidth - thumbWidth;
	CGFloat inset = _clippingView.frame.origin.x;
	
	NSInteger xPos = self.value * labelWidth - labelWidth - inset;
    NSInteger yPos = 0.0f;
    NSUInteger height = self.bounds.size.height;
    _leftLabel.frame = CGRectMake(xPos, yPos, labelWidth, height);
    xPos = switchWidth + (self.value * labelWidth - labelWidth) - inset;
    _rightLabel.frame = CGRectMake(xPos, yPos, labelWidth, height);
}

- (void)setOn:(BOOL)turnOn animated:(BOOL)animated;
{
	on = turnOn;
	
	if (animated)
	{
		[UIView	 beginAnimations:nil context:nil];
		[UIView setAnimationDuration:0.2];
	}
	
	if (on)
	{
		self.value = 1.0;
	}
	else
	{
		self.value = 0.0;
	}
	
	if (animated)
	{
		[UIView	commitAnimations];
	}
}

- (void)setOn:(BOOL)turnOn
{
	[self setOn:turnOn animated:NO];
}


- (void)endTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
	[super endTrackingWithTouch:touch withEvent:event];
	m_touchedSelf = YES;
	
	[self setOn:on animated:YES];
}

- (void)touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
	[super touchesBegan:touches withEvent:event];
	m_touchedSelf = NO;
	on = !on;
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    [super touchesMoved:touches withEvent:event];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [super touchesCancelled:touches withEvent:event];
    on = !on;
    if (!m_touchedSelf)
	{
		[self setOn:on animated:YES];
		[self sendActionsForControlEvents:UIControlEventValueChanged];
	}
}

- (void)touchesEnded:(NSSet*)touches withEvent:(UIEvent*)event
{
	[super touchesEnded:touches withEvent:event];
	
	if (!m_touchedSelf)
	{
		[self setOn:on animated:YES];
		[self sendActionsForControlEvents:UIControlEventValueChanged];
	}
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
#endif
