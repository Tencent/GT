//
//  GTACWindow.m
//  GTKit
//
//  Created   on 12-4-19.
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

#import <sys/types.h>
#import <sys/sysctl.h>
#import <sys/mman.h>
#import <mach/mach.h>

#import <netinet/in.h>
#import <arpa/inet.h>

#import "GTACWindow.h"
#import "GTOutputList.h"
#import "GTConfig.h"
#import "GTDetailView.h"
#import "GTInputList.h"
#import "GTParaInSelectBoard.h"
#import "GTImage.h"
#import "GTDebugDef.h"
#import "GTUIManager.h"
#import "GTProgressHUD.h"
#import "GTLang.h"
#import "GTLangDef.h"
#import "GTMTA.h"



#define M_GT_FONT_SIZE_22PX 11.0f



#pragma mark -

@implementation GTInputBar

#define M_GT_TAR_INTERVAL 0.0f

@synthesize titles = _titles;
@synthesize values = _values;

- (id)initWithFrame:(CGRect)frame inputLists:(NSMutableArray *)array
{
    self = [super initWithFrame:frame];
    if (self)
	{
        self.backgroundColor = [UIColor clearColor];
		_backgroundView = [[UIImageView alloc] initWithFrame:self.bounds];
		[self addSubview:_backgroundView];
        
        self.buttons = [NSMutableArray arrayWithCapacity:[array count]];
        self.titles = [NSMutableArray arrayWithCapacity:[array count]];
        self.values = [NSMutableArray arrayWithCapacity:[array count]];
        
		UIButton *btn;
        UILabel  *title;
        UILabel  *value;
		CGFloat width = self.frame.size.width / [array count];
        
		for (int i = 0; i < [array count]; i++)
		{
			btn = [UIButton buttonWithType:UIButtonTypeCustom];
			btn.showsTouchWhenHighlighted = NO;
			btn.tag = i;
            CGFloat x = width * i;
            CGFloat y = 2;
            CGFloat w = width;
            CGFloat h = frame.size.height - 2;
			btn.frame = CGRectMake(x, y, w, h);

			[btn addTarget:self action:@selector(tabBarButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
			[self.buttons addObject:btn];
            [self addSubview:btn];
            
            GTInputObject *obj = [[GTInputList sharedInstance] objectForKey:[array objectAtIndex:i]];
            
            title = [[UILabel alloc] initWithFrame:CGRectMake(x, y+3, w, 12)];
            title.font = [UIFont systemFontOfSize:11.0];
            title.text = [[obj dataInfo] alias];
            // #9398a6
            title.textColor = [UIColor colorWithRed:0.576 green:0.596 blue:0.651 alpha:1];
            title.textAlignment = NSTextAlignmentCenter;
            title.backgroundColor = [UIColor clearColor];
            [self addSubview:title];
            [title release];
            [self.titles addObject:title];
            
            value = [[UILabel alloc] initWithFrame:CGRectMake(x+2, y+15, w-4, h-15)];
            value.font = [UIFont systemFontOfSize:11.0];
            value.text = [[obj dataInfo] value];
            value.textColor = [UIColor whiteColor];
            value.textAlignment = NSTextAlignmentCenter;
            value.backgroundColor = [UIColor clearColor];
            [self addSubview:value];
            [value release];
            [self.values addObject:value];
		}
        
        
    }
    return self;
}

- (void)updateValue:(GTList *)list
{
    for (int i = 0; i < [[list keys] count]; i++)
	{
        GTInputObject *obj = [list objectForKey:[[list keys] objectAtIndex:i]];
		UILabel *title = [self.titles objectAtIndex:i];
        title.text = [[obj dataInfo] alias];
        
        UILabel *value = [self.values objectAtIndex:i];
        value.text = [[obj dataInfo] value];
    }
}

- (void)tabBarButtonClicked:(id)sender
{
	UIButton *btn = sender;
    if ([_delegate respondsToSelector:@selector(tabBar:didSelectIndex:)])
    {
        [_delegate tabBar:self didSelectIndex:btn.tag];
    }
}


- (void)dealloc
{
    self.titles = nil;
    self.values = nil;
    
    
    [super dealloc];
}



 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextBeginPath(context);
    CGContextSetLineWidth(context, 0.2f);
    CGContextSetStrokeColorWithColor(context, [[UIColor blackColor] colorWithAlphaComponent:1.0].CGColor);
    
    CGFloat width = self.frame.size.width / [self.buttons count];
    
    for (int i = 0; i < [self.buttons count]; i++)
    {
        CGFloat x = width * i;
        CGFloat y = 2;
        CGFloat h = self.frame.size.height - 2;
        
        if (i > 0) {
            CGContextMoveToPoint(context, x, y + 2);
            CGContextAddLineToPoint(context, x, y + h - 2);
        }
        
    }
    
    CGContextStrokePath(context);
}


@end

typedef enum {
	GTAdjustHeight = 0,
    GTAdjustFirst,
    GTAdjustMax
} GTAdjustType;

@implementation GTACOutputCell

+ (CGFloat)cellHeight
{
	return 21.0f;
}

+ (float)cellBigHeight:(NSObject *)data bound:(CGSize)bound
{
    //考虑文字随时变化，这里用最大高度
    return 21.0f*5;

    CGSize constrainedToSize = CGSizeMake(bound.width - 58.0f, 100);
    GTOutputObject *obj = (GTOutputObject *)data;
    
    GTOutputValue *value = [[obj dataInfo] value];
    float height = [[value content] sizeWithFont:[UIFont boldSystemFontOfSize:17.0f]
                                              constrainedToSize:constrainedToSize
                                                  lineBreakMode:NSLineBreakByTruncatingMiddle].height;
    if (height < 21.0f) {
        height = 21.0f;
    }
    
    
	return height;
}

+ (float)cellHeight:(NSObject *)data bound:(CGSize)bound
{
    //考虑文字随时变化，这里用最大高度
    return 21.0f*5;

    CGSize constrainedToSize = CGSizeMake(bound.width - 58.0f, 100);
    GTOutputObject *obj = (GTOutputObject *)data;
    GTOutputValue *value = [[obj dataInfo] value];
    float height = [[value content] sizeWithFont:[UIFont systemFontOfSize:12.0f]
                                                                       constrainedToSize:constrainedToSize
                                                                           lineBreakMode:NSLineBreakByTruncatingMiddle].height;
    if (height < 21.0f) {
        height = 21.0f;
    }
    
	return height;
}

- (void)cellLayout
{
	_title.frame = CGRectMake( 6.0f, 0.0f, 42, 21.0f );
    _value.frame = CGRectMake( 52.0f, 0.0f, self.bounds.size.width - 58, 21.0f );
    
    _title.lineBreakMode = NSLineBreakByCharWrapping;
    _title.numberOfLines = 1;
    
    _value.lineBreakMode = NSLineBreakByTruncatingTail;
    _value.numberOfLines = 1;
    
    [self setBackgroundView:nil];
}

- (void)cellLayoutHeight
{
    float height = [GTACOutputCell cellHeight:[self cellData] bound:self.bounds.size];
    _title.frame = CGRectMake( 6.0f, 0.0f, 42, 21.0f );
    _value.frame = CGRectMake( 52.0f, 0.0f, self.bounds.size.width - 58, height );
    
    _title.lineBreakMode = NSLineBreakByCharWrapping;
    _title.numberOfLines = 0;
    
    _value.lineBreakMode = NSLineBreakByTruncatingTail;
    _value.numberOfLines = 0;
    _value.font = [UIFont systemFontOfSize:11.0];
    UIImageView * view = [[UIImageView alloc] init];
    [view setImage:[GTImage imageNamed:@"gt_ac_selected" ofType:@"png"]];
    [self setBackgroundView:view];
    [view release];
    
}

- (void)cellLayoutFirst
{
    _title.frame = CGRectMake( 6.0f, 0.0f, 42, 21.0f );
    _value.font = [UIFont boldSystemFontOfSize:17.0];
    
    [self setBackgroundView:nil];
}

- (void)load
{
	[super load];
    
    self.backgroundColor = [UIColor clearColor];
    
	_title = [[UILabel alloc] init];
	_title.font = [UIFont systemFontOfSize:11.0];
    // #9398a6
	_title.textColor = [UIColor colorWithRed:0.576 green:0.596 blue:0.651 alpha:1];
    _title.lineBreakMode = NSLineBreakByCharWrapping;
	_title.textAlignment = NSTextAlignmentRight;
    _title.backgroundColor = [UIColor clearColor];
	[self addSubview:_title];
    
	_value = [[UILabel alloc] init];
	_value.font = [UIFont systemFontOfSize:11.0];
	_value.textColor = [UIColor whiteColor];
    _value.lineBreakMode = NSLineBreakByTruncatingTail;
	_value.textAlignment = NSTextAlignmentLeft;
    _value.backgroundColor = [UIColor clearColor];
	[self addSubview:_value];
    
}

- (void)unload
{
	M_GT_SAFE_FREE( _title );
	M_GT_SAFE_FREE( _value );
	
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_AC_UPDATE object:nil];
	[super unload];
}

- (void)showData:(NSObject *)data
{
    GTOutputObject *obj = (GTOutputObject *)data;
    GTOutputDataInfo *dataInfo = [obj dataInfo];
    
    [_title setText:[dataInfo alias]];
    
    GTOutputValue *value = [[dataInfo value] retain];
    NSString *content = [value content];
    if (content != nil) {
        [_value setText:content];
    }
    [value release];
}

- (void)bindData:(NSObject *)data
{
    [super bindData:data];
    [self showData:data];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateCell:) name:M_GT_NOTIFICATION_AC_UPDATE object:nil];
}

- (void)updateCell:(NSNotification *)n
{
    [self showData:self.cellData];
}

- (void)clearData
{
	[_title setText:nil];
	[_value setText:nil];
}


@end



#pragma mark -

@interface GTACWindow ()
@property (nonatomic, retain) UIButton *closeButton;

@end

@implementation GTACWindow

@synthesize isPortrait = _isPortrait;
@synthesize tableView = _tableView;
@synthesize selectIndexPath = _selectIndexPath;


- (void)initHeaderUI
{
    float height = [[GTConfig sharedInstance] acHeaderHeight];
    CGRect frame = self.bounds;
    frame.origin.y = 10.0f;
    frame.size.width = _width;
    frame.size.height = height;
    
    _headerView = [[UIImageView alloc] initWithFrame:frame];
    [_headerView setImage:[GTImage imageNamed:@"gt_ac_top" ofType:@"png"]];
    [_VC.view addSubview:_headerView];
    
    _watch = [[UIButton alloc]initWithFrame:CGRectMake(8, 11+(height-15)/2, 50, 15)];
    [_watch.titleLabel setFont:[UIFont systemFontOfSize:10]];
    _watch.backgroundColor = [UIColor clearColor];
    [_VC.view addSubview:_watch];
    [_watch setTitle:@"0.0\"" forState:UIControlStateNormal];
    NSDate *startTime = [[GTConfig sharedInstance] startTime];
    if ( startTime != nil) {
        [self updateHeaderTimeUI];
        _watchTimer = [NSTimer scheduledTimerWithTimeInterval:0.1  target:self selector:@selector(watchTimerNotify:) userInfo:nil repeats:YES];
        [_watchTimer retain];
    } else {
        NSTimeInterval time = [[GTConfig sharedInstance] watchTime];
        NSString *timeStr = [NSString stringWithFormat:@"%.1f\"", time];
        [_watch setTitle:timeStr forState:UIControlStateNormal];
    }

    
    UIButton *btn = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 60, height+10)];
    [btn addTarget:self action:@selector(onWatch:) forControlEvents:UIControlEventTouchUpInside];
    [_VC.view addSubview:btn];
    [btn release];
    
    _switchInfo = [[UILabel alloc] initWithFrame:CGRectMake(frame.size.width - 100, 12, 75, height-2)];
    _switchInfo.textAlignment = NSTextAlignmentRight;
    [_switchInfo setBackgroundColor:[UIColor clearColor]];
    [_switchInfo setFont:[UIFont systemFontOfSize:9]];
    [_VC.view addSubview:_switchInfo];
    
    
    _acSwitch = [[UIButton alloc] initWithFrame:CGRectMake(frame.size.width - 20, (height - 14)/2+11, 15, 15)];
    _acSwitch.backgroundColor = [UIColor clearColor];
    [_VC.view addSubview:_acSwitch];
    [self updateACSwitch];
    
    
    btn = [[UIButton alloc] initWithFrame:CGRectMake(frame.size.width - 60, 0, 60, height+10)];
    [btn addTarget:self action:@selector(onACSwitch:) forControlEvents:UIControlEventTouchUpInside];
    
    [_VC.view addSubview:btn];
    [btn release];
    
}

- (void)initOutputUI
{
    CGRect frame = self.bounds;
    frame.origin.y = frame.origin.y + 20;
    CGFloat cellHeight = [GTACOutputCell cellHeight];
    frame.size.height = cellHeight * [[[GTOutputList sharedInstance] acArray] count] + 3.0f;
    
    _tableView = [[UITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.rowHeight = 44;
    _tableView.showsVerticalScrollIndicator = NO;
    _tableView.showsHorizontalScrollIndicator = NO;
    _tableView.bounces = NO;
    _tableView.sectionHeaderHeight = 0.0f;
    _tableView.sectionFooterHeight = 0.0f;
    _tableView.scrollEnabled = NO;
    [_tableView.tableHeaderView setNeedsLayout];
    [_tableView.tableHeaderView setNeedsDisplay];
    
    UIImageView * view = [[UIImageView alloc] init];
    [view setImage:[GTImage imageNamed:@"gt_ac_output" ofType:@"png"]];
    [_tableView setBackgroundView:view];
    [view release];
    
    [_VC.view addSubview:_tableView];
}

- (void)initInputUI
{
    CGFloat height = 0;
    NSMutableArray *array= [[GTInputList sharedInstance] acArray ];
    if ([array count] > 0) {
        if ([[GTUIManager sharedInstance] inputExtended]) {
            _editIcon = [[UIButton alloc] initWithFrame:CGRectMake(0, height, _width, 38.0f)];
            [_editIcon setBackgroundColor:[UIColor clearColor]];
            [_editIcon setImage:[GTImage imageNamed:@"gt_ac_edit" ofType:@"png"] forState:UIControlStateNormal];
            [_VC.view addSubview:_editIcon];
            
            _bar = [[GTInputBar alloc] initWithFrame:CGRectMake(10, height, _width-10, 38.0f) inputLists:array];
            _bar.delegate = self;
            [_VC.view addSubview:_bar];
            height += 38.0f;
        }
        
        _extendView = [[UIImageView alloc] initWithFrame:CGRectMake(0, height, _width, 15.0f)];
        [_extendView setImage:[GTImage imageNamed:@"gt_ac_bottom" ofType:@"png"]];
        [_VC.view addSubview:_extendView];
        
        _extendBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, height, _width, 25.0f)];
        [_extendBtn setBackgroundColor:[UIColor clearColor]];
        
        [_extendBtn addTarget:self action:@selector(extendButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_VC.view addSubview:_extendBtn];

        [_extendBtn setImageEdgeInsets:UIEdgeInsetsMake(4.5, _width/2 - 5, 14.5, _width/2 - 5)];
        if ([[GTUIManager sharedInstance] inputExtended]) {
            [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_up" ofType:@"png"] forState:UIControlStateNormal];
        } else {
            [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_down" ofType:@"png"] forState:UIControlStateNormal];
        }
    }
}
- (void)initPortrait:(UIInterfaceOrientation)orientation
{
    if (orientation == UIInterfaceOrientationPortrait) {
        _isPortrait = YES;
    } else if (orientation == UIInterfaceOrientationPortraitUpsideDown) {
        _isPortrait = YES;
    } else if (orientation == UIInterfaceOrientationLandscapeLeft) {
        _isPortrait = NO;
    } else {
        _isPortrait = NO;
    }
}

- (void)layoutView
{
    float headerHeight = [[GTConfig sharedInstance] acHeaderHeight];
    CGRect frame = self.bounds;
    frame.origin.y = 10.0f;
    frame.size.height = headerHeight;
    frame.size.width = _width;
    CGFloat height = frame.origin.y + frame.size.height;
    
    frame.origin.y = height;
    CGFloat cellHeight = [GTACOutputCell cellHeight];
    CGSize bound = CGSizeMake( _width, 0.0f );
    
    if (_adjustHeight) {
        frame.size.height = cellHeight * ([[[GTOutputList sharedInstance] acArray] count] - 1) + [GTACOutputCell cellHeight:[self contentObject:_selectIndexPath] bound:bound] + 0.0f;
    } else {
        frame.size.height = cellHeight * [[[GTOutputList sharedInstance] acArray] count] + 0.0f;
    }
    
    height += frame.size.height;
    [_tableView setFrame:frame];
    
    NSMutableArray *array= [[GTInputList sharedInstance] acArray ];
    if ([array count] > 0) {
        if ([[GTUIManager sharedInstance] inputExtended]) {
            _editIcon.hidden = NO;
            _bar.hidden = NO;
            [_editIcon setFrame:CGRectMake(0, height, _width, 38.0f)];
            [_bar setFrame:CGRectMake(10, height, _width-10, 38.0f)];
            height += 38.0f;
            [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_up" ofType:@"png"] forState:UIControlStateNormal];
        } else {
            _editIcon.hidden = YES;
            _bar.hidden = YES;
            [_editIcon setFrame:CGRectMake(0, height, _width, 0.0f)];
            [_bar setFrame:CGRectMake(10, height, _width-10, 0.0f)];
            
            [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_down" ofType:@"png"] forState:UIControlStateNormal];
        }
        [_extendView setFrame:CGRectMake(0, height, _width, 15.0f)];
        [_extendBtn setFrame:CGRectMake(0, height, _width, 25.0f)];
        
        height += 25.0f;
    }
    
    [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, _width, height)];
    //保存高度，用于窗口高度被APP意外修改时可修复
    _height = height;
    
    if (!_isFirst) {
        if (!_isPortrait) {
            [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, height, _width)];
        }
        [_consoleDelegate didRotate:_isPortrait];
    }
    
    _isFirst = NO;
}

- (void)initUI
{
    [self initHeaderUI];
    [self initOutputUI];
    [self initInputUI];
    [self layoutView];
}

- (id)initWithFrame:(CGRect)frame delegate:(id<GTACDelegate>)delegate
{
    self = [super initWithFrame:frame];
    if (self) {
        _consoleDelegate = delegate;
        _isFirst = YES;
        
        _width = frame.size.width;
        self.backgroundColor = [UIColor clearColor];
        // for DEBUG
//        self.backgroundColor = [UIColor redColor];
        self.hidden = NO;
        self.layer.borderWidth = 0;
        self.layer.borderColor =[[UIColor clearColor] CGColor];
        self.layer.cornerRadius = 1;
        self.layer.masksToBounds = YES;
        self.windowLevel = UIWindowLevelStatusBar + 200.0f;

        _VC = [[GTRotateBoard alloc] init];
        
        //这里不能直接使用addSubview，否则在ios5上会crash
        if ([self respondsToSelector:@selector(setRootViewController:)]) {
            self.rootViewController = _VC;
        } else {
            [self addSubview:_VC.view];
        }
        
        [_VC setDelegate:self];
        
        _VC.view.frame = self.bounds;
        _VC.view.backgroundColor = [UIColor clearColor];
        
        [self initPortrait:[_VC interfaceOrientation]];
        
        [self initUI];
        [self updateOutputUI];

        _floatingRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePan:)];
        [_floatingRecognizer setDelegate:self];
        _floatingRecognizer.maximumNumberOfTouches = 1;
        [self addGestureRecognizer:_floatingRecognizer];
        
        [self startUpdateTimer];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadData) name:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateHeaderSwitchUI:) name:M_GT_NOTIFICATION_AC_UPDATE object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateACSwitch) name:M_GT_NOTIFICATION_OUT_GW_UPDATE object:nil];
        
        [GTMTA trackPageViewBegin:NSStringFromClass([self class])];
//        NSLog(@"trackPageViewBegin:%@", NSStringFromClass([self class]));
    }
    return self;
}


- (void)updateInputUI
{
    M_GT_SAFE_RELEASE_SUBVIEW(_bar);
    M_GT_SAFE_RELEASE_SUBVIEW(_editIcon);
    M_GT_SAFE_RELEASE_SUBVIEW(_extendView);
    M_GT_SAFE_RELEASE_SUBVIEW(_extendBtn);

    [self initInputUI];
}


- (void)reloadData
{
    return;
    [self setSelectIndexPath:nil];
    
    M_GT_SAFE_RELEASE_SUBVIEW(_headerView);
    M_GT_SAFE_RELEASE_SUBVIEW(_watch);
    M_GT_SAFE_RELEASE_SUBVIEW(_switchInfo);
    M_GT_SAFE_RELEASE_SUBVIEW(_acSwitch);
    
    M_GT_SAFE_RELEASE_SUBVIEW(_tableView);
    
    M_GT_SAFE_RELEASE_SUBVIEW(_editIcon);
    M_GT_SAFE_RELEASE_SUBVIEW(_bar);
    M_GT_SAFE_RELEASE_SUBVIEW(_extendView);
    M_GT_SAFE_RELEASE_SUBVIEW(_extendBtn);
    
    [self initUI];
}

- (void)updateHeaderTimeUI
{
    NSDate *startTime = [[GTConfig sharedInstance] startTime];
    if ( startTime != nil) {
        NSTimeInterval time = [[NSDate date] timeIntervalSinceDate:startTime];
        if (time > 1000) {
            [[GTConfig sharedInstance] setStartTime:[NSDate date]];
            return;
        }
        [[GTConfig sharedInstance] setWatchTime:time];
        NSString *timeStr = [NSString stringWithFormat:@"%.1f\"", time];
        [_watch setTitle:timeStr forState:UIControlStateNormal];
    }
}

- (void)updateHeaderPerfUI
{
    BOOL profilerSwitch = [[GTLogConfig sharedInstance] profilerSwitch];
    if (profilerSwitch) {
        [_acSwitch setImage:[GTImage imageNamed:@"gt_stop" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        [_acSwitch setImage:[GTImage imageNamed:@"gt_start" ofType:@"png"] forState:UIControlStateNormal];
    }
}


- (void)updateOutputUI
{
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_AC_UPDATE object:nil];
    
    return;
}

- (void)updateUI
{
    [self updateHeaderTimeUI];
    [self updateHeaderPerfUI];
    [self updateOutputUI];
    [self updateInputUI];
    [self layoutView];
}

- (void)unload
{
    [self stopTimer];
    
    [self removeGestureRecognizer:_floatingRecognizer];
    M_GT_SAFE_FREE(_floatingRecognizer);
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_AC_UPDATE object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_GW_UPDATE object:nil];
    
    M_GT_SAFE_FREE(_selectIndexPath);
    M_GT_SAFE_FREE(_headerView);
    M_GT_SAFE_FREE(_watch);
    M_GT_SAFE_FREE(_switchInfo);
    M_GT_SAFE_FREE(_acSwitch);
    M_GT_SAFE_FREE(_tableView);
    M_GT_SAFE_FREE(_editIcon);
    M_GT_SAFE_FREE(_bar);
    M_GT_SAFE_FREE(_extendView);
    M_GT_SAFE_FREE(_extendBtn);
    
    M_GT_SAFE_FREE(_VC);
}

- (void)dealloc
{
    [self unload];
    
    [GTMTA trackPageViewEnd:NSStringFromClass([self class])];
//    NSLog(@"trackPageViewEnd:%@", NSStringFromClass([self class]));
    
    [super dealloc];
}

- (void)floatingTimerNotify:(id)sender
{
    [self updateOutputUI];
}

- (void)watchTimerNotify:(id)sender
{
    [self updateHeaderTimeUI];
}

- (void)stopWatchTimer
{
    if (_watchTimer) {
        [_watchTimer invalidate];
        [_watchTimer release];
        _watchTimer = nil;
    }
    
}

- (void)startUpdateTimer
{
    if (_updateTimer == nil) {
        _updateTimer = [[NSTimer alloc] initWithFireDate:[NSDate date] interval:[[GTConfig sharedInstance] acInterval] target:self selector:@selector(floatingTimerNotify:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_updateTimer forMode:NSRunLoopCommonModes];
    }
    
}

- (void)stopUpdateTimer
{
    if (_updateTimer) {
        [_updateTimer invalidate];
        [_updateTimer release];
        _updateTimer = nil;
    }
}

- (void)startAniTimer
{
    if (_aniTimer == nil) {
        _aniTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(aniTimerNotify:) userInfo:nil repeats:YES];
        [_aniTimer retain];
    }
}

- (void)stopAniTimer
{
    if (_aniTimer) {
        [_aniTimer invalidate];
        [_aniTimer release];
        _aniTimer = nil;
    }
}

- (void)stopTimer
{
    [self stopUpdateTimer];
    [self stopWatchTimer];
    [self stopAniTimer];
}


#pragma mark -

- (NSArray *)content:(NSInteger)section {
    return [[GTOutputList sharedInstance] acArray];
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
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self content:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTACOutputCell * cell = nil;
    cell = (GTACOutputCell *)[self.tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
        cell = [[[GTACOutputCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:self.tableView.frame] autorelease];
    }
    
    [cell bindData:[self contentObject:indexPath]];
    if (indexPath.row == 0) {
        [cell cellLayoutFirst];
    }
    
    if (_adjustHeight) {
        if ([_selectIndexPath isEqual:indexPath]) {
            [cell cellLayoutHeight];
        }
    }
    
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    cell.textLabel.backgroundColor = [UIColor clearColor];
    return cell;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath;
{
    CGSize bound = CGSizeMake( tableView.bounds.size.width, 0.0f );
    CGFloat height = [GTACOutputCell cellHeight];
    
    if (_adjustHeight) {
        if ([_selectIndexPath isEqual:indexPath]) {
            CGFloat adjustHeight = [GTACOutputCell cellHeight:[self contentObject:indexPath] bound:bound];
            
            _adjustHeight = [self needAdjustHeight:indexPath];
            if (_adjustHeight) {
                return adjustHeight;
            } else {
                return height;
            }
        }
    }
    
    return height;
}

- (BOOL)needAdjustHeight:(NSIndexPath *)indexPath
{
    CGSize bound = CGSizeMake( self.bounds.size.width, 0.0f );
    
    CGFloat height = [GTACOutputCell cellHeight];
    CGFloat adjustHeight = [GTACOutputCell cellHeight:[self contentObject:indexPath] bound:bound];
    
    //对于第一行因为字体不一样需要另外计算
    if (indexPath.row == 0) {
        CGFloat bigHeight = [GTACOutputCell cellBigHeight:[self contentObject:indexPath] bound:bound];
        if ((bigHeight > height)) {
            return YES;
        }
    }
    
    if (adjustHeight > height) {
        return YES;
    }
    
    return NO;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    [self stopUpdateTimer];
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTACOutputCell * cell = nil;
    cell = (GTACOutputCell *)[self.tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    [cell cellLayoutHeight];
    
    if ((_adjustHeight) && (([_selectIndexPath isEqual:indexPath]))) {
        _adjustHeight = NO;
        
    } else {
        BOOL adjustHeight = [self needAdjustHeight:indexPath];
        if (adjustHeight) {
            _adjustHeight = adjustHeight;
            [self setSelectIndexPath:indexPath];
        }
    }
    
    [self.tableView reloadData];
    [self layoutView];
    [self startUpdateTimer];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = [UIColor clearColor];
    
    if (_adjustHeight) {
        if ([_selectIndexPath isEqual:indexPath]) {
            cell.backgroundColor = [UIColor blackColor];
        }
    }
    
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return nil;
}

- (void)tableView:(UITableView *)tableView willDisplayHeaderView:(UIView *)view forSection:(NSInteger)section
{
}

#pragma mark - GTTabBarDelegate

- (void)tabBar:(GTInputBar *)tabBar didSelectIndex:(NSInteger)index
{
    _selectedIndex = index;
    id key = [[[GTInputList sharedInstance] acArray ] objectAtIndex:index];
    id selectObj = [[GTInputList sharedInstance] objectForKey:key];
    
    [_consoleDelegate onACEditWindow:selectObj];
}

#pragma mark - Button

- (void)extendButtonClicked:(id)sender
{
	BOOL extended = [[GTUIManager sharedInstance] inputExtended];
    
    [[GTUIManager sharedInstance] setInputExtended:!extended];
    
    [self updateInputUI];
    [self layoutView];
}

- (void)onWatch:(id)sender
{
    NSDate *startTime = [[GTConfig sharedInstance] startTime];
    if ( startTime == nil) {
        if ([_watch.titleLabel.text isEqualToString:@"0.0\""]) {
            [[GTConfig sharedInstance] setStartTime:[NSDate date]];
            _watchTimer = [NSTimer scheduledTimerWithTimeInterval:0.1  target:self selector:@selector(watchTimerNotify:) userInfo:nil repeats:YES];
            [_watchTimer retain];
        } else {
            [[GTConfig sharedInstance] setWatchTime:0];
            [_watch setTitle:@"0.0\"" forState:UIControlStateNormal];
            [self stopWatchTimer];
        }
    
    } else {
        NSTimeInterval time = [[NSDate date] timeIntervalSinceDate:startTime];
        NSString *timeStr = [NSString stringWithFormat:@"%.1f\"", time];
        [_watch setTitle:timeStr forState:UIControlStateNormal];
        [[GTConfig sharedInstance] setStartTime:nil];
        [self stopWatchTimer];
    }
}


- (void)updateHeaderSwitchUI:(NSNotification *)n
{
    if ([[GTConfig sharedInstance] acSwtichIndex] == GTACSwitchProfiler) {
        [_switchInfo setText:M_GT_LOCALSTRING(M_GT_PROFILER_KEY)];
    } else {
        [_switchInfo setText:M_GT_LOCALSTRING(M_GT_SETTING_AC_GW_KEY)];
    }

}

- (void)updateACSwitch
{
    BOOL on = NO;
    
    if ([[GTConfig sharedInstance] acSwtichIndex] == GTACSwitchProfiler) {
        on = [[GTLogConfig sharedInstance] profilerSwitch];
    } else {
        on = [[GTConfig sharedInstance] gatherSwitch];
    }
    
    if (on) {
        [_acSwitch setImage:[GTImage imageNamed:@"gt_stop" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        [_acSwitch setImage:[GTImage imageNamed:@"gt_start" ofType:@"png"] forState:UIControlStateNormal];
    }
}


- (void)onGatherSwitch:(id)sender
{
    BOOL gatherSwitch = [[GTConfig sharedInstance] gatherSwitch];
    gatherSwitch = !gatherSwitch;
    [[GTConfig sharedInstance] setGatherSwitch:gatherSwitch];
    
    [self updateACSwitch];
}

- (void)onACSwitch:(id)sender
{
    if ([[GTConfig sharedInstance] acSwtichIndex] == GTACSwitchProfiler) {
        [self onProfilerSwitch:sender];
    } else {
        [self onGatherSwitch:sender];
    }
}

- (void)onProfilerSwitch:(id)sender
{
    BOOL profilerSwitch = [[GTLogConfig sharedInstance] profilerSwitch];
    profilerSwitch = !profilerSwitch;
    [[GTLogConfig sharedInstance] setProfilerSwitch:profilerSwitch];
    [self updateACSwitch];
}

- (void)aniTimerNotify:(id)sender
{
    BOOL profilerSwitch = [[GTLogConfig sharedInstance] profilerSwitch];
    if (profilerSwitch) {
        [UIView beginAnimations:@"animationID" context:nil];
        [UIView setAnimationDuration:0.5f];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
        [UIView setAnimationRepeatAutoreverses:NO];
        [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:_acSwitch cache:YES];
        
        [_acSwitch setImage:[GTImage imageNamed:@"gt_stop" ofType:@"png"] forState:UIControlStateNormal];
        
        [UIView commitAnimations];
    } else {
        [self stopAniTimer];
    }
    
}

-(void)handlePan:(UIPanGestureRecognizer*)recognizer
{
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        CGPoint pt = [recognizer locationInView:self] ;
        _startPoint = pt;
        
    }
    if ((recognizer.state == UIGestureRecognizerStateChanged) || (recognizer.state == UIGestureRecognizerStateEnded)) {
        CGPoint pt = [recognizer locationInView:self];
        CGRect frame = [self frame];
        frame.origin.y += pt.y - _startPoint.y;
        frame.origin.x += pt.x - _startPoint.x;
        
        CGPoint offset = CGPointMake(pt.x - _startPoint.x, pt.y - _startPoint.y);
        [_consoleDelegate handlePanOffset:offset state:recognizer.state];
    }
    
}


#pragma mark - GTRotateDelegate

- (void)didRotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ((interfaceOrientation == UIInterfaceOrientationLandscapeLeft) || (interfaceOrientation == UIInterfaceOrientationLandscapeRight)) {
        if (_isPortrait) {
            [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.height, self.frame.size.width)];
        }
        _isPortrait = NO;
        
    }else if ((interfaceOrientation == UIInterfaceOrientationPortrait) || (interfaceOrientation == UIInterfaceOrientationPortraitUpsideDown)) {
        if (!_isPortrait) {
            [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.height, self.frame.size.width)];
        }
        _isPortrait = YES;
    }
    
    _VC.view.frame = self.bounds;
    if (_consoleDelegate) {
        [_consoleDelegate didRotate:_isPortrait];
    }
}

@end

#endif
