//
//  GTACSettingBoard.m
//  GTKit
//
//  Created   on 13-10-18.
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
#import "GTACSettingBoard.h"
#import "GTSettingRow.h"
#import "GTSettingCell.h"
#import <QuartzCore/QuartzCore.h>
#import "GTConfig.h"
#import "GTUISwitch.h"

#import "GTLang.h"
#import "GTLangDef.h"



typedef enum {
	GTACSettingSwitch = 1,
    GTACSettingProfiler,
    GTACSettingGW
} GTACSettingRowID;


@interface GTACSettingBoard ()

@end

@implementation GTACSettingBoard

@synthesize tableView = _tableView;
@synthesize lastIndexPath = _lastIndexPath;

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
	_settings = [[NSMutableArray alloc] init];
    GTSettingRow *row;
    {
        NSMutableArray *array = [NSMutableArray array];
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowSwitch title:M_GT_LOCALSTRING(M_GT_SETTING_AC_SHOW_KEY) info:@""];
        [row setRowID:GTACSettingSwitch];
        [array addObject:row];
        [row release];
        
        [_settings addObject:array];
    }
    
    {
        NSMutableArray *array = [NSMutableArray array];
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowSelected title:M_GT_LOCALSTRING(M_GT_PROFILER_KEY) info:@""];
        [row setRowID:GTACSettingProfiler];
        [array addObject:row];
        [row release];
        
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowSelected title:@"G&W" info:@""];
        [row setRowID:GTACSettingGW];
        [array addObject:row];
        [row release];
        
        [_settings addObject:array];
        self.lastIndexPath = [NSIndexPath indexPathForRow:[[GTConfig sharedInstance] acSwtichIndex] inSection:1];
    }
    
    
}

- (void)unload
{
//    NSLog(@"%s in", __FUNCTION__);
    
	[_settings removeAllObjects];
	[_settings release];
    [_tableView release];
    self.lastIndexPath = nil;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [self initUI];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -

- (void)initUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [[self navigationController] setNavigationBarHidden:YES];
    [self createTopBar];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_SETTING_AC_KEY)];
    
    CGRect frame = M_GT_APP_FRAME;
    
    frame.origin.x = 10;
    frame.origin.y = frame.origin.y + 5;
    frame.size.height = frame.size.height - 25.0f;
    frame.size.width = frame.size.width - 2 *frame.origin.x;
    
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
    [self.view addSubview:_tableView];
}


#pragma mark -

- (NSArray *)section {
    return _settings;
}

- (NSArray *)content:(NSUInteger)section {
    return [[self section] objectAtIndex:section];
}


- (id) contentObject:(NSIndexPath *)indexPath {
    return [[self content:indexPath.section] objectAtIndex:indexPath.row];
}

#pragma mark -

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 1) {
        return 44.0f;
    }
	return 15.0f;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    CGFloat width = CGRectGetWidth(tableView.bounds);
    CGFloat height = [self tableView:tableView heightForHeaderInSection:section];
    UIView *container = [[[UIView alloc] initWithFrame:CGRectMake(0,0,width,height)] autorelease];
    [container setBackgroundColor:[UIColor clearColor]];
    
    if (section == 1) {
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(5.0f, 5.0f, width, height-5)];
		label.font = [UIFont systemFontOfSize:15.0f];
		label.textColor = M_GT_LABEL_COLOR;
		label.backgroundColor = [UIColor clearColor];
		label.textAlignment = NSTextAlignmentLeft;
        label.text = M_GT_LOCALSTRING(M_GT_SETTING_AC_Q_SWITCH_KEY);
		[container addSubview:label];
		[label release];
    }
	return container;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [[self section] count];
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	CGSize bound = CGSizeMake( tableView.bounds.size.width, 0.0f );
	return [GTSettingCell cellSize:nil bound:bound].height;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [[self content:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTSettingCell * cell = (GTSettingCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTSettingCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    
    GTSettingRow * data = [self contentObject:indexPath];
    [cell bindData:data];
    
    if ([data rowType] == GTSettingRowSwitch) {
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        GTUISwitch *switchView = [[[GTUISwitch alloc] initWithFrame:CGRectMake(0, 0, 80, 30)] autorelease];
        switchView.enabled = YES;
        
        switch ([data rowID]) {
            case GTACSettingSwitch:
            {
                switchView.on = [[GTConfig sharedInstance] showAC];
                break;
            }
            default:
                break;
        }
        
        [switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
        
        [switchView setTag:[data rowID]];
        
        cell.accessoryView = switchView;
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];
    GTSettingRow * data = [self contentObject:indexPath];
    if ([data rowType] == GTSettingRowChildView) {
        GTUIViewController * board = [(GTUIViewController *)[NSClassFromString( [data boardName] ) alloc] init];
        if ( board )
        {
            [board setNavTitle:[data title]];
            [self.navigationController pushViewController:board animated:YES];
            [board release];
        }
    } else if ([data rowType] == GTSettingRowSelected) {
        if(self.lastIndexPath){
            UITableViewCell *lastcell = [tableView cellForRowAtIndexPath:self.lastIndexPath];
            lastcell.backgroundColor = M_GT_CELL_BKGD_COLOR;
            [lastcell.textLabel setTextColor:[UIColor grayColor]];
        }
        
        UITableViewCell* newCell = [tableView cellForRowAtIndexPath:indexPath];
        newCell.backgroundColor = M_GT_SELECTED_COLOR;
        [newCell.textLabel setTextColor:[UIColor whiteColor]];
        self.lastIndexPath = indexPath;
        [[GTConfig sharedInstance] setAcSwtichIndex:indexPath.row];
    }
	
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if ([self.lastIndexPath isEqual:indexPath] ) {
        cell.backgroundColor = M_GT_SELECTED_COLOR;
        [cell.textLabel setTextColor:[UIColor whiteColor]];
    } else {
        cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
        [cell.textLabel setTextColor:[UIColor grayColor]];
    }
    
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

#pragma mark -

- (IBAction) switchAction:(id)sender
{
    GTUISwitch *switchView = (GTUISwitch*)sender;
    
    switch ([switchView tag]) {
        case GTACSettingSwitch:
        {
            [[GTConfig sharedInstance] setShowAC:switchView.on];
            break;
        }
        default:
            break;
    }
    
}

@end
#endif
