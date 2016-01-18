//
//  GTACWindow.h
//  GTKit
//
//  Created   on 12-4-19.
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
#import <QuartzCore/QuartzCore.h>
#import "GTLog.h"
#import <SystemConfiguration/SystemConfiguration.h>
#import "GTCommonCell.h"
#import "GTTabBar.h"
#import "GTInputObject.h"
#import "GTRotateBoard.h"

@interface GTInputBar : GTTabBar
{
    NSMutableArray *_titles;
    NSMutableArray *_values;
}

@property (nonatomic, retain) NSMutableArray *titles;
@property (nonatomic, retain) NSMutableArray *values;

- (id)initWithFrame:(CGRect)frame inputLists:(NSMutableArray*)list;

@end


@interface GTACOutputCell : GTCommonCell
{
	UILabel *		_title;
	UILabel *		_value;
}

- (void)cellLayoutHeight;
- (void)cellLayoutFirst;

@end

@protocol GTACDelegate <NSObject>

-(void)handlePanOffset:(CGPoint)offset state:(UIGestureRecognizerState)state;
-(void)onACEditWindow:(GTInputObject *)obj;
-(void)onACAdjust:(CGFloat)heightOffset;
-(void)didRotate:(BOOL)isPortrait;

@end

@interface GTACWindow : UIWindow <UITableViewDataSource, UITableViewDelegate, UIGestureRecognizerDelegate, GTTabBarDelegate, GTRotateDelegate>
{
    GTRotateBoard  *_VC;
    BOOL            _isPortrait;
    CGFloat         _width;
    
    UIImageView *_headerView;
    UIButton    *_watch;
    UILabel     *_switchInfo;
    UIButton    *_acSwitch;
    
    UITableView *_tableView;
    NSIndexPath *_selectIndexPath;
    BOOL         _adjustHeight;
    UIButton    *_editIcon;
    GTInputBar  *_bar;
    NSUInteger   _selectedIndex;
    UIImageView *_extendView;
    UIButton    *_extendBtn;
    
    CGPoint      _startPoint;
    NSTimer     *_updateTimer;
    NSTimer     *_watchTimer;
    NSTimer     *_aniTimer;
    UIPanGestureRecognizer * _floatingRecognizer;
    id<GTACDelegate> _consoleDelegate;
    BOOL        _isFirst;
    
    CGFloat  _height;
}

@property (nonatomic) BOOL isPortrait;
@property (nonatomic) CGFloat height;
@property (nonatomic, retain) UITableView * tableView;
@property (nonatomic, retain) NSIndexPath * selectIndexPath;

- (id)initWithFrame:(CGRect)frame delegate:(id)delegate;
- (void)stopTimer;

- (void)updateUI;
- (void)layoutView;

@end

#endif
