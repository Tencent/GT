//
//  GTProvisionBoard.m
//  GTKit
//
//  Created by  on 13-11-26.
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

#import "GTProvisionBoard.h"
#import "GTDebugDef.h"
#import "GTUtility.h"

@interface GTProvisionBoard ()

@end

@implementation GTProvisionBoard

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)initUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [self createTopBar];
    [self setNavTitle:@"Terms and Privacy"];
    
    //这里使用M_GT_BOARD_FRAME，多减去M_GT_HEADER_HEIGHT导致显示铺不满，故单独处理
    CGRect frame = M_GT_BOARD_FRAME_6_0;
    
    if ([[GTUtility sharedInstance] systemVersion] >= 7) {
        frame = M_GT_BOARD_FRAME_7_0;
    }
    
    UIWebView *webView = [[UIWebView alloc] initWithFrame:frame];
    webView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
//    [webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:[[NSBundle frameworkBundle] pathForResource:@"EULA" ofType:@"html"]]]];
    [webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"http://gt.qq.com/wp-content/EULA_EN.html"]]];
    
    [self.view addSubview:webView];
    [webView release];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    [self initUI];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
