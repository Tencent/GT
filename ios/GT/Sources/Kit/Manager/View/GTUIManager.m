//
//  GTUIManager.m
//  GTKit
//
//  Created   on 12-10-11.
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

#import "GTUIManager.h"
#import <QuartzCore/QuartzCore.h>
#import "GTUtility.h"
#import "GTImage.h"
#import "GTInputList.h"
#import "GTDetailView.h"
#import "GTUINavigationController.h"
#import "GTConfig.h"
#import "GTOutputList.h"


@implementation GTUIManager


#pragma mark - GTDebugShortcut

M_GT_DEF_SINGLETION(GTUIManager);

@synthesize hidden = _hidden;
@synthesize detailedIndex = _detailedIndex;
@synthesize inputExtended = _inputExtended;

@synthesize onOpenCallBack = _onOpenCallBack;
@synthesize onCloseCallBack = _onCloseCallBack;
@synthesize shouldAutorotate = _shouldAutorotate;

- (id)init
{
//	CGRect screenBound = [UIScreen mainScreen].bounds;
    CGRect screenBound = [[UIScreen mainScreen] fullScreenBounds];
	
    CGRect shortcutFrame;
	shortcutFrame.size.width = M_GT_LOGO_WIDTH;
	shortcutFrame.size.height = M_GT_LOGO_HEIGHT;
	shortcutFrame.origin.x = CGRectGetMaxX(screenBound) - shortcutFrame.size.width;
	shortcutFrame.origin.y = CGRectGetMaxY(screenBound) - shortcutFrame.size.height - M_GT_LOGO_HEIGHT;
    
	self = [super init];
	if ( self )
	{
        _detailedWindow = nil;
        _acWindow = nil;
        
        _detailedIndex = 0;
        
        _inputExtended = YES;
        
        _hidden = NO;
        _logoWindow = [[GTLogoWindow alloc] initWithFrame:shortcutFrame delegate:self];
        
        
	}
    
	return self;
}



-(void)dealloc
{
    
    M_GT_SAFE_FREE(_detailedWindow);
    
    [self releaseAcWindow];
    
    [super dealloc];
}

- (void)releaseAcWindow
{
    
    if (_acWindow) {
        [_acWindow stopTimer];
        [_acWindow release];
        _acWindow = nil;
    }
}


#pragma mark -

-(void)closeFloatingWindow
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
    [UIView setAnimationDelay:0.3f];
    [UIView setAnimationDelegate:self];
    
    [_acWindow setFrame:CGRectMake(_logoWindow.frame.origin.x + M_GT_LOGO_WIDTH/2, _logoWindow.frame.origin.y + M_GT_LOGO_HEIGHT/2, 0, 0)];
    
    [UIView commitAnimations];
    
    [self releaseAcWindow];
}


#pragma mark - GTDetailDelegate

-(void)onDetailedClose
{
    //电池条恢复应用中的状态
    [UIApplication sharedApplication].statusBarHidden = [[GTConfig sharedInstance] appStatusBarHidden];
    [[[GTConfig sharedInstance] appKeyWindow] makeKeyWindow];
    [[GTConfig sharedInstance] setAppKeyWindow:nil];
    
    if (_detailedWindow) {
        //释放内存没清理彻底，暂时修改为隐藏方式
        _detailedWindow.hidden = YES;
        [_detailedWindow release];
        _detailedWindow = nil;
    }
    
    [_logoWindow setHidden:NO];
    //恢复logo位置
    //防止窗口大小被修改
    [_logoWindow setFrame:CGRectMake(_logoFrame.origin.x, _logoFrame.origin.y, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
//    [_logoWindow setFrame:_logoFrame];
    
    //对于没有开始采集操作，且有采集项和用户有勾选行为但时，GT自动展示AC，用于用户便捷的启动GW
    if (![[GTConfig sharedInstance] gatherSwitch] && [[GTConfig sharedInstance] userClicked]
        && [[GTOutputList sharedInstance] hasItemHistoryOn]) {
        [[GTConfig sharedInstance] setShowAC:YES];
    }
    
    BOOL showAC = [[GTConfig sharedInstance] showAC];
    if (showAC) {
        [self switchFloating:YES];
        //恢复floating位置
        if (_acFrameBackup) {
            //防止窗口大小被修改
            [_acWindow setFrame:CGRectMake(_acFrame.origin.x, _acFrame.origin.y, _acWindow.frame.size.width, [_acWindow height])];
//            [_acWindow setFrame:CGRectMake(_acFrame.origin.x, _acFrame.origin.y, _acWindow.frame.size.width, _acWindow.frame.size.height)];
        }
        
    } else {
        [self switchFloating:NO];
    }
    
    // navy add，在关闭后，恢复状态栏方向，必须在[self layoutFrame]之前设置，不然退出后，logo图标就会消失在屏幕中
    if ([GTConfig sharedInstance].supportedInterfaceOrientations & UIInterfaceOrientationMaskLandscape) { // 如果支持横屏
        [[UIApplication sharedApplication] setStatusBarOrientation:[GTConfig sharedInstance].appStatusBarOrientation]; // 恢复当前状态栏方向
        [[GTConfig sharedInstance] setShouldAutorotate:self.shouldAutorotate]; // 设置完方向后，恢复Rotate设置
        if (self.onCloseCallBack) {
            self.onCloseCallBack(); // 调用关闭回调函数，回调函数里需要设置用户的rootViewController的shouldAutorotate恢复默认返回值
        }
    }
    
    [self layoutFrame];
}

#pragma mark - GTLogoDelegate

-(void)switchFloating:(BOOL)showAC
{
    [_logoWindow setLogoFloating:showAC];
    if (showAC) {
        if (_acWindow == nil) {
            
        }
        CGFloat width = 160.0f;
        CGFloat height = 160.0f;
        _acWindow = [[GTACWindow alloc] initWithFrame:CGRectMake(0, 0, width, height) delegate:self];
        [self layoutFloatingFrame];
    } else {
        //关闭悬浮框
        [self closeFloatingWindow];
    }
}

- (void)onIconACWindow
{
    BOOL showAC = [[GTConfig sharedInstance] showAC];
    if (showAC) {
        [[GTConfig sharedInstance] setShowAC:NO];
        [self switchFloating:NO];
        [self layoutFrame];
    } else {
        [[GTConfig sharedInstance] setShowAC:YES];
        [self switchFloating:YES];
    }
    
}

- (void)onIconDetailWindow
{
    // navy add，打开后保存状态栏方向，在横屏时，需要强制设置为竖屏来显示GTDetailedWindow
    if ([GTConfig sharedInstance].supportedInterfaceOrientations & UIInterfaceOrientationMaskLandscape) { // 如果支持横屏
        self.shouldAutorotate = [GTConfig sharedInstance].shouldAutorotate;
        UIInterfaceOrientation statusBarOrientation = [UIApplication sharedApplication].statusBarOrientation;
        if (statusBarOrientation == UIInterfaceOrientationLandscapeLeft || statusBarOrientation == UIInterfaceOrientationLandscapeRight) {
            if (self.onOpenCallBack) {
                self.onOpenCallBack(); // 调用打开回调函数，回调函数里需要设置用户的rootViewController的shouldAutorotate返回NO
            }
            // 保存当前状态栏方向，以便退出后恢复
            [[GTConfig sharedInstance] setAppStatusBarOrientation:[UIApplication sharedApplication].statusBarOrientation];
            
            [[GTConfig sharedInstance] setShouldAutorotate:NO]; // 设置为NO，禁止旋转才能设置状态栏方向
            [[UIApplication sharedApplication] setStatusBarOrientation:UIInterfaceOrientationPortrait]; // 默认只支持Portrait，UI显示才正常
        }
    }

    //记录进入前的状态
    [[GTConfig sharedInstance] setAppStatusBarHidden:[UIApplication sharedApplication].statusBarHidden];
    [[GTConfig sharedInstance] setAppKeyWindow:[[UIApplication sharedApplication] keyWindow]];
    
    //记录logo位置
    _logoFrame = _logoWindow.frame;
    _acFrameBackup = YES;
    _acFrame = _acWindow.frame;
    if (_acWindow == nil) {
        _acFrameBackup = NO;
    }
    
    [_logoWindow setHidden:YES];
    
    [self switchFloating:NO];
    
    //清除用户已有点击行为
    [[GTConfig sharedInstance] setUserClicked:NO];
    
    if (_detailedWindow == nil) {
        if ([[GTUtility sharedInstance] systemVersion] >= 7) {
//            _detailedWindow = [[GTDetailedWindow alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame] boardIndex:_detailedIndex delegate:self];
            _detailedWindow = [[GTDetailedWindow alloc] initWithFrame:[[UIScreen mainScreen] screenBounds] boardIndex:_detailedIndex delegate:self]; // navy modified

        } else {
//            _detailedWindow = [[GTDetailedWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds] boardIndex:_detailedIndex delegate:self];
            _detailedWindow = [[GTDetailedWindow alloc] initWithFrame:[[UIScreen mainScreen] fullScreenBounds] boardIndex:_detailedIndex delegate:self]; // navy modified
        }
    }
    _detailedWindow.hidden = NO;
    [_detailedWindow makeKeyAndVisible];
}

#pragma mark - GTFloatingDelegate

-(void)onACAdjust:(CGFloat)heightOffset
{
    [_logoWindow setFrame:CGRectMake(_logoWindow.frame.origin.x, _logoWindow.frame.origin.y + heightOffset, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
}



-(void)onACEditWindow:(GTInputObject *)obj
{
    //记录进入前的状态
    [[GTConfig sharedInstance] setAppStatusBarHidden:[UIApplication sharedApplication].statusBarHidden];
    [[GTConfig sharedInstance] setAppKeyWindow:[[UIApplication sharedApplication] keyWindow]];

    //记录logo位置
    _logoFrame = _logoWindow.frame;
    _acFrame = _acWindow.frame;
    
    [_logoWindow setHidden:YES];
    [self switchFloating:NO];
    
//    CGRect screenBound = [UIScreen mainScreen].bounds;
    CGRect screenBound = [[UIScreen mainScreen] fullScreenBounds]; // navy modified
    _editWindow = [[UIWindow alloc] initWithFrame:screenBound];
    _editWindow.windowLevel = UIWindowLevelStatusBar + 200.0f;
    _editWindow.backgroundColor = [UIColor clearColor];
    
    GTParaInSelectBoard * board = [[GTParaInSelectBoard alloc] init];
    if ( board )
    {
        [board bindData:obj];
        [board setDelegate:self];
        GTUINavigationController*  navController;
        navController = [[GTUINavigationController alloc] initWithRootViewController:board];
        navController.navigationBar.barStyle = UIBarStyleBlackOpaque;
        
        UIBarButtonItem * btn;
        btn = [[[UIBarButtonItem alloc] initWithTitle:@"返回"
                                                style:UIBarButtonItemStylePlain
                                               target:self
                                               action:@selector(didLeftBarButtonTouched)] autorelease];
        board.navigationItem.leftBarButtonItem = btn;
        
        if ([_editWindow respondsToSelector:@selector(setRootViewController:)]) {
            _editWindow.rootViewController = navController;
        } else {
            
        }
        [_editWindow addSubview:navController.view];
        [_editWindow makeKeyAndVisible];
        
        _board = board;
        [navController release];
    }
    
}

-(void)didRotate:(BOOL)isPortrait
{
    [self layoutFrame];
}

#pragma mark - GTParaInSelectDelegate

- (void)onParaInSelectCancel
{
    [self onParaInSelectOK];
}

- (void)onParaInSelectOK
{
    [_board closeKeyBoard];
    
    M_GT_SAFE_FREE(_board);
    M_GT_SAFE_FREE(_editWindow);
    
    [self onDetailedClose];
}


#pragma mark - UIGestureRecognizerDelegate

- (void)handlePanOffset:(CGPoint)offset state:(UIGestureRecognizerState)state
{
    CGRect frame;
    frame = CGRectOffset(_acWindow.frame, offset.x, offset.y);
    
    //防止窗口大小被修改
    [_acWindow setFrame:CGRectMake(frame.origin.x, frame.origin.y, frame.size.width, [_acWindow height])];
//    [_acWindow setFrame:frame];
    
    frame = CGRectOffset(_logoWindow.frame, offset.x, offset.y);
    //防止窗口大小被修改
    [_logoWindow setFrame:CGRectMake(frame.origin.x, frame.origin.y, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
//    [_logoWindow setFrame:frame];

    if (state == UIGestureRecognizerStateEnded) {
        [self layoutFrame];
    }
}

#define M_GT_EDAGE_HEIGHT 44
- (void)layoutLogoFrame
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
    [UIView setAnimationDelay:0.3f];
    [UIView setAnimationDelegate:self];
    //    [UIView setAnimationDidStopSelector:@selector(Shortcut_animationDidStop:finished:context:)];
    
    CGRect frame = _logoWindow.frame;
    
    if ([_logoWindow isPortrait]) {
        if (frame.origin.y < M_GT_EDAGE_HEIGHT)
        {
            frame.origin.y = M_GT_EDAGE_HEIGHT;
        }
        
        if (frame.origin.y > [UIScreen mainScreen].bounds.size.height - M_GT_LOGO_HEIGHT - M_GT_EDAGE_HEIGHT )
        {
            frame.origin.y = [UIScreen mainScreen].bounds.size.height - M_GT_LOGO_HEIGHT - M_GT_EDAGE_HEIGHT;
        }
        
        if (frame.origin.x  < (([UIScreen mainScreen].bounds.size.width - M_GT_LOGO_HEIGHT)/2) ) {
            frame.origin.x = 0.0f;
        } else {
            frame.origin.x = [UIScreen mainScreen].bounds.size.width - M_GT_LOGO_WIDTH;
        }
    } else {
        if (frame.origin.x < M_GT_EDAGE_HEIGHT)
        {
            frame.origin.x = M_GT_EDAGE_HEIGHT;
        }
        
        if (frame.origin.x > [UIScreen mainScreen].bounds.size.width - M_GT_LOGO_WIDTH - M_GT_EDAGE_HEIGHT )
        {
            frame.origin.x = [UIScreen mainScreen].bounds.size.width - M_GT_LOGO_WIDTH - M_GT_EDAGE_HEIGHT;
        }
        
        if (frame.origin.y  < (([UIScreen mainScreen].bounds.size.height - M_GT_LOGO_HEIGHT)/2) ) {
            frame.origin.y = 0.0f;
        } else {
            frame.origin.y = [UIScreen mainScreen].bounds.size.height - M_GT_LOGO_HEIGHT;
        }
    }
    
    //防止窗口大小被修改
    [_logoWindow setFrame:CGRectMake(frame.origin.x, frame.origin.y, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
//    [_logoWindow setFrame:frame];
    
    [UIView commitAnimations];
}

- (void)layoutFloatingFrame
{
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat width = _acWindow.frame.size.width;
    CGFloat height = _acWindow.frame.size.height;
    CGRect frame = _acWindow.frame;
    
    [_acWindow setFrame:CGRectMake(_logoWindow.frame.origin.x + M_GT_LOGO_WIDTH/2, _logoWindow.frame.origin.y + M_GT_LOGO_HEIGHT/2, 0, 0)];
    
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
    [UIView setAnimationDelay:0.3f];
    [UIView setAnimationDelegate:self];
    
    if ([_logoWindow isPortrait]) {
        //在左上角
        frame.origin.x = _logoWindow.frame.origin.x - width + M_GT_IN_AC_WIDTH;
        frame.origin.y = _logoWindow.frame.origin.y - height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET;
        
        //logo右下角有空间
        x = _logoWindow.frame.origin.x + M_GT_LOGO_WIDTH - M_GT_IN_AC_WIDTH;
        y = _logoWindow.frame.origin.y + M_GT_LOGO_HEIGHT - M_GT_IN_AC_HEIGHT - M_GT_IN_AC_OFFSET;
        if ((x + width <= M_GT_SCREEN_WIDTH) && (y + height <= M_GT_SCREEN_HEIGHT)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        //logo左下角有空间
        x = _logoWindow.frame.origin.x - width + M_GT_IN_AC_WIDTH;
        y = _logoWindow.frame.origin.y + M_GT_LOGO_HEIGHT - M_GT_IN_AC_HEIGHT - M_GT_IN_AC_OFFSET;
        if ((x >= 0) && (y + height <= M_GT_SCREEN_HEIGHT)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        //logo右上角有空间
        x = _logoWindow.frame.origin.x + M_GT_LOGO_WIDTH - M_GT_IN_AC_WIDTH;
        y = _logoWindow.frame.origin.y - height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET;
        if ((x + width <= M_GT_SCREEN_WIDTH) && (y >= 0)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        //logo左上角有空间
        x = _logoWindow.frame.origin.x - width + M_GT_IN_AC_WIDTH;
        y = _logoWindow.frame.origin.y - height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET;
        if ((x >= 0) && (y >= 0)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
    } else {
        [_acWindow setIsPortrait:[_logoWindow isPortrait]];
        
        frame.size.height = width;
        frame.size.width = height;
        
        //在左下角
        frame.origin.x = _logoWindow.frame.origin.x - height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET;
        frame.origin.y = _logoWindow.frame.origin.y + M_GT_LOGO_WIDTH - M_GT_IN_AC_WIDTH;
        
        //logo右上角有空间
        x = _logoWindow.frame.origin.x + M_GT_LOGO_WIDTH - M_GT_IN_AC_WIDTH - M_GT_IN_AC_OFFSET;
        y = _logoWindow.frame.origin.y - width + M_GT_IN_AC_HEIGHT;
        if ((x + height <= M_GT_SCREEN_WIDTH) && (y >= 0)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        //logo右下角有空间
        x = _logoWindow.frame.origin.x + M_GT_LOGO_WIDTH - M_GT_IN_AC_WIDTH - M_GT_IN_AC_OFFSET;
        y = _logoWindow.frame.origin.y + M_GT_LOGO_HEIGHT - M_GT_IN_AC_HEIGHT;
        if ((x + height <= M_GT_SCREEN_WIDTH) && (y + width <= M_GT_SCREEN_HEIGHT)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        //logo左上角有空间
        x = _logoWindow.frame.origin.x - height + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET;
        y = _logoWindow.frame.origin.y - width + M_GT_IN_AC_HEIGHT;
        if ((x >= 0) && (y >= 0)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        //logo左下角有空间
        x = _logoWindow.frame.origin.x - height + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET;
        y = _logoWindow.frame.origin.y + M_GT_LOGO_HEIGHT - M_GT_IN_AC_HEIGHT;
        if ((x >= 0) && (y + width <= M_GT_SCREEN_HEIGHT)) {
            frame.origin.x = x;
            frame.origin.y = y;
        }
        
        
    }
    
    //防止窗口大小被修改
    [_acWindow setFrame:CGRectMake(frame.origin.x, frame.origin.y, frame.size.width, [_acWindow height])];
//    [_acWindow setFrame:frame];
    
    [UIView commitAnimations];
}

- (void)layoutLogoWithFloatingFrame
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
    [UIView setAnimationDelay:0.3f];
    [UIView setAnimationDelegate:self];
    
    CGRect frame = _logoWindow.frame;
    CGRect floatingFrame = _acWindow.frame;
       
    if ([_logoWindow isPortrait]) {
        //左上角够空间
        if ((floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH >= 0 )
            && (floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET >= 0))
        {
            frame.origin.x = floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH;
            frame.origin.y = floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET;
        }
        
        
        //右上角够空间
        if ((floatingFrame.origin.x + floatingFrame.size.width + frame.size.width <= M_GT_SCREEN_WIDTH + M_GT_IN_AC_WIDTH)
            && (floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET >= 0))
        {
            frame.origin.x = floatingFrame.origin.x + floatingFrame.size.width - M_GT_IN_AC_WIDTH;
            frame.origin.y = floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET;
        }
        
        
        //左下角够空间
        if ((floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH >= 0 )
            && (floatingFrame.origin.y + floatingFrame.size.height + frame.size.height <= M_GT_SCREEN_HEIGHT + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET))
        {
            frame.origin.x = floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH;
            frame.origin.y = floatingFrame.origin.y + floatingFrame.size.height - M_GT_IN_AC_HEIGHT - M_GT_IN_AC_OFFSET;
        }
        
        
        //右下角够空间
        if ((floatingFrame.origin.x + floatingFrame.size.width + frame.size.width <= M_GT_SCREEN_WIDTH + M_GT_IN_AC_WIDTH)
            && (floatingFrame.origin.y + floatingFrame.size.height + frame.size.height <= M_GT_SCREEN_HEIGHT + M_GT_IN_AC_HEIGHT + M_GT_IN_AC_OFFSET))
        {
            frame.origin.x = floatingFrame.origin.x + floatingFrame.size.width - M_GT_IN_AC_WIDTH;
            frame.origin.y = floatingFrame.origin.y + floatingFrame.size.height - M_GT_IN_AC_HEIGHT - M_GT_IN_AC_OFFSET;
        }
    } else {

        //左下角够空间
        if ((floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET >= 0 )
            && (floatingFrame.origin.y + floatingFrame.size.height + frame.size.height <= M_GT_SCREEN_HEIGHT + M_GT_IN_AC_WIDTH))
        {
            frame.origin.x = floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET;
            frame.origin.y = floatingFrame.origin.y + floatingFrame.size.height - M_GT_IN_AC_HEIGHT;
        }
        
        //左上角够空间
        if ((floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET >= 0 )
            && (floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT >= 0))
        {
            frame.origin.x = floatingFrame.origin.x - frame.size.width + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET;
            frame.origin.y = floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT;
        }
        
        //右下角够空间
        if ((floatingFrame.origin.x + floatingFrame.size.width + frame.size.width <= M_GT_SCREEN_WIDTH + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET)
            && (floatingFrame.origin.y + floatingFrame.size.height + frame.size.height <= M_GT_SCREEN_HEIGHT + M_GT_IN_AC_HEIGHT))
        {
            frame.origin.x = floatingFrame.origin.x + floatingFrame.size.width - M_GT_IN_AC_WIDTH - M_GT_IN_AC_OFFSET;
            frame.origin.y = floatingFrame.origin.y + floatingFrame.size.height - M_GT_IN_AC_HEIGHT;
        }
        
        //右上角够空间
        if ((floatingFrame.origin.x + floatingFrame.size.width + frame.size.width <= M_GT_SCREEN_WIDTH + M_GT_IN_AC_WIDTH + M_GT_IN_AC_OFFSET)
            && (floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT >= 0))
        {
            frame.origin.x = floatingFrame.origin.x + floatingFrame.size.width - M_GT_IN_AC_WIDTH - M_GT_IN_AC_OFFSET;
            frame.origin.y = floatingFrame.origin.y - frame.size.height + M_GT_IN_AC_HEIGHT;
        }
        
    }
    
    //防止窗口大小被修改
    [_logoWindow setFrame:CGRectMake(frame.origin.x, frame.origin.y, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
//    [_logoWindow setFrame:frame];
    
    [UIView commitAnimations];
}

- (void)layoutFrame
{
    BOOL showAC = [[GTConfig sharedInstance] showAC];
    if (showAC) {
        [self layoutLogoWithFloatingFrame];
        return;
    }
    
    [self layoutLogoFrame];
}



- (void)setGTHidden:(BOOL)hidden
{
    _hidden = hidden;
    
    [_logoWindow setHidden:hidden];
    
    if (hidden == YES) {
        BOOL showAC = [[GTConfig sharedInstance] showAC];
        if (showAC) {
            [[GTConfig sharedInstance] setShowAC:NO];
            [self switchFloating:NO];
            [self layoutFrame];
        }
    }
}


- (BOOL)getGTHidden
{
	return _hidden;
}

- (void)closeDetailedWindow
{
    [_detailedWindow onCloseWindow:nil];
}


- (void)setLogoFrame:(CGRect)frame
{
    //防止窗口大小被修改
    [_logoWindow setFrame:CGRectMake(frame.origin.x, frame.origin.y, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
//    [_logoWindow setFrame:frame];
    
    [self layoutFrame];
}
@end

#pragma mark - User Interface

void func_setLogoPoint(float x, float y)
{
    [[GTUIManager sharedInstance] setLogoFrame:CGRectMake(x, y, M_GT_LOGO_WIDTH, M_GT_LOGO_HEIGHT)];
}

void func_showGTAC()
{
    if ([[GTConfig sharedInstance] useGT]) {
        BOOL showAC = [[GTConfig sharedInstance] showAC];
        if (!showAC) {
            [[GTUIManager sharedInstance] onIconACWindow];
        }
    }
    
}

void func_hideGTAC()
{
    if ([[GTConfig sharedInstance] useGT]) {
        BOOL showAC = [[GTConfig sharedInstance] showAC];
        if (showAC) {
            [[GTUIManager sharedInstance] onIconACWindow];
        }
    }
    
}

void func_closeGTDetail()
{
    if ([[GTConfig sharedInstance] useGT]) {
        [[GTUIManager sharedInstance] closeDetailedWindow];
    }
    
}

void func_setLogoCallBack(void(* onOpenCallBack)(void), void(* onCloseCallBack)(void))
{
    [GTUIManager sharedInstance].onOpenCallBack = onOpenCallBack;
    [GTUIManager sharedInstance].onCloseCallBack = onCloseCallBack;
}

#endif
