//
//  GTLogSearchView.m
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
#import "GTLogSearchView.h"
#import "GTLog.h"
#import "GTConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"



@implementation GTLogSearchHistory

M_GT_DEF_SINGLETION(GTLogSearchHistory)

@end



@implementation GTLogSearchView

@synthesize selContent = _selContent;
@synthesize matchValues = _matchValues;
@synthesize viewController;
@synthesize delegate = _delegate;
@synthesize tableView = _tableView;

- (id)initWithFrame:(CGRect)frame viewController:(UIViewController*)givenViewController
{
    self = [super initWithFrame:frame];
    if (self) {
        self.viewController = givenViewController;
        [self load];
        [self initUI];
    }
    
    return self;
}


- (void)dealloc
{
    [self unload];
    [super dealloc];
}

- (void)load
{
    _selContent = @"";
}

- (void)unload
{
    M_GT_SAFE_FREE(_filterField);
    M_GT_SAFE_FREE(_tableView);
    M_GT_SAFE_FREE(_btnCancel);
    M_GT_SAFE_FREE(_searchView);
    self.delegate = nil;
}

- (void)initUI
{
    CGFloat height = self.bounds.size.height - 2 * 3;
    CGFloat width = self.bounds.size.width - 2 * 5;
    CGRect frame;
    
    self.backgroundColor = [UIColor clearColor];
    
    _searchView = [[UIView alloc] initWithFrame:self.bounds];
    _searchView.backgroundColor = M_GT_CELL_BKGD_COLOR;
    [self addSubview:_searchView];
    
    frame = CGRectMake(5.0, 3.0, width - 65 - 5, height);
    
    _filterField = [[GTUITextField alloc] initWithFrame:frame];
    _filterField.placeholder = M_GT_LOCALSTRING(M_GT_LOG_SEARCH_KW_INFO_KEY);
    _filterField.textColor = [UIColor whiteColor];
    _filterField.backgroundColor = [UIColor blackColor];
    _filterField.font = [UIFont systemFontOfSize:12];
    _filterField.clearButtonMode = UITextFieldViewModeAlways;
    _filterField.rightViewMode = UITextFieldViewModeAlways;
    _filterField.delegate = self;
    _filterField.autocorrectionType = UITextAutocorrectionTypeNo;
    _filterField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _filterField.returnKeyType = UIReturnKeySearch;
    _filterField.textAlignment = NSTextAlignmentLeft;
    _filterField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _filterField.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _filterField.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    
    //左端缩进15像素
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 44)];
    _filterField.leftView = view;
    _filterField.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [_searchView addSubview:_filterField];
    
    
    frame.origin.x = width - 65 + 5;
    frame.size.width = 65;
    _btnCancel = [[UIButton alloc] initWithFrame:frame];
    [_btnCancel.titleLabel setFont:[UIFont systemFontOfSize:12.0f]];
    [_btnCancel addTarget:self action:@selector(onClickCancel:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnCancel setTitle:M_GT_LOCALSTRING(M_GT_ALERT_BACK_KEY) forState:UIControlStateNormal];
    _btnCancel.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _btnCancel.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnCancel.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [_searchView addSubview:_btnCancel];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.backgroundView = nil;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.rowHeight = 44;
    _tableView.showsVerticalScrollIndicator = NO;
    _tableView.showsHorizontalScrollIndicator = NO;
    _tableView.bounces = NO;
    [_tableView.tableHeaderView setNeedsLayout];
    [_tableView.tableHeaderView setNeedsDisplay];
    [self addSubview:_tableView];
}



#pragma mark - Button
- (void) onClickCancel:(id)sender forEvent:(UIEvent*)event
{
    //返回
    [self.viewController.navigationController popViewControllerAnimated:YES];
}

#pragma mark - touches

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self setSelContent:@""];
    [_filterField setText:_selContent];
    [_delegate updateContent:_selContent];
    [self cancelSearch];
    
    [_tableView reloadData];
}

#pragma mark - TextField


- (void)cancelSearch
{
    [_filterField resignFirstResponder];
    [self setFrame:_searchView.frame];
    self.backgroundColor = [UIColor clearColor];
    [_tableView setFrame:CGRectZero];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    [self.viewController.view bringSubviewToFront:self];
    [self setFrame:self.viewController.view.bounds];
    self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6];
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    return YES;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    [self cancelSearch];
    [self setSelContent:@""];
    [_filterField setText:_selContent];
    [_delegate updateContent:_selContent];
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self cancelSearch];
    [self setSelContent:textField.text];
    if ([textField.text length] > 0) {
        [[GTLogSearchHistory sharedInstance] setObject:_selContent forKey:_selContent];
    }
    
    [_delegate updateContent:_selContent];
    
    return YES;
}

#pragma mark -
- (void) updateMatchValues
{
    NSMutableArray *array = [NSMutableArray array];
    
    NSArray *values = [[GTLogSearchHistory sharedInstance] keys];
    
    if ([_selContent length] == 0) {
        [array setArray:values];
    }
    
    for (int i = 0; i < [values count]; i++) {
        NSString *content = [values objectAtIndex:i];
        
        NSRange range = [content rangeOfString:_selContent options:NSCaseInsensitiveSearch];
        if ((range.location != NSNotFound)) {
            [array addObject:content];
        }
    }
    
    [self setMatchValues:array];
    
    CGRect frame = _searchView.frame;
    frame.origin.y = _searchView.frame.origin.y + _searchView.frame.size.height;
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wnonnull"
    frame.size.height = [array count] * [self tableView:_tableView heightForRowAtIndexPath:nil];
#pragma clang diagnostic pop
    if (frame.size.height > 160) {
        frame.size.height = 160;
    }
    [_tableView setFrame:frame];
    
    [_tableView reloadData];
}

- (NSArray *)content
{
    return _matchValues;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [[self content] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier] autorelease];
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
    }
    cell.textLabel.text = [[self content] objectAtIndex:indexPath.row];
    cell.textLabel.textColor = [UIColor whiteColor];
    
    UIView * v = [[[UIView alloc] init] autorelease];
    v.backgroundColor = M_GT_SELECTED_COLOR;
    cell.selectedBackgroundView = v;
    cell.backgroundColor = [UIColor clearColor];
    return cell;
}

#pragma mark - Table view delegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 40.0f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];
    [self setSelContent:[[self content] objectAtIndex:indexPath.row]];
    [_filterField setText:_selContent];
    [self updateMatchValues];
    [_delegate updateContent:_selContent];
    [_tableView reloadData];
    
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = M_GT_BTN_BKGD_COLOR;
    [cell.textLabel setTextColor:[UIColor grayColor]];
    cell.layer.borderColor = [UIColor blackColor].CGColor;
	cell.layer.borderWidth = 0.5;
}

@end

#endif
