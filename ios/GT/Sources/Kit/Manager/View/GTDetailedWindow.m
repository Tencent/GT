//
//  GTDetailedWindow.m
//  GTKit
//
//  Created   on 12-10-10.
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

#import "GTDetailedWindow.h"
#import "GT.h"
#import "GTDebugDef.h"
#import "GTConfig.h"
#import "GTUIManager.h"
#import <QuartzCore/QuartzCore.h>
#import "GTImage.h"
#import "GTUINavigationController.h"
#import "GTLang.h"
#import "GTLangDef.h"
#import "GTVersionDef.h"
#import "GTMTA.h"

#pragma mark -

//#define M_GT_PARA       @"Para."
//#define M_GT_PROFILER   @"Profiler"
//#define M_GT_LOG        @"Log"
//#define M_GT_PLUGIN     @"Plugin"
//#define M_GT_SETTING    @"Setting"

@implementation GTDetailedWindow


- (void)load
{
    _boards = [[NSMutableArray alloc] init];
    [_boards addObject:[NSArray arrayWithObjects:M_GT_LOCALSTRING(M_GT_PARA_KEY), @"GTParaBoard", nil]];
	[_boards addObject:[NSArray arrayWithObjects:M_GT_LOCALSTRING(M_GT_PROFILER_KEY), @"GTLogProfilerBoard", nil]];
    [_boards addObject:[NSArray arrayWithObjects:M_GT_LOCALSTRING(M_GT_LOG_KEY), @"GTLogBoard", nil]];
    [_boards addObject:[NSArray arrayWithObjects:M_GT_LOCALSTRING(M_GT_PLUGIN_KEY), @"GTPluginBoard", nil]];
    [_boards addObject:[NSArray arrayWithObjects:M_GT_LOCALSTRING(M_GT_SETTING_KEY), @"GTSettingBoard", nil]];
}

- (void)unload
{
    [_boards removeAllObjects];
	[_boards release];
    
    _consoleDelegate = nil;
//    NSLog(@"%s _barController:%@ [_barController retainCount]:%u", __FUNCTION__, _barController, [_barController retainCount]);
}

- (void)dealloc
{
	[self unload];
    [super dealloc];
}


- (void)initUIWithIndex:(NSUInteger)index
{
    NSMutableArray *controllers = [NSMutableArray array];
    NSMutableArray *titleArray = [NSMutableArray array];
    //数组数量
    NSUInteger count = [_boards count];
    
    for (int i = 0; i < count; i++)
    {
        NSArray * data = [_boards objectAtIndex:i];
        //创建ViewController
        GTUIViewController *viewController = (GTUIViewController *)[[NSClassFromString( [data objectAtIndex:1] ) alloc] init];
        [viewController setHidesBottomBarWhenPushed:YES];
        
        //设置标题
        [viewController setTitle:[data objectAtIndex:0]];
        [titleArray addObject:[data objectAtIndex:0]];
        
        [self addBackBtnForVC:viewController action:@selector(onCloseWindow:)];
        
        
        //对于出参的界面，用户点击采集时会做自动退出的处理，所以这里设置委托处理返回操作
        if ([[data objectAtIndex:1] isEqualToString:@"GTParaBoard"]) {
            if ([viewController respondsToSelector:@selector(setDelegate:)])
            {
                [(GTParaBoard *)viewController setDelegate:self];
            }
        }
        
        
        //绑定tabViewController用来响应按钮点击事件
        GTUINavigationController *nav = [[GTUINavigationController alloc] initWithRootViewController:viewController];
        [viewController release];
        
        //设置风格
        nav.navigationBar.barStyle = UIBarStyleBlackOpaque;
        nav.delegate = self;
        
        //添加这个UINavigationController
        [controllers addObject:nav];
        
        //释放对象
        [nav release];
    }
    
    _barController = [[GTTabBarController alloc] initWithViewControllers:controllers titleArray:titleArray withIndex:index];
    [_barController setTabBarTransparent:NO];
    [_barController setHidesBottomBarWhenPushed:YES];
    _barController.delegate = self;
    
//    [self addSubview:_barController.view];
    if ([self respondsToSelector:@selector(setRootViewController:)]) {
        self.rootViewController = _barController;
    } else {
        [self addSubview:_barController.view];
    }
   
    [self makeKeyAndVisible];
    
    [_barController release];
}

- (id)initWithFrame:(CGRect)rect boardIndex:(NSUInteger)index delegate:(id)delegate
{
	self = [super initWithFrame:rect];
	if (self)
	{
        self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6f];
		self.hidden = YES;
		self.windowLevel = UIWindowLevelStatusBar + 200;
        
        _consoleDelegate = delegate;
        
        [self load];
        [self initUIWithIndex:index];
        
        if (![[GTConfig sharedInstance] hasReported]) {
            [[GTConfig sharedInstance] setHasReported:YES];
            
            // 上报被测应用的app及对应的GT版本
            NSString *appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleIdentifierKey];
            NSMutableDictionary *dictionary = [NSMutableDictionary dictionaryWithCapacity:2];
            [dictionary setObject:appName forKey:@"appName"];
            [dictionary setObject:M_GT_VERSION forKey:@"sdkVersion"];
            [GTMTA trackCustomKeyValueEvent:@"AUT" props:dictionary];
            
//            NSLog(@"report:%@", appName);
        }
        
    }
    
    return self;
}


#pragma mark - Action

- (void)onCloseWindow:(id)sender
{
    if (_consoleDelegate) {
        [_consoleDelegate onDetailedClose];
    }
}

- (void)popViewController:(id)sender
{
    [[_barController selectedViewController].navigationController popViewControllerAnimated:YES];
}

- (void)tabBar:(UITabBar *)tabBar didSelectItem:(UITabBarItem *)item {
	// Act like a single tab bar
	
}

#pragma mark - UITabBarControllerDelegate

- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController
{
    /* 添加代码，处理定制标签栏结束之后的操作 */
    [[GTUIManager sharedInstance] setDetailedIndex:[_barController selectedIndex]];
}



#pragma mark - UINavigationControllerDelegate

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    //TODO TAB不做隐藏
    return;
//    [viewController.navigationController setNavigationBarHidden:YES];
    if (viewController.hidesBottomBarWhenPushed)
    {
        [_barController hidesTabBar:NO animated:YES];
    }
    else
    {
        [_barController hidesTabBar:YES animated:YES];
        [_barController setTabBarTransparent:YES];
    }
}

- (void)addBackBtnForVC:(GTUIViewController *)viewController action:(SEL)action
{
    UIBarButtonItem *btn        = nil;
    UIView          *backView   = nil;
    UIButton        *backBtn    = nil;
    
    backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_NAVBAR_HEIGHT, M_GT_NAVBAR_HEIGHT)];
    
    backBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, M_GT_NAVBAR_HEIGHT, M_GT_NAVBAR_HEIGHT)];
    [backBtn setImageEdgeInsets:UIEdgeInsetsMake((M_GT_NAVBAR_HEIGHT - 24)/2, (M_GT_NAVBAR_HEIGHT-24)/2, (M_GT_NAVBAR_HEIGHT - 24)/2, (M_GT_NAVBAR_HEIGHT-24)/2)];
    if (action != nil) {
        [backBtn addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    }
    [backBtn setImage:[GTImage imageNamed:@"gt_back" ofType:@"png"] forState:UIControlStateNormal];
    [backBtn setImage:[GTImage imageNamed:@"gt_back_sel" ofType:@"png"] forState:UIControlStateSelected];
    [backView addSubview:backBtn];
    [backBtn release];
    
    
    [viewController createTopBar];
    btn = [[UIBarButtonItem alloc] initWithCustomView:backView];
    [backView release];
    
    [viewController.navItem setLeftBarButtonItem:btn];
    [btn release];
}

#pragma mark - GTParaBoardDelegate
- (void)didClickGW
{
    //如果时需要采集，则跳出GT页面，返回到被测应用
    if ([[GTConfig sharedInstance] gatherSwitch]) {
        [self onCloseWindow:nil];
    }
    
}

@end
#endif
