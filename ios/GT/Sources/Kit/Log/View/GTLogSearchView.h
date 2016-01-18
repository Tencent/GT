//
//  GTLogSearchView.h
//  GTKit
//
//  Created   on 13-11-17.
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
#import "GTUITextField.h"
#import "GTList.h"


@protocol GTLogSearchDelegate

- (void)updateContent:(NSString *)content;

@end

@interface GTLogSearchHistory : GTList

M_GT_AS_SINGLETION( GTLogSearchHistory );

@end


@interface GTLogSearchView : UIView <UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate>
{
    UIView          *_searchView;
    GTUITextField   *_filterField;
    UIButton        *_btnCancel;
    NSString        *_selContent;
    NSMutableArray  *_matchValues;
    
    id <GTLogSearchDelegate> _delegate;
    UITableView     *_tableView;
}
@property (nonatomic, retain) NSString *selContent;
@property (nonatomic, retain) NSMutableArray *matchValues;
@property (nonatomic, assign) UIViewController *viewController;
@property (nonatomic, assign) id<GTLogSearchDelegate> delegate;

@property (nonatomic, retain) UITableView *tableView;

- (id)initWithFrame:(CGRect)frame viewController:(UIViewController*) givenViewController;

@end

#endif
