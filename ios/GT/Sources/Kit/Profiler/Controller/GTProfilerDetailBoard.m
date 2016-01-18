//
//  GTProfilerDetailBoard.m
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

#import <QuartzCore/QuartzCore.h>
#import "GTProfilerDetailBoard.h"
#import "GTDebugDef.h"
#import "GTLog.h"
#import "GTCommonCell.h"
#import "GTImage.h"
#import "GTProfilerValue.h"
#import "GTLang.h"
#import "GTLangDef.h"

@interface GTLogProfilerCell : GTCommonCell
{
    UILabel *		_date;
	UILabel *		_content;
    UILabel *		_time;
}
@end

@implementation GTLogProfilerCell

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 30.0f );
}


- (void)cellLayout
{
	_content.frame = CGRectMake( 3.0f, 2.0f, self.bounds.size.width - 80.0f, 30.0f );
	_time.frame = CGRectMake( self.bounds.size.width - 80.0f, 2.0f, 75.0f, 30.0f );
}

- (void)load
{
	[super load];

	_content = [[UILabel alloc] init];
	_content.font = [UIFont systemFontOfSize:14.0];
	_content.textColor = [UIColor whiteColor];
	_content.textAlignment = NSTextAlignmentLeft;
    _content.backgroundColor = [UIColor clearColor];
	[self addSubview:_content];
    
    _time = [[UILabel alloc] init];
	_time.font = [UIFont systemFontOfSize:12.0];
	_time.textColor = [UIColor orangeColor];
	_time.textAlignment = NSTextAlignmentRight;
    _time.backgroundColor = [UIColor clearColor];
	[self addSubview:_time];
}

- (void)unload
{
	M_GT_SAFE_FREE( _content );
	M_GT_SAFE_FREE( _time );
	
	[super unload];
}

- (void)bindData:(NSObject *)data
{
    [super bindData:data];
    GTProfilerValue *value = (GTProfilerValue *)data;
    NSString *str = [NSString stringWithFormat:@"%.3f", [value time]];
    [_time setText:NSLocalizedString(str,)];
    
    return;
}

- (void)clearData
{
	[_time setText:nil];
	[_content setText:nil];
}

@end

#pragma mark -

@implementation GTProfilerDetailBoard

@synthesize list = _list;
@synthesize content = _content;
@synthesize key = _key;
@synthesize tableView = _tableView;

- (id)initWithContent:(NSString *)content forKey:(NSString *)key
{
    self = [super init];
    if (self) {
        [self setContent:content];
        [self setKey:key];
        _showObj = [[GTProfiler sharedInstance] getLogAnalyse:[self content] forKey:[self key]];
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
    _plotDataBuf = [[GTPlotsData alloc] init];
}

- (void)unload
{
    self.list = nil;
    [_tableView release];
    [_plotDataBuf release];
}

- (void)initNavBarUI
{
    [self createTopBar];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_TIME_DETAIL_TITLE_KEY)];
    
    UIBarButtonItem *btn        = nil;
    UIView          *backView   = nil;
    UIButton        *backBtn    = nil;
    
    backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_NAVBAR_HEIGHT, M_GT_NAVBAR_HEIGHT)];
    
    backBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, M_GT_NAVBAR_HEIGHT, M_GT_NAVBAR_HEIGHT)];
    [backBtn setImageEdgeInsets:UIEdgeInsetsMake((M_GT_NAVBAR_HEIGHT - 24)/2, (M_GT_NAVBAR_HEIGHT-24)/2, (M_GT_NAVBAR_HEIGHT - 24)/2, (M_GT_NAVBAR_HEIGHT-24)/2)];
    [backBtn addTarget:self action:@selector(onBackClicked:) forControlEvents:UIControlEventTouchUpInside];
    [backBtn setImage:[GTImage imageNamed:@"gt_back" ofType:@"png"] forState:UIControlStateNormal];
    [backBtn setImage:[GTImage imageNamed:@"gt_back_sel" ofType:@"png"] forState:UIControlStateSelected];
    [backView addSubview:backBtn];
    [backBtn release];
    
    btn = [[[UIBarButtonItem alloc] initWithCustomView:backView] autorelease];
    [backView release];
    
    [self.navItem setLeftBarButtonItem:btn];
}


- (void)initDetailUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];

    _timeView = [[[GTProfilerSummaryView alloc] initWithFrame:M_GT_BOARD_FRAME] autorelease];
    [_timeView bindData:_showObj];
    [_timeView setPlotsDataSource:self];
    [self.view addSubview:_timeView];
    [_timeView update];
}
- (void)initUI
{
    [self initNavBarUI];
    [self initDetailUI];
    
    return;
    
    CGRect rect = M_GT_APP_FRAME;
    rect.origin.y = rect.origin.y + 40;
    rect.size.height = rect.size.height - 40.0f;
    
    _tableView = [[UITableView alloc] initWithFrame:rect style:UITableViewStylePlain];
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
    [self.view addSubview:_tableView];
}

- (NSArray *)rightBarButtonItems
{
    UIView *barView = nil;
    UIButton *barBtn = nil;
    
    barView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 28, 44.0)];
    barBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 28, 44.0)];
    [barBtn addTarget:self action:@selector(onSaveTouched:) forControlEvents:UIControlEventTouchUpInside];
    [barBtn setImageEdgeInsets:UIEdgeInsetsMake(10, 0, 10, 4)];
    [barBtn setImage:[GTImage imageNamed:@"gt_save" ofType:@"png"] forState:UIControlStateNormal];
    [barBtn setImage:[GTImage imageNamed:@"gt_save_sel" ofType:@"png"] forState:UIControlStateSelected];
    [barView addSubview:barBtn];
    [barBtn release];
    
    UIBarButtonItem *bar1 = nil;
    bar1 = [[UIBarButtonItem alloc] initWithCustomView:barView];
    [barView release];
    
    NSArray *array = [NSArray arrayWithObjects:bar1, nil];
    [bar1 release];
    
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
    [self setTitle:M_GT_LOCALSTRING(M_GT_TIME_TITLE_KEY)];
    _tableView.contentInset = UIEdgeInsetsMake(0.0f, 0, 44.0f, 0);
}


- (void)handleProfilerModify:(NSNotification *)n
{
    _showObj = [[GTProfiler sharedInstance] getLogAnalyse:[self content] forKey:[self key]];
    [self setList:[_showObj timeArray]];
    [_timeView update];
    [_timeView bindData:_showObj];
    [_tableView reloadData];
}

- (NSArray *)content:(NSUInteger)section {
    return _list;
}

- (id) contentObject:(NSIndexPath *)indexPath {
    return [_list objectAtIndex:indexPath.row];
}

#pragma mark -
#pragma mark UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 30.0f;
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
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTLogProfilerCell * cell = (GTLogProfilerCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTLogProfilerCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}

    [cell bindData:[self contentObject:indexPath]];
    return cell;

}

#pragma mark - 数据缓冲区相关逻辑

//初始化缓冲区，使用内存中的历史数据信息
- (void)initPlotDataBuf
{
    NSMutableArray *histroy = [_showObj timeArray];
    NSUInteger count = [histroy count];
    
    NSMutableArray *dates = [[NSMutableArray alloc] initWithCapacity:count];
    NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTProfilerValue *obj = nil;
    
    for (int i = 0; i < count; i++) {
        obj = [histroy objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
        [values addObject:[NSNumber numberWithDouble:[obj time]]];
    }
    
    [_plotDataBuf setDates:dates];
    [dates release];
    
    //这里支持多曲线，所以输入对应为二维数组
    [_plotDataBuf setCurves:[NSMutableArray arrayWithObjects:values, nil]];
    [values release];
    
    //数据都在内存里，故这里偏移为0
    [_plotDataBuf setHistoryIndex:0];
    [_plotDataBuf setHistoryCnt:count];
}

#pragma mark - GTPlotsViewDataSource

- (GTPlotsData *)chartDatas
{
    [self initPlotDataBuf];
    return _plotDataBuf;
}


#pragma mark Button Action

- (void)onSaveTouched:(id)sender
{
    GTUIAlertView * alertView = [[GTUIAlertView alloc] initWithTitle:M_GT_LOCALSTRING(M_GT_ALERT_SAVE_TITLE_KEY)                                                      message:M_GT_LOCALSTRING(M_GT_ALERT_INPUT_SAVED_FILE_KEY)
                                                            delegate:self
                                                   cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_CANCEL_KEY)
                                                   otherButtonTitles:M_GT_LOCALSTRING(M_GT_ALERT_OK_KEY)];
    [alertView setTag:M_GT_ALERT_TAG_SAVE];
    [alertView addTextFieldWithTag:0];
    [[alertView textFieldAtTag:0] setText:[_showObj fileName]];
    [alertView show];
    [alertView release];
}

#pragma mark GTUIAlertViewDelegate

//根据被点击按钮的索引处理点击事件
- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    //save
    if ([alertView tag] == M_GT_ALERT_TAG_SAVE)
    {
        if (buttonIndex == 1) {
            UITextField *saveLogName = [alertView textFieldAtIndex:0];
            [_showObj setFileName:[saveLogName text]];
            [_showObj saveAll:[saveLogName text]];
            [_tableView reloadData];
        }
    }
    
}

- (void)onBackClicked:(id)sender
{
    //返回
    [self.navigationController popViewControllerAnimated:YES];
    
}

@end

#endif
