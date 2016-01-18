//
//  UIViewController.m
//  GTKit
//
//  Created   on 13-4-1.
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
#import "GTUIViewController.h"
#import "GTDebugDef.h"
#import "GTImage.h"
#import "GTUtility.h"

@implementation GTUIViewController

@synthesize navBar  = _navBar;
@synthesize navItem = _navItem;

- (id)init
{
    self = [super init];
    if (self) {
        _topBarCreated = NO;

        if ([[GTUtility sharedInstance] systemVersion] >= 7) {
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
    }
    
    return self;
}

- (void)dealloc
{
    self.navBar = nil;
    self.navItem = nil;
    [super dealloc];
}


- (void)createTopBar
{
    [[self navigationController] setNavigationBarHidden:YES];

    if (_topBarCreated) {
        return;
    }
    _topBarCreated = YES;
    
    _navBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, 0, M_GT_SCREEN_WIDTH, M_GT_HEADER_HEIGHT)];
	[_navBar setAutoresizingMask:UIViewAutoresizingFlexibleWidth];
    
    _navItem = [[UINavigationItem alloc] init];
    [_navItem setTitle:self.title];
    [_navItem setLeftBarButtonItems:[self leftBarButtonItems] animated:YES];
    [_navItem setRightBarButtonItems:[self rightBarButtonItems] animated:YES];
    
	_navBar.items = [NSArray arrayWithObject:_navItem];
	[self.view addSubview:_navBar];
    
    
    //标题栏颜色设置
    CGSize bkgdSize = _navBar.bounds.size;
    CGFloat x = _navBar.frame.origin.x;
    CGFloat y = _navBar.frame.origin.y;
    [_navBar setFrame:CGRectMake(x, y, bkgdSize.width, M_GT_HEADER_HEIGHT)];
    CGSize imageSize = CGSizeMake(bkgdSize.width, M_GT_NAVBAR_HEIGHT);
    CGSize lineSize = CGSizeMake(bkgdSize.width, M_GT_HEADER_HEIGHT - M_GT_NAVBAR_HEIGHT);
    UIGraphicsBeginImageContextWithOptions(bkgdSize, 0, [UIScreen mainScreen].scale);
    [M_GT_NAV_BAR_COLOR set];
    UIRectFill(CGRectMake(0, 0, imageSize.width, imageSize.height));
    [M_GT_NAV_BARLINE_COLOR set];
    UIRectFill(CGRectMake(0, 0 + imageSize.height, lineSize.width, lineSize.height));
    UIImage *bkgdImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    [_navBar setBackgroundImage:bkgdImage forBarMetrics:UIBarMetricsDefault];
}

- (void)setNavBarHidden:(BOOL)hidden
{
    _navBar.hidden = hidden;
}

- (void)setNavTitle:(NSString *)title
{
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont systemFontOfSize:20];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.text = title;
    titleLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    
    CGSize maxSize = CGSizeMake(M_GT_SCREEN_WIDTH - 88, M_GT_NAVBAR_HEIGHT);
    CGSize sz = [title sizeWithFont:titleLabel.font constrainedToSize:maxSize lineBreakMode:NSLineBreakByWordWrapping];
    [titleLabel setFrame:CGRectMake(0, 0, sz.width, M_GT_NAVBAR_HEIGHT)];
    self.navItem.titleView = titleLabel;
    [titleLabel release];
}

- (NSArray *)leftBarButtonItems
{
    UIView      *barView = nil;
    UIButton    *barBtn  = nil;
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_NAVBAR_HEIGHT, M_GT_NAVBAR_HEIGHT)];
    
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, M_GT_NAVBAR_HEIGHT, M_GT_NAVBAR_HEIGHT)];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake((M_GT_NAVBAR_HEIGHT - 24)/2, (M_GT_NAVBAR_HEIGHT-24)/2, (M_GT_NAVBAR_HEIGHT - 24)/2, (M_GT_NAVBAR_HEIGHT-24)/2)];
    
    [barBtn addTarget:self action:@selector(onBackClicked:) forControlEvents:UIControlEventTouchUpInside];
    
    [barBtn setImage:[GTImage imageNamed:@"gt_back" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_back_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView] ;
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar1, nil];
    [bar1 release];
    
    return array;
}

- (NSArray *)rightBarButtonItems
{
    return nil;
}

//iOS7
- (BOOL)prefersStatusBarHidden
{
    return NO;
}

#pragma mark - BarButtonItem

- (void)onBackClicked:(id)sender
{
    //返回
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark -

- (BOOL)shouldAutorotate
{
    return YES;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAll;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return YES;
 }
@end
#endif
