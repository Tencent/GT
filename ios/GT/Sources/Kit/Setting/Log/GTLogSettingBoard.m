//
//  GTLogSettingBoard.m
//  GTKit
//
//  Created   on 13-4-15.
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
#import "GTLogSettingBoard.h"
#import "GTSettingRow.h"
#import "GTSettingCell.h"
#import "GTLogConfig.h"
#import <QuartzCore/QuartzCore.h>
#import "GTLang.h"
#import "GTLangDef.h"



typedef enum {
	GTLogSettingSwitch = 1,
    GTLogSettingRedirectSwitch,
	GTLogSettingAutoSave
} GTLogSettingRowID;


@interface GTLogSettingBoard ()

@end

@implementation GTLogSettingBoard

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
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowSwitch title:M_GT_LOCALSTRING(M_GT_SETTING_LOG_SWITCH_KEY) info:@""];
        [row setRowID:GTLogSettingSwitch];
        [array addObject:row];
        [row release];
        
        row = [[GTSettingRow alloc] initSettingType:GTSettingRowSwitch title:M_GT_LOCALSTRING(M_GT_SETTING_LOG_AUTO_SAVE_KEY) info:@""];
        [row setRowID:GTLogSettingAutoSave];
        [array addObject:row];
        [row release];
        
        [_settings addObject:array];
    }
    
    
}

- (void)unload
{
	[_settings removeAllObjects];
	[_settings release];
    [_tableView release];
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
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_SETTING_LOG_KEY)];
    
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
        GTUISwitch *switchView = [[[GTUISwitch alloc] initWithFrame:CGRectMake(0, 0, 80, 30)] autorelease];
        switchView.enabled = YES;
        
        switch ([data rowID]) {
            case GTLogSettingSwitch:
            {
                switchView.on = [[GTLogConfig sharedInstance] logSwitch];
                break;
            }
            case GTLogSettingAutoSave:
            {
                if ([[GTLogConfig sharedInstance] logSwitch] == NO) {
                    [[GTLogConfig sharedInstance] setBufferAutoSave:NO];
                    switchView.enabled = NO;
                }
                switchView.on = [[GTLogConfig sharedInstance] bufferAutoSave];
                _autoSaveSwitch = switchView;
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
        case GTLogSettingSwitch:
        {
            [[GTLogConfig sharedInstance] setLogSwitch:switchView.on];
            
            //如果总开关关闭则自动保存日志开关设置关闭且不可操作
            if (switchView.on == NO) {
                [[GTLogConfig sharedInstance] setBufferAutoSave:NO];
                _autoSaveSwitch.on = [[GTLogConfig sharedInstance] bufferAutoSave];
                _autoSaveSwitch.enabled = NO;
            } else {
                _autoSaveSwitch.enabled = YES;
            }
            break;
        }
        case GTLogSettingAutoSave:
        {
            [[GTLogConfig sharedInstance] setBufferAutoSave:switchView.on];
            break;
        }
        default:
            break;
    }
    
}

@end
#endif
