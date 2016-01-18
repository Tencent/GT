//
//  GTParaOutputBoard.m
//  GTKit
//
//  Created   on 12-12-3.
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
#import "GTParaOutputBoard.h"
#import "GTOutputList.h"
#import "GTConfig.h"
#import "GTCommonCell.h"
#import "GTDetailView.h"
#import "GTParaOutDetailBoard.h"
#import "GTParaElang.h"
#import "GTImage.h"
#import "GTUISwitch.h"
#include <unistd.h>
#import "GTParaConfig.h"
#import "GTSingleDetailBoard.h"
#import "GTMultiDetailBoard.h"
#import "GTProgressHUD.h"
#import "GTLang.h"
#import "GTLangDef.h"


#define M_GT_OUTPUT_SECTION_HEIGHT 39.0f



typedef enum {
	GTOutputInvalid = 0,
    GTOutputFloating = 0,
    GTOutputNormal,
    GTOutputDisable,
    GTOutputSectionMax
} GTOutputSectionType;

#pragma mark -

#define M_GT_TABLE_MAKE_TAG(indexPath) ((indexPath.section<<8) + indexPath.row)
#define M_GT_TABLE_GET_SECTION(tag) (tag >> 8)
#define M_GT_TABLE_GET_ROW(tag) (tag & 0xFF)

@interface GTParaOutputBoard ()

@property (nonatomic, assign) UIViewController  *viewController;


@end

@implementation GTParaOutputBoard

@synthesize viewController;
@synthesize tableView = _tableView;
@synthesize delegate = _delegate;

- (id)initWithViewController:(UIViewController*) givenViewController
{
    self = [super initWithFrame:M_GT_APP_FRAME];
    if (self) {
        self.viewController = givenViewController;
        self.delegate = (id)givenViewController;
        [self load];
        [self initUI];
        [self observeTick];
    }
    
    return self;
}

- (void)load
{
    
}

- (void)unload
{
    M_GT_SAFE_FREE(_topIntro);
    M_GT_SAFE_FREE(_tableView);
    
    [self unobserveTick];
    
}

- (void) dealloc
{
    [self unload];
    [super dealloc];
}

- (void)initUI
{
    CGRect rect = M_GT_BOARD_FRAME;
    CGFloat width = rect.size.width - 20;
    
    [self setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    
    CGRect frame = rect;
    frame.origin.y = 0;
    frame.size.height = M_GT_PARA_OUT_HEADER_HEIGHT;
    
    _topIntro = [[GTParaOutHeaderView alloc] initWithFrame:frame];
    [_topIntro setDelegate:self];
    [self addSubview:_topIntro];
    
    frame.origin.x = 10;
    frame.origin.y += frame.size.height;
    frame.size.height = M_GT_APP_HEIGHT - frame.origin.y;
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
    [self addSubview:_tableView];
    
    [_tableView setAllowsSelectionDuringEditing:YES];

    // 为了headerview在展示时随cell一起滚动，因此这里需要做处理
    [_tableView setContentOffset:CGPointMake(0, M_GT_OUTPUT_SECTION_HEIGHT) animated:NO];
}

- (void)update
{
    [self unobserveTick];
    [_tableView reloadData];
    [self observeTick];
}

- (void)viewWillAppear
{
    [self unobserveTick];
    [_tableView reloadData];
    [self observeTick];
}

- (void)viewDidDisappear
{
    [self unobserveTick];
}

#pragma mark - Timer
- (void)observeTick
{
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:YES];
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
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_PARA object:nil];
}

#pragma mark -

- (NSArray *)content:(NSInteger)section {
    switch (section) {
        case GTOutputFloating:
            return [[GTOutputList sharedInstance] acArray];
            
        case GTOutputNormal:
            return [[GTOutputList sharedInstance] normalArray];
            
        case GTOutputDisable:
            return [[GTOutputList sharedInstance] disabledArray];
            
        default:
            break;
    }
    
    return nil;
}

- (GTList *)contentList:(NSIndexPath *)indexPath {
    return [GTOutputList sharedInstance];
}

- (id) contentObject:(NSIndexPath *)indexPath {
    
    id key = [[self content:indexPath.section] objectAtIndex:indexPath.row];
    id selectObj = [[self contentList:indexPath] objectForKey:key];
    return selectObj;
}


#pragma mark - UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return GTOutputSectionMax;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self content:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTParaOutputCell * cell = (GTParaOutputCell *)[_tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTParaOutputCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    
    [cell setSelectionStyle:UITableViewCellSelectionStyleBlue];
    GTOutputObject *obj = [self contentObject:indexPath];
    [cell bindData:obj withIndexPath:indexPath];
    [cell setDelegate:self];
    cell.textLabel.backgroundColor = [UIColor clearColor];
    
    [cell switchEditMode:[[GTParaConfig sharedInstance] isEditMode]];

    return cell;
}


- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)
sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath
{
    GTOutputObject* fromObj = [self contentObject:sourceIndexPath];
    NSString *key2 = [[fromObj dataInfo] key];
    
    if ([sourceIndexPath isEqual:destinationIndexPath]) {
        return;
    }
    
    //更新设置
    if (destinationIndexPath.section == GTOutputFloating) {
        [[GTOutputList sharedInstance] setStatus:GTParaOnAc forKey:key2];
    } else if(destinationIndexPath.section == GTOutputNormal){
        [[GTOutputList sharedInstance] setStatus:GTParaOnNormal forKey:key2];
    } else if(destinationIndexPath.section == GTOutputDisable){
        [[GTOutputList sharedInstance] setStatus:GTParaOnDisabled forKey:key2];
    }
    
    //移动key所在的位置
    if (destinationIndexPath.row < [[self content:destinationIndexPath.section] count]) {
        GTOutputObject* toObj = [self contentObject:destinationIndexPath];
        NSString *key1 = [[toObj dataInfo] key];
        
        [[GTOutputList sharedInstance] insertKey:key2 atKey:key1];
    }
    
    [_tableView reloadData];
    return;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 44.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        return 44.0f;
    }
    return M_GT_OUTPUT_SECTION_HEIGHT;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    CGFloat width = CGRectGetWidth(tableView.bounds);
    UIView  *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, width, M_GT_OUTPUT_SECTION_HEIGHT)] autorelease];
    [headerView setBackgroundColor:[UIColor clearColor]];
    
    UILabel *titleLabel=[[[UILabel alloc] initWithFrame:CGRectMake(0, 15, width, 18)] autorelease];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont systemFontOfSize:15];
    titleLabel.textColor = M_GT_LABEL_COLOR;
    switch (section) {
        case GTOutputFloating:
            titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_FLOATING_KEY);
            break;
        case GTOutputNormal:
            titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_UNFLOATING_KEY);
            break;
        case GTOutputDisable:
            titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_DISABLE_KEY);
        default:
            break;
    }
    
	return titleLabel;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if ([[self content:section] count] == 0) {
        if (section == GTOutputFloating) {
            return 70.0f;
        }
    }
    
    return 5.0f;
}


- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *footerView = nil;
    UILabel *titleLabel = nil;
    
    CGFloat width = CGRectGetWidth(tableView.bounds);
    CGFloat height = [self tableView:tableView heightForFooterInSection:section];
    footerView = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, width, height)] autorelease];
    [footerView setBackgroundColor:[UIColor clearColor]];
    
    if ([[self content:section] count] == 0) {
        titleLabel= [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, width, 30)] autorelease];
        titleLabel.backgroundColor = [UIColor clearColor];
        titleLabel.font = [UIFont systemFontOfSize:20];
        titleLabel.textAlignment = NSTextAlignmentCenter;
        titleLabel.textColor = M_GT_LABEL_COLOR;
        titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_FLOATING_ZERO_KEY);
        [footerView addSubview:titleLabel];
        
        if (section == GTOutputFloating) {
            UILabel *infoLabel= [[[UILabel alloc] initWithFrame:CGRectMake(0, 30, width, 30)] autorelease];
            infoLabel.backgroundColor = [UIColor clearColor];
            infoLabel.font = [UIFont systemFontOfSize:15];
            infoLabel.textAlignment = NSTextAlignmentCenter;
            infoLabel.textColor = M_GT_LABEL_COLOR;
            infoLabel.text = M_GT_LOCALSTRING(M_GT_PARA_FLOATING_INFO_KEY);
            [footerView addSubview:infoLabel];
        }
        
    }
    return footerView;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];GTOutputObject* obj = [self contentObject:indexPath];
    
    if ([[GTParaConfig sharedInstance] isEditMode]) {
        return;
    }
    
    
    if ([obj showWarning] == YES) {
        [obj setShowWarning:NO];
    }
    
    GTParaOutCommonBoard * board = nil;
    
    if ([obj vcForDetail] == nil) {
        
        if ([obj switchForHistory] == GTParaHistroyDisabled) {
            board = [[GTParaOutDetailBoard alloc] init];
        } else {
            if ([obj paraDelegate] && [[obj paraDelegate] respondsToSelector:@selector(objForHistory)]) {
                board = [[GTMultiDetailBoard alloc] init];
            } else {
                board = [[GTSingleDetailBoard alloc] init];
            }
        }
        
    } else {
        board = [[NSClassFromString([obj vcForDetail]) alloc] init];
    }
    
    if ( board )
    {
        [board bindData:obj];
        [self.viewController.navigationController pushViewController:board animated:YES];
        [board release];
    }
}



- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    GTOutputObject *obj = [self contentObject:indexPath];
    
    if([obj status] == GTParaOnDisabled){
        cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    } else {
        if ([obj showWarning] == YES) {
            cell.backgroundColor = M_GT_WARNING_COLOR;
        } else {
            cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
        }
    }
    
//    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}


- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView
           editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleNone;
}


//移动row时执行
-(NSIndexPath *)tableView:(UITableView *)tableView targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath *)sourceIndexPath toProposedIndexPath:(NSIndexPath *)proposedDestinationIndexPath

{
    //在悬浮框数目达到三个时，限制不可以移动到悬浮框列表
    if ([[[GTOutputList sharedInstance] acArray] count] >= 3) {
        if(sourceIndexPath.section != proposedDestinationIndexPath.section){
            if(proposedDestinationIndexPath.section == GTOutputFloating){
                return sourceIndexPath;
            }
            
        }
    }
    
    return proposedDestinationIndexPath;
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    // 为了headerview在展示时随cell一起滚动，因此这里需要做处理
    CGFloat sectionHeaderHeight = M_GT_OUTPUT_SECTION_HEIGHT;
    if ((scrollView.contentOffset.y <= sectionHeaderHeight) && (scrollView.contentOffset.y >= 0))
    {
        scrollView.contentInset = UIEdgeInsetsMake(-scrollView.contentOffset.y, 0, 0, 0);
    } else if (scrollView.contentOffset.y >= sectionHeaderHeight) {
        scrollView.contentInset = UIEdgeInsetsMake(-sectionHeaderHeight, 0, 0, 0);
    }
}

#pragma mark -

- (UIView *) tableViewForHeader
{
	// 为了headerview在展示时随cell一起滚动，因此这里需要预留空间
    CGFloat offsetY = M_GT_OUTPUT_SECTION_HEIGHT;
    UIView *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0,0, _tableView.bounds.size.width, offsetY)] autorelease];
    
	return headerView;
}

#pragma mark - GTParaOutputCellDelegate

- (void)didClickTop:(NSIndexPath *)indexPath
{
    if (indexPath.row < 1) {
        return;
    }
    
    NSIndexPath *toIndexPath = [NSIndexPath indexPathForRow:0 inSection:indexPath.section];
    
    GTOutputObject* fromObj = [self contentObject:indexPath];
    NSString *key2 = [[fromObj dataInfo] key];
    
    if (toIndexPath.row < [[self content:toIndexPath.section] count]) {
        GTOutputObject* toObj = [self contentObject:toIndexPath];
        NSString *key1 = [[toObj dataInfo] key];
        [[GTOutputList sharedInstance] insertKey:key2 atKey:key1];
    }
    
    [_tableView reloadData];
}

- (void)didClickChecked:(NSIndexPath *)indexPath
{
    GTOutputObject *obj = [self contentObject:indexPath];
    
    if ([obj switchForHistory] == GTParaHistroyOn) {
        [obj setSwitchForHistory:GTParaHistroyOff];
    } else if ([obj switchForHistory] == GTParaHistroyOff) {
        [obj setSwitchForHistory:GTParaHistroyOn];
    }
}

#pragma mark - GTParaOutHeaderViewDelegate
- (void)didClickGW
{
    //刷新TableView
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_PARA object:nil];
    
    if (_delegate && [_delegate respondsToSelector:@selector(didClickGW)])
    {
        [_delegate didClickGW];
    }
    
}


#pragma mark -

- (void)switchEditMode:(BOOL)edit
{
    [_topIntro switchEditMode:edit];
    if (edit) {
        [_tableView setEditing:YES animated:YES];
    } else {
        [_tableView setEditing:NO animated:YES];
    }
    [_tableView reloadData];
    
}


- (IBAction) switchAction:(id)sender
{
    GTUISwitch *switchView = (GTUISwitch*)sender;
    NSUInteger section = M_GT_TABLE_GET_SECTION([switchView tag]);
    NSUInteger row = M_GT_TABLE_GET_ROW([switchView tag]);
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:row inSection:section];
    
    GTOutputObject *obj = [self contentObject:indexPath];
    
    if (switchView.on == YES) {
        [obj setSwitchForHistory:GTParaHistroyOn];
    } else {
        [obj setSwitchForHistory:GTParaHistroyOff];
    }
    
}

@end

#endif
