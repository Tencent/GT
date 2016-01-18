//
//  GTParaInSelectBoard.m
//  GTKit
//
//  Created   on 13-1-20.
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
#import "GTParaInSelectBoard.h"
#import "GTCommonCell.h"
#import <QuartzCore/QuartzCore.h>
#import "GTConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"



@interface GTParaInSelectBoard ()
{

}
@property (nonatomic, retain) NSArray *values;

@end

@implementation GTParaInSelectBoard

@synthesize lastIndexPath = _lastIndexPath;
@synthesize textField = _textField;
@synthesize tableView = _tableView;
@synthesize delegate = _delegate;
@synthesize values;

- (id) init
{
    self = [super init];
    if (self) {
        [self load];
    }
    
    return self;
}


- (void)dealloc
{
	[self unload];
    [super dealloc];
}

- (void) load
{

}

- (void) unload
{
    self.values = nil;
    self.lastIndexPath = nil;
    _tableView.dataSource = nil;
    _tableView.delegate = nil;
    [_tableView release];
    [_textField release];
    
}
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    [self initUI];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
//    [[self navigationController] setNavigationBarHidden:YES];
}


- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [_textField becomeFirstResponder];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self closeKeyBoard];
}

- (void)closeKeyBoard
{
    [_textField resignFirstResponder];
}

- (void)initNavBarUI
{
	[self createTopBar];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_PARA_IN_CELL_TITLE_KEY)];
}

- (NSArray *)leftBarButtonItems
{
    UIView      *barView = nil;
    UIButton    *barBtn  = nil;
    
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_BTN_WIDTH, M_GT_BTN_HEIGHT)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, M_GT_BTN_WIDTH, M_GT_BTN_HEIGHT)];
    [barBtn addTarget:self action:@selector(onCancelTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY) forState:UIControlStateNormal];
    [barBtn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    barBtn.titleLabel.font = [UIFont systemFontOfSize:M_GT_BTN_FONTSIZE];
    barBtn.backgroundColor = M_GT_BTN_BKGD_COLOR;
    barBtn.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    barBtn.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar1, nil];
    [bar1 release];
    
    return array;
}


- (NSArray *)rightBarButtonItems
{
    UIView      *barView = nil;
    UIButton    *barBtn  = nil;
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_BTN_WIDTH, M_GT_BTN_HEIGHT)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, M_GT_BTN_WIDTH, M_GT_BTN_HEIGHT)];
    [barBtn addTarget:self action:@selector(onOkTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setTitle:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)  forState:UIControlStateNormal];
    [barBtn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    barBtn.titleLabel.font = [UIFont systemFontOfSize:M_GT_BTN_FONTSIZE];
    barBtn.backgroundColor = M_GT_BTN_BKGD_COLOR;
    barBtn.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    barBtn.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar1, nil];
    [bar1 release];
    
    return array;
}

- (void)initInputUI
{
    CGRect rect = M_GT_BOARD_FRAME;
    self.view.backgroundColor = M_GT_CELL_BKGD_COLOR;
    CGFloat width = M_GT_SCREEN_WIDTH - 20.0f;
    
    CGRect frame;
    frame.origin.x = (rect.size.width - width)/2 + 10.0f;
    frame.origin.y = rect.origin.y;
    frame.size.width = width;
    frame.size.height = 44.0f;
    
    UILabel *key = [[UILabel alloc] initWithFrame:frame];
	key.font = [UIFont systemFontOfSize:18.0];
	key.textColor = [UIColor grayColor];
    [key setText:[[_data dataInfo] key]];
	key.textAlignment = NSTextAlignmentLeft;
    key.contentMode = UIViewContentModeCenter;
    key.backgroundColor = M_GT_CELL_BKGD_COLOR;
	[self.view addSubview:key];
    [key release];
    
    frame.origin.x = (rect.size.width - width)/2;
    frame.origin.y = rect.origin.y + 40.0f;
    _textField = [[GTUITextField alloc] initWithFrame:frame];
    [_textField setBorderStyle:UITextBorderStyleNone];
    [_textField setText:[NSString stringWithFormat:@"%@", _data.dataInfo.value]];
    _textField.textColor = [UIColor grayColor];
    _textField.backgroundColor = M_GT_TXT_FIELD_COLOR;
    _textField.font = [UIFont systemFontOfSize:18];
    _textField.placeholder = @"<please enter a new value>";
    _textField.clearButtonMode = UITextFieldViewModeAlways;
    _textField.delegate = self;
    _textField.autocorrectionType = UITextAutocorrectionTypeNo;
    _textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _textField.returnKeyType = UIReturnKeyDone;
    
    _textField.textAlignment = NSTextAlignmentLeft;
    _textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _textField.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _textField.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    
    
    
    //左端缩进15像素
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 44)];
    _textField.leftView = view;
    _textField.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [self.view addSubview:_textField];
    
    frame.origin.x = (rect.size.width - width)/2;
    frame.origin.y = rect.origin.y + 44.0f + 40.0f;
//    frame.size.height = rect.size.height - 44.0f - 40.0f - 256.0f;
    frame.size.height = rect.size.height - 44.0f - 40.0f;
    frame.size.width = width;
    
    _tableView = [[UITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.backgroundView = nil;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.rowHeight = 40.0f;
    _tableView.showsVerticalScrollIndicator = NO;
    _tableView.showsHorizontalScrollIndicator = NO;
    _tableView.bounces = NO;
    [_tableView.tableHeaderView setNeedsLayout];
    [_tableView.tableHeaderView setNeedsDisplay];
    
    [self.view addSubview:_tableView];
    
    
    [_tableView reloadData];
}

- (void)initUI
{
    [self initNavBarUI];
    [self initInputUI];
}

- (void)bindData:(GTInputObject *)data
{
	_data = data;
    
    self.values = [[_data dataInfo] valueArray];
    self.lastIndexPath = [NSIndexPath indexPathForRow:[[_data dataInfo] valueIndex] inSection:0];
}


#pragma mark - UITableViewDataSource

- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.values.count;
}

- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTCommonCell * cell = (GTCommonCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTCommonCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    
    [cell.textLabel setText:[[self.values objectAtIndex:indexPath.row] description]];
    [cell.textLabel setBackgroundColor:[UIColor clearColor]];
    [cell.textLabel setTextColor:[UIColor grayColor]];
    [cell.textLabel setFont:[UIFont systemFontOfSize:18]];
    
    if ([self.lastIndexPath isEqual:indexPath]) {
        cell.backgroundColor = M_GT_SELECTED_COLOR;
        [cell.textLabel setTextColor:[UIColor whiteColor]];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    
    return cell;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 40.0f;
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];
    if(self.lastIndexPath){
		UITableViewCell *lastcell = [tableView cellForRowAtIndexPath:self.lastIndexPath];
        lastcell.backgroundColor = M_GT_CELL_BKGD_COLOR;
        [lastcell.textLabel setTextColor:[UIColor grayColor]];
	}

	UITableViewCell* newCell = [tableView cellForRowAtIndexPath:indexPath];
    newCell.backgroundColor = M_GT_SELECTED_COLOR;
    [newCell.textLabel setTextColor:[UIColor whiteColor]];
	self.lastIndexPath = indexPath;

    _textField.text = [self.values objectAtIndex:indexPath.row];
    
    [_textField resignFirstResponder];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([self.lastIndexPath isEqual:indexPath] ) {
        cell.backgroundColor = M_GT_SELECTED_COLOR;
        [cell.textLabel setTextColor:[UIColor whiteColor]];
    } else {
        cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
        [cell.textLabel setTextColor:[UIColor grayColor]];
    }
    cell.layer.borderColor = [UIColor blackColor].CGColor;
	cell.layer.borderWidth = 0.5;
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView
{
    [_textField resignFirstResponder];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    CGRect rect = M_GT_BOARD_FRAME;
    CGFloat width = M_GT_SCREEN_WIDTH - 20.0f;
    CGRect frame;
    
    frame.origin.x = (rect.size.width - width)/2;
    frame.origin.y = rect.origin.y + 44.0f + 40.0f;
    frame.size.height = rect.size.height - 44.0f - 40.0f;
    frame.size.width = width;
    [_tableView setFrame:frame];
    
    [_textField resignFirstResponder];
}

#pragma mark - UITextFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    CGRect rect = M_GT_BOARD_FRAME;
    CGFloat width = M_GT_SCREEN_WIDTH - 20.0f;
    CGRect frame;
    
    frame.origin.x = (rect.size.width - width)/2;
    frame.origin.y = rect.origin.y + 44.0f + 40.0f;
//    frame.size.height = rect.size.height - 44.0f - 40.0f - 256.0f;
    frame.size.height = rect.size.height - 44.0f - 40.0f;
    frame.size.width = width;
    [_tableView setFrame:frame];
    
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self onOkTouched:nil];
    return YES;
}

#pragma mark - BarButtonItem

- (void)onOkTouched:(id)sender
{
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    
    [_textField resignFirstResponder];
	[[_data dataInfo] addDataValue:_textField.text];

    //返回
    [self.navigationController popViewControllerAnimated:YES];
    
    if (_delegate) {
        [_delegate onParaInSelectOK];
    }
}


- (void)onCancelTouched:(id)sender
{
    //返回
    [self.navigationController popViewControllerAnimated:YES];
    
    if (_delegate) {
        [_delegate onParaInSelectCancel];
    }
}

@end
#endif
