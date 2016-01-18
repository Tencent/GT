//
//  GTLogSearchBoard.m
//  GTKit
//
//  Created   on 13-4-4.
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

#import "GTLogSearchBoard.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>
#import "GTLog.h"
#import "GTConfig.h"
#import "GTCommonCell.h"

#define M_GT_FILTER_HEIGHT 40.0f


@interface GTLogSearchContent : UILabel
{
    NSString        *_selContent;
    NSObject        *_cellData;
    NSUInteger  _row;
}
@property (nonatomic, retain) NSString *selContent;
@property (nonatomic, retain) NSObject *cellData;
@property (nonatomic) NSUInteger row;
@end


@interface GTLogSearchCell : GTCommonCell
{
    NSUInteger  _row;
    NSUInteger  _locationRow;
    NSString    *_selContent;
//    GTLogSearchContent *_logContent;

}

@property (nonatomic) NSUInteger row;
@property (nonatomic) NSUInteger locationRow;
@property (nonatomic, retain) NSString *selContent;
//@property (nonatomic, retain) GTLogSearchContent *logContent;

+ (float)cellHeight:(NSObject *)data bound:(CGSize)bound;

@end



@implementation GTLogSearchContent
@synthesize cellData = _cellData;
@synthesize selContent = _selContent;
@synthesize row = _row;
- (void)drawRect:(CGRect)rect{
    GTLogRecord *obj = (GTLogRecord *)self.cellData;
    
    CGSize size;
    CGFloat width = 0;
    CGFloat viewWidth = self.bounds.size.width;
    CGFloat viewHeight = self.bounds.size.height;
    
    BOOL changeColor = NO;
    if ([_selContent length] > 0)
    {
        NSRange range = [[obj content] rangeOfString:_selContent options:NSCaseInsensitiveSearch];
        if (range.location != NSNotFound) {
            changeColor = YES;
        }
    }
    
    width = 0;
    NSString *rowNo = [NSString stringWithFormat:@"%.3lu ", (unsigned long)_row];
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor whiteColor].CGColor);
    if (changeColor) {
        CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor blackColor].CGColor);
    }
    [rowNo drawInRect:CGRectMake( 0.0f, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    width += 35;
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor grayColor].CGColor);
    size = [[NSString stringWithTimeEx:[obj date]] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    width += size.width;
    size = [@" " drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor orangeColor].CGColor);
    width += size.width;
    size = [[obj levelStr] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    width += size.width;
    size = [@"|" drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor orangeColor].CGColor);
    width += size.width;
    size = [[obj tag] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    width += size.width;
    size = [@"|" drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor orangeColor].CGColor);
    width += size.width;
    size = [[obj thread] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    if (changeColor) {
        CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor blackColor].CGColor);
    }
    
    width += size.width;
    size = [@" " drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    width += size.width;
    
    NSMutableString *str = [NSMutableString stringWithCapacity:1];
    //一个空格占用四个width
    for (int i = 0; i < width/4 + 1; i++) {
        [str appendString:@" "];
    }
    [str appendString:[obj content]];
    [str drawInRect:CGRectMake( 0, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    
    
}

@end

@implementation GTLogSearchCell

@synthesize row = _row;
@synthesize locationRow = _locationRow;
@synthesize selContent = _selContent;
//@synthesize logContent = _logContent;

+ (float)cellHeight:(NSObject *)data bound:(CGSize)bound
{
    GTLogRecord *obj = (GTLogRecord *)data;
    
    CGSize constrainedToSize = CGSizeMake(bound.width, 900);
    
    NSString *text = [NSString stringWithFormat:@"            %@ %@|%@|%@ %@", [NSString stringWithTimeEx:[obj date]], [obj levelStr], [obj tag], [obj thread], [obj content]];
    
    CGSize size = [text sizeWithFont:[UIFont systemFontOfSize:14.0f]
                   constrainedToSize:constrainedToSize
                       lineBreakMode:NSLineBreakByWordWrapping];
    float height = size.height;
    height += 10;
    
    return height;
}

- (void)drawRect:(CGRect)rect
{
//    [_logContent setNeedsDisplay];
    GTLogRecord *obj = (GTLogRecord *)self.cellData;
    
    CGSize size;
    CGFloat width = 0;
    CGFloat viewWidth = self.bounds.size.width;
    CGFloat viewHeight = self.bounds.size.height;
    
    BOOL changeColor = NO;
    if ([_selContent length] > 0)
    {
        NSRange range = [[obj content] rangeOfString:_selContent options:NSCaseInsensitiveSearch];
        if (range.location != NSNotFound) {
            changeColor = YES;
        }
    }
    
    
    if (changeColor) {
        CGContextRef context = UIGraphicsGetCurrentContext();
        if (context) {
            CGContextClearRect( context, self.bounds );
            if (_locationRow == _row) {
                CGColorRef color = [UIColor yellowColor].CGColor;
                CGContextSetFillColorWithColor(context, color);
                CGContextFillRect(context, CGRectMake( 0.0f, 0.0f, viewWidth, viewHeight ));
            } else {
                CGColorRef color = M_GT_LABEL_VALUE_COLOR.CGColor;
                CGContextSetFillColorWithColor(context, color);
                CGContextFillRect(context, CGRectMake( 0.0f, 0.0f, viewWidth, viewHeight ));
            }
        }
        
    }
    
    
    
    width = 0;
    NSString *rowNo = [NSString stringWithFormat:@"%.3lu ", (unsigned long)_row];
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor whiteColor].CGColor);
    if (changeColor) {
        CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor blackColor].CGColor);
    }
    
    [rowNo drawInRect:CGRectMake( 0.0f, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    width += 35;
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor grayColor].CGColor);
    size = [[NSString stringWithTimeEx:[obj date]] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    width += size.width;
    size = [@" " drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor orangeColor].CGColor);
    width += size.width;
    size = [[obj levelStr] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    width += size.width;
    size = [@"|" drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor orangeColor].CGColor);
    width += size.width;
    size = [[obj tag] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    width += size.width;
    size = [@"|" drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor orangeColor].CGColor);
    width += size.width;
    size = [[obj thread] drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    if (changeColor) {
        CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor blackColor].CGColor);
    }
    
    width += size.width;
    size = [@" " drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    width += size.width;
    
    NSMutableString *str = [NSMutableString stringWithCapacity:1];
    //一个空格占用四个width
    for (int i = 0; i < width/4 + 1; i++) {
        [str appendString:@" "];
    }
    [str appendString:[obj content]];
    [str drawInRect:CGRectMake( 0, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    
}


- (void)bindData:(NSObject *)data
{
    [super bindData:data];
//    _logContent.cellData = data;
    [self setNeedsDisplay];
    return;
}
- (void)cellLayout
{
//    _logContent.frame = CGRectMake( 3, 2, self.bounds.size.width, self.bounds.size.height);
}
- (void)load
{
    [super load];
//    _logContent = [[[GTLogSearchContent alloc] init] autorelease];
//    _logContent.userInteractionEnabled = NO;// 不设为NO会屏蔽cell的点击事件
//    _logContent.backgroundColor = [UIColor clearColor];// 设为透明从而使得cell.backgroundColor有效.
//    [self.contentView addSubview:_logContent];
}

@end

#pragma mark -

@interface GTLogSearchBoard ()

@end

@implementation GTLogSearchBoard

@synthesize tableView = _tableView;
@synthesize array = _array;
@synthesize resultArray = _resultArray;
@synthesize selContent = _selContent;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
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

- (void)load
{
    _selContent = @"";
    _currentIndex = 0;
    _isFingerTouched = NO;
    
    self.resultArray = [NSMutableArray array];
}

- (void)unload
{
    self.resultArray = nil;
    self.array = nil;
    
    M_GT_SAFE_FREE(_resultView);
    M_GT_SAFE_FREE(_resultInfo);
    M_GT_SAFE_FREE(_btnPrev);
    M_GT_SAFE_FREE(_btnNext);
    M_GT_SAFE_FREE(_accessoryView);
    

    [_tableView removeObserver:self forKeyPath:@"contentOffset"];
    M_GT_SAFE_FREE(_tableView);
    [_verticalScrollBar unobserveTick];
    M_GT_SAFE_FREE(_verticalScrollBar);
}

- (void)dealloc
{
    [self unload];
    [super dealloc];
}

- (void)initNavBarUI
{
    [self createTopBar];
    [self setNavBarHidden:YES];
    [self setNavTitle:@"Search"];
    
}

- (void)initSearchUI
{
    
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    
    CGRect rect = CGRectMake(0, 0, M_GT_SCREEN_WIDTH, M_GT_SCREEN_HEIGHT - M_GT_TARBAR_HEIGHT);
    CGRect frame = rect;
    
    frame.size.height = M_GT_FILTER_HEIGHT;
    _searchView = [[GTLogSearchView alloc] initWithFrame:frame viewController:self];
    [_searchView setDelegate:self];
    [self.view addSubview:_searchView];
    [_searchView release];
    
    frame.origin.x = frame.origin.x + 5.0f;
    frame.origin.y = rect.origin.y + M_GT_FILTER_HEIGHT;
    frame.size.height = rect.size.height - M_GT_FILTER_HEIGHT;
    frame.size.width = frame.size.width - 2 *5.0f;
    
    _tableView = [[GTUITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.touchesDelegate = self;
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
    
    _verticalScrollBar = [[GTVerticalScrollBar alloc] initWithFrame:frame];
    [_verticalScrollBar setScrollView:_tableView];
    [_verticalScrollBar setHandleHidden:YES];
    [self.view addSubview:_verticalScrollBar];
    
    [_tableView addObserver:self
                     forKeyPath:@"contentOffset"
                        options:NSKeyValueObservingOptionNew
                        context:nil];
    
    _accessoryView = [[GTAccessoryView alloc] initWithFrame:CGRectMake(0, 0, 65, 30)];
    [_accessoryView setForegroundColor:[UIColor colorWithWhite:0.2f alpha:1.0f]];
    [_verticalScrollBar setHandleAccessoryView:_accessoryView];
    
    _resultView = [[UIView alloc] initWithFrame:CGRectZero];
    _resultView.backgroundColor = M_GT_CELL_BKGD_COLOR;
    _resultView.hidden = YES;
    [self.view addSubview:_resultView];
    
    frame = CGRectMake(5, 3, 170, 38);
    _resultInfo = [[UILabel alloc] initWithFrame:frame];
    _resultInfo.font = [UIFont systemFontOfSize:11.0];
	_resultInfo.textColor = [UIColor whiteColor];
	_resultInfo.textAlignment = NSTextAlignmentCenter;
    _resultInfo.backgroundColor = [UIColor clearColor];
    [_resultInfo setText:@"Waiting..."];
    [_resultView addSubview:_resultInfo];
    
    frame.origin.x += frame.size.width + 5;
    frame.size.width = 65;
    _btnPrev = [[UIButton alloc] initWithFrame:frame];
    [_btnPrev.titleLabel setFont:[UIFont systemFontOfSize:12.0f]];
    [_btnPrev addTarget:self action:@selector(onClickPrev:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnPrev setTitle:@"Prev" forState:UIControlStateNormal];
    _btnPrev.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _btnPrev.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnPrev.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [_resultView addSubview:_btnPrev];
    
    frame.origin.x += frame.size.width + 5;
    frame.size.width = 65;
    _btnNext = [[UIButton alloc] initWithFrame:frame];
    [_btnNext.titleLabel setFont:[UIFont systemFontOfSize:12.0f]];
    [_btnNext addTarget:self action:@selector(onClickNext:forEvent:) forControlEvents:UIControlEventTouchUpInside];
    [_btnNext setTitle:@"Next" forState:UIControlStateNormal];
    _btnNext.backgroundColor = M_GT_BTN_BKGD_COLOR;
    _btnNext.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    _btnNext.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [_resultView addSubview:_btnNext];
}

#pragma mark - Button
- (void)onClickPrev:(id)sender forEvent:(UIEvent*)event
{
    NSUInteger oldIndex = _currentIndex;
    _currentIndex = [self getPrevIndex];
    
    [self reloadRowsAtIndex:oldIndex];
    [self reloadRowsAtIndex:_currentIndex];
    [_tableView selectRowAtIndexPath:[self indexPathAtIndex:_currentIndex]
                                animated:NO
                          scrollPosition:UITableViewScrollPositionMiddle];
    [_resultInfo setText:[self resultInfo]];
}

- (void)onClickNext:(id)sender forEvent:(UIEvent*)event
{
    NSUInteger oldIndex = _currentIndex;
    _currentIndex = [self getNextIndex];
    [self reloadRowsAtIndex:oldIndex];
    [self reloadRowsAtIndex:_currentIndex];
    [_tableView selectRowAtIndexPath:[self indexPathAtIndex:_currentIndex]
                                animated:NO
                          scrollPosition:UITableViewScrollPositionMiddle];
    [_resultInfo setText:[self resultInfo]];
}

- (void)initUI
{
    [self initNavBarUI];
    [self initSearchUI];
    
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
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



#pragma mark -

- (NSArray *)content:(NSUInteger)section {
    return _array;
}

- (id) contentObject:(NSIndexPath *)indexPath {
    return [_array objectAtIndex:indexPath.row];
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
    //解决滑屏时日志显示不全的问题
//    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier(%u)", indexPath.row%20];
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"cellIdentifier"];
    
    GTLogSearchCell * cell = (GTLogSearchCell *)[_tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTLogSearchCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    [cell bindData:[self contentObject:indexPath]];
    [cell setRow:indexPath.row];
    if (_currentIndex < [_resultArray count]) {
        [cell setLocationRow:[[_resultArray objectAtIndex:_currentIndex] integerValue]];
    }
    
    [cell setSelContent:_selContent];
    return cell;
    
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGRect bounds = tableView.bounds;
	CGSize bound = CGSizeMake( bounds.size.width, 0.0f );
	return [GTLogSearchCell cellHeight:[self contentObject:indexPath] bound:bound];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
//    GTLogRecord *obj = [self contentObject:indexPath];
//    if ([_selContent length] > 0) {
//        NSRange range = [[obj content] rangeOfString:_selContent options:NSCaseInsensitiveSearch];
//        if ((range.location != NSNotFound)) {
//            cell.backgroundColor = M_GT_LABEL_VALUE_COLOR;
//        }
//        
//        if (_currentIndex < [_resultArray count]) {
//            if ([[_resultArray objectAtIndex:_currentIndex] integerValue] == indexPath.row) {
//                cell.backgroundColor = [UIColor yellowColor];
//            }
//        }
//        
//    } else {
//        cell.backgroundColor = [UIColor clearColor];
//    }
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 0.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UILabel *titleLabel=[[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
    titleLabel.backgroundColor = [UIColor grayColor];
    titleLabel.textColor=[UIColor colorWithRed:(CGFloat)0x9d/(CGFloat)0xff green:(CGFloat)0x47/(CGFloat)0xff blue:(CGFloat)0x01/0xff alpha:1];
    titleLabel.text = @"11111";
	return titleLabel;
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView
{
    _isFingerTouched = YES;
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    _isFingerTouched = NO;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (!_isFingerTouched) {
        return;
    }
    [self observeTick];
    
    int currentPostion = scrollView.contentOffset.y;
    //第一次不计算
    if (_lastPosition == 0) {
        _lastPosition = currentPostion;
    }
    _sumOffset += currentPostion - _lastPosition;
    _lastPosition = currentPostion;
    
    if (abs(_sumOffset) > 1000) {
        [_verticalScrollBar setHandleHidden:NO];
        [_verticalScrollBar unobserveTick];
        [_verticalScrollBar observeTick];
        _sumOffset = 0;
        [self unobserveTick];
    }
}

#pragma mark - GTUITableViewTouchesDelegate
- (void)view:(UIView*)view touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
    [_tableView reloadData];
}

#pragma mark -
- (void)observeTick
{
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:NO];
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
    if (abs(_sumOffset) > 1000) {
        [_verticalScrollBar setHandleHidden:NO];
        [_verticalScrollBar unobserveTick];
        [_verticalScrollBar observeTick];
    }
    _sumOffset = 0;
    [self unobserveTick];
}

#pragma mark - GTLogSearchDelegate

- (void)updateResultArray
{
    [_resultArray removeAllObjects];
    
    if ([_selContent length] == 0) {
        return;
    }
    
    for (int i = 0; i < [_array count]; i++) {
        GTLogRecord *obj = [_array objectAtIndex:i];
        NSRange range = [[obj content] rangeOfString:_selContent options:NSCaseInsensitiveSearch];
        if ((range.location != NSNotFound)) {
            [_resultArray addObject:[NSNumber numberWithInteger:i]];
        }
    }
    
    _currentIndex = 0;
}

- (NSUInteger)getPrevIndex
{
    if ([_resultArray count] == 0) {
        return 0;
    }

    NSUInteger index = _currentIndex;
    
    if (index > 0) {
        index--;
    } else {
        index = [_resultArray count] - 1;
    }
    
    return index;
}

- (NSUInteger)getNextIndex
{
    if ([_resultArray count] == 0) {
        return 0;
    }
    
    NSUInteger index = _currentIndex;
    
    index++;
    if (index >= [_resultArray count]) {
        index = 0;
    }
    
    return index;
}

- (NSIndexPath *)indexPathAtIndex:(NSUInteger)row
{
    if (row >= [_resultArray count]) {
        return nil;
    }
    
    NSUInteger indexPathRow = [[_resultArray objectAtIndex:row] integerValue];
    if (indexPathRow >= [_array count]) {
        return nil;
    }
    return [NSIndexPath indexPathForRow:indexPathRow inSection:0];
}

- (void)reloadRowsAtIndex:(NSUInteger)row
{
    NSIndexPath *indexPath = [self indexPathAtIndex:row];
    if (indexPath == nil) {
        return;
    }
    
    NSArray * array = [NSArray arrayWithObject:indexPath];
    [_tableView reloadRowsAtIndexPaths:array withRowAnimation:UITableViewRowAnimationNone];
}

- (NSString*)resultInfo
{
    if ([_resultArray count] == 0) {
        return [NSString stringWithFormat:@"No results"];
    }
    
    return [NSString stringWithFormat:@"%lu/%lu results", (unsigned long)(_currentIndex+1), (unsigned long)[_resultArray count]];
}

- (void)updateContent:(NSString *)content
{
    [self setSelContent:content];
    [self updateResultArray];
    
    CGRect rect = CGRectMake(0, 0, M_GT_SCREEN_WIDTH, M_GT_SCREEN_HEIGHT - M_GT_TARBAR_HEIGHT);
    CGRect frame;
    
    frame.origin.x = rect.origin.x + 5.0f;
    frame.origin.y = rect.origin.y + M_GT_FILTER_HEIGHT;
    frame.size.height = rect.size.height - M_GT_FILTER_HEIGHT;
    frame.size.width = rect.size.width - 2 *5.0f;
    
    if ([_selContent length] > 0) {
        
        frame.size.height -= 44.0f;
        [_tableView setFrame:frame];
        [_verticalScrollBar setFrame:frame];
        
        frame.origin.x = rect.origin.x;
        frame.size.width = rect.size.width;
        frame.size.height = 44.0f;
        frame.origin.y = rect.size.height - 44.0f;
        [_resultInfo setText:[self resultInfo]];
        _resultView.hidden = NO;
        [_resultView setFrame:frame];
    } else {
        [_tableView setFrame:frame];
        [_verticalScrollBar setFrame:frame];
        _resultView.hidden = YES;
        [_resultView setFrame:CGRectZero];
    }
    
    [_tableView reloadData];
    [self reloadRowsAtIndex:_currentIndex];
    [_tableView selectRowAtIndexPath:[self indexPathAtIndex:_currentIndex]
                                animated:NO
                          scrollPosition:UITableViewScrollPositionMiddle];
}

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
