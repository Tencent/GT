//
//  GTParaSettingBoard.m
//  GTKit
//
//  Created   on 13-8-29.
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

#import "GTParaSettingBoard.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>
#import "GTImage.h"
#import "GTConfig.h"
#import "GTSettingRow.h"
#import "GTSettingCell.h"
#import "GTUISwitch.h"
#import "GTLang.h"
#import "GTLangDef.h"





@interface GTParaSettingBoard ()

@end

@implementation GTParaSettingBoard

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
    
}

- (void)unload
{
	[_settings removeAllObjects];
	[_settings release];
    
    [_monitorInterval release];
    [_gatherDuration release];
    
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
    [self setNavTitle:M_GT_LOCALSTRING(M_GT_SETTING_PARA_KEY)];
    
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
    _tableView.tableHeaderView = [self tableViewForHeader];
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
    //    container.alpha = 0.6;
    
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
- (UIView *)viewForMonitor
{
    UIView *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.bounds.size.width, 109.0)] autorelease];
    CGRect frame = headerView.frame;
    
    CGFloat width = frame.size.width;
    frame.origin.x = 0;
    frame.origin.y = 15;
    frame.size.height = 15;
    
    _monitorInterval = [[UILabel alloc] initWithFrame:frame];
    [_monitorInterval setBackgroundColor:[UIColor clearColor]];
    [_monitorInterval setTextColor:M_GT_LABEL_COLOR];
    [_monitorInterval setFont:[UIFont systemFontOfSize:15]];
    _monitorInterval.textAlignment = NSTextAlignmentLeft;
    [_monitorInterval setText:[NSString stringWithFormat:@"%@ : %.1fs", M_GT_LOCALSTRING(M_GT_PARA_MONITOR_INTERVAL_KEY), [[GTConfig sharedInstance] monitorInterval]]];
    [headerView addSubview:_monitorInterval];
    
    frame.origin.y = 34;
    frame.size.height = 70;
    UIView *sliderView = [[UIView alloc] initWithFrame:frame];
    [sliderView setBackgroundColor:M_GT_CELL_BKGD_COLOR];
    sliderView.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    sliderView.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [headerView addSubview:sliderView];
    [sliderView release];
    
    frame.origin.x = 10;
    frame.origin.y = 5;
    frame.size.height = 15;
    frame.size.width = width - 20;
    
    UILabel *info = [[UILabel alloc] initWithFrame:frame];
    info.backgroundColor = [UIColor clearColor];
    info.text = @"10s";
    info.textColor = M_GT_LABEL_COLOR;
    info.textAlignment = NSTextAlignmentLeft;
    [info setFont:[UIFont systemFontOfSize:10]];
    [sliderView addSubview:info];
    [info release];
    
    info = [[UILabel alloc] initWithFrame:frame];
    info.backgroundColor = [UIColor clearColor];
    info.text = @"1s";
    info.textColor = M_GT_LABEL_COLOR;
    info.textAlignment = NSTextAlignmentCenter;
    [info setFont:[UIFont systemFontOfSize:10]];
    [sliderView addSubview:info];
    [info release];
    
    info = [[UILabel alloc] initWithFrame:frame];
    info.backgroundColor = [UIColor clearColor];
    info.text = @"0.1s";
    info.textColor = M_GT_LABEL_COLOR;
    info.textAlignment = NSTextAlignmentRight;
    [info setFont:[UIFont systemFontOfSize:10]];
    [sliderView addSubview:info];
    [info release];
    
    CGFloat lableWidth = frame.size.width;
    frame.size.width = lableWidth/2.0 + lableWidth/18.0 + 10;
    info = [[UILabel alloc] initWithFrame:frame];
    info.backgroundColor = [UIColor clearColor];
    info.text = @"5s";
    info.textColor = M_GT_LABEL_COLOR;
    info.textAlignment = NSTextAlignmentCenter;
    [info setFont:[UIFont systemFontOfSize:10]];
    [sliderView addSubview:info];
    [info release];
    
    frame.origin.x = lableWidth/2.0;
    frame.size.width = lableWidth/2.0 + lableWidth/18.0 + 10;
    info = [[UILabel alloc] initWithFrame:frame];
    info.backgroundColor = [UIColor clearColor];
    info.text = @"0.5s";
    info.textColor = M_GT_LABEL_COLOR;
    info.textAlignment = NSTextAlignmentCenter;
    [info setFont:[UIFont systemFontOfSize:10]];
    [sliderView addSubview:info];
    [info release];
    
    frame.origin.x = 5;
    frame.origin.y = 20;
    frame.size.width = width - 10;
    frame.size.height = 40;
    
    UIImageView *sliderImage = [[UIImageView alloc] initWithFrame:frame];
    [sliderImage setImage:[GTImage imageNamed:@"gt_track" ofType:@"png"]];
    [sliderView addSubview:sliderImage];
    [sliderImage release];
    
    frame.origin.x = 11.5;
    frame.size.width = width - 23;
    
    UISlider *slider = [[UISlider alloc] initWithFrame:frame];
    slider.backgroundColor = [UIColor clearColor];
    [slider setMinimumValue:0.1];
    [slider setMaximumValue:10];
    
    CGFloat interval = ([slider maximumValue] - [slider minimumValue])/18.0;
    CGFloat monitorInterval = [[GTConfig sharedInstance] monitorInterval];
    CGFloat value = 0;
    if (monitorInterval >= 1) {
        value = ([slider maximumValue] - monitorInterval) * interval;
    } else {
        value = (18 + 1 - monitorInterval/0.1) * interval;
    }
    
    [slider setValue:value];
    [slider addTarget:self action:@selector(monitorSliderChanged:) forControlEvents:UIControlEventValueChanged];
    
    // 左右轨的图片 设置为透明
    CGSize imageSize = CGSizeMake(slider.bounds.size.width, slider.bounds.size.height);
    UIGraphicsBeginImageContextWithOptions(imageSize, 0, [UIScreen mainScreen].scale);
    [[UIColor clearColor] set];
    UIRectFill(CGRectMake(0, 0, imageSize.width, imageSize.height));
    UIImage *stetchTrack = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    [slider setMinimumTrackImage:stetchTrack forState:UIControlStateNormal];
    [slider setMaximumTrackImage:stetchTrack forState:UIControlStateNormal];
    
    // 滑块图片
    UIImage *thumbImage = [GTImage imageNamed:@"gt_thumb" ofType:@"png"];
    thumbImage = [GTImage image:thumbImage scaleAspectFitSize:CGSizeMake(15, 24)];
    // 注意这里要加UIControlStateHightlighted的状态，否则当拖动滑块时滑块将变成原生的控件
    [slider setThumbImage:thumbImage forState:UIControlStateHighlighted];
    [slider setThumbImage:thumbImage forState:UIControlStateNormal];
    
    [sliderView addSubview:slider];
    [slider release];
    
	return headerView;
}


- (UIView *)tableViewForHeader
{
    CGFloat offsetY = 0;
    UIView *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0,0, _tableView.bounds.size.width, 109.0*2 + offsetY)] autorelease];
    UIView *view = [self viewForMonitor];
    [view setFrame:CGRectMake(0,0, _tableView.bounds.size.width, 109.0)];
    [headerView addSubview:view];
    
    return headerView;
}


- (void)monitorSliderChanged:(id)sender{
    UISlider* slider = (UISlider*)sender;
    CGFloat interval = ([slider maximumValue] - [slider minimumValue])/18.0;
    NSUInteger cnt = [slider value]/interval;
    CGFloat value = 0;
    if (cnt <= 9) {
        value = 10 - 1 * cnt;
    } else {
        value = 1 - 0.1 * (cnt - 9);
    }
    
    [[GTConfig sharedInstance] setMonitorInterval:value];
    [_monitorInterval setText:[NSString stringWithFormat:@"%@ : %.1fs", M_GT_LOCALSTRING(M_GT_PARA_MONITOR_INTERVAL_KEY), value]];
}


@end
