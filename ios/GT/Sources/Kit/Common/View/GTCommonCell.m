//
//  GTCommonCell.m
//  GTKit
//
//  Created   on 13-1-28.
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
#import "GTCommonCell.h"
#import "GTDebugDef.h"

@implementation GTCommonCell

@synthesize cellData = _cellData;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier withFrame:(CGRect)frame
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.alpha = 1.0f;
        self.frame = frame;
        [self attachTapHandler];
        //        [self attachLongPressHandler];
        [self load];
        [self cellLayout];
    }
    return self;
}


- (void)dealloc
{
    [self unload];
    
    [super dealloc];
}

- (void)bindData:(NSObject *)data
{
	if ( nil == data ) {
		return;
    }

    self.cellData = data;
    [self cellLayout];
}

- (void)cellLayout
{
	
}

- (void)load
{
    UIView * v = [[[UIView alloc] init] autorelease];
    v.backgroundColor = M_GT_SELECTED_COLOR;
    self.selectedBackgroundView = v;
}

- (void)unload
{
    self.cellData = nil;
    self.selectedBackgroundView = nil;
}

- (void)clearData
{
    self.cellData = nil;
}

- (BOOL)canBecomeFirstResponder
{
    return YES;
}

- (void)attachTapHandler
{
    self.userInteractionEnabled = YES;
    UITapGestureRecognizer *touch = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
    touch.numberOfTapsRequired = 2;
    [self addGestureRecognizer:touch];
    [touch release];
}

- (void)attachLongPressHandler
{
    self.userInteractionEnabled = YES;
    UILongPressGestureRecognizer *press = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
    press.minimumPressDuration= 0.5;
    [self addGestureRecognizer:press];
    [press release];
}

- (void)handleTap:(UIGestureRecognizer*) recognizer
{

}
@end
#endif
