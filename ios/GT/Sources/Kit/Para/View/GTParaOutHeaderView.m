//
//  GTParaOutHeaderView.m
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
#import "GTParaOutHeaderView.h"
#import "GTUIAlertView.h"
#import "GTConfig.h"
#import "GTOutputList.h"
#import "GTProgressHUD.h"
#import "GTLang.h"
#import "GTLangDef.h"


//#define M_GT_PARA_OUT_ITEMS @"Output Para Items"
//#define M_GT_PARA_OUT_TOP   @"Top"
//#define M_GT_PARA_OUT_DRAG  @"Drag"
//#define M_GT_PARA_OUT_GW    @"Gather & Warning(G&W)"

@implementation GTParaOutHeaderView


@synthesize delegate = _delegate;

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
    [self unload];
    [super dealloc];
}

- (void)unload
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_GW_UPDATE object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_CELL_SEL object:nil];
    
    M_GT_SAFE_FREE(_items);
    M_GT_SAFE_FREE(_top);
    M_GT_SAFE_FREE(_drag);
    M_GT_SAFE_FREE(_gwDescription);
    M_GT_SAFE_FREE(_btnClear);
    M_GT_SAFE_FREE(_btnSave);
    M_GT_SAFE_FREE(_btnGWSwitch);
    M_GT_SAFE_FREE(_btnAllSel);
}

- (void)load
{
    UIColor *color = M_GT_COLOR_WITH_HEX(0xCB7418);
    
    _items = [[UILabel alloc] init];
    _items.font = [UIFont systemFontOfSize:12.0];
    _items.textColor = color;
    _items.textAlignment = NSTextAlignmentLeft;
    _items.backgroundColor = [UIColor clearColor];
    [self addSubview:_items];
    
    _top = [[UILabel alloc] init];
    _top.font = [UIFont systemFontOfSize:12.0];
    _top.textColor = color;
    _top.textAlignment = NSTextAlignmentCenter;
    _top.backgroundColor = [UIColor clearColor];
    [self addSubview:_top];
    
    _drag = [[UILabel alloc] init];
    _drag.font = [UIFont systemFontOfSize:12.0];
    _drag.textColor = color;
    _drag.textAlignment = NSTextAlignmentCenter;
    _drag.backgroundColor = [UIColor clearColor];
    [self addSubview:_drag];
    
    _gwDescription = [[UILabel alloc] init];
    _gwDescription.font = [UIFont systemFontOfSize:12.0];
    _gwDescription.textColor = M_GT_WARNING_COLOR;
    _gwDescription.textAlignment = NSTextAlignmentCenter;
    _gwDescription.backgroundColor = [UIColor clearColor];
    [self addSubview:_gwDescription];
    
    _btnClear = [[UIButton alloc] init];
    [_btnClear addTarget:self action:@selector(onClearTouched:) forControlEvents:UIControlEventTouchUpInside];
    [_btnClear setImage:[GTImage imageNamed:@"gt_clear" ofType:@"png"] forState:UIControlStateNormal];
    [_btnClear setImage:[GTImage imageNamed:@"gt_clear_sel" ofType:@"png"] forState:UIControlStateSelected];
    _btnClear.backgroundColor = [UIColor clearColor];
    [self addSubview:_btnClear];
    
    _btnSave = [[UIButton alloc] init];
    [_btnSave addTarget:self action:@selector(onSaveTouched:) forControlEvents:UIControlEventTouchUpInside];
    [_btnSave setImage:[GTImage imageNamed:@"gt_save" ofType:@"png"] forState:UIControlStateNormal];
    [_btnSave setImage:[GTImage imageNamed:@"gt_save_sel" ofType:@"png"] forState:UIControlStateSelected];
    _btnSave.backgroundColor = [UIColor clearColor];
    [self addSubview:_btnSave];
    
    _btnGWSwitch = [[UIButton alloc] init];
    [_btnGWSwitch addTarget:self action:@selector(onGWTouched:) forControlEvents:UIControlEventTouchUpInside];
    _btnGWSwitch.backgroundColor = [UIColor clearColor];
    [self addSubview:_btnGWSwitch];
    
    _btnAllSel = [[UIButton alloc] init];
    [_btnAllSel addTarget:self action:@selector(onAllSelTouched:) forControlEvents:UIControlEventTouchUpInside];
    _btnAllSel.backgroundColor = [UIColor clearColor];
    [self addSubview:_btnAllSel];
    
    [self updateAllSel];
    [self updateGatherSwitch];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateAllSel) name:M_GT_NOTIFICATION_OUT_CELL_SEL object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateGatherSwitch) name:M_GT_NOTIFICATION_OUT_GW_UPDATE object:nil];
}

- (void)viewLayout:(CGRect)frame
{
    CGFloat x = frame.origin.x;
    CGFloat y = 0;
    CGFloat width = frame.size.width - 20;
    
    CGFloat height = frame.size.height;
    
    _items.frame = CGRectMake( 10 + x, y, width - 88, height );
    _top.frame = CGRectMake( 10 + x + width - 88, y, 44, height );
    _drag.frame = CGRectMake( 10 + x + width - 44 , y, 44, height );
    
    //btn按钮显示图像会有8个像素空余
    height = frame.size.height - 44 + 8;
    _gwDescription.frame = CGRectMake( 10 + x + width - 44*4 , y, 44*4, height );
    
    //预留44像素作为下一行btnClear等的空间
    y = frame.size.height - 44;
    
    _btnClear.frame = CGRectMake( 10 + x + width - 44*4, y, 44, 44 );
    [_btnClear setImageEdgeInsets:UIEdgeInsetsMake(8, 10, 12, 10)];
    
    _btnSave.frame = CGRectMake( 10 + x + width - 44*3, y, 44, 44 );
    [_btnSave setImageEdgeInsets:UIEdgeInsetsMake(8, 10, 12, 10)];
    
    _btnGWSwitch.frame = CGRectMake( 10 + x + width - 44*2, y, 44, 44 );
    [_btnGWSwitch setImageEdgeInsets:UIEdgeInsetsMake(8, 10, 12, 10)];
    
    _btnAllSel.frame = CGRectMake( 10 + x + width - 44, y, 44, 44 );
    [_btnAllSel setImageEdgeInsets:UIEdgeInsetsMake(8, 10, 12, 10)];
}

- (void)showData
{
    [_items setText:M_GT_LOCALSTRING(M_GT_PARA_OUT_ITEMS_KEY)];
    [_top setText:M_GT_LOCALSTRING(M_GT_PARA_TOP_KEY)];
    [_drag setText:M_GT_LOCALSTRING(M_GT_PARA_DRAG_KEY)];
    [_gwDescription setText:M_GT_LOCALSTRING(M_GT_PARA_OUT_GW_KEY)];
}

- (void)switchEditMode:(BOOL)edit
{
    if (edit) {
        _top.hidden = NO;
        _drag.hidden = NO;
        
        _gwDescription.hidden = YES;
        _btnClear.hidden = YES;
        _btnSave.hidden = YES;
        _btnGWSwitch.hidden = YES;
        _btnAllSel.hidden = YES;
    } else {
        _top.hidden = YES;
        _drag.hidden = YES;
        
        _gwDescription.hidden = NO;
        _btnClear.hidden = NO;
        _btnSave.hidden = NO;
        _btnGWSwitch.hidden = NO;
        _btnAllSel.hidden = NO;
        
        BOOL gatherSwitch = [[GTConfig sharedInstance] gatherSwitch];
        if (gatherSwitch) {
            _btnClear.hidden = YES;
            _btnSave.hidden = YES;
        }
    }
}

- (void)updateAllSel
{
    BOOL result = [[GTOutputList sharedInstance] itemsAllHistoryOn];
    
    if (result) {
        [_btnAllSel setImage:[GTImage imageNamed:@"gt_checkbox_sel" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        [_btnAllSel setImage:[GTImage imageNamed:@"gt_checkbox" ofType:@"png"] forState:UIControlStateNormal];
    }
}


- (void)updateGatherSwitch
{
    BOOL gatherSwitch = [[GTConfig sharedInstance] gatherSwitch];
    if (gatherSwitch) {
        _btnClear.hidden = YES;
        _btnSave.hidden = YES;
        _btnAllSel.enabled = NO;
        [_btnGWSwitch setImage:[GTImage imageNamed:@"gt_stop" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        _btnClear.hidden = NO;
        _btnSave.hidden = NO;
        _btnAllSel.enabled = YES;
        [_btnGWSwitch setImage:[GTImage imageNamed:@"gt_start" ofType:@"png"] forState:UIControlStateNormal];
    }
    
    [self viewLayout:self.frame];
    
}

#pragma mark - Button clicked

- (void)onClearTouched:(id)sender
{
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_CLEAR_TITLE_KEY)                                                             message:M_GT_LOCALSTRING(M_GT_ALERT_CLEAR_INFO_KEY)
                                                            delegate:self
                                                   cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY)
                                                   otherButtonTitles:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)];
    [alertView setTag:M_GT_ALERT_TAG_CLEAR];
    [alertView show];
    [alertView release];
    
}

- (void)onSaveTouched:(id)sender
{
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_SAVE_TITLE_KEY)
                                                             message:M_GT_LOCALSTRING(M_GT_ALERT_INPUT_SAVED_FILE_KEY)
                                                            delegate:self
                                                   cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY)
                                                   otherButtonTitles:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)];
    [alertView setTag:M_GT_ALERT_TAG_SAVE];
    [alertView addTextFieldWithTag:0];
    [[alertView textFieldAtTag:0] setText:[[GTOutputList sharedInstance] dirName]];
    [alertView show];
    [alertView release];
}


- (void)onGWTouched:(id)sender
{
    BOOL gatherSwitch = [[GTConfig sharedInstance] gatherSwitch];
    gatherSwitch = !gatherSwitch;
    [[GTConfig sharedInstance] setGatherSwitch:gatherSwitch];
    
    [self updateGatherSwitch];
    
    if (_delegate && [_delegate respondsToSelector:@selector(didClickGW)])
    {
        [_delegate didClickGW];
    }
    
    
}

- (void)onAllSelTouched:(id)sender
{
    BOOL result = [[GTOutputList sharedInstance] itemsAllHistoryOn];
    if (result) {
        [[GTOutputList sharedInstance] setAllHistoryOff];
    } else {
        [[GTOutputList sharedInstance] setAllHistoryOn];
    }
    
    [self updateAllSel];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_ALL_SEL object:nil];
    
    //记录用户已有点击行为
    [[GTConfig sharedInstance] setUserClicked:YES];
}


#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    //clear
    if ([alertView tag] == M_GT_ALERT_TAG_CLEAR) {
        if (buttonIndex == 1) {
            [[GTOutputList sharedInstance] clearAllHistroy];
//            [GTProgressHUD showWithString:@"Clear history successfully!\r\n"];
        }
    }
    //save
    else if ([alertView tag] == M_GT_ALERT_TAG_SAVE)
    {
        if (buttonIndex == 1) {
            UITextField *saveLogName = [alertView textFieldAtTag:0];
            [[GTOutputList sharedInstance] saveHistroyForDirName:[saveLogName text]];
        }
    }
}

@end
#endif
