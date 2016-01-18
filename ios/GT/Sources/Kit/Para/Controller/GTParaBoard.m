//
//  GTParaBoard.m
//  GTKit
//
//  Created   on 12-11-18.
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
#import <Foundation/Foundation.h>
#import <QuartzCore/QuartzCore.h>
#import "GTParaBoard.h"
#import "GTInputList.h"
#import "GTOutputList.h"
#import "GTConfig.h"
#import "GTParaInSelectBoard.h"
#import "GTImage.h"
#import "GTParaConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"
//#define M_GT_PARA_IN    @"In"
//#define M_GT_PARA_OUT   @"Out"


@implementation GTParaBoard

@synthesize selectedIndex = _selectedIndex;
@synthesize delegate = _delegate;

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
    _selectedIndex = [[GTParaConfig sharedInstance] selectedIndex];
    _paraInView = [[GTParaInputBoard alloc] initWithViewController:self];
    _paraOutView = [[GTParaOutputBoard alloc] initWithViewController:self];
}

- (void)unload
{
    M_GT_SAFE_FREE(_barBtn);
    M_GT_SAFE_FREE(_paraInView);
    
    [_paraOutView unobserveTick];
    M_GT_SAFE_FREE(_paraOutView);
    
}

- (void)initUI
{
    NSArray *arr = [NSArray arrayWithObjects:M_GT_LOCALSTRING(M_GT_PARA_IN_KEY), M_GT_LOCALSTRING(M_GT_PARA_OUT_KEY), nil];
    _bar = [[[GTParaBar alloc] initWithFrame:CGRectMake(0, 0, 128.0f, 30.0f) buttonTitles:arr] autorelease];
    _bar.delegate = self;
    [self.navItem setTitleView:_bar];
    
    [_bar selectTabAtIndex:_selectedIndex];
    [self tabBar:_bar didSelectIndex:_selectedIndex];
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
	[[self navigationController] setNavigationBarHidden:YES];
    
//    [self tabBar:_bar didSelectIndex:_selectedIndex];
    if (_selectedIndex == 0) {
        [_paraInView update];
    } else {
        [_paraOutView update];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}


- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    [_paraInView viewDidDisappear];
    [_paraOutView viewDidDisappear];
}


#pragma mark -
- (NSArray *)rightBarButtonItems
{
    UIView *barView = nil;
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_BTN_WIDTH, M_GT_BTN_HEIGHT)];
    _barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, M_GT_BTN_WIDTH, M_GT_BTN_HEIGHT)];
    [_barBtn addTarget:self action:@selector(onEditTouched:) forControlEvents:UIControlEventTouchUpInside];
    _barBtn.titleLabel.font = [UIFont systemFontOfSize:M_GT_BTN_FONTSIZE];
    _barBtn.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _barBtn.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _barBtn.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [barView addSubview:_barBtn];
    
    //默认显示非edit模式
    [[GTParaConfig sharedInstance] setIsEditMode:NO];
    [self updateBarBtnTitle];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar1, nil];
    [bar1 release];
    
    return array;
}

- (void)onEditTouched:(id)sender
{
    BOOL isEditMode = [[GTParaConfig sharedInstance] isEditMode];
    isEditMode = !isEditMode;
    [[GTParaConfig sharedInstance] setIsEditMode:isEditMode];
    [_paraInView switchEditMode:isEditMode];
    [_paraOutView switchEditMode:isEditMode];
    
    [self updateBarBtnTitle];
}

- (void)updateBarBtnTitle
{
    BOOL isEditMode = [[GTParaConfig sharedInstance] isEditMode];
    
    if (isEditMode == YES) {
        [_barBtn setBackgroundColor:M_GT_SELECTED_COLOR];
        [_barBtn setTitle:M_GT_LOCALSTRING(M_GT_PARA_DONE_KEY) forState:UIControlStateNormal];
        [_barBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    } else {
        [_barBtn setBackgroundColor:M_GT_BTN_BKGD_COLOR];
        [_barBtn setTitle:M_GT_LOCALSTRING(M_GT_PARA_EDIT_KEY) forState:UIControlStateNormal];
        [_barBtn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    }
    [_paraInView switchEditMode:isEditMode];
    [_paraOutView switchEditMode:isEditMode];
}

#pragma mark - tabBar delegates

- (void)tabBar:(GTParaBar *)tabBar didSelectIndex:(NSInteger)index
{
    _selectedIndex = index;
    [[GTParaConfig sharedInstance] setSelectedIndex:index];
    if (index == 0) {
        [_paraOutView viewDidDisappear];
        [_paraOutView removeFromSuperview];
        [self.view addSubview:_paraInView];
        [_paraInView viewWillAppear];
    } else {
        [_paraInView viewDidDisappear];
        [_paraInView removeFromSuperview];
        [self.view addSubview:_paraOutView];
        [_paraOutView viewWillAppear];
    }
    
    [self updateBarBtnTitle];
}


#pragma mark - GTParaOutDelegate
- (void)didClickGW
{
    if (_delegate && [_delegate respondsToSelector:@selector(didClickGW)])
    {
        [_delegate didClickGW];
    }
}

@end
#endif
