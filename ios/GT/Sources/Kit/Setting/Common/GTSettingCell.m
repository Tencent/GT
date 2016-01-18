//
//  GTSettingCell.m
//  GTKit
//
//  Created   on 13-4-15.
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

#import "GTSettingCell.h"
#import "GTSettingRow.h"
#import <QuartzCore/QuartzCore.h>
#import "GTDebugDef.h"

@implementation GTSettingCell

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 44.0f );
}

- (void)cellLayout
{
	_title.frame = CGRectMake( 6.0f, 8.0f, self.bounds.size.width - 12.0f, 18.0f );
	_intro.frame = CGRectMake( 6.0f, 28.0f, self.bounds.size.width - 12.0f, 11.0f );
    GTSettingRow *row = (GTSettingRow *)self.cellData;
    if ([[row info] length] == 0) {
        _title.frame = CGRectMake( 6.0f, 5.0f, self.bounds.size.width - 12.0f, 32.0f );
    }
}

- (void)load
{
	[super load];
	
    self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6f];
	self.layer.borderColor = [UIColor colorWithWhite:0.2f alpha:1.0f].CGColor;
	self.layer.borderWidth = 1.0f;
    
	_title = [[UILabel alloc] init];
	_title.font = [UIFont systemFontOfSize:15.0];
	_title.textColor = M_GT_CELL_TEXT_COLOR;
	_title.textAlignment = NSTextAlignmentLeft;
    _title.backgroundColor = [UIColor clearColor];
	[self addSubview:_title];
    
	_intro = [[UILabel alloc] init];
	_intro.font = [UIFont systemFontOfSize:11.0];
	_intro.textColor = M_GT_CELL_TEXT_COLOR;
	_intro.textAlignment = NSTextAlignmentLeft;
    _intro.backgroundColor = [UIColor clearColor];
	[self addSubview:_intro];
}

- (void)unload
{
	M_GT_SAFE_FREE( _title );
	M_GT_SAFE_FREE( _intro );
	
	[super unload];
}

- (void)bindData:(NSObject *)data
{
	[super bindData:data];
    
    GTSettingRow *row = (GTSettingRow *)data;
	[_title setText:[row title]];
	[_intro setText:[row info]];
}

- (void)clearData
{
	[_title setText:nil];
	[_intro setText:nil];
}

@end
#endif
