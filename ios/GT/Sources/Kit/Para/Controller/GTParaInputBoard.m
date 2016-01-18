//
//  GTParaInputBoard.m
//  GTKit
//
//  Created   on 12-12-2.
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
#import "GTParaOutDetailBoard.h"
#import "GTParaInSelectBoard.h"
#import "GTParaInputBoard.h"
#import "GTInputList.h"
#import "GTParaElang.h"
#import "GTImage.h"
#import "GTParaConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"

typedef enum {
    GTInputInvalid = 0,
	GTInputFloating = 0,
    GTInputNormal,
    GTInputDisabled,
    GTInputSectionMax
} GTInputSectionType;

#define M_GT_INPUT_SECTION_HEIGHT 39.0f

//#define M_GT_PARA_IN_ITEMS @"Input Para Items"
//#define M_GT_PARA_IN_TOP   @"Top"
//#define M_GT_PARA_IN_DRAG  @"Drag"
#pragma mark -

@implementation GTParaInHeaderView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setBackgroundColor:[UIColor clearColor]];
        
        UIView *backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height - 4)];
        [backgroundView setBackgroundColor:M_GT_COLOR_WITH_HEX(0x29292D)];
        [self addSubview:backgroundView];
        [backgroundView release];
        
        [self load];
        [self viewLayout:frame];
        [self showData];
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
    UIColor *color = M_GT_COLOR_WITH_HEX(0xCB7418);
    _items = [[UILabel alloc] init];
    _items.font = [UIFont systemFontOfSize:12.0];
    _items.textColor = color;
    _items.textAlignment = NSTextAlignmentLeft;
    _items.backgroundColor = [UIColor clearColor];
    [self addSubview:_items];
    
    _top = [[UILabel alloc] init];
    _top.font = [UIFont systemFontOfSize:12.0];
    _top.textColor = color;
    _top.textAlignment = NSTextAlignmentCenter;
    _top.backgroundColor = [UIColor clearColor];
    [self addSubview:_top];
    
    _drag = [[UILabel alloc] init];
    _drag.font = [UIFont systemFontOfSize:12.0];
    _drag.textColor = color;
    _drag.textAlignment = NSTextAlignmentCenter;
    _drag.backgroundColor = [UIColor clearColor];
    [self addSubview:_drag];
}

- (void)unload
{
    M_GT_SAFE_FREE(_items);
    M_GT_SAFE_FREE(_top);
    M_GT_SAFE_FREE(_drag);
}

- (void)viewLayout:(CGRect)frame
{
    CGFloat x = frame.origin.x;
    CGFloat y = 0;
    CGFloat width = frame.size.width - 20;
    CGFloat height = frame.size.height;
    
    _items.frame = CGRectMake( 10 + x, y, width - 88, height );
    _top.frame = CGRectMake( 10 + x + width - 88, y, 44, height );
    _drag.frame = CGRectMake( 10 + x + width - 44 , y, 44, height );
}

- (void)showData
{
    [_items setText:M_GT_LOCALSTRING(M_GT_PARA_IN_ITEMS_KEY)];
    [_top setText:M_GT_LOCALSTRING(M_GT_PARA_TOP_KEY)];
    [_drag setText:M_GT_LOCALSTRING(M_GT_PARA_DRAG_KEY)];
}

- (void)switchEditMode:(BOOL)edit
{
    if (edit) {
        _top.hidden = NO;
        _drag.hidden = NO;
    } else {
        _top.hidden = YES;
        _drag.hidden = YES;
    }
}

@end

@implementation GTParaInputCell

@synthesize indexPath = _indexPath;
@synthesize delegate = _delegate;

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 44.0f );
}

- (void)cellLayout
{
	_title.frame = CGRectMake( 10.0f, 5.0f, self.bounds.size.width-90, 20.0f );
    _value.frame = CGRectMake( 10, 25.0f, self.bounds.size.width-90, 15.0f );
    _btnTop.frame = CGRectMake( self.bounds.size.width-80, 5.0f, 40, 35.0f );
}

- (void)load
{
	[super load];
    
    self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6f];
    
	_title = [[UILabel alloc] init];
	_title.font = [UIFont systemFontOfSize:15.0];
	_title.textColor = M_GT_CELL_TEXT_COLOR;
	_title.textAlignment = NSTextAlignmentLeft;
    _title.backgroundColor = [UIColor clearColor];
	[self addSubview:_title];
    
	_value = [[UILabel alloc] init];
	_value.font = [UIFont systemFontOfSize:12.0];
	_value.textColor = M_GT_CELL_TEXT_COLOR;
	_value.textAlignment = NSTextAlignmentLeft;
    _value.backgroundColor = [UIColor clearColor];
	[self addSubview:_value];
    
    _btnTop = [[UIButton alloc] init];
    [_btnTop setImageEdgeInsets:UIEdgeInsetsMake(8, 8, 8, 8)];
    [_btnTop setBackgroundColor:[UIColor clearColor]];
    [_btnTop addTarget:self action:@selector(didClickTop:) forControlEvents:UIControlEventTouchUpInside];
    [_btnTop setImage:[GTImage imageNamed:@"gt_para_top" ofType:@"png"] forState:UIControlStateNormal];
    [_btnTop setImage:[GTImage imageNamed:@"gt_para_top_sel" ofType:@"png"] forState:UIControlStateSelected];
    [self addSubview:_btnTop];
}

- (void)unload
{
	self.indexPath = nil;
    self.delegate = nil;
    
    M_GT_SAFE_FREE( _title );
	M_GT_SAFE_FREE( _value );
    M_GT_SAFE_FREE( _btnTop );
	
	[super unload];
}

- (void)bindData:(NSObject *)data withIndexPath:(NSIndexPath*)indexPath
{
    [super bindData:data];
    
    GTInputObject *obj = (GTInputObject *)data;
    GTInputDataInfo *dataInfo = [obj dataInfo];
    NSString *dataStr = [NSString stringWithFormat:@"%@ ( %@ )", [dataInfo alias], [dataInfo key]];
    
    [_title setText:dataStr];
	[_value setText:NSLocalizedString([dataInfo dataValueInfo],)];
    if ([obj status] == GTParaOnDisabled) {
        _value.textColor = M_GT_CELL_TEXT_DISABLE_COLOR;
        _title.textColor = M_GT_CELL_TEXT_DISABLE_COLOR;
    }

    [self setIndexPath:indexPath];
}

- (void)clearData
{
	[_title setText:nil];
	[_value setText:nil];
}

- (void)switchEditMode:(BOOL)edit
{
    if (edit) {
        _btnTop.hidden = NO;
        _value.hidden = YES;
        _title.frame = CGRectMake( 10.0f, 5.0f, self.bounds.size.width-100, 35.0f );
    } else {
        _btnTop.hidden = YES;
        _value.hidden = NO;
        _title.frame = CGRectMake( 10.0f, 5.0f, self.bounds.size.width-100, 20.0f );
    }
    
}

- (void)didClickTop:(id)sender
{
    if (_delegate && [_delegate respondsToSelector:@selector(didClickTop:)])
    {
        [_delegate didClickTop:_indexPath];
    }
}


@end

#pragma mark -

@interface GTParaInputBoard ()

@property (nonatomic, assign) UIViewController  *viewController;


@end

@implementation GTParaInputBoard

@synthesize viewController;
@synthesize tableView = _tableView;

- (id)initWithViewController:(UIViewController*) givenViewController {
    self = [super initWithFrame:M_GT_BOARD_FRAME];
	if (self) {
		self.viewController = givenViewController;
        [self load];
        [self initUI];
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
    M_GT_SAFE_FREE(_topIntro);
    M_GT_SAFE_FREE(_tableView);
}

- (void)initUI
{
    CGRect rect = M_GT_BOARD_FRAME;
    CGFloat width = rect.size.width - 20;
    
    [self setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    
    CGRect frame = rect;
    frame.origin.y = 0;
    frame.size.height = 44;
    
    _topIntro = [[GTParaInHeaderView alloc] initWithFrame:frame];
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
    [_tableView setUserInteractionEnabled:YES];
    
    // 为了headerview在展示时随cell一起滚动，因此把第一个header作为预留空间
    [_tableView setContentOffset:CGPointMake(0, M_GT_INPUT_SECTION_HEIGHT) animated:NO];
}

- (void)update
{
    [_tableView reloadData];
}

- (void)viewWillAppear
{
    [_tableView reloadData];
}

- (void)viewDidDisappear
{
    
}
#pragma mark -

- (NSArray *)content:(NSInteger)section {
    switch (section) {
        case GTInputFloating:
            return [[GTInputList sharedInstance] acArray];
        case GTInputNormal:
            return [[GTInputList sharedInstance] normalArray];
        case GTInputDisabled:
            return [[GTInputList sharedInstance] disabledArray];
        default:
            break;
    }
    
    return nil;
}

- (GTList *)contentList:(NSIndexPath *)indexPath {
    return [GTInputList sharedInstance];
}

- (id) contentObject:(NSIndexPath *)indexPath {
    
    id key = [[self content:indexPath.section] objectAtIndex:indexPath.row];
    id selectObj = [[self contentList:indexPath] objectForKey:key];
    return selectObj;
}

#pragma mark - UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return GTInputSectionMax;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self content:section] count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTParaInputCell * cell = (GTParaInputCell *)[_tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTParaInputCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];

	}
    
    [cell setSelectionStyle:UITableViewCellSelectionStyleBlue];
    [cell bindData:[self contentObject:indexPath] withIndexPath:indexPath];
    [cell setDelegate:self];
    
    cell.textLabel.backgroundColor = [UIColor clearColor];
    
    [cell switchEditMode:[[GTParaConfig sharedInstance] isEditMode]];
    
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return YES;
}

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)
sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath {
    GTInputObject* fromObj = [self contentObject:sourceIndexPath];
    NSString *key2 = [[fromObj dataInfo] key];
    
    if ([sourceIndexPath isEqual:destinationIndexPath]) {
        return;
    }
    
    //更新设置
    if (destinationIndexPath.section == GTInputFloating) {
        [[GTInputList sharedInstance] setStatus:GTParaOnAc forKey:key2];
    } else if(destinationIndexPath.section == GTInputNormal){
        [[GTInputList sharedInstance] setStatus:GTParaOnNormal forKey:key2];
    } else if(destinationIndexPath.section == GTInputDisabled){
        [[GTInputList sharedInstance] setStatus:GTParaOnDisabled forKey:key2];
    }
    
    //移动key所在的位置
    if (destinationIndexPath.row < [[self content:destinationIndexPath.section] count]) {
        GTOutputObject* toObj = [self contentObject:destinationIndexPath];
        NSString *key1 = [[toObj dataInfo] key];
        [[GTInputList sharedInstance] insertKey:key2 atKey:key1];
        
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
    return M_GT_INPUT_SECTION_HEIGHT;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    CGFloat width = CGRectGetWidth(tableView.bounds);
    CGFloat y = 0.0f;
    CGFloat height = y + M_GT_INPUT_SECTION_HEIGHT;
    UIView  *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, width, height)] autorelease];
    [headerView setBackgroundColor:[UIColor clearColor]];
    
    UILabel *titleLabel=[[[UILabel alloc] initWithFrame:CGRectMake(0, y + 15, width, 18)] autorelease];
    [titleLabel setBackgroundColor:[UIColor clearColor]];
    titleLabel.font = [UIFont systemFontOfSize:15];
    titleLabel.textColor = M_GT_LABEL_COLOR;
    switch (section) {
        case GTInputFloating:
            titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_FLOATING_KEY);
            break;
        case GTInputNormal:
            titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_UNFLOATING_KEY);
            break;
        case GTInputDisabled:
            titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_DISABLE_KEY);
            break;
        default:
            break;
    }
    
    [headerView addSubview:titleLabel];
	return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if ([[self content:section] count] == 0) {
        if (section == GTInputFloating) {
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
        titleLabel= [[UILabel alloc] initWithFrame:CGRectMake(0, 0, width, 30)];
        titleLabel.font = [UIFont systemFontOfSize:20];
        titleLabel.textAlignment = NSTextAlignmentCenter;
        [titleLabel setBackgroundColor:[UIColor clearColor]];
        titleLabel.textColor = M_GT_LABEL_COLOR;
        titleLabel.text = M_GT_LOCALSTRING(M_GT_PARA_FLOATING_ZERO_KEY);
        [footerView addSubview:titleLabel];
        [titleLabel release];
        
        if (section == GTInputFloating) {
            UILabel *infoLabel= [[UILabel alloc] initWithFrame:CGRectMake(0, 30, width, 30)];
            infoLabel.font = [UIFont systemFontOfSize:15];
            [infoLabel setBackgroundColor:[UIColor clearColor]];
            infoLabel.textAlignment = NSTextAlignmentCenter;
            infoLabel.textColor = M_GT_LABEL_COLOR;
            infoLabel.text = M_GT_LOCALSTRING(M_GT_PARA_FLOATING_INFO_KEY);
            [footerView addSubview:infoLabel];
            [infoLabel release];
        }
    }

    return footerView;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if ([[GTParaConfig sharedInstance] isEditMode]) {
        return;
    }
    
    
    GTInputObject* inObj = [self contentObject:indexPath];
    GTParaInSelectBoard * board = [[GTParaInSelectBoard alloc] init];
    if ( board )
    {
        [board bindData:inObj];
        
        [self.viewController.navigationController pushViewController:board animated:YES];
        [board release];
    }
    
    return;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
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
    if ([[[GTInputList sharedInstance] acArray] count] >= 3) {
        if(sourceIndexPath.section != proposedDestinationIndexPath.section){
            if(proposedDestinationIndexPath.section == GTInputFloating){
                return sourceIndexPath;
            }
        }
    }
    
    return proposedDestinationIndexPath;
}

#pragma mark -

- (UIView *) tableViewForHeader
{
    // 为了headerview在展示时随cell一起滚动，因此这里需要预留空间
    CGFloat offsetY = M_GT_INPUT_SECTION_HEIGHT;
    UIView *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0,0, _tableView.bounds.size.width, offsetY)] autorelease];
    
	return headerView;
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    // 为了headerview在展示时随cell一起滚动，因此这里需要做处理
    CGFloat sectionHeaderHeight = M_GT_INPUT_SECTION_HEIGHT;
    
    if ((scrollView.contentOffset.y <= sectionHeaderHeight) && (scrollView.contentOffset.y >= 0))
    {
        scrollView.contentInset = UIEdgeInsetsMake(-scrollView.contentOffset.y, 0, 0, 0);
    } else if (scrollView.contentOffset.y >= sectionHeaderHeight) {
        scrollView.contentInset = UIEdgeInsetsMake(-sectionHeaderHeight, 0, 0, 0);
    }
}


#pragma mark -

- (void)didClickTop:(NSIndexPath *)indexPath
{
    if (indexPath.row < 1) {
        return;
    }
    
    NSIndexPath *toIndexPath = [NSIndexPath indexPathForRow:0 inSection:indexPath.section];
    
    GTInputObject* fromObj = [self contentObject:indexPath];
    NSString *key2 = [[fromObj dataInfo] key];
    
    if (toIndexPath.row < [[self content:toIndexPath.section] count]) {
        GTInputObject* toObj = [self contentObject:toIndexPath];
        NSString *key1 = [[toObj dataInfo] key];
        [[GTInputList sharedInstance] insertKey:key2 atKey:key1];
    }
    
    [_tableView reloadData];
}

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

@end
#endif
