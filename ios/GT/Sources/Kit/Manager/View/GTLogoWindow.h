//
//  GTLogoWindow.h
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
#import <UIKit/UIKit.h>
#import "GTRotateBoard.h"
#import <AudioToolbox/AudioToolbox.h>
#import "GTUIAlertView.h"

#define M_GT_LOGO_WIDTH     (50.0f)
#define M_GT_LOGO_HEIGHT    (50.0f)
#define M_GT_IN_AC_OFFSET   (10.0f)
#define M_GT_IN_AC_HEIGHT   (0.0f)
#define M_GT_IN_AC_WIDTH    (0.0f)


@protocol GTLogoDelegate <NSObject>

-(void)handlePanOffset:(CGPoint)offset state:(UIGestureRecognizerState)state;
-(void)onIconDetailWindow;
-(void)onIconACWindow;
-(void)didRotate:(BOOL)isPortrait;

@end

@interface GTLogoWindow : UIWindow <UIGestureRecognizerDelegate, GTRotateDelegate, GTUIAlertViewDelegate>
{
    GTRotateBoard  *_VC;
    BOOL            _isPortrait;
    BOOL            _showWarning;
    CGRect          _portraitFrame;
    UIButton       *_iconButton;
    NSTimer        *_periodTimer;
    NSTimer        *_aniTimer;
    SystemSoundID   _soundID;
    CGPoint         _startPoint;
    id<GTLogoDelegate> _consoleDelegate;
    BOOL             _showSwitch;
    
    BOOL             _alertShow;
}

@property (nonatomic) BOOL isPortrait;

- (id)initWithFrame:(CGRect)frame delegate:(id)delegate;
- (void)setLogoFloating:(BOOL)on;

@end
#endif
