//
//  GTParaOutDetailBoard.m
//  GTKit
//
//  Created   on 13-1-19.
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
#import "GTParaOutDetailBoard.h"
#import "GTImage.h"
#import <QuartzCore/QuartzCore.h>
#import "GTOutputList.h"
#import "GTLang.h"
#import "GTLangDef.h"

@interface GTParaOutCommonBoard ()

@end

@implementation GTParaOutCommonBoard

- (id)init
{
    self = [super init];
    if (self) {
        [self load];
    }
    return self;
}

- (void)dealloc
{
	[self unload];
    [super dealloc];
}


- (void)load
{
	
}

- (void)unload
{
	[self unobserveTick];
}

- (void)initNavBarUI
{
    [self createTopBar];
    [self setNavTitle:_data.dataInfo.key];
}


- (void)initDetailUI
{
    CGRect rect = M_GT_BOARD_FRAME;
    CGFloat offset = 10;
    CGFloat width = [self widthForOutDetail];
    
    self.view.backgroundColor = M_GT_CELL_BKGD_COLOR;
    
    CGRect frame;

    CGFloat height = [self heightForHeader];
    UIView *headerView = [self viewForHeader];
    
    if ((height > 0) && (headerView != nil)) {
        frame.origin.x = offset;
        frame.origin.y = rect.origin.y;
        frame.size.height = height;
        frame.size.width = width;
        [headerView setFrame:frame];
        [self.view addSubview:headerView];
    }
    
    [self updateData];
}

- (void)initUI
{
    [self initNavBarUI];
    [self initDetailUI];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self initUI];
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
	[self observeTick];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self unobserveTick];
}

- (NSArray *)rightBarButtonItems
{
    UIView *barView = nil;
    UIButton *barBtn = nil;
    
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 5, 10, 5)];
    [barBtn addTarget:self action:@selector(onSaveTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImage:[GTImage imageNamed:@"gt_save" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_save_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar2 = nil;
    bar2 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 5, 10, 5)];
    [barBtn addTarget:self action:@selector(onClearTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImage:[GTImage imageNamed:@"gt_clear" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_clear_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar3 = nil;
    bar3 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar2, bar3, nil];
    [bar2 release];
    [bar3 release];
    
    return array;
}

- (void)bindData:(GTOutputObject *)data
{
	_data = data;
}

- (void)updateData
{
    
}

- (void)resetData
{
    
}

- (CGFloat)widthForOutDetail
{
    CGFloat width = M_GT_SCREEN_WIDTH - 20;
    
    return width;
}

#pragma mark - Timer
- (void)observeTick
{
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:YES];
        [_timer retain];
    }
	
}

- (void)unobserveTick
{
    if (_timer) {
        [_timer invalidate];
        [_timer release];
        _timer = nil;
    }
}


- (void)handleTick
{
    [self updateData];
}

#pragma mark - Button clicked

- (void)onClearTouched:(id)sender
{
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_CLEAR_TITLE_KEY)
                                                             message:M_GT_LOCALSTRING(M_GT_ALERT_CLEAR_INFO_KEY)
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
    [[alertView textFieldAtTag:0] setText:[_data fileName]];
    [alertView show];
    [alertView release];
}

#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    //clear
    if ([alertView tag] == M_GT_ALERT_TAG_CLEAR) {
        if (buttonIndex == 1) {
            [[GTOutputList sharedInstance] clearHistroyForKey:[[_data dataInfo] key]];
            [self resetData];
        }
    }
    //save
    else if ([alertView tag] == M_GT_ALERT_TAG_SAVE)
    {
        if (buttonIndex == 1) {
            UITextField *saveLogName = [alertView textFieldAtTag:0];
            
            //启动新线程保存文件
            [[GTOutputList sharedInstance] saveHistroyForKey:[[_data dataInfo] key] fileName:[saveLogName text] inThread:YES];
        }
    }
}


#pragma mark - 扩展HeaderView
- (CGFloat)heightForHeader
{
    return 0.0f;
}

- (UIView *)viewForHeader
{
    return nil;
}


@end


@interface GTParaOutDetailBoard ()

@end

@implementation GTParaOutDetailBoard

- (void)initDetailUI
{
    CGRect rect = M_GT_BOARD_FRAME;
    CGFloat offset = 10;
    CGFloat width = [self widthForOutDetail];
    
    self.view.backgroundColor = M_GT_CELL_BKGD_COLOR;
    
    CGRect frame;
    
    CGFloat height = [self heightForHeader];
    UIView *headerView = [self viewForHeader];
    
    if ((height > 0) && (headerView != nil)) {
        frame.origin.x = offset;
        frame.origin.y = rect.origin.y;
        frame.size.height = height;
        frame.size.width = width;
        [headerView setFrame:frame];
        [self.view addSubview:headerView];
    }
    
    frame.origin.x = offset;
    frame.origin.y = rect.origin.y + height;
    frame.size.height = M_GT_BOARD_HEIGHT - height;
    frame.size.width = width;
    
    _content = [[UITextView alloc] initWithFrame:frame];
    _content.font = [UIFont systemFontOfSize:15.0f];
    _content.textColor = M_GT_CELL_TEXT_COLOR;
    _content.textAlignment = NSTextAlignmentLeft;
    _content.editable = NO;
    _content.dataDetectorTypes = UIDataDetectorTypeLink;
    _content.scrollEnabled = YES;
    _content.backgroundColor = M_GT_CELL_BKGD_COLOR;
    _content.keyboardType = UIKeyboardTypeDefault;
    [self.view addSubview:_content];
    [self updateData];
}


- (void)updateData
{
    [super updateData];
    GTOutputValue *value = [_data.dataInfo value];
    _content.text = [value content];
    
}


@end
#endif
