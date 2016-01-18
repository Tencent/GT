//
//  GTCrashBoard.m
//  GTKit
//
//  Created   on 13-6-27.
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

#import "GTCrashBoard.h"
#import "GTCrashFileHandler.h"
#import "GTCrashDetailBoard.h"
#import <QuartzCore/QuartzCore.h>
#import "GTCommonCell.h"
#import "GTConfig.h"
#import "GTDebugDef.h"

@interface GTCrashBoard ()

@end

@implementation GTCrashBoard

@synthesize dataSourceArray = _dataSourceArray;
@synthesize tableView = _tableView;

- (id) init
{
    self = [super init];
    if (self) {
        
    }
    
    return self;
}


- (void)dealloc
{
    [_dataSourceArray release];
    [_tableView release];
    [super dealloc];
}


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)initUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [self createTopBar];
    [self setNavTitle:@"Crash Record"];
    
    CGRect frame = M_GT_BOARD_FRAME;
    
    //菜单数组内容
    _dataSourceArray = [[NSMutableArray alloc] initWithArray:[GTCrashFileHandler getCrashFileList]];
    
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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier]; // If no cell is available, create a new one using the given identifier.
    if (cell == nil)
    {
        // Use the default cell style.
        cell = [[[GTCommonCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier] autorelease];
    }
    // Set up the cell.
    NSString *cellItem = [_dataSourceArray objectAtIndex:indexPath.row]; //根据行号显示菜单文本
    cell.textLabel.text = cellItem;
    cell.textLabel.textColor =[UIColor whiteColor];
    cell.textLabel.font = [UIFont systemFontOfSize:15];
    
    cell.textLabel.backgroundColor =[UIColor clearColor];
    return cell;
}



- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    //得到当前文件的完整路径
    //获取到document下面的文件：
    NSString* filename=[_dataSourceArray objectAtIndex:indexPath.row];
    NSString *crashDirPath = [NSString stringWithFormat:@"%@/%@", [[GTConfig sharedInstance] usrDir], M_GT_CRASH_DIR];
    NSString *filePath = [NSString stringWithFormat:@"%@/%@",crashDirPath,filename];
    
    BOOL delSuccess = [GTCrashFileHandler removeFileInPath:filePath];
    if (!delSuccess) {
        NSLog(@"delete failed! filePath:%@", filePath);
    }
    
    [_dataSourceArray removeObjectAtIndex:indexPath.row];
    [tableView reloadData];
    
}

#pragma mark - UITableViewDelegate

//列表横向滑动产生delete操作
- (UITableViewCellEditingStyle)tableView:(UITableView *)tv editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
	return UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger index = indexPath.row;
    NSString* filename=[_dataSourceArray objectAtIndex:index];
    
    //获取到document下面的文件：
    NSString *crashDirPath = [NSString stringWithFormat:@"%@/%@", [[GTConfig sharedInstance] usrDir], M_GT_CRASH_DIR];
    NSString *filePath = [NSString stringWithFormat:@"%@/%@",crashDirPath,filename];
    
    GTCrashDetailBoard *detail = [[[GTCrashDetailBoard alloc] init] autorelease];
    detail.filePath = filePath;
    //进入crash详细页面
    [self.navigationController pushViewController:detail animated:YES];
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

@end
#endif
