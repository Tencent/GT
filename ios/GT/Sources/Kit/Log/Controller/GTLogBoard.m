//
//  GTLogBoard.m
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
#import "GTLogBoard.h"
#import "GTLog.h"
#import "GTLogCell.h"
#import "GTImage.h"
#import <CoreText/CoreText.h>
#import <QuartzCore/QuartzCore.h>
#import "GTUIAlertView.h"
#include <math.h>
#import "GTLogSearchBoard.h"
#import "GTLang.h"
#import "GTLangDef.h"

#define M_GT_FILTER_HEIGHT 40.0f


#pragma mark -

@implementation GTLogBoard

@synthesize tableView = _tableView;
@synthesize selContent = _selContent;
@synthesize selLevel = _selLevel;
@synthesize selTag = _selTag;
@synthesize array = _array;
@synthesize lastDate = _lastDate;


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

- (id)init
{
    self = [super init];
    if (self) {
        [self load];
    }
    
    return self;
}

- (void) dealloc
{
    [self unload];
    [super dealloc];
}


- (void)load
{
    _filterShow = NO;
    [self setLastDate:[NSDate date]];
    _sumOffset = 0;
    _isFingerTouched = NO;
    
    _selContent = @"";
    _selLevel = @"ALL";
    _selTag = @"TAG";
    
    GTLogLevel level = [self getLevel:_selLevel];
    
    self.array = [[GTLog sharedInstance] searchContent:_selContent withTag:_selTag withLevel:level inArray:nil];
}


- (void)unload
{
    [self unobserveTick];
    
    self.array = nil;
    self.lastDate = nil;
    self.selContent = nil;
    self.selLevel = nil;
    self.selTag = nil;
    
    //如果不设置nil则会概率性出现GTDetailWindow释放的时候crash
    _tableView.dataSource = nil;
    _tableView.delegate = nil;
    _tableView.touchesDelegate = nil;
    [_tableView removeObserver:self forKeyPath:@"contentOffset"];
    
    M_GT_SAFE_FREE(_filterView);
    M_GT_SAFE_FREE(_tableView);
    
    [_verticalScrollBar unobserveTick];
    M_GT_SAFE_FREE(_verticalScrollBar);
    M_GT_SAFE_FREE(_accessoryView);
    M_GT_SAFE_FREE(_btnToTop);
    M_GT_SAFE_FREE(_btnToBottom);
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self initUI];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)initNavBarUI
{
    [[self navigationController] setNavigationBarHidden:YES];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_LOG_KEY)];
}


- (NSArray *)rightBarButtonItems
{
    UIView *barView = nil;
    UIButton *barBtn = nil;
    
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(13, 8, 13, 8)];
    [barBtn addTarget:self action:@selector(onSearchTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImage:[GTImage imageNamed:@"gt_search" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_search" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 5, 10, 5)];
    [barBtn addTarget:self action:@selector(onSaveTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImage:[GTImage imageNamed:@"gt_save" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_save_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar2 = nil;
    bar2 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 5, 10, 5)];
    [barBtn addTarget:self action:@selector(onClearTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImage:[GTImage imageNamed:@"gt_clear" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_clear_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar3 = nil;
    bar3 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar1, bar2, bar3, nil];
    [bar1 release];
    [bar2 release];
    [bar3 release];
    
    return array;
}

- (void)initUI
{
    [self initNavBarUI];
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    
    CGRect rect = M_GT_APP_FRAME;
    
    CGRect frame = rect;
    frame.size.height = M_GT_FILTER_HEIGHT;
    UIView *filterBGView = [[UIView alloc] initWithFrame:frame];
    [self.view addSubview:filterBGView];
    [filterBGView release];
    
    _filterView = [[GTLogFilterView alloc] initWithFrame:frame viewController:self];
    [_filterView setDelegate:self];
    [self.view addSubview:_filterView];
    //设置为不显示，initWithFrame里记录了位置，需要这里重设一下
    [_filterView setFrame:CGRectZero];
    
    frame.origin.x = frame.origin.x + 5.0f;
    frame.origin.y = rect.origin.y;
    frame.size.height = rect.size.height;
    frame.size.width = frame.size.width - 2 *5.0f;
    
    _tableView = [[GTUITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.touchesDelegate = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.backgroundView = nil;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.rowHeight = 44;
    _tableView.showsVerticalScrollIndicator = NO;
    _tableView.showsHorizontalScrollIndicator = NO;
    _tableView.bounces = YES;
    [_tableView.tableHeaderView setNeedsLayout];
    [_tableView.tableHeaderView setNeedsDisplay];
    [self.view addSubview:_tableView];
    
    _verticalScrollBar = [[GTVerticalScrollBar alloc] initWithFrame:frame];
    [_verticalScrollBar setScrollView:_tableView];
    [_verticalScrollBar setHandleHidden:YES];
    [self.view addSubview:_verticalScrollBar];
    
    
    [_tableView addObserver:self
                  forKeyPath:@"contentOffset"
                     options:NSKeyValueObservingOptionNew
                     context:nil];
    
    _accessoryView = [[GTAccessoryView alloc] initWithFrame:CGRectMake(0, 0, 65, 30)];
    [_accessoryView setForegroundColor:[UIColor colorWithWhite:0.2f alpha:1.0f]];
    [_verticalScrollBar setHandleAccessoryView:_accessoryView];
    
    frame.origin.x = 250.0f;
    frame.origin.y = rect.size.height/2 - 50.0f;
    frame.size.height = 40.0f;
    frame.size.width = 50.0f;
    
    _btnToTop = [[UIButton alloc] initWithFrame:frame];
    [_btnToTop.titleLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [_btnToTop addTarget:self action:@selector(onClickTop:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnToTop setTitle:@"Top" forState:UIControlStateNormal];
    _btnToTop.backgroundColor = M_GT_SELECTED_COLOR;
    _btnToTop.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnToTop.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    _btnToTop.alpha = 0.8;
    [self.view addSubview:_btnToTop];
    
    
    frame.origin.x = 250.0f;
    frame.origin.y = rect.size.height/2 + 120.0f;
    frame.size.height = 40.0f;
    frame.size.width = 50.0f;
    
    _btnToBottom = [[UIButton alloc] initWithFrame:frame];
    [_btnToBottom.titleLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [_btnToBottom addTarget:self action:@selector(onClickBottom:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnToBottom setTitle:@"Bottom" forState:UIControlStateNormal];
    _btnToBottom.backgroundColor = M_GT_SELECTED_COLOR;
    _btnToBottom.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnToBottom.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    _btnToBottom.alpha = 0.8;
    [self.view addSubview:_btnToBottom];
    
    [self hideTopBottomBtn];
}

- (void) onClickBottom:(id)sender forEvent:(UIEvent*)event
{
    [self updateTable];
    [self scrollToBottomAnimated:YES];
    [self hideTopBottomBtn];
    _isBottom = YES;
    
}

- (void) onClickTop:(id)sender forEvent:(UIEvent*)event
{
    [_tableView setContentOffset:CGPointMake(0, 0) animated:NO];
    [self hideTopBottomBtn];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [[self navigationController] setNavigationBarHidden:YES];
    
    [self updateTable];
    [self scrollToBottomAnimated:YES];
    _isBottom = YES;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleLogModify:) name:M_GT_NOTIFICATION_LOG_MOD object:nil];
}


- (void)viewWillDisappear:(BOOL)animated
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_LOG_MOD object:nil];
    [super viewWillDisappear:animated];
}


-(void)updateTable
{
    GTLogLevel level = [self getLevel:[_filterView selLevel]];
	self.array = [[GTLog sharedInstance] searchContent:_selContent withTag:_selTag withLevel:level inArray:nil];
    
    [_tableView reloadData];
}

- (void)handleLogModify:(NSNotification *)n
{
    if (_isBottom) {
        [self updateTable];
        [self scrollToBottomAnimated:YES];
    }

}

- (void)viewDidUnload
{
    [self unobserveTick];
    
    [super viewDidUnload];
}

- (NSArray *)content:(NSUInteger)section {
    return _array;
}

- (id) contentObject:(NSIndexPath *)indexPath {
    return [_array objectAtIndex:indexPath.row];
}

#pragma mark -
#pragma mark UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    CGRect bounds = tableView.bounds;
	CGSize bound = CGSizeMake( bounds.size.width, 0.0f );
//
//    GTLogCell * cell = (GTLogCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
//    
//    if (cell == nil) {
//		cell = [[[GTLogCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
//	}
//    [cell setNeedsUpdateConstraints];
//    [cell updateConstraintsIfNeeded];
//    
    CGFloat height = [GTLogCell cellHeight:[self contentObject:indexPath] bound:bound];
//    cell.bounds = CGRectMake(0, 0, bounds.size.width, height);
//    
//    [cell setNeedsDisplay];
//    [cell layoutIfNeeded];
    
	return height;
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = [UIColor clearColor];
}


#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self content:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    //解决滑屏时日志显示不全的问题
//    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier(%u)", indexPath.row%20];
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    
    GTLogCell * cell = (GTLogCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTLogCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    [cell bindData:[self contentObject:indexPath]];
    cell.backgroundColor = [UIColor clearColor];
    
    
//    [cell setNeedsUpdateConstraints];
//    [cell updateConstraintsIfNeeded];
    
    return cell;

}

#pragma mark -
#pragma mark UITableViewHeader

- (UIView *)tableViewForHeader
{
    return nil;
}

#pragma mark - GTUITableViewTouchesDelegate
- (void)view:(UIView*)view touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
    //排除srcollview滑动的交互
    if (_isFingerTouched) {
        return;
    }
    if (_filterShow) {
        [self hideFilterView];
    } else {
        [self showFilterView];
    }
    [self updateTable];
}

#pragma mark - UIScrollViewDelegate

- (void)showFilterView
{
    if (_filterShow) {
        return;
    }
    CGRect rect = M_GT_APP_FRAME;
    CGRect frame = rect;
    
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
    [UIView setAnimationDelay:0.3f];
    [UIView setAnimationDelegate:self];
    
    frame.size.height = 0;
    [_filterView setFrame:frame];
    
    frame.size.height = M_GT_FILTER_HEIGHT;
    [_filterView setFrame:frame];
    [self.view bringSubviewToFront:_filterView];

    [UIView commitAnimations];
    
//    [_verticalScrollBar setHandleHidden:YES];
    [self showTopBottomBtn];
    _filterShow = YES;
}

- (void)hideFilterView
{
    if (!_filterShow) {
        return;
    }
    
    //正在过滤中，过滤条不隐藏
    if ([_filterView filtering]) {
        return;
    }
    
    CGRect rect = M_GT_APP_FRAME;
    CGRect frame = rect;
    
    //_filterView设置CGRectZero会覆盖navBar，这里把navBar放到视图最前面
    [self.view bringSubviewToFront:self.navBar];
    
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
    [UIView setAnimationDelay:0.3f];
    [UIView setAnimationDelegate:self];
    
    frame.origin.x = 0;
    frame.origin.y = 0;
    frame.size.height = 0;
    [_filterView setFrame:CGRectZero];
    [UIView commitAnimations];
    
    [self hideTopBottomBtn];
    _filterShow = NO;
}

- (void)showTopBottomBtn
{
    //正在过滤中，快捷键隐藏
    if ([_filterView filtering]) {
        _btnToBottom.hidden = YES;
        _btnToTop.hidden = YES;
        return;
    }
}

- (void)hideTopBottomBtn
{
    _btnToBottom.hidden = YES;
    _btnToTop.hidden = YES;
}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView
{
    _isFingerTouched = YES;
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    _isFingerTouched = NO;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ([self getOffset] <= 0.0) {
        //第一次设置为YES，需要更新下最新的数据
        if (_isBottom == NO) {
            _isBottom = YES;
            [self performSelector:@selector(handleLogModify:) withObject:nil afterDelay:0.1];
        }
        
    } else {
        _isBottom = NO;
    }
    
    if (!_isFingerTouched) {
        return;
    }
    [self hideFilterView];
    [self observeTick];
    
    int currentPostion = scrollView.contentOffset.y;
    //第一次不计算
    if (_lastPosition == 0) {
        _lastPosition = currentPostion;
    }
    _sumOffset += currentPostion - _lastPosition;
    _lastPosition = currentPostion;
    
    if (abs(_sumOffset) > 1000) {
        [_verticalScrollBar setHandleHidden:NO];
        [_verticalScrollBar unobserveTick];
        [_verticalScrollBar observeTick];
        _sumOffset = 0;
        [self unobserveTick];
    }
 
}


- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
}

#pragma mark -
- (void)observeTick
{
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:NO];
        [_timer retain];
    }
	
}

- (void)unobserveTick
{
    if (_timer) {
        [_timer invalidate];
        [_timer release];
        _timer = nil;
    }
}


- (void)handleTick
{
    if (abs(_sumOffset) > 1000) {
        [_verticalScrollBar setHandleHidden:NO];
        [_verticalScrollBar unobserveTick];
        [_verticalScrollBar observeTick];
    }
    _sumOffset = 0;
    [self unobserveTick];
}

#pragma mark - PickerView lifecycle

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 3;
}

#pragma mark - TextField

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newString = [textField.text stringByReplacingCharactersInRange:range withString:string];
    [self setSelContent:newString];
    [self updateTable];
    return YES;
}


- (CGFloat)getOffset
{
    CGPoint offset = _tableView.contentOffset;
    CGRect bounds = _tableView.bounds;
    CGSize size = _tableView.contentSize;
    UIEdgeInsets inset = _tableView.contentInset;
    
    CGFloat currentOffset = offset.y + bounds.size.height - inset.bottom;
    CGFloat maximumOffset = size.height;
    return (maximumOffset - currentOffset);
}


-(void)scrollToBottomAnimated:(BOOL)animated
{
    CGRect bounds = _tableView.bounds;
    CGSize size = _tableView.contentSize;
    UIEdgeInsets inset = _tableView.contentInset;
    
    CGFloat bottomOffset = size.height - bounds.size.height + inset.bottom;
    
    // 已经到底，不需要滑动
    if ([self getOffset] <= 0.0) {
        return;
    }
    
    [_tableView setContentOffset:CGPointMake(0, bottomOffset) animated:animated];
}

#pragma mark - Button clicked

- (void)onSearchTouched:(id)sender
{
    GTLogSearchBoard *board = [[GTLogSearchBoard alloc] init];
    if ( board )
    {
        [board setArray:_array];
        
        [self.navigationController pushViewController:board animated:YES];
        [board release];
    }
}

- (void)onClearTouched:(id)sender
{
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_CLEAR_TITLE_KEY)
                                                             message:M_GT_LOCALSTRING(M_GT_ALERT_CLEAR_INFO_KEY)
                                                            delegate:self
                                                   cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY)
                                                   otherButtonTitles:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)];
    [alertView setTag:M_GT_ALERT_TAG_CLEAR];
    [alertView show];
    [alertView release];
    
}

- (void)onSaveTouched:(id)sender
{
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_SAVE_TITLE_KEY)
                                                             message:M_GT_LOCALSTRING(M_GT_ALERT_INPUT_SAVED_FILE_KEY)
                                                            delegate:self
                                                   cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY)
                                                   otherButtonTitles:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)];
    [alertView setTag:M_GT_ALERT_TAG_SAVE];
    [alertView addTextFieldWithTag:0];
    [[alertView textFieldAtTag:0] setText:[[GTLog sharedInstance] fileName]];
    [alertView show];
    [alertView release];
}

#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    //clear
    if ([alertView tag] == M_GT_ALERT_TAG_CLEAR) {
        if (buttonIndex == 1) {
            [[GTLog sharedInstance] clearAll];
            [self updateTable];
        }
    }
    //save
    else if ([alertView tag] == M_GT_ALERT_TAG_SAVE)
    {
        if (buttonIndex == 1) {
            UITextField *saveLogName = [alertView textFieldAtTag:0];
            [[GTLog sharedInstance] saveLogs:_array fileName:[saveLogName text]];
            [_tableView reloadData];
        }
    }
}

#pragma mark - GTLogFilterDelegate

- (void)updateContent:(NSString *)content
{
    [self setSelContent:content];
    [self updateTable];
}

- (void)updateLevel:(NSString *)level
{
    [self setSelLevel:level];
    [self updateTable];
}

- (void)updateTag:(NSString *)tag
{
    [self setSelTag:tag];
    [self updateTable];
}


- (NSMutableArray *)logArray
{
    return _array;
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context
{
    if (![keyPath isEqualToString:@"contentOffset"]) {
        return;
    }
    
    CGFloat contentOffsetY = [_tableView contentOffset].y;
    CGFloat contentHeight = [_tableView contentSize].height;
    CGFloat frameHeight = [_tableView frame].size.height;
    
    CGFloat percent = (contentOffsetY / (contentHeight - frameHeight)) * 100;
    [[_accessoryView textLabel] setText:[NSString stringWithFormat:@"%i%%", (int)percent]];
}

@end
#endif
