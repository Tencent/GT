//
//  GTAccessoryView.m
//  GTKit
//
//  Created   on 13-4-10.
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
#import "GTAccessoryView.h"

@implementation GTAccessoryView

@synthesize arrowWidth = _arrowWidth;
@synthesize foregroundColor = _foregroundColor;

@synthesize labelEdgeInsets = _labelEdgeInsets;

- (id)initWithFrame:(CGRect)frame
{
    if ((self = [super initWithFrame:frame])) {
        [self setOpaque:NO];
        
        _arrowWidth = 10;
        _foregroundColor = [[UIColor blackColor] retain];
        _labelEdgeInsets = UIEdgeInsetsMake(4, 6, 4, 6);
        
        _textLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        [_textLabel setBackgroundColor:[UIColor clearColor]];
        [_textLabel setTextColor:[UIColor whiteColor]];
//        [_textLabel setMinimumFontSize:8.0f];
        [_textLabel setMinimumScaleFactor:8.0f];
        [self addSubview:_textLabel];
    }
    return self;
}

- (void)dealloc
{
    self.textLabel = nil;
    self.foregroundColor = nil;
    
    [super dealloc];
}

- (UILabel *)textLabel
{
    return _textLabel;
}

- (void)setTextLabel:(UILabel *)textLabel
{
    [_textLabel release];
    _textLabel = [textLabel retain];
    
    [self setNeedsLayout];
}

- (void)layoutSubviews
{
    CGRect bounds = [self bounds];
    
    [_textLabel setFrame:UIEdgeInsetsInsetRect(bounds, _labelEdgeInsets)];
}

- (void)drawRect:(CGRect)rect
{
    CGRect bounds = [self bounds];
    CGContextRef context = UIGraphicsGetCurrentContext();
    if (context) {
        // +-----------+
        // |            \
        // |            /
        // +-----------+
        
        CGMutablePathRef path = CGPathCreateMutable();
        CGPathMoveToPoint(path, NULL, 0, 0);
        CGPathAddLineToPoint(path, NULL, bounds.size.width - _arrowWidth, 0);
        CGPathAddLineToPoint(path, NULL, bounds.size.width, bounds.size.height / 2);
        CGPathAddLineToPoint(path, NULL, bounds.size.width - _arrowWidth, bounds.size.height);
        CGPathAddLineToPoint(path, NULL, 0, bounds.size.height);
        CGPathCloseSubpath(path);
        
        CGContextAddPath(context, path);
        CGContextSetFillColorWithColor(context, [[self foregroundColor] CGColor]);
        CGContextFillPath(context);
        
        CGPathRelease(path);
    }
    
}

@end
#endif
