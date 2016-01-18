//
//  GTParaOutputCell.m
//  GTKit
//
//  Created   on 13-11-20.
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
#import "GTParaOutputCell.h"
#import "GTOutputObject.h"
#import "GTConfig.h"

@implementation GTParaOutputCell

@synthesize indexPath = _indexPath;
@synthesize delegate = _delegate;

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 30.0f );
}

- (void)cellLayout
{
    _title.frame = CGRectMake( 10.0f, 5.0f, self.bounds.size.width-100, 20.0f );
    _value.frame = CGRectMake( 10, 25.0f, self.bounds.size.width-100, 15.0f );
    _historyCount.frame = CGRectMake( self.bounds.size.width-65, 15.0f, 25, 15.0f );
    _btnTop.frame = CGRectMake( self.bounds.size.width-80, 5.0f, 40, 35.0f );
    _btnChecked.frame = CGRectMake( self.bounds.size.width-40, 2.0f, 40, 40.0f );
}

- (void)load
{
	[super load];
    
	_title = [[UILabel alloc] init];
	_title.font = [UIFont systemFontOfSize:15.0];
	_title.textColor = M_GT_CELL_TEXT_COLOR;
	_title.textAlignment = NSTextAlignmentLeft;
    _title.backgroundColor = [UIColor clearColor];
    [self addSubview:_title];
    
    _value = [[UILabel alloc] init];
	_value.font = [UIFont systemFontOfSize:12.0];
	_value.textColor = M_GT_CELL_TEXT_COLOR;
	_value.textAlignment = NSTextAlignmentLeft;
    _value.backgroundColor = [UIColor clearColor];
	[self addSubview:_value];
    
    _historyCount = [[UILabel alloc] init];
	_historyCount.font = [UIFont systemFontOfSize:12.0];
	_historyCount.textColor = M_GT_CELL_TEXT_DISABLE_COLOR;
	_historyCount.textAlignment = NSTextAlignmentCenter;
    _historyCount.backgroundColor = [UIColor clearColor];
    _historyCount.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _historyCount.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    _historyCount.layer.cornerRadius = 8;
	[self addSubview:_historyCount];
    
    _btnTop = [[UIButton alloc] init];
    [_btnTop setImageEdgeInsets:UIEdgeInsetsMake(8, 8, 8, 8)];
    [_btnTop setBackgroundColor:[UIColor clearColor]];
    [_btnTop addTarget:self action:@selector(didClickTop:) forControlEvents:UIControlEventTouchUpInside];
    [_btnTop setImage:[GTImage imageNamed:@"gt_para_top" ofType:@"png"] forState:UIControlStateNormal];
    [_btnTop setImage:[GTImage imageNamed:@"gt_para_top_sel" ofType:@"png"] forState:UIControlStateSelected];
    [self addSubview:_btnTop];
    
    _btnChecked = [[UIButton alloc] init];
    [_btnChecked setImageEdgeInsets:UIEdgeInsetsMake(8, 8, 8, 8)];
    [_btnChecked setBackgroundColor:[UIColor clearColor]];
    [_btnChecked addTarget:self action:@selector(didClickChecked:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_btnChecked];
}

- (void)unload
{
	self.indexPath = nil;
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_PARA object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_ALL_SEL object:nil];
    
    M_GT_SAFE_FREE( _title );
	M_GT_SAFE_FREE( _value );
	M_GT_SAFE_FREE( _historyCount );
	M_GT_SAFE_FREE( _btnTop );
	M_GT_SAFE_FREE( _btnChecked );
    
	[super unload];
}

- (void)showData:(NSObject *)data
{
    GTOutputObject *obj = (GTOutputObject *)data;
    GTOutputDataInfo *dataInfo = [obj dataInfo];
    NSString *dataStr = [NSString stringWithFormat:@"%@ ( %@ )", [dataInfo alias], [dataInfo key]];
    [_title setText:dataStr];
    
    NSInteger count = [obj historyCnt];
    if (count > 0) {
        [_historyCount setText:[NSString stringWithFormat:@"%ld", (long)count]];
        if (count > 99) {
            [_historyCount setText:@"99⁺"];
        }
    } else {
        [_historyCount setText:@"0"];
    }
    
    
    GTOutputValue *value = [[dataInfo value] retain];
    NSString *content = [value content];
    if (content != nil) {
        [_value setText:content];
    }
    [value release];
    
    if ([obj status] == GTParaOnDisabled) {
        _value.textColor = M_GT_CELL_TEXT_DISABLE_COLOR;
        _title.textColor = M_GT_CELL_TEXT_DISABLE_COLOR;
        self.backgroundColor = M_GT_CELL_BKGD_COLOR;
    } else {
        _value.textColor = M_GT_CELL_TEXT_COLOR;
        _title.textColor = M_GT_CELL_TEXT_COLOR;
        if ([obj showWarning] == YES) {
            self.backgroundColor = M_GT_WARNING_COLOR;
        } else {
            self.backgroundColor = M_GT_CELL_BKGD_COLOR;
        }
    }
    
    [self updateBtnChecked];
}

- (void)updateBtnChecked
{
    GTOutputObject *obj = (GTOutputObject *)self.cellData;
    
    if ([obj switchForHistory] == GTParaHistroyOn) {
        [_btnChecked setImage:[GTImage imageNamed:@"gt_checkbox_sel" ofType:@"png"] forState:UIControlStateNormal];
    } else if ([obj switchForHistory] == GTParaHistroyOff) {
        [_btnChecked setImage:[GTImage imageNamed:@"gt_checkbox" ofType:@"png"] forState:UIControlStateNormal];
    }
    
    if ([[GTConfig sharedInstance] gatherSwitch] == YES) {
        _btnChecked.enabled = NO;
    } else {
        _btnChecked.enabled = YES;
    }
}

- (void)bindData:(NSObject *)data withIndexPath:(NSIndexPath*)indexPath
{
    [super bindData:data];
    [self showData:data];
    [self setIndexPath:indexPath];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateCell:) name:M_GT_NOTIFICATION_OUT_PARA object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateCell:) name:M_GT_NOTIFICATION_OUT_ALL_SEL object:nil];
}

- (void)updateCell:(NSNotification *)n
{
    [self showData:self.cellData];
}

- (void)clearData
{
	[_title setText:nil];
    [_value setText:nil];
}

- (void)switchEditMode:(BOOL)edit
{
    if (edit) {
        _btnTop.hidden = NO;
        _value.hidden = YES;
        _historyCount.hidden = YES;
        _btnChecked.hidden = YES;
        _title.frame = CGRectMake( 10.0f, 5.0f, self.bounds.size.width-100, 35.0f );
    } else {
        _btnTop.hidden = YES;
        _value.hidden = NO;
        _historyCount.hidden = NO;
        _btnChecked.hidden = NO;
        _title.frame = CGRectMake( 10.0f, 5.0f, self.bounds.size.width-100, 20.0f );
    }
    
    GTOutputObject *obj = (GTOutputObject *)self.cellData;
    
    if ((([obj status] == GTParaOnDisabled) || ([obj switchForHistory] == GTParaHistroyDisabled))) {
        _btnChecked.hidden = YES;
        _historyCount.hidden = YES;
    }
}

- (void)didClickTop:(id)sender
{
    if (_delegate && [_delegate respondsToSelector:@selector(didClickTop:)])
    {
        [_delegate didClickTop:_indexPath];
    }
}

- (void)didClickChecked:(id)sender
{
    
    if (_delegate && [_delegate respondsToSelector:@selector(didClickChecked:)])
    {
        [_delegate didClickChecked:_indexPath];
        [self updateBtnChecked];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_CELL_SEL object:nil];
    
    //记录用户已有点击行为
    [[GTConfig sharedInstance] setUserClicked:YES];
}

@end

#endif
