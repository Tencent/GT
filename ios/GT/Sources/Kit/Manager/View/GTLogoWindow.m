 //
//  GTLogoWindow.m
//  GTKit
//
//  Created   on 13-3-24.
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
#import "GTLogoWindow.h"
#import <QuartzCore/QuartzCore.h>
#import "GTImage.h"
#import "GTDebugDef.h"
#import "GTOutputList.h"

#define M_GT_PERIOD_INTERVAL    6
#define M_GT_WARNING_INTERVAL   0.5
#define M_GT_WARNING_DURATION   3

@interface GTLogoWindow()

@property (nonatomic, retain) NSDate *startAniDate;

@end


@implementation GTLogoWindow

@synthesize isPortrait = _isPortrait;
@synthesize startAniDate;

- (id)initWithFrame:(CGRect)frame delegate:(id)delegate
{
    self = [super initWithFrame:frame];
    if (self) {
        _consoleDelegate = delegate;
        _portraitFrame = frame;
        _showWarning = NO;
        _alertShow = NO;
        _soundID = 0;
        NSString *path = [[NSBundle frameworkBundle] pathForResource:@"gt_alarm" ofType:@"caf"];
        if (path) {
            NSURL *fileUrl = [[NSURL alloc] initFileURLWithPath:path];
            AudioServicesCreateSystemSoundID((CFURLRef)fileUrl, &_soundID);
            [fileUrl release];
        }
        
        [self initLogoUI];
    }
    
    return self;
}

- (void)layoutLogoFrame:(UIInterfaceOrientation)orientation
{
    CGRect screenBound = [UIScreen mainScreen].bounds;
    CGRect frame;
    
    CGFloat offsetX = 0;
    CGFloat offsetY = 44.0f;
    
	frame.origin.x = 0;
	frame.origin.y = 0;
    frame.size.width = M_GT_LOGO_WIDTH;
	frame.size.height = M_GT_LOGO_HEIGHT;
	
    //坐标调整
    if (orientation == UIInterfaceOrientationPortrait) {
        _isPortrait = YES;
        frame.origin.x = screenBound.size.width - frame.size.width - offsetX;
        frame.origin.y = screenBound.size.height - frame.size.height - offsetY;
    } else if (orientation == UIInterfaceOrientationPortraitUpsideDown) {
        _isPortrait = YES;
        frame.origin.x = 0;
        frame.origin.y = offsetY ;
    } else if (orientation == UIInterfaceOrientationLandscapeLeft) {
        _isPortrait = NO;
        frame.origin.x = screenBound.size.width - M_GT_LOGO_WIDTH - offsetY;
        frame.origin.y = 0;
    } else {
        _isPortrait = NO;
        frame.origin.x = offsetY;
        frame.origin.y = screenBound.size.height - frame.size.height;
    }
    
    [self setFrame:frame];
}

-(void)initLogoUI
{
    self.layer.borderWidth = 0;
    self.layer.borderColor = [[UIColor clearColor] CGColor];
    self.layer.cornerRadius = 0;
    self.layer.masksToBounds = NO;
    self.backgroundColor = [UIColor clearColor];
    // for DEBUG
//    self.backgroundColor = [UIColor greenColor];
    self.hidden = NO;
    self.windowLevel = UIWindowLevelStatusBar + 199.0f;
    
    _VC = [[GTRotateBoard alloc] init];
    
    //这里不能直接使用addSubview，否则在ios5上会crash
    if ([self respondsToSelector:@selector(setRootViewController:)]) {
        self.rootViewController = _VC;
    } else {
        [self addSubview:_VC.view];
    }

    
    [_VC setDelegate:self];
    _VC.view.frame = self.bounds;
    _VC.view.backgroundColor = [UIColor clearColor];
    [self layoutLogoFrame:[_VC interfaceOrientation]];
    
    _iconButton = [[UIButton alloc] initWithFrame:self.bounds];
    _iconButton.backgroundColor = [UIColor clearColor];
    _iconButton.adjustsImageWhenHighlighted = YES;
    [_iconButton setImage:[GTImage imageNamed:@"gt_logo" ofType:@"png"] forState:UIControlStateNormal];
    CGFloat offset = (M_GT_LOGO_WIDTH - 36)/2;
    [_iconButton setImageEdgeInsets:UIEdgeInsetsMake(offset, offset, offset, offset)];
    
    [_iconButton addTarget:self action:@selector(onDetailedWindow:) forControlEvents:UIControlEventTouchUpInside];
    [_VC.view addSubview:_iconButton];
    
    UIPanGestureRecognizer *recognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePan:)];
    [recognizer setDelegate:self];
    recognizer.maximumNumberOfTouches = 1;
    [_iconButton addGestureRecognizer:recognizer];
    [recognizer release];
    
    UILongPressGestureRecognizer *longPressGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleLongPressGesture:)];
    [longPressGesture setDelegate:self];
    longPressGesture.minimumPressDuration = 0.5;
    [_iconButton addGestureRecognizer:longPressGesture];
    [longPressGesture release];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleOutWarning:) name:M_GT_NOTIFICATION_OUT_LST_WARNING object:nil];

}

- (void)dealloc
{
    self.startAniDate = nil;
    M_GT_SAFE_FREE(_iconButton);
    if (_soundID) {
        AudioServicesDisposeSystemSoundID(_soundID);
        _soundID = 0;
    }
    [self stopPeriodTimer];
    [self stopAniTimer];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_LST_WARNING object:nil];
    
    [_VC setDelegate:nil];
    [_VC release];
    _VC = nil;
    
    
    [super dealloc];
}


- (void)setLogoFloating:(BOOL)on
{
    if (on) {
        [_iconButton setImage:[GTImage imageNamed:@"gt_logo_ac" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        [_iconButton setImage:[GTImage imageNamed:@"gt_logo" ofType:@"png"] forState:UIControlStateNormal];
    }
    
    if (_showWarning == YES) {
        [_iconButton setImage:[GTImage imageNamed:@"gt_logo_warning" ofType:@"png"] forState:UIControlStateNormal];
    }
}

#pragma mark - 定时器 山雀音播放
- (void)startPeriodTimer
{
    if (_periodTimer == nil) {
        _periodTimer = [[NSTimer alloc] initWithFireDate:[NSDate date] interval:M_GT_PERIOD_INTERVAL target:self selector:@selector(periodTimerNotify:) userInfo:nil repeats:NO];
        [[NSRunLoop mainRunLoop] addTimer:_periodTimer forMode:NSRunLoopCommonModes];
    }
}

- (void)stopPeriodTimer
{
    if (_periodTimer) {
        [_periodTimer invalidate];
        [_periodTimer release];
        _periodTimer = nil;
    }
}

- (void)periodTimerNotify:(id)sender
{
    if (_showWarning) {
        if (_soundID != 0) {
            
            //山雀音
            AudioServicesPlaySystemSound(_soundID);
            
            //震动
            AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
            
        } else {
            //alarm.caf 震动
            AudioServicesPlaySystemSound(1304);
        }
        
        [self startAniTimer];
    } else {
        [self stopPeriodTimer];
    }
}

#pragma mark - 定时器 图标闪动
- (void)startAniTimer
{
    if (_aniTimer == nil) {
        self.startAniDate = [NSDate date];
        
        _aniTimer = [[NSTimer alloc] initWithFireDate:[NSDate date] interval:M_GT_WARNING_INTERVAL target:self selector:@selector(aniTimerNotify:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_aniTimer forMode:NSRunLoopCommonModes];
    }
}

- (void)stopAniTimer
{
    if (_aniTimer) {
        [_aniTimer invalidate];
        [_aniTimer release];
        _aniTimer = nil;
    }
}

- (void)aniTimerNotify:(id)sender
{
    if (_showWarning) {
        NSDate *date = [NSDate date];
        NSTimeInterval interval = [date timeIntervalSinceDate:self.startAniDate];
        if (interval > M_GT_WARNING_DURATION) {
            _iconButton.alpha = 1;
            [_iconButton setImage:[GTImage imageNamed:@"gt_logo_warning" ofType:@"png"] forState:UIControlStateNormal];
            [self stopAniTimer];
            return;
        }
        if (_showSwitch) {
            [_iconButton setImage:[GTImage imageNamed:@"gt_logo_warning" ofType:@"png"] forState:UIControlStateNormal];
        } else {
            [_iconButton setImage:[GTImage imageNamed:@"gt_logo" ofType:@"png"] forState:UIControlStateNormal];
        }
        _showSwitch = !_showSwitch;
    } else {
        [self stopAniTimer];
    }
    
}

#pragma mark - 告警通知处理逻辑
- (void)handleOutWarning:(NSNotification *)n
{
    NSDictionary* dic = [n userInfo];
    
    NSString *result = [dic objectForKey:@"result"];
    
    if ([result isEqualToString:@"warning"]) {
        [self showWarningTip];
    } else {
        [self hideWarningTip];
    }
    
}

- (void)showWarningTip
{
    _showWarning = YES;
    _showSwitch = YES;
    
    //停止当前启动的定时器
    [self stopAniTimer];
    [self stopPeriodTimer];
    
    //重新启动
    [self startPeriodTimer];
    
    [_iconButton setImage:[GTImage imageNamed:@"gt_logo_warning" ofType:@"png"] forState:UIControlStateNormal];
}

- (void)hideWarningTip
{
    _showWarning = NO;
    _iconButton.alpha = 1;
    
    [self stopAniTimer];
    [self stopPeriodTimer];
    
    [_iconButton setImage:[GTImage imageNamed:@"gt_logo" ofType:@"png"] forState:UIControlStateNormal];
}

#pragma mark - 手势处理逻辑
-(void)handlePan:(UIPanGestureRecognizer*)recognizer
{
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        CGPoint pt = [recognizer locationInView:self] ;
        _startPoint = pt;
        
    }
    if ((recognizer.state == UIGestureRecognizerStateChanged) || (recognizer.state == UIGestureRecognizerStateEnded)) {
        CGPoint pt = [recognizer locationInView:self];
        CGRect frame = [self frame];
        frame.origin.y += pt.y - _startPoint.y;
        frame.origin.x += pt.x - _startPoint.x;
        
        CGPoint offset = CGPointMake(pt.x - _startPoint.x, pt.y - _startPoint.y);
        [_consoleDelegate handlePanOffset:offset state:recognizer.state];
    }
}

-(void)handleLongPressGesture:(UILongPressGestureRecognizer *)gR
{
    switch (gR.state)
    {
        case UIGestureRecognizerStateBegan:
        {
            self.userInteractionEnabled = NO;
            [_consoleDelegate onIconACWindow];
            break;
        }
            
        case UIGestureRecognizerStateEnded:
        {
            self.userInteractionEnabled = YES;
        }
            
        case UIGestureRecognizerStateChanged:
        {
            self.userInteractionEnabled = YES;
        }
            
        default:
            break;
    }
}

#pragma mark - ButtonClicked
- (void)onDetailedWindow:(id)sender
{
//    @autoreleasepool {
//        if (_iconButton == sender) {
//            
//        }
//        
//    }
    
    [_consoleDelegate onIconDetailWindow];
    
}


#pragma mark - GTRotateDelegate

- (void)didRotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ((interfaceOrientation == UIInterfaceOrientationLandscapeLeft) || (interfaceOrientation == UIInterfaceOrientationLandscapeRight)) {
        if (_isPortrait) {
            [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.height, self.frame.size.width)];
        }
        _isPortrait = NO;
        
    }
    
    
    if ((interfaceOrientation == UIInterfaceOrientationPortrait) || (interfaceOrientation == UIInterfaceOrientationPortraitUpsideDown)) {
        if (!_isPortrait) {
            [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.height, self.frame.size.width)];
        }
        _isPortrait = YES;
    }
    
    _VC.view.frame = self.bounds;
    if (_consoleDelegate) {
        [_consoleDelegate didRotate:_isPortrait];
    }
}


#pragma mark - GTUIAlertViewDelegate
- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    _alertShow = NO;
}

@end
#endif
