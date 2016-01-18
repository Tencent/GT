//
//  GTNSLogBoard.m
//  GTKit
//
//  Created   on 13-7-9.
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
#import "GTNSLogBoard.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>
#import "GTUISwitch.h"
#import "GTLogConfig.h"
#import "GTNSLog.h"
#import "GTImage.h"
#import "GTUIAlertView.h"
#import "GTCommonCell.h"
#import "GTLang.h"
#import "GTLangDef.h"

@implementation GTNSLogCell

@synthesize row = _row;

+ (float)cellHeight:(NSObject *)data bound:(CGSize)bound
{
    NSString *str = (NSString *)data;
    CGSize constrainedToSize = CGSizeMake(bound.width, 900);
    NSString *text = [NSString stringWithFormat:@"%5d %@", 9999, str];
    
    CGSize size = [text sizeWithFont:[UIFont systemFontOfSize:14.0f]
                   constrainedToSize:constrainedToSize
                       lineBreakMode:NSLineBreakByWordWrapping];
    return (size.height + 10);
}

- (void)drawRect:(CGRect)rect
{
//    CGSize size;
    CGFloat width = 0;
    CGFloat viewWidth = self.bounds.size.width;
    CGFloat viewHeight = self.bounds.size.height;
    
    NSString *data = (NSString *)_cellData;
    
    width = 0;
    NSString *rowNo = [NSString stringWithFormat:@"%5lu ", (unsigned long)_row];
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor whiteColor].CGColor);
    [rowNo drawInRect:CGRectMake( 0.0f, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    width += 35;
    NSMutableString *str = [NSMutableString stringWithCapacity:1];
    //一个空格占用四个width
    for (int i = 0; i < width/4 + 1; i++) {
        [str appendString:@" "];
    }
    [str appendString:data];
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    [str drawInRect:CGRectMake( 0.0f, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
}

- (void)bindData:(NSObject *)data
{
    [super bindData:data];
    
    [self setNeedsDisplay];
    return;
}

@end

@interface GTNSLogBoard ()

@end

@implementation GTNSLogBoard

@synthesize dataSourceArray = _dataSourceArray;
@synthesize tableView = _tableView;
@synthesize isTouched = _isTouched;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        _isTouched = false;
    }
    return self;
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
    
}


- (void)unload
{
    [_tableView release];
}

- (void)initUI
{
    CGRect rect = M_GT_BOARD_FRAME;
    [self.view setBackgroundColor:M_GT_CELL_BKGD_COLOR];
    
    CGRect frame;
    
    frame.origin.x = 5.0f;
    frame.origin.y = rect.origin.y + 5.0f;
    frame.size.width = rect.size.width - 2*5.0f - 80;
    frame.size.height = 30.0f;
    
    
    UILabel *label = [[UILabel alloc] initWithFrame:frame];
    label.font = [UIFont systemFontOfSize:15.0];
    label.text = @"NSLog Redirect Switch";
    label.textColor = M_GT_LABEL_COLOR;
    label.textAlignment = NSTextAlignmentLeft;
    label.backgroundColor = [UIColor clearColor];
    [self.view addSubview:label];
    [label release];
    
    frame.origin.x = rect.origin.x + rect.size.width - 90;
    frame.origin.y = rect.origin.y + 5;
    frame.size.width = 80;
    frame.size.height = 30;
    
    GTUISwitch *switchView = [[GTUISwitch alloc] initWithFrame:frame];
    switchView.enabled = YES;
    switchView.on = [[GTNSLog sharedInstance] nslogSwitch];
    [switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:switchView];
    [switchView release];
    
    [self switchAction:switchView];
    
    frame.origin.x = rect.origin.x + 5;
    frame.origin.y = frame.origin.y + 40;
    frame.size.width = rect.size.width - 10;
    frame.size.height = rect.size.height - 40;
    
    _tableView = [[UITableView alloc] initWithFrame:frame];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
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
}



- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    [self initUI];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [[GTNSLog sharedInstance] unobserveTick];
    [[GTNSLog sharedInstance] observeTick:M_GT_NSLOG_PLUGIN_TIME];
    if ([[GTNSLog sharedInstance] nslogSwitch]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateText:) name:M_GT_NOTIFICATION_NSLOG_MOD object:nil];
    }
    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_NSLOG_MOD object:nil];
    [[GTNSLog sharedInstance] unobserveTick];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSArray *)rightBarButtonItems
{
    UIView *barView = nil;
    UIButton *barBtn = nil;
    
    
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
    
    NSArray *array = [NSArray arrayWithObjects:bar2, bar3, nil];
    [bar2 release];
    [bar3 release];
    
    return array;
}

#pragma mark -
- (IBAction) switchAction:(id)sender
{
    GTUISwitch *switchView = (GTUISwitch*)sender;
    
    [[GTNSLog sharedInstance] setNslogSwitch:switchView.on];
    
    if (switchView.on == YES) {
        _dataSourceArray = [[GTNSLog sharedInstance] logArray];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateText:) name:M_GT_NOTIFICATION_NSLOG_MOD object:nil];
    } else {
        _dataSourceArray = nil;
        [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_NSLOG_MOD object:nil];
    }
    
    [self.tableView reloadData];
    
}

- (void)updateText:(NSNotification *)n
{
    if ([[GTNSLog sharedInstance] nslogSwitch]) {
        [self.tableView reloadData];
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
    [[alertView textFieldAtTag:0] setText:[[GTNSLog sharedInstance] fileName]];
    [alertView show];
    [alertView release];
}

#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    //clear
    if ([alertView tag] == M_GT_ALERT_TAG_CLEAR) {
        if (buttonIndex == 1) {
            [[GTNSLog sharedInstance] clearAll];
            [self.tableView reloadData];
            [self.navItem setRightBarButtonItems:[self rightBarButtonItems] animated:YES];
        }
    }
    //save
    else if ([alertView tag] == M_GT_ALERT_TAG_SAVE)
    {
        if (buttonIndex == 1) {
            UITextField *saveLogName = [alertView textFieldAtTag:0];
            [[GTNSLog sharedInstance] saveAll:[saveLogName text]];
        }
    }
    
}


#pragma mark - UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [_dataSourceArray count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    //本函数用于显示每行的内容
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    GTNSLogCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier]; // If no cell is available, create a new one using the given identifier.
    if (cell == nil)
    {
        cell = [[[GTNSLogCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
    }
    [cell bindData:[_dataSourceArray objectAtIndex:indexPath.row]];
    [cell setRow:indexPath.row];
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    if (_isTouched) {
        cell.backgroundColor = [UIColor clearColor];
        _isTouched = false;
    }
    return cell;
}


#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGRect bounds = tableView.bounds;
	CGSize bound = CGSizeMake( bounds.size.width, 0.0f );
	return [GTNSLogCell cellHeight:[_dataSourceArray objectAtIndex:indexPath.row] bound:bound];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {

}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    [tableView reloadData];
}


#pragma mark - NSKeyValueObserving
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
