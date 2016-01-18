//
//  GTAboutBoard.m
//  GTKit
//
//  Created   on 12-12-25.
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
#import "GTAboutBoard.h"
#import "GTUtility.h"
#import <QuartzCore/QuartzCore.h>
#import "GTImage.h"
#import "GTSettingRow.h"
#import "GTSettingCell.h"
#import "GTVersionDef.h"
#import "GTProvisionBoard.h"
#import "GTLang.h"
#import "GTLangDef.h"


@implementation GTAboutBoard


- (id) init
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
    M_GT_SAFE_FREE(_iconView);
    M_GT_SAFE_FREE(_intro);
}

- (void)initUI
{
    [self createTopBar];
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [[self navigationController] setNavigationBarHidden:YES];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_SETTING_ABOUT_KEY)];
    
    _iconView = [[UIImageView alloc] init];
    _iconView.image = [GTImage imageNamed:@"gt_about" ofType:@"png"];
    _iconView.backgroundColor = [UIColor clearColor];
    
    CGRect imgFrame = M_GT_BOARD_FRAME;
    imgFrame.size.width = 104;
    imgFrame.size.height = 104;
    imgFrame.origin.x = (self.view.frame.size.width - imgFrame.size.width) / 2.0f;
    imgFrame.origin.y = imgFrame.origin.y + 15;
    
    [_iconView setFrame:imgFrame];
    [self.view addSubview:_iconView];
    
    _intro = [[UILabel alloc] init];
    _intro.font = [UIFont systemFontOfSize:18.0];
    _intro.textColor = [UIColor whiteColor];
    _intro.textAlignment = NSTextAlignmentCenter;
    _intro.lineBreakMode = NSLineBreakByWordWrapping;
    _intro.numberOfLines = 0;
    _intro.backgroundColor = [UIColor clearColor];
    [_intro setText:M_GT_ABOUT_TXT];
    
    CGRect txtFrame;
    
    
    txtFrame.size.width = 200;
    
    CGSize constrainedToSize = CGSizeMake(txtFrame.size.width, 900);
    CGSize size = [_intro.text sizeWithFont:_intro.font
                          constrainedToSize:constrainedToSize
                              lineBreakMode:NSLineBreakByWordWrapping];

    txtFrame.size.height = size.height;
    txtFrame.origin.x = imgFrame.origin.x + (imgFrame.size.width - txtFrame.size.width)/2 ;
    txtFrame.origin.y = imgFrame.origin.y + imgFrame.size.height + 5;
    
    [_intro setFrame:txtFrame];
    [self.view addSubview:_intro];
    
    CGRect frame = M_GT_BOARD_FRAME;
    
    frame.origin.x = 10;
    frame.origin.y = 5 + txtFrame.origin.y + txtFrame.size.height;
    frame.size.height = frame.size.height - txtFrame.origin.y - txtFrame.size.height - 50;
    frame.size.width = frame.size.width - 2 *frame.origin.x;
    
    
    frame = M_GT_BOARD_FRAME;
    frame.origin.x = frame.origin.x + 5 ;
    frame.origin.y = frame.origin.y + frame.size.height - 55;
    frame.size.height = 40;
    
    UIButton *btnProvision = [[UIButton alloc] initWithFrame:frame];
    [btnProvision addTarget:self action:@selector(onProvision:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btnProvision];
    [btnProvision release];
    
    frame.size.height = 20;
    UILabel *info = [[UILabel alloc] initWithFrame:frame];
    info.font = [UIFont systemFontOfSize:11.0];
    info.textColor = M_GT_COLOR_WITH_HEX(0x8697B5);
    info.textAlignment = NSTextAlignmentCenter;
    info.lineBreakMode = NSLineBreakByWordWrapping;
    info.numberOfLines = 0;
    info.backgroundColor = [UIColor clearColor];
    [info setText:@"Terms and Privacy"];
    [self.view addSubview:info];
    [info release];
    
    frame.origin.y = frame.origin.y + frame.size.height;
    frame.size.height = 12;
    
    info = [[UILabel alloc] initWithFrame:frame];
    info.font = [UIFont boldSystemFontOfSize:11.0];
    info.textColor = [UIColor grayColor];
    info.textAlignment = NSTextAlignmentCenter;
    info.lineBreakMode = NSLineBreakByWordWrapping;
    info.numberOfLines = 0;
    info.backgroundColor = [UIColor clearColor];
    [info setText:@"Tencent 腾讯"];
    [self.view addSubview:info];
    [info release];
    
    frame.origin.y = frame.origin.y + frame.size.height;
    frame.size.height = 22;
    
    info = [[UILabel alloc] initWithFrame:frame];
    info.font = [UIFont systemFontOfSize:8.0];
    info.textColor = [UIColor grayColor];
    info.textAlignment = NSTextAlignmentCenter;
    info.lineBreakMode = NSLineBreakByWordWrapping;
    info.numberOfLines = 0;
    info.backgroundColor = [UIColor clearColor];
    [info setText:@"Copyright © 1998-2015 Tencent.\r\n All rights reserved."];
//    [info setText:@"Copyright ©[Insert Year of First Publication] - 2015 Tencent.All Rights Reserved. This software is licensed under the terms in the LICENSE.TXT file that accompanies this software."];
    [self.view addSubview:info];
    [info release];
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
	
}

- (void)viewDidUnload
{
    M_GT_SAFE_RELEASE_SUBVIEW(_iconView);
    M_GT_SAFE_RELEASE_SUBVIEW(_intro);
    [super viewDidUnload];
}

#pragma mark Button

- (void)onProvision:(id)sender
{
    GTProvisionBoard *board = [[[GTProvisionBoard alloc] init] autorelease];
    [self.navigationController pushViewController:board animated:YES];
}

@end
#endif
