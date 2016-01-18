//
//  GTLogBoard.h
//  GTKit
//
//  Created   on 12-12-7.
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
#import "GTUIViewController.h"
#import "GTList.h"
#import "GTVerticalScrollBar.h"
#import "GTAccessoryView.h"
#import "GTUITableView.h"
#import "GTUITextField.h"
#import "GTLogFilterView.h"


@interface GTLogBoard : GTUIViewController <UITableViewDataSource, UITableViewDelegate,GTLogFilterDelegate, GTUITableViewTouchesDelegate>
{
    NSString *_selContent;
    NSString *_selLevel;
    NSString *_selTag;
    
    GTLogFilterView *_filterView;
    BOOL             _filterShow;
    GTUITableView     *_tableView;
    GTVerticalScrollBar *_verticalScrollBar;
    GTAccessoryView *_accessoryView;
    BOOL            _isFingerTouched;
    NSTimer         *_timer;
    int              _lastPosition;
    int              _sumOffset;
    NSDate          *_lastDate;
    
    UIButton        *_btnToTop;
    UIButton        *_btnToBottom;
    
    NSMutableArray  *_array;
    
    BOOL             _isBottom;
}

@property (nonatomic, retain) GTUITableView *tableView;
@property (nonatomic, retain) NSString *selContent;
@property (nonatomic, retain) NSString *selLevel;
@property (nonatomic, retain) NSString *selTag;
@property (nonatomic, retain) NSMutableArray *array;
@property (nonatomic, retain) NSDate    *lastDate;

@end
#endif
