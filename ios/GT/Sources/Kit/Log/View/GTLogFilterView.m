//
//  GTLogFilterView.m
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

#import "GTLogFilterView.h"
#import "GTLog.h"
#import "GTImage.h"
#import "GTLogSearchBoard.h"
#import "GTLang.h"
#import "GTLangDef.h"





@implementation GTLogFilterHistory

M_GT_DEF_SINGLETION(GTLogFilterHistory)

@end


@implementation GTLogFilterView

@synthesize filtering = _filtering;
@synthesize selContent = _selContent;
@synthesize selLevel = _selLevel;
@synthesize selTag = _selTag;
@synthesize tags = _tags;
@synthesize matchValues = _matchValues;

@synthesize viewController;
@synthesize delegate = _delegate;
@synthesize tableView = _tableView;

- (id)initWithFrame:(CGRect)frame viewController:(UIViewController*) givenViewController
{
    self = [super initWithFrame:frame];
    if (self) {
        _filterFrame = frame;
        self.viewController = givenViewController;
        [self load];
        [self initUI];
    }
    
    return self;
}

- (void) dealloc
{
    [self unload];
    [super dealloc];
}

- (GTLogLevel)getLevel:(NSString *)levelStr
{
    GTLogLevel level = GT_LOG_INVALID;
    if ([levelStr isEqual:@"INFO"]) {
        level = GT_LOG_INFO;
    } else if ([levelStr isEqual:@"DEBUG"]) {
        level = GT_LOG_DEBUG;
    } else if ([levelStr isEqual:@"WARNING"]) {
        level = GT_LOG_WARNING;
    } else if ([levelStr isEqual:@"ERROR"]) {
        level = GT_LOG_ERROR;
    }
    
    return level;
}

- (void)load
{
    _filtering = NO;
    _selContent = @"";
    _levels = [[[GTLog sharedInstance] levels] retain];
    _selLevel = @"ALL";
    [self setTags:[[[GTLog sharedInstance] tags] keys]];
    _selTag = @"TAG";
}

- (void)unload
{
    self.viewController = nil;
    self.delegate = nil;
    [self.matchValues removeAllObjects];
    self.matchValues = nil;
    
    M_GT_SAFE_FREE(_popVC);
    M_GT_SAFE_FREE(_popLevel);
    M_GT_SAFE_FREE(_popTag);
    
    M_GT_SAFE_FREE(_levels);
    M_GT_SAFE_FREE(_tags);
    
    M_GT_SAFE_FREE(_filterView);
    M_GT_SAFE_FREE(_filterField);
    M_GT_SAFE_FREE(_tableView);
    M_GT_SAFE_FREE(_btnLevel);
    M_GT_SAFE_FREE(_btnTag);
    M_GT_SAFE_FREE(_btnCancel);
    
}

- (void)initUI
{
    CGFloat scale = M_GT_SCREEN_WIDTH/320;
    
    CGFloat height = self.bounds.size.height - 2 * 3;
    CGFloat width = self.bounds.size.width - 2 * 5 * scale;
    
    CGRect frame = CGRectMake(5.0 * scale, 3.0, 126 * scale, height);
    
    self.backgroundColor = [UIColor clearColor];
    
    _filterView = [[UIView alloc] initWithFrame:self.bounds];
    _filterView.backgroundColor = M_GT_CELL_BKGD_COLOR;
    [self addSubview:_filterView];
    
    _filterField = [[GTUITextField alloc] initWithFrame:frame];
    [_filterField setBorderStyle:UITextBorderStyleNone];
    _filterField.textColor = [UIColor whiteColor];
    _filterField.backgroundColor = M_GT_TXT_FIELD_COLOR;
    _filterField.font = [UIFont systemFontOfSize:12];
    _filterField.placeholder = M_GT_LOCALSTRING(M_GT_LOG_FILTER_BY_MSG_KEY);
    _filterField.clearButtonMode = UITextFieldViewModeAlways;
    _filterField.rightViewMode = UITextFieldViewModeAlways;
    _filterField.delegate = self;
    _filterField.autocorrectionType = UITextAutocorrectionTypeNo;
    _filterField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _filterField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _filterField.returnKeyType = UIReturnKeyDone;
    
    _filterField.textAlignment = NSTextAlignmentLeft;
    _filterField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _filterField.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _filterField.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    
    //左端缩进15像素
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 44)];
    _filterField.leftView = view;
    _filterField.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [_filterView addSubview:_filterField];
    
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
    
    
    frame.origin.x = frame.origin.x + frame.size.width + 5.0 * scale;
    frame.size.width = 68 * scale;
    _btnLevel = [[UIButton alloc] initWithFrame:frame];
    [_btnLevel.titleLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [_btnLevel addTarget:self action:@selector(onClickLevel:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnLevel setTitle:_selLevel forState:UIControlStateNormal];
    [_btnLevel setTitleEdgeInsets:UIEdgeInsetsMake(0, -20, 0, 13)];
    [_btnLevel setImage:[GTImage imageNamed:@"gt_filter" ofType:@"png"] forState:UIControlStateNormal];
    [_btnLevel setImageEdgeInsets:UIEdgeInsetsMake(13, 52*scale, 13, 3*scale)];
    _btnLevel.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _btnLevel.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnLevel.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [_filterView addSubview:_btnLevel];
    
    frame.origin.x = frame.origin.x + frame.size.width + 2.0 * scale;
    frame.size.width = 110 * scale;
    _btnTag = [[UIButton alloc] initWithFrame:frame];
    [_btnTag.titleLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [_btnTag addTarget:self action:@selector(onClickTag:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnTag setTitle:_selTag forState:UIControlStateNormal];
    [_btnTag setTitleEdgeInsets:UIEdgeInsetsMake(0, -28, 0, 9)];
    [_btnTag setImage:[GTImage imageNamed:@"gt_filter" ofType:@"png"] forState:UIControlStateNormal];
    [_btnTag setImageEdgeInsets:UIEdgeInsetsMake(13, 92*scale, 13, 3*scale)];
    _btnTag.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _btnTag.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnTag.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [_filterView addSubview:_btnTag];
    
    frame.origin.x = 5 * scale + width - 65*scale;
    frame.size.width = 65*scale;
    _btnCancel = [[UIButton alloc] initWithFrame:frame];
    [_btnCancel.titleLabel setFont:[UIFont systemFontOfSize:12.0f]];
    [_btnCancel addTarget:self action:@selector(onClickCancel:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnCancel setTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY) forState:UIControlStateNormal];
    _btnCancel.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _btnCancel.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnCancel.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [_filterView addSubview:_btnCancel];
    _btnCancel.hidden = YES;
}


- (void)delayCloseFiltering
{
    _filtering = NO;
}

- (void)closeFiltering
{
    _filtering = NO;
}

#pragma mark - update
- (void)updateTag:(NSString *)tag
{
    [self setSelTag:tag];
    [_btnTag setTitle:tag forState:UIControlStateNormal];
}

#pragma mark - Button

- (void)onClickLevel:(id)sender forEvent:(UIEvent*)event
{
    _filtering = YES;
    _popLevel = [[GTPopoverTableView alloc] initWithArray:_levels];
    _popLevel.popDelegate = self;
    [_popLevel setSelectedTitle:_selLevel];
    
    _popVC = [[GTPopoverController alloc] initWithContentViewController:_popLevel];
    
    _popVC.popDelegate = self;
    
    [_popVC showPopoverWithViewController:self.viewController forEvent:event];
}
- (void)onClickTag:(id)sender forEvent:(UIEvent*)event
{
    _filtering = YES;
    //tags会随时更新
    [self setTags:[[[GTLog sharedInstance] tags] keys]];
    _popTag = [[GTPopoverTableView alloc] initWithArray:_tags];
    _popTag.popDelegate = self;
    [_popTag setSelectedTitle:_selTag];
    
    _popVC = [[GTPopoverController alloc] initWithContentViewController:_popTag];
    _popVC.popDelegate = self;
    
    [_popVC showPopoverWithViewController:self.viewController forEvent:event];
}

- (void)onClickCancel:(id)sender forEvent:(UIEvent*)event
{
    _filtering = YES;
    [self cancelFilter];
    [self setSelContent:@""];
    [_filterField setText:_selContent];
    [_delegate updateContent:_selContent];
}

- (void)onClickSearch:(id)sender forEvent:(UIEvent*)event
{
    GTLogSearchBoard *board = [[GTLogSearchBoard alloc] init];
    if ( board )
    {
        [board setArray:[_delegate logArray]];
        [self.viewController.navigationController pushViewController:board animated:YES];
        [board release];
    }
}

- (void)popoverTable:(GTPopoverTableView*)tableView didSelectRow:(NSUInteger)row
{
    if (tableView == _popLevel) {
        [self setSelLevel:[_levels objectAtIndex:row]];
        [_btnLevel setTitle:_selLevel forState:UIControlStateNormal];
        [_delegate updateLevel:_selLevel];
        
    } else {
        [self setSelTag:[_tags objectAtIndex:row]];
        [_btnTag setTitle:_selTag forState:UIControlStateNormal];
        [_delegate updateTag:_selTag];
    }
    
    M_GT_SAFE_FREE(_popLevel);
    M_GT_SAFE_FREE(_popTag);
    
    [_popVC dismissPopoverAnimatd:NO];
    
    M_GT_SAFE_FREE(_popVC);
    
    [self delayCloseFiltering];
}

- (void) dismissPopoverController:(GTPopoverController* )controller
{
    [self delayCloseFiltering];
}

#pragma mark - touches

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self setSelContent:@""];
    [_filterField setText:_selContent];
    [_delegate updateContent:_selContent];
    [self cancelFilter];
}

#pragma mark - TextField


- (void)cancelFilter
{
    CGFloat scale = M_GT_SCREEN_WIDTH/320;
    
    CGFloat height = _filterView.bounds.size.height - 2 * 3;
    
    CGRect frame = CGRectMake(5.0*scale, 3.0, 126*scale, height);
    [_filterField setFrame:frame];
    
    _btnCancel.hidden = YES;
    _btnLevel.hidden = NO;
    _btnTag.hidden = NO;
    _btnSearch.hidden = NO;
    
    [_filterField resignFirstResponder];
    [self setFrame:_filterFrame];
    [_filterView setFrame:self.bounds];
    [_tableView setFrame:CGRectZero];
    self.backgroundColor = [UIColor clearColor];
    [self delayCloseFiltering];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    CGFloat scale = M_GT_SCREEN_WIDTH/320;
    
    _filtering = YES;
    CGFloat height = _filterView.bounds.size.height - 2 * 3;
    CGFloat width = _filterView.bounds.size.width - 2 * 5 * scale;
    CGRect frame = CGRectMake(5.0*scale, 3.0, width - (65 + 5)*scale, height);
    
    [_filterField setFrame:frame];
    
    _btnCancel.hidden = NO;
    _btnLevel.hidden = YES;
    _btnTag.hidden = YES;
    _btnSearch.hidden = YES;
    [self.viewController.view bringSubviewToFront:self];
    
    [_filterView setFrame:_filterFrame];
    [self setFrame:self.viewController.view.bounds];
    
    self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6];
    [self updateMatchValues];
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newString = [textField.text stringByReplacingCharactersInRange:range withString:string];
    [self setSelContent:newString];
    [self updateMatchValues];
    [_delegate updateContent:_selContent];
    return YES;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    [self cancelFilter];
    [self setSelContent:@""];
    [_filterField setText:_selContent];
    [_delegate updateContent:_selContent];
    return NO;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self cancelFilter];
    [self setSelContent:textField.text];
    if ([textField.text length] > 0) {
        [[GTLogFilterHistory sharedInstance] setObject:_selContent forKey:_selContent];
    }
    
    [_delegate updateContent:_selContent];
    return YES;
}

#pragma mark -
- (void) updateMatchValues
{
    NSMutableArray *array = [NSMutableArray array];
    
    NSArray *values = [[GTLogFilterHistory sharedInstance] keys];
    
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
    
    CGRect frame = _filterFrame;
    frame.origin.y = _filterFrame.origin.y + _filterFrame.size.height;
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
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
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
    
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_BTN_BKGD_COLOR;
    [cell.textLabel setTextColor:[UIColor grayColor]];
    cell.layer.borderColor = [UIColor blackColor].CGColor;
	cell.layer.borderWidth = 0.5;
}


@end

#endif
