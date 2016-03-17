//
//  GTUIManager.h
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

#import <UIKit/UIKit.h>
#import "GTDebugDef.h"
#import "GTParaInSelectBoard.h"
#import "GTDetailedWindow.h"
#import "GTACWindow.h"
#import "GTLogoWindow.h"

typedef void (* onCallBack)(void); // navy add

@interface GTUIManager : NSObject <UIGestureRecognizerDelegate, GTLogoDelegate,GTACDelegate, GTDetailDelegate, GTParaInSelectDelegate>
{
    BOOL _hidden;

    GTLogoWindow *_logoWindow;
    CGRect        _logoFrame;
    BOOL          _inputExtended;
    
    BOOL              _showDetailWindow;
    GTDetailedWindow *_detailedWindow;
    NSUInteger        _detailedIndex;
    
    
    GTACWindow      *_acWindow;
    BOOL            _acFrameBackup;
    CGRect          _acFrame;
    
    
    UIWindow *      _editWindow;
    GTParaInSelectBoard * _board;
    UIWindow *      _textWindow;
}

M_GT_AS_SINGLETION(GTUIManager)


@property (nonatomic, assign) BOOL hidden;
@property (nonatomic, assign) NSUInteger detailedIndex;
@property (nonatomic, assign) BOOL inputExtended;

// navy add，定义Logo图标打开和关闭的回调
@property (nonatomic, assign) onCallBack onOpenCallBack;
@property (nonatomic, assign) onCallBack onCloseCallBack;
// navy add, 定义变量临时存储shouldAutorotate状态
@property (nonatomic, assign) BOOL shouldAutorotate;


- (void)setGTHidden:(BOOL)hidden;

@end

#endif
