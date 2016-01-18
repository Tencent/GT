//
//  GTProfilerBoard.m
//  GTKit
//
//  Created   on 13-1-27.
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
#import <QuartzCore/QuartzCore.h>
#import "GTProfilerBoard.h"
#import "GTLog.h"
#import "GTCommonCell.h"
#import "GTProfilerDetailBoard.h"
#import "GTImage.h"
#import "GTUIAlertView.h"
#import "GTLang.h"
#import "GTLangDef.h"




#define M_GT_PROFILER_SECTION_HEIGHT 39.0f

@interface GTLogAnalyseCell : GTCommonCell
{
	UILabel *	  _content;
    UILabel *	  _count;
    UILabel *     _totalTime;
    UILabel *     _maxTime;
    UILabel *     _avgTime;
}

@end


@implementation GTLogAnalyseCell

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 60.0f );
}

- (void)cellLayout
{    
    CGFloat width = self.bounds.size.width - 12;
    CGFloat x = 6.0f;
    _content.frame = CGRectMake( 6.0f, 11.0f, width, 13.0f );
    
    width = self.bounds.size.width - 10 - 40;
    x = 0.0f;
    CGFloat y = 34.0f;
    CGFloat height = 13;
    
    _count.frame = CGRectMake( x, y, 40, height );
    _totalTime.frame = CGRectMake( 40 + x + 5, y, width/3 - 5, height );
    _maxTime.frame = CGRectMake( 40 + x + width/3 + 5, y, width/3 - 5, height );
    _avgTime.frame = CGRectMake( 40 + x + 2*width/3 + 5, y, width/3 - 5, height );
}

- (void)load
{
	[super load];
    
	_content = [[UILabel alloc] init];
	_content.font = [UIFont systemFontOfSize:13.0];
	_content.textColor = M_GT_LABEL_COLOR;
	_content.textAlignment = NSTextAlignmentLeft;
    _content.backgroundColor = [UIColor clearColor];
	[self addSubview:_content];
    
    _count = [[UILabel alloc] init];
	_count.font = [UIFont systemFontOfSize:14.0];
	_count.textColor = M_GT_LABEL_VALUE_COLOR;
	_count.textAlignment = NSTextAlignmentRight;
    _count.backgroundColor = [UIColor clearColor];
	[self addSubview:_count];
    
    _totalTime = [[UILabel alloc] init];
	_totalTime.font = [UIFont systemFontOfSize:14.0];
	_totalTime.textColor = M_GT_LABEL_VALUE_COLOR;
	_totalTime.textAlignment = NSTextAlignmentRight;
    _totalTime.backgroundColor = [UIColor clearColor];
	[self addSubview:_totalTime];
    
    _maxTime = [[UILabel alloc] init];
	_maxTime.font = [UIFont systemFontOfSize:14.0];
	_maxTime.textColor = M_GT_LABEL_VALUE_COLOR;
	_maxTime.textAlignment = NSTextAlignmentRight;
    _maxTime.backgroundColor = [UIColor clearColor];
	[self addSubview:_maxTime];
    
    _avgTime = [[UILabel alloc] init];
	_avgTime.font = [UIFont systemFontOfSize:14.0];
	_avgTime.textColor = M_GT_LABEL_VALUE_COLOR;
	_avgTime.textAlignment = NSTextAlignmentRight;
    _avgTime.backgroundColor = [UIColor clearColor];
	[self addSubview:_avgTime];
}

- (void)unload
{
    
	M_GT_SAFE_FREE( _content );
	M_GT_SAFE_FREE( _count );
    M_GT_SAFE_FREE( _totalTime );
    M_GT_SAFE_FREE( _maxTime );
    M_GT_SAFE_FREE( _avgTime );
	
	[super unload];
}

- (void)bindData:(NSObject *)data
{
    [super bindData:data];
    GTProfilerDetail *obj = (GTProfilerDetail *)data;
    
    [_content setText:NSLocalizedString([obj key],)];
    [_count setText:[NSString stringWithFormat:@"%lu", (unsigned long)[obj count]]];
    [_totalTime setText:[NSString stringWithFormat:@"%.3f", [obj totalTime]]];
    [_maxTime setText:[NSString stringWithFormat:@"%.3f", [obj maxTime]]];
    [_avgTime setText:[NSString stringWithFormat:@"%.3f", [obj avgTime]]];
    
    
}

- (void)clearData
{
	[_content setText:nil];
    [_count setText:nil];
	[_totalTime setText:nil];
	[_avgTime setText:nil];
	
}

@end

#pragma mark -


@interface GTLogProfilerBoard ()

@end

@implementation GTLogProfilerBoard

@synthesize list = _list;
@synthesize tableView = _tableView;

- (id)init
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


- (void)load
{
    
}

- (void)unload
{
    [_tableView release];
    
}

- (void)initNavBarUI
{
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_PROFILER_KEY)];
}

- (void)initTimeUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    
    CGRect frame = M_GT_APP_FRAME;
    CGFloat width = self.view.bounds.size.width;
    
    _topIntro = [[GTProfilerHeaderView alloc] initWithFrame:CGRectMake(0, frame.origin.y, width, 30.0)];
    [self.view addSubview:_topIntro];
    [_topIntro release];
    
    
    width = width - 20;
    frame.origin.x = 10;
    frame.origin.y = frame.origin.y + 30;
    frame.size.height = frame.size.height - 30.0f;
    frame.size.width = width;
    
    _tableView = [[UITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
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
    _tableView.tableHeaderView = [self tableViewForHeader];
    _tableView.tableFooterView = [self tableViewForFooter];
    [self.view addSubview:_tableView];
    
    
    _tableView.contentInset = UIEdgeInsetsMake(0.0f, 0, 0.0f, 0);
    [_tableView reloadData];
    
    // 为了headerview在展示时随cell一起滚动，因此把第一个header作为预留空间
    [_tableView setContentOffset:CGPointMake(0, M_GT_PROFILER_SECTION_HEIGHT) animated:NO];
}


- (void)initUI
{
    [self initNavBarUI];
    [self initTimeUI];
}

- (NSArray *)rightBarButtonItems
{
    UIView *barView = nil;
    UIButton *barBtn = nil;
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 28, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 28, 44.0)];
    [barBtn addTarget:self action:@selector(onProfilerSwitch) forControlEvents:UIControlEventTouchUpInside];
    [barBtn.titleLabel setFont:[UIFont systemFontOfSize:12]];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 0, 10, 4)];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    if ([[GTLogConfig sharedInstance] profilerSwitch]) {
        [barBtn setImage:[GTImage imageNamed:@"gt_stop" ofType:@"png"] forState:UIControlStateNormal];
        NSArray *array = [NSArray arrayWithObjects:bar1, nil];
        [bar1 release];
        
        return array;
    } else {
        [barBtn setImage:[GTImage imageNamed:@"gt_start" ofType:@"png"] forState:UIControlStateNormal];
    }
    
    
    if ([[self section] count] == 0) {
        NSArray *array = [NSArray arrayWithObjects:bar1, nil];
        [bar1 release];
        
        return array;
    }
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn addTarget:self action:@selector(onSaveTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 5, 10, 5)];
    [barBtn setImage:[GTImage imageNamed:@"gt_save" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_save_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar2 = nil;
    bar2 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 34, 44.0)];
    [barBtn addTarget:self action:@selector(onClearTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 5, 10, 5)];
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

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [[self navigationController] setNavigationBarHidden:YES];
    
    [self handleProfilerModify:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleProfilerModify:) name:M_GT_NOTIFICATION_PROFILER_MOD object:nil];
   
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_PROFILER_MOD object:nil];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)updateTable:(BOOL)needBottom
{
    CGPoint offset = _tableView.contentOffset;
    CGRect bounds = _tableView.bounds;
    CGSize size = _tableView.contentSize;
    UIEdgeInsets inset = _tableView.contentInset;
    
    CGFloat currentOffset = offset.y + bounds.size.height - inset.bottom;
    CGFloat maximumOffset = size.height;
    
    if ((maximumOffset - currentOffset) == 0.0) {
        // then we are at the end 底部
        needBottom = YES;
        
    } else if (currentOffset == 0) {
        // then we are at the top 顶部
        needBottom = NO;
    }
    
    [_tableView reloadData];
    
    if (needBottom)
    {
        [self scrollToBottom];
    }
    
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


-(void)scrollToBottom
{
    CGRect bounds = _tableView.bounds;
    CGSize size = _tableView.contentSize;
    UIEdgeInsets inset = _tableView.contentInset;
    
    CGFloat bottomOffset = size.height - bounds.size.height + inset.bottom;
    
    // 已经到底，不需要滑动
    if ([self getOffset] <= 0.0) {
        return;
    }
    
    [_tableView setContentOffset:CGPointMake(0, bottomOffset) animated:YES];
}


- (void)handleProfilerModify:(NSNotification *)n
{
    [self updateTable:NO];
}

- (NSArray *)section {
    return [[[GTProfiler sharedInstance] analyseList] keys];
}

- (NSArray *)content:(NSUInteger)section {
    id key = [self.section objectAtIndex:section];
    return [[[[GTProfiler sharedInstance] analyseList] objectForKey:key] keys];
}


- (id) contentObject:(NSIndexPath *)indexPath {
    id key1 = [self.section objectAtIndex:indexPath.section];
    id key2 = [[self content:indexPath.section] objectAtIndex:indexPath.row];
    
    return [[[[GTProfiler sharedInstance] analyseList] objectForKey:key1] objectForKey:key2];
}


#pragma mark -
#pragma mark UITableViewDelegate
- (UIView *)tableViewForHeader
{
    // 为了headerview在展示时随cell一起滚动，因此这里需要预留空间
    CGFloat offsetY = M_GT_PROFILER_SECTION_HEIGHT;
    UIView *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0,0, _tableView.bounds.size.width, offsetY)] autorelease];
    
	return headerView;
}

- (UIView *)tableViewForFooter
{
    UIView  *footerView = nil;
    UILabel *info = nil;
    CGFloat width = _tableView.bounds.size.width;
    if ([[GTLogConfig sharedInstance] profilerSwitch]) {
        footerView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, width, 30.0f)] autorelease];
        
        info = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, width, 30.0f)];
        [info setBackgroundColor:[UIColor clearColor]];
        [info setTextColor:[UIColor whiteColor]];
        info.textAlignment = NSTextAlignmentCenter;
        [info setFont:[UIFont systemFontOfSize:15]];
        [info setText:[NSString stringWithFormat:@"%@",M_GT_LOCALSTRING(M_GT_PROFILER_COUNTING_KEY)]];
        [footerView addSubview:info];
        [info release];
        
        
        UIActivityIndicatorView *progressInd = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle: UIActivityIndicatorViewStyleGray];
        
        progressInd.center = CGPointMake(width/2 - 40,15);
        [footerView addSubview:progressInd];
        [progressInd release];
        
        [progressInd startAnimating];
        return footerView;
    } else {
        if ([[self section] count] == 0) {
            CGFloat height = _tableView.bounds.size.height;
            footerView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, width, height)] autorelease];
            
            info = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, width, height)];
            [info setBackgroundColor:[UIColor clearColor]];
            [info setTextColor:[UIColor whiteColor]];
            info.textAlignment = NSTextAlignmentCenter;
            [info setFont:[UIFont systemFontOfSize:20]];
            [info setText:[NSString stringWithFormat:@"%@",M_GT_LOCALSTRING(M_GT_PROFILER_NOT_START_KEY)]];
            [footerView addSubview:info];
            [info release];
            
            return footerView;
        }
        
        return nil;
    }
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	CGSize bound = CGSizeMake( tableView.bounds.size.width, 0.0f );
	return [GTLogAnalyseCell cellSize:[self contentObject:indexPath] bound:bound].height;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
	return M_GT_PROFILER_SECTION_HEIGHT;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    CGFloat width = CGRectGetWidth(tableView.bounds);
    CGFloat height = [self tableView:tableView heightForHeaderInSection:section];
    UIView *container = [[[UIView alloc] initWithFrame:CGRectMake(0,0,width,height)] autorelease];
    UILabel *titleLabel =[[UILabel alloc] initWithFrame:CGRectMake(0,0,width,height)];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont systemFontOfSize:15];
    titleLabel.textColor = M_GT_LABEL_COLOR;
    titleLabel.text = [self.section objectAtIndex:section];
    [container addSubview:titleLabel];
    [titleLabel release];
    
	return container;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 5.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *footerView = nil;
    CGFloat width = CGRectGetWidth(tableView.bounds);
    CGFloat height = [self tableView:tableView heightForFooterInSection:section];
    footerView = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, width, height)] autorelease];
    [footerView setBackgroundColor:[UIColor clearColor]];
    return footerView;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

#pragma mark - Table view data source

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return [self.section objectAtIndex:section];
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self.section count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self content:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    
    GTLogAnalyseCell * cell = (GTLogAnalyseCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTLogAnalyseCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}

    [cell bindData:[self contentObject:indexPath]];
    return cell;

}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];
    GTProfilerDetail *logRec = [self contentObject:indexPath];
    
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    GTLogAnalyseCell * cell = (GTLogAnalyseCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    ;
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    cell.backgroundColor = M_GT_SELECTED_COLOR;
    
    GTProfilerDetailBoard * board = [[GTProfilerDetailBoard alloc] initWithContent:[logRec key] forKey:[self.section objectAtIndex:indexPath.section]];
    if ( board )
    {
        [self.navigationController pushViewController:board animated:YES];
        [board release];
    }
    
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
}


- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    // 为了headerview在展示时随cell一起滚动，因此这里需要做处理
    CGFloat sectionHeaderHeight = M_GT_PROFILER_SECTION_HEIGHT;
    
    if ((scrollView.contentOffset.y <= sectionHeaderHeight) && (scrollView.contentOffset.y >= 0))
    {
        scrollView.contentInset = UIEdgeInsetsMake(-scrollView.contentOffset.y, 0, 0, 0);
    } else if (scrollView.contentOffset.y >= sectionHeaderHeight) {
        scrollView.contentInset = UIEdgeInsetsMake(-sectionHeaderHeight, 0, 0, 0);
    }
}


#pragma mark - Button Action

- (void)onProfilerSwitch
{
    BOOL profilerSwitch = [[GTLogConfig sharedInstance] profilerSwitch];
	[[GTLogConfig sharedInstance] setProfilerSwitch:!profilerSwitch];
    [self.navItem setRightBarButtonItems:[self rightBarButtonItems] animated:YES];
    _tableView.tableFooterView = [self tableViewForFooter];
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
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_SAVE_TITLE_KEY)                                                             message:M_GT_LOCALSTRING(M_GT_ALERT_INPUT_SAVED_FILE_KEY)
                                                            delegate:self
                                                   cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY)
                                                   otherButtonTitles:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)];
    [alertView setTag:M_GT_ALERT_TAG_SAVE];
    [alertView addTextFieldWithTag:0];
    [[alertView textFieldAtTag:0] setText:[[GTProfiler sharedInstance] fileName]];
    [alertView show];
    [alertView release];
}

#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    //clear
    if ([alertView tag] == M_GT_ALERT_TAG_CLEAR) {
        if (buttonIndex == 1) {
            [[GTProfiler sharedInstance] clearAll];
            [self.navItem setRightBarButtonItems:[self rightBarButtonItems] animated:YES];
            _tableView.tableFooterView = [self tableViewForFooter];
            [_tableView reloadData];
        }
    }
    //save
    else if ([alertView tag] == M_GT_ALERT_TAG_SAVE)
    {
        if (buttonIndex == 1) {
            UITextField *saveLogName = [alertView textFieldAtTag:0];
            [[GTProfiler sharedInstance] saveAll:[saveLogName text]];
            [_tableView reloadData];
        }
    }
    
}
@end
#endif
