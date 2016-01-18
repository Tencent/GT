//
//  GTPluginBoard.m
//  GTKit
//
//  Created   on 13-1-23.
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
#import "GTPluginBoard.h"
#import "GTPluginList.h"
#import "GTImage.h"
#import "GTSandboxBoard.h"
#import "GTCrashBoard.h"
#import "GTLang.h"
#import "GTLangDef.h"



@implementation GTPluginCell

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 44.0f );
}

- (void)cellLayout
{
    _iconView.frame = CGRectMake( 6.0f, 5.0f, 34.0f, 34.0f );
	_title.frame = CGRectMake( 50.0f, 5.0f, self.bounds.size.width - 56.0f, 18.0f );
	_intro.frame = CGRectMake( 50.0f, 25.0f, self.bounds.size.width - 56.0f, 14.0f );
    
    GTPlugin *obj = (GTPlugin *)self.cellData;
    if ([[obj pluginInfo] length] == 0) {
        _title.frame = CGRectMake( 50.0f, 5.0f, self.bounds.size.width - 56.0f, 34.0f );
    }
}

- (void)load
{
	[super load];
	
    self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6f];
	self.layer.borderColor = [UIColor colorWithWhite:0.2f alpha:1.0f].CGColor;
	self.layer.borderWidth = 1.0f;
    
    _iconView = [[UIImageView alloc] init];
    [self addSubview:_iconView];
    
    
	_title = [[UILabel alloc] init];
	_title.font = [UIFont systemFontOfSize:15.0];
	_title.textColor = M_GT_CELL_TEXT_COLOR;
	_title.textAlignment = NSTextAlignmentLeft;
    _title.backgroundColor = [UIColor clearColor];
	[self addSubview:_title];
    
	_intro = [[UILabel alloc] init];
	_intro.font = [UIFont systemFontOfSize:11.0];
	_intro.textColor = M_GT_CELL_TEXT_COLOR;
	_intro.textAlignment = NSTextAlignmentLeft;
    _intro.backgroundColor = [UIColor clearColor];
	[self addSubview:_intro];
}

- (void)unload
{
    M_GT_SAFE_FREE( _iconView );
	M_GT_SAFE_FREE( _title );
	M_GT_SAFE_FREE( _intro );
	
	[super unload];
}

- (void)bindData:(NSObject *)data
{
	[super bindData:data];
    
    GTPlugin *obj = (GTPlugin *)data;
    
    [_iconView setImage:[obj pluginIcon]];
    [_title setText:[obj pluginName]];
	[_intro setText:[obj pluginInfo]];
	
}

- (void)clearData
{
	[_title setText:nil];
	[_intro setText:nil];
}

@end

@implementation GTPluginBoard

@synthesize tableView = _tableView;

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

- (void)load
{
}

- (void)unload
{
    [_tableView release];
}

- (void)initUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [[self navigationController] setNavigationBarHidden:YES];
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_PLUGIN_KEY)];
    
    CGRect frame = M_GT_APP_FRAME;
    
    frame.origin.x = 10;
    frame.origin.y = frame.origin.y + 20;
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
}

#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [[[GTPluginList sharedInstance] keys] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTPluginCell * cell = (GTPluginCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTPluginCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    
    id key = [[[GTPluginList sharedInstance] keys] objectAtIndex:indexPath.row];
    [cell bindData:[[GTPluginList sharedInstance] objectForKey:key]];
    return cell;
}

#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	CGSize bound = CGSizeMake( tableView.bounds.size.width, 0.0f );
	return [GTPluginCell cellSize:nil bound:bound].height;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	[_tableView deselectRowAtIndexPath:indexPath animated:YES];
    id key = [[[GTPluginList sharedInstance] keys] objectAtIndex:indexPath.row];
    id obj = [[GTPluginList sharedInstance] objectForKey:key];
    
    if ([obj respondsToSelector:@selector(pluginView)]) {
        id board = [obj pluginView];
        
        [self.navigationController pushViewController:board animated:YES];
    }

}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

@end
#endif
