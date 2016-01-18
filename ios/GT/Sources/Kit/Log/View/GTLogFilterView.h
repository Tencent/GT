//
//  GTLogFilterView.h
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
#import "GTPopoverController.h"
#import "GTPopoverTableView.h"
#import "GTPopoverSearchView.h"
#import "GTUITextField.h"
#import "GTList.h"

@protocol GTLogFilterDelegate

- (void)updateContent:(NSString *)content;
- (void)updateLevel:(NSString *)level;
- (void)updateTag:(NSString *)tag;
- (NSMutableArray *)logArray;

@end


@interface GTLogFilterHistory : GTList

M_GT_AS_SINGLETION( GTLogFilterHistory );

@end


@interface GTLogFilterView : UIView <UITextFieldDelegate, GTPopoverTableViewDelegate, GTPopoverControllerDelegate, UITableViewDataSource, UITableViewDelegate>
{
    UIView      *_filterView;
    CGRect       _filterFrame;
    BOOL         _filtering;
    
    GTPopoverController *_popVC;
    
    GTUITextField *_filterField;
    UIButton    *_btnCancel;
    UITableView *_tableView;
    NSMutableArray *_matchValues;
    GTPopoverSearchView *_popText;
    
    UIButton    *_btnLevel;
    GTPopoverTableView  *_popLevel;
    
    UIButton    *_btnTag;
    GTPopoverTableView  *_popTag;
    
    UIButton    *_btnSearch;
    
    NSString *_selContent;
    NSArray  *_contents;
    
    NSArray *_levels;
    NSString *_selLevel;
    
    NSArray *_tags;
    NSString *_selTag;
    
    id <GTLogFilterDelegate> _delegate;
}

@property (nonatomic) BOOL filtering;
@property (nonatomic, retain) NSString *selContent;
@property (nonatomic, retain) NSString *selLevel;
@property (nonatomic, retain) NSString *selTag;
@property (nonatomic, retain) NSArray  *tags;
@property (nonatomic, retain) NSMutableArray *matchValues;

@property (nonatomic, assign) UIViewController *viewController;
@property (nonatomic, assign) id<GTLogFilterDelegate> delegate;

@property (nonatomic, retain) UITableView *tableView;

- (id)initWithFrame:(CGRect)frame viewController:(UIViewController*) givenViewController;
- (void)updateTag:(NSString *)tag;

@end


#endif
