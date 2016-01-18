//
//  GTToolBoard.m
//  GTKit
//
//  Created   on 12-11-18.
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

#import "GTSettingBoard.h"
#import <QuartzCore/QuartzCore.h>
#import "GTLog.h"
#import "GTUISwitch.h"
#import "GTSettingRow.h"
#import "GTSettingCell.h"
#import "GTConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"



#pragma mark -

typedef enum {
	GTSettingFloatingSwitch = 1
} GTSettingRowID;


@interface GTSettingBoard ()

@end

@implementation GTSettingBoard

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
	_settings = [[NSMutableArray alloc] init];
    GTSettingRow *row;
    {
        NSMutableArray *array = [NSMutableArray array];
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowChildView title:M_GT_LOCALSTRING(M_GT_SETTING_AC_KEY) info:@"" ];
        [row setBoardName:@"GTACSettingBoard"];
        [array addObject:row];
        [row release];
        
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowChildView title:M_GT_LOCALSTRING(M_GT_SETTING_LOG_KEY) info:@"" ];
        [row setBoardName:@"GTLogSettingBoard"];
        [array addObject:row];
        [row release];
        
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowChildView title:M_GT_LOCALSTRING(M_GT_SETTING_PARA_KEY) info:@"" ];
        [row setBoardName:@"GTParaSettingBoard"];
        [array addObject:row];
        [row release];
        
        [_settings addObject:array];
    }
    
    {
        NSMutableArray *array = [NSMutableArray array];
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowChildView title:M_GT_LOCALSTRING(M_GT_SETTING_ABOUT_KEY) info:@"" ];
        [row setBoardName:@"GTAboutBoard"];
        [array addObject:row];
        [row release];
        
        [_settings addObject:array];
    }
    
}

- (void)unload
{
    [_settings removeAllObjects];
	
    M_GT_SAFE_FREE(_settings);
    M_GT_SAFE_FREE(_tableView);
}

- (void)initUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [[self navigationController] setNavigationBarHidden:YES];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_SETTING_KEY)];
    
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
    
    _tableView.contentInset = UIEdgeInsetsMake(0.0f, 0, 44.0f, 0);
    [_tableView reloadData];
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
	return 15.0f;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    CGFloat width = CGRectGetWidth(tableView.bounds);
    CGFloat height = [self tableView:tableView heightForHeaderInSection:section];
    UIView *container = [[[UIView alloc] initWithFrame:CGRectMake(0,0,width,height)] autorelease];
    [container setBackgroundColor:[UIColor clearColor]];
    
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
        GTUISwitch *switchView = [[GTUISwitch alloc] initWithFrame:CGRectMake(0, 0, 80, 30)];
        switchView.enabled = YES;
        
        switch ([data rowID]) {
            
            
            default:
                break;
        }
        
        [switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
        
        [switchView setTag:[data rowID]];
        
        cell.accessoryView = switchView;
        [switchView release];
    }

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    GTSettingRow * data = [self contentObject:indexPath];
    if ([data rowType] == GTSettingRowChildView) {
        GTUIViewController * board = [(GTUIViewController *)[NSClassFromString( [data boardName] ) alloc] init];
        if ( board )
        {
            [board setNavTitle:[data title]];
            [self.navigationController pushViewController:board animated:YES];
            [board release];
        }
    }
	
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

#pragma mark -
- (IBAction) switchAction:(id)sender
{
    GTUISwitch *switchView = (GTUISwitch*)sender;
    
    switch ([switchView tag]) {
        
        default:
            break;
    }
    
}

@end

#endif
