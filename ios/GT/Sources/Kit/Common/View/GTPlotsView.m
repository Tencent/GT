//
//  GTPlotsView.m
//  GTKit
//
//  Created   on 12-10-13.
//
//   ______    ______    ______
//  /\  __ \  /\  ___\  /\  ___\
//  \ \  __<  \ \  __\_ \ \  __\_
//   \ \_____\ \ \_____\ \ \_____\
//    \/_____/  \/_____/  \/_____/
//
//
//  Copyright (c) 2014-2015, Geek Zoo Studio
//  http://www.bee-framework.com
//
//
//  Permission is hereby granted, free of charge, to any person obtaining a
//  copy of this software and associated documentation files (the "Software"),
//  to deal in the Software without restriction, including without limitation
//  the rights to use, copy, modify, merge, publish, distribute, sublicense,
//  and/or sell copies of the Software, and to permit persons to whom the
//  Software is furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//  IN THE SOFTWARE.
//
//
//

#ifndef GT_DEBUG_DISABLE

#import "GTPlotsView.h"
#import "GTDebugDef.h"
#import "GTProfilerValue.h"
#import "GTConfig.h"
#import "GTLang.h"
#import "GTLangDef.h"


typedef enum {
	GTWarningOutside = 0,
	GTWarningEdgeStart,
    GTWarningEdgeEnd,
    GTWarningInside
} GTWarningResult;

#define M_GT_POP_LABEL_HEIGHT 15

@implementation GTPlotsData

@synthesize dates = _dates;
@synthesize curves = _curves;
@synthesize historyIndex = _historyIndex;
@synthesize historyCnt = _historyCnt;

- (id)init
{
    self = [super init];
    if (self) {
        self.dates = nil;
        self.curves = nil;
        self.historyIndex = nil;
        self.historyCnt = nil;
    }
    
    return self;
}

- (void)dealloc
{
    self.dates  = nil;
    self.curves = nil;
    [super dealloc];
}

- (NSString *)description
{
    if ([_curves count] > 0) {
        return [NSString stringWithFormat:@"dates count:%lu [_curves objectAtIndex:0] count:%lu\r\nhistoryIndex:%lu historyCnt:%lu\r\n", (unsigned long)[_dates count], (unsigned long)[[_curves objectAtIndex:0] count], (unsigned long)_historyIndex, (unsigned long)_historyCnt];
    }
    return [NSString stringWithFormat:@"dates count:%lu\r\ncurves count:%lu\r\nhistoryIndex:%lu\r\nhistoryCnt:%lu\r\n", (unsigned long)[_dates count], (unsigned long)[_curves count], (unsigned long)_historyIndex, (unsigned long)_historyCnt];
}

@end


@interface GTPlotsView()
{
    UIView          *_popView;
    NSMutableArray  *_labels;
}
@end

@implementation GTPlotsView

@synthesize xValues = _xValues;
@synthesize yValues = _yValues;

@synthesize lineColors = _lineColors;
@synthesize warningColor = _warningColor;
@synthesize xLayerBound = _xLayerBound;
@synthesize yLayerBound = _yLayerBound;
@synthesize lineWidth = _lineWidth;
@synthesize lowerBound = _lowerBound;
@synthesize upperBound = _upperBound;
@synthesize autoCalBound = _autoCalBound;
@synthesize showAvg = _showAvg;
@synthesize status = _status;
@synthesize capacity = _capacity;
@synthesize startIndex = _startIndex;
@synthesize showMode = _showMode;
@synthesize plots = _plots;
@synthesize lowerWarningList = _lowerWarningList;
@synthesize upperWarningList = _upperWarningList;

@synthesize dataSource = _dataSource;

@synthesize xDesc = _xDesc;
@synthesize yDesc = _yDesc;

- (id)initWithFrame:(CGRect)frame
{
	self = [super initWithFrame:frame];
	if (self)
	{
        //PopView
        _popView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, self.frame.size.width, 20)];
        [_popView setBackgroundColor:[UIColor clearColor]];
        [_popView setAlpha:1.0f];
        [self addSubview:_popView];
        
        
        UIPanGestureRecognizer *recognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePan:)];
        [recognizer setDelegate:self];
        recognizer.maximumNumberOfTouches = 1;
        [self addGestureRecognizer:recognizer];
        [recognizer release];
        
        UILongPressGestureRecognizer *longPressGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleLongPressGesture:)];
        [longPressGesture setDelegate:self];
        longPressGesture.minimumPressDuration = 0.5;
        [self addGestureRecognizer:longPressGesture];
        [longPressGesture release];
        
        [self load];
	}
	return self;
}

- (void)load
{
    _xValues = [[NSMutableArray alloc] initWithCapacity:1];
    _dateValues = [[NSMutableArray alloc] initWithCapacity:1];
    _yValues = [[NSMutableArray alloc] initWithCapacity:1];
    _avgValues = [[NSMutableArray alloc] initWithCapacity:1];
    _labels = [[NSMutableArray alloc] initWithCapacity:1];
    
    self.lineColors = [NSArray arrayWithObjects:M_GT_PLOTS_LINE_COLOR, M_GT_PLOTS_LINE1_COLOR, M_GT_PLOTS_LINE2_COLOR, nil];
    [self setLabelNum:3];
    
    self.warningColor = M_GT_WARNING_COLOR;
    self.xLayerBound = 40;
    self.yLayerBound = 40;
    self.lineWidth = 1.0f;
    self.lowerBound = 0.0f;
    self.upperBound = 1.0f;
    self.capacity = 50;
    self.startIndex = 0;
    self.plots = nil;
    self.lowerWarningList = nil;
    self.upperWarningList = nil;
    _status = GTPlotsStatusNormal;
    _showMode = GTPlotsShowLatest;
    _upperIndex = 0;
    _lowerIndex = 0;
    _showValue = NO;
    _autoCalBound = YES;
    _dataSource = nil;
    self.xDesc = M_GT_LOCALSTRING(M_GT_TIME_KEY);
    self.yDesc = @"";
}

- (void)unload
{
    [_xValues removeAllObjects];
    [_xValues release];
    
    [_dateValues removeAllObjects];
    [_dateValues release];
    
    [_yValues removeAllObjects];
    [_yValues release];
    
    [_avgValues removeAllObjects];
    [_avgValues release];
    
    [_labels removeAllObjects];
    [_labels release];
    
    [_warningColor release];
	[_plots release];
	
    [_popView release];
    
    self.lineColors = nil;
    self.lowerWarningList = nil;
    self.upperWarningList = nil;
    self.xDesc = nil;
    self.yDesc = nil;
}

- (void)dealloc
{
	[self unload];
	[super dealloc];
}

//校验数据是否准确，如果是查看最新的数据，会根据historyCnt更新开始和结束坐标
- (BOOL)checkDatas
{
    //判断曲线是否有队列
    if ([[_plots curves] count] == 0) {
        return NO;
    }
    
    //判断曲线队列里是否有数据
    if ([[[_plots curves] objectAtIndex:0] count] == 0) {
        return NO;
    }
    
    //判断历史数据记录是否非0
    if ([_plots historyCnt] == 0) {
        return NO;
    }
    
    //判断日期的个数和数组的个数是否一致
    NSUInteger dateCnt = [[_plots dates] count];
    for (int j = 0; j < [[_plots curves] count]; j++) {
        NSUInteger curveCnt = [[[_plots curves] objectAtIndex:j] count];
        if (curveCnt != dateCnt) {
            return NO;
        }
    }
    
    return YES;
}

- (BOOL)checkIndexValid
{
    if ([[_plots curves] count] == 0) {
        return NO;
    }
    
    NSInteger endIndex = MIN(_newStartIndex + self.capacity, [_plots historyCnt]);
    
    //判断开始坐标是否都落在_plots里
    if ((_newStartIndex >= [_plots historyIndex])
        && (endIndex <= [_plots historyIndex] + [[[_plots curves] objectAtIndex:0] count])
        ) {
        return YES;
    }
    
    return NO;
}

// 预读数据，曲线前后预留5屏
- (void)preReadData
{
    if ([_dataSource respondsToSelector:@selector(loadHistroyDatas:)]) {
        [_dataSource loadHistroyDatas:_newStartIndex];
    }
    return;
    
    if ([[_plots curves] count] == 0) {
        return;
    }
    
    NSInteger preOffset = self.capacity * 5;
    NSInteger preStartIndex = MAX(0, _newStartIndex - preOffset);
    NSInteger preEndIndex = MIN(_newStartIndex + self.capacity + preOffset, [_plots historyCnt]);
    
    //左边界超出,预读
    if (preStartIndex < [_plots historyIndex])
    {
        if ([_dataSource respondsToSelector:@selector(loadHistroyDatas:)]) {
            [_dataSource loadHistroyDatas:_newStartIndex];
            _status = GTPlotsStatusLoading;
        }
        
    }
    
    //右边界超出,预读
    if (preEndIndex > [_plots historyIndex] + [[[_plots curves] objectAtIndex:0] count]) {
        if ([_dataSource respondsToSelector:@selector(loadHistroyDatas:)]) {
            [_dataSource loadHistroyDatas:_newStartIndex];
            _status = GTPlotsStatusLoading;
        }
        
    }

}

- (void)loadXValues
{
    //判断下标是否有效
    if (![self checkIndexValid]) {
        //需要查看的数据不在当前_plots中，知会上层获取
        if (_status == GTPlotsStatusLoading) {
            return;
        }
        
        if ([_dataSource respondsToSelector:@selector(loadHistroyDatas:)]) {
            [_dataSource loadHistroyDatas:_newStartIndex];
            _status = GTPlotsStatusLoading;
            
            //_newStartIndex恢复之前有效的记录
            _newStartIndex = _startIndex;
        }
        return;
    }
    
    //预读数据处理
    [self preReadData];
    
    _status = GTPlotsStatusNormal;
    //记录新展示的起始坐标
    _startIndex = _newStartIndex;
    
    //清除老数据
    [_xValues removeAllObjects];
    [_dateValues removeAllObjects];

    NSMutableArray *array = nil;
    
    for (int j = 0; j < [[_plots curves] count]; j++) {
        array = [[NSMutableArray alloc] init];
        
        NSInteger startIndex = _startIndex - [_plots historyIndex];
        //此处取MAX为保护操作，正常情况下startIndex不会超出队列
//        NSInteger startIndex = MAX(0, _startIndex - [_plots historyIndex]);
        
        //此处取MIN为保护操作，正常情况下endIndex不会超出队列（不包括endIndex下标）
        NSInteger endIndex = MIN(startIndex + self.capacity, [[[_plots curves] objectAtIndex:j] count]);
        
        for (NSInteger i = startIndex; i < endIndex; i++) {
            [array addObject:[[[_plots curves] objectAtIndex:j] objectAtIndex:i]];
            [_dateValues addObject:[[_plots dates] objectAtIndex:i]];
        }
        [_xValues addObject:array];
        [array release];
    }
}

- (void)initXAxis
{
    //保证用户删除数据时及时刷新UI
    if ([_plots historyCnt] == 0) {
        //清除老数据
        [_xValues removeAllObjects];
        [_dateValues removeAllObjects];
        return;
    }
    
    
    //数据有效，才考虑获取数据更新_xValues
    if ([self checkDatas]) {
        if (_showMode == GTPlotsShowLatest) {
            //如果是展示最新数据，根据historyCnt最新计算开始和结束坐标
            _newStartIndex = [_plots historyIndex];
            if ([[_plots curves] count] > 0) {
                if ([[[_plots curves] objectAtIndex:0] count] > _capacity) {
                    _newStartIndex = [_plots historyIndex] + [[[_plots curves] objectAtIndex:0] count] - _capacity - 1;
                }
            }
            
        } else {
            //如果是展示历史数据，使用滑动时计算的开始和结束坐标，这里不需要计算
        }
        
        
        [self loadXValues];
    }
    
}

- (void)initYAxis
{
    CGFloat lowerBound = 0.0f;
    CGFloat upperBound = 0.0f;
    CGFloat avgValue = 0.0f;
    
    [_yValues removeAllObjects];
    [_avgValues removeAllObjects];
    
    if ([_xValues count] > 0) {
        NSArray *array = [_xValues objectAtIndex:0];
        if ([array count] > 0) {
            //初始值默认为数组的第一条数据
            lowerBound = [[array objectAtIndex:0] floatValue];
            upperBound = [[array objectAtIndex:0] floatValue];
        }
    }
    
    for (int i = 0; i < _xValues.count; i++) {
        NSArray *array = [_xValues objectAtIndex:i];
        float sum = 0;
        for (int j = 0; j < [array count]; j++) {
            NSNumber *value = [array objectAtIndex:j];
            sum += [value floatValue];
            lowerBound = fminf( [value floatValue], lowerBound );
            upperBound = fmaxf( [value floatValue], upperBound );
        }
        if (_showAvg) {
            if ([array count] > 0) {
                avgValue = sum / [array count];
                [_avgValues addObject:[NSNumber numberWithFloat:avgValue]];
            }
        }
        
        
    }
    
    // 如果需要计算边框值则使用计算后的值
    if (_autoCalBound) {
        _upperBound = upperBound;
        _lowerBound = lowerBound;
        
        if (_upperBound > 0) {
            _upperBound *= 1.1;
        } else {
            _upperBound *= 0.9;
        }
        
        if (_lowerBound >0) {
            _lowerBound *= 0.9;
        } else {
            _lowerBound *= 1.1;
        }
    }
    
    //Y轴划分
    int cnt = self.frame.size.height*6/self.frame.size.width;
    float interval = (_upperBound - _lowerBound)/cnt;
    
    for (int i = 0; i < cnt + 1; i++) {
        [_yValues addObject:[NSNumber numberWithFloat:(_lowerBound + interval * i)]];
    }
}


-(void)drawXAxis
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    if (!context) {
        return;
    }
    CGContextBeginPath(context);
    CGContextSetShouldAntialias(context, NO);
    CGContextSetLineWidth(context, 1.f);
    CGContextSetStrokeColorWithColor(context, M_GT_PLOTS_AXIS_COLOR.CGColor);
    
    CGRect frame = self.bounds;
    
    CGFloat x = frame.origin.x + _xLayerBound;
    CGFloat y = frame.origin.y + frame.size.height - _yLayerBound;
    CGFloat width = frame.size.width - _xLayerBound - _xLayerBound;
    CGFloat height = frame.size.height - _yLayerBound - _yLayerBound;
    
    // 画X轴
    CGContextMoveToPoint(context, x, y);
    CGContextAddLineToPoint(context, x + width + 10, y);
    CGContextStrokePath(context);
    
    CGFloat interval = width/_capacity;
    NSUInteger index = 0;
    
    CGContextSetStrokeColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_SHADOW_COLOR.CGColor);
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_TEXT_COLOR.CGColor);
    
    // 画X轴上的纵线
    for(int i = 0; i <= _capacity; i++)
    {
        //分五格，对应纵线六条
        if (i % (_capacity/5) == 0) {
            index = i;
            //需要每次设置一下，否则在ios7上会和text颜色冲突
            CGContextSetStrokeColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_SHADOW_COLOR.CGColor);
            CGContextMoveToPoint(context, x + interval * index, y - height - 5);
            CGContextAddLineToPoint(context, x + interval * index, y);
            CGContextStrokePath(context);
        }
        
        //对应时间刻度显示三个
        if (i % (_capacity/3) == 0) {
            index = i;
//            //如果对应有数据，则显示对应数据的时间刻度
//            NSInteger startIndex = _startIndex - [_plots historyIndex];
//            //此处取MIN为保护操作，正常情况下endIndex不会超出队列（不包括endIndex下标）
//            NSInteger endIndex = MIN(startIndex + self.capacity, [[[_plots curves] objectAtIndex:j] count]);
            
            if (index < [_dateValues count]) {
                NSNumber *value = [_dateValues objectAtIndex:index];
                NSString *str = [NSString stringWithTime:[value doubleValue]];
                [str drawAtPoint:CGPointMake(x + interval * index - 15, y + 5) withFont:[UIFont systemFontOfSize:10]];
            }
        }
    }
    
    
    
    CGContextStrokePath(context);
    
    // 横坐标单位描述
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_TEXT_COLOR.CGColor);
    [self.xDesc drawAtPoint:CGPointMake(x + interval * _capacity + 7, y + _yLayerBound/2) withFont:[UIFont systemFontOfSize:10]];
    
    CGContextStrokePath(context);
}

-(void)drawYAxis
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    if (!context) {
        return;
    }
    
    CGContextBeginPath(context);
	CGContextSetShouldAntialias(context, NO);
	CGContextSetLineWidth(context, 1.f);
	CGContextSetStrokeColorWithColor(context, M_GT_PLOTS_AXIS_COLOR.CGColor);
    
    CGRect frame = self.bounds;
    
    CGFloat x = frame.origin.x;
    CGFloat y = frame.origin.y + _yLayerBound;
    CGFloat height = frame.size.height - _yLayerBound - _yLayerBound;
    CGFloat width = frame.size.width - _xLayerBound - _xLayerBound;
    
    // 画Y轴
    CGContextMoveToPoint(context, x + _xLayerBound, y - 10);
    CGContextAddLineToPoint(context, x + _xLayerBound, y + height);
    CGContextStrokePath(context);
    
    CGFloat interval = 0;
    if (self.yValues.count > 1) {
         interval = height/(self.yValues.count - 1);
    }
    
    CGContextSetStrokeColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_SHADOW_COLOR.CGColor);
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_TEXT_COLOR.CGColor);
    
    // 画Y轴上水平线
	for(int i = 0; i < self.yValues.count; i++){
        if (i > 0) {
            //需要每次设置一下，否则在ios7上会和text颜色冲突
            CGContextSetStrokeColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_SHADOW_COLOR.CGColor);
            CGContextMoveToPoint(context, x + _xLayerBound, y + height - interval * i);
            CGContextAddLineToPoint(context, x + _xLayerBound + width + 5, y + height - interval * i);
            CGContextStrokePath(context);
        }
        
        
        NSNumber *number = [self.yValues objectAtIndex:i];
        NSString * str = [NSString stringWithFormat:@"%.3f", [number floatValue]];
        [str drawInRect:CGRectMake(x + 5, y + height - 10 - interval * i, 30, 12) withFont:[UIFont systemFontOfSize:9] lineBreakMode:NSLineBreakByCharWrapping alignment:NSTextAlignmentRight];
	}
    
	CGContextStrokePath(context);
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_PLOTS_AXIS_TEXT_COLOR.CGColor);
    [self.yDesc drawInRect:CGRectMake(x + 5, y - _yLayerBound/2 - 12, 30, 12) withFont:[UIFont systemFontOfSize:9] lineBreakMode:NSLineBreakByCharWrapping alignment:NSTextAlignmentRight];
    
    CGContextStrokePath(context);
    
    if (!_showAvg) {
        return;
    }
    
    // 只有单条曲线时画平均线, 防止过多平均线混乱
    if ([_avgValues count] != 1) {
        return;
    }
    
    CGFloat avgValue = [[_avgValues objectAtIndex:0] floatValue];
    
    CGContextSetStrokeColorWithColor(context, M_GT_PLOTS_AXIS_AVG_COLOR.CGColor);
    CGFloat f = ( avgValue - _lowerBound ) / (_upperBound - _lowerBound);
    
    CGContextMoveToPoint(context, x + _xLayerBound, y + height * (1.0 - f));
    CGContextAddLineToPoint(context, x + _xLayerBound + width + 35, y + height * (1.0 - f));
    CGContextStrokePath(context);
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_VALUE_COLOR.CGColor);
    NSString * str = [NSString stringWithFormat:@"%.3f", avgValue];
    [str drawInRect:CGRectMake(x + _xLayerBound + width, y + height * (1.0 - f) - 12, 35, 12) withFont:[UIFont systemFontOfSize:9] lineBreakMode:NSLineBreakByWordWrapping alignment:NSTextAlignmentRight];
    
    CGContextStrokePath(context);
    
    
}

- (GTWarningResult)valueInWarningList:(NSTimeInterval)date
{
    
    GTWarningResult result = GTWarningOutside;
    BOOL checkNext = NO;
    do {
        checkNext = NO;
        if (_upperIndex < [[_upperWarningList keys] count]) {
            id key = [[_upperWarningList keys] objectAtIndex:_upperIndex];
            GTWarningSegment *segment = [_upperWarningList objectForKey:key];
            if (segment == nil) {
                break;
            }
            
            NSTimeInterval secondBetweenDates = date - [segment startDate];
            
            //当前时间迟于startDate
            if (secondBetweenDates > 0) {
                secondBetweenDates = date - [segment endDate];
                
                //当前时间早于endDate
                if (secondBetweenDates < 0) {
                    result = GTWarningInside;
                } else  if (secondBetweenDates == 0) {
                    result = GTWarningEdgeEnd;
                } else {
                    //过了该区间，跳下一个
                    _upperIndex++;
                    checkNext = YES;
                }
            } else if (secondBetweenDates == 0) {
                result = GTWarningEdgeStart;
            }
        }
    } while (checkNext);
    
    
    do {
        checkNext = NO;
        if (_lowerIndex < [[_lowerWarningList keys] count]) {
            id key = [[_lowerWarningList keys] objectAtIndex:_lowerIndex];
            GTWarningSegment *segment = [_lowerWarningList objectForKey:key];
            if (segment == nil) {
                break;
            }
            
            NSTimeInterval secondBetweenDates = date - [segment startDate];
            
            //当前时间迟于startDate
            if (secondBetweenDates > 0) {
                secondBetweenDates = date - [segment endDate];
                
                //当前时间早于endDatedate
                if (secondBetweenDates < 0) {
                    result = GTWarningInside;
                } else  if (secondBetweenDates == 0) {
                    result = GTWarningEdgeEnd;
                } else {
                    //过了该区间，跳下一个
                    _lowerIndex++;
                    checkNext = YES;
                }
            } else if (secondBetweenDates == 0) {
                result = GTWarningEdgeStart;
            }
        }
    } while (checkNext);
    
    
    return result;
}

-(void)drawChart
{
    CGRect frame = self.bounds;
    
    CGFloat x = frame.origin.x + _xLayerBound;
    CGFloat y = frame.origin.y + _yLayerBound;
    CGFloat width = frame.size.width - _xLayerBound - _xLayerBound;
    CGFloat height = frame.size.height - _yLayerBound - _yLayerBound;

	CGContextRef context = UIGraphicsGetCurrentContext();
	if (!context) {
        return;
    }

    CGContextSetAllowsAntialiasing(context, true);
    
    CGPoint baseLine;
    CGFloat f;
    CGPoint p;
    
    for (int j = 0; j < _xValues.count; j++) {
        NSUInteger step = 0;
        NSArray *array = [_xValues objectAtIndex:j];
        baseLine.x = x;
        baseLine.y = y + height;
        if ([array count] == 0) {
            continue;
        }
        for ( int i = 0; i < [array count]; i++ )
        {
            GTWarningResult result = GTWarningOutside;
            if (i < [_dateValues count]) {
                //取该数据的时间，判断是否则告警线上；
                NSNumber *date = [_dateValues objectAtIndex:i];
                result = [self valueInWarningList:[date doubleValue]];
            }
            
            if ((result == GTWarningInside) || (result == GTWarningEdgeEnd)) {
                CGContextSetStrokeColorWithColor( context, self.warningColor.CGColor );
            } else {
                if (_lineColors.count > j) {
                    CGContextSetStrokeColorWithColor( context, ((UIColor*)[_lineColors objectAtIndex:j]).CGColor );
                }
                else {
                    CGContextSetStrokeColorWithColor( context, M_GT_CELL_TEXT_COLOR.CGColor );
                }
            }
            
            NSNumber *value = [array objectAtIndex:i];
            if (_upperBound - _lowerBound != 0) {
                f = ( [value floatValue]- _lowerBound ) / (_upperBound - _lowerBound);
            } else {
                f = 0;
            }
            
            baseLine.x += width / _capacity;
            
            p = CGPointMake( baseLine.x, baseLine.y - height * f );
            if (i > 0) {
                CGContextAddLineToPoint( context, p.x, p.y );
                CGContextStrokePath( context );
            }
            
            
            if (result != GTWarningOutside) {
                CGContextSetStrokeColorWithColor( context, self.warningColor.CGColor );
            } else {
                if (_lineColors.count > j) {
                    CGContextSetStrokeColorWithColor( context, ((UIColor*)[_lineColors objectAtIndex:j]).CGColor );
                }
                else {
                    CGContextSetStrokeColorWithColor( context, M_GT_CELL_TEXT_COLOR.CGColor );
                }
            }
            
            CGContextAddEllipseInRect(context, CGRectMake(p.x - 1, p.y - 1, 2, 2));
            CGContextSetLineWidth( context, self.lineWidth );
            CGContextSetLineCap( context, kCGLineCapRound );
            CGContextSetLineJoin( context, kCGLineJoinRound );
            CGContextStrokePath( context );
            CGContextMoveToPoint( context, p.x, p.y );
            
            step += 1;
            if ( step >= _capacity ) {
                break;
            }
        }
        
    }

}

-(void)drawValue
{
    if (!_showValue) {
        for (int i = 0; i < _labels.count; i++) {
            ((UILabel*)[_labels objectAtIndex:i]).hidden = YES;
        }
        return;
    }
    NSArray * labelStrs = nil;
    if (_dataSource && [_dataSource respondsToSelector:@selector(popValueStrs:)]) {
        labelStrs = [_dataSource popValueStrs:(_valueIndex+_startIndex)];
    }
    
    for (int i = 0; i < [_labels count]; i++) {
        UILabel * labelnow = [_labels objectAtIndex:i];
        labelnow.hidden = NO;
        
        if ([_xValues count] == 0) {
            return;
        }
        if (_valueIndex >= [[_xValues objectAtIndex:0] count]) {
            return;
        }
        
        NSString *valueStr = [NSMutableString string];
        
        if (labelStrs != nil) {
            if (i < [labelStrs count]) {
                valueStr = (NSString *)[labelStrs objectAtIndex:i];
            }
            
        } else {
            if (i < [_xValues count]) {
                NSArray *array = [_xValues objectAtIndex:i];
                if (_valueIndex < [array count]) {
                    if (_valueIndex < [_dateValues count]) {
                        NSNumber *date = [_dateValues objectAtIndex:_valueIndex];
                        NSNumber *value = [array objectAtIndex:_valueIndex];
                        valueStr = [NSString stringWithFormat:@"%@,%.3f", [NSString stringWithTimeEx:[date doubleValue]], [value floatValue]];
                    }
                    
                }
                
            }
        }
        
        [labelnow setText:valueStr];
        
        CGContextRef context = UIGraphicsGetCurrentContext();
        
        if (!context) {
            return;
        }
        
        //开启反锯齿和字体平滑
        //        CGContextSetShouldAntialias(context, true);
        //        CGContextSetShouldSmoothFonts(context, true);
        //        CGContextSetAllowsAntialiasing(context, true);
        
        CGContextBeginPath(context);
        CGContextSetShouldAntialias(context, NO);
        CGContextSetStrokeColorWithColor(context, [UIColor blackColor].CGColor);
        
        CGRect frame = self.bounds;
        
        CGFloat x = frame.origin.x + _xLayerBound;
        CGFloat y = frame.origin.y + frame.size.height - _yLayerBound;
        CGFloat width = frame.size.width - _xLayerBound - _xLayerBound;
        CGFloat height = frame.size.height - _yLayerBound - _yLayerBound;
        
        CGFloat interval = width/_capacity;
        
        CGContextSetLineWidth(context, 1.f);
        CGContextMoveToPoint(context, x + interval * (_valueIndex + 1), y - height - 40);
        CGContextAddLineToPoint(context, x + interval * (_valueIndex + 1), y);
        
        CGContextStrokePath(context);
        
        NSUInteger strWidth = [valueStr sizeWithFont:[UIFont systemFontOfSize:11]].width + 15;
        labelnow.frame = CGRectMake(MIN((frame.size.width-strWidth + 5),(x + interval * (_valueIndex + 1) + 5)), M_GT_POP_LABEL_HEIGHT*i, strWidth, M_GT_POP_LABEL_HEIGHT*(i+1));
        
    }
}

- (void)drawStart:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextClearRect( context, self.bounds );
    
    //填充背景颜色
    CGColorRef color = M_GT_CELL_BKGD_COLOR.CGColor;
    CGContextSetFillColorWithColor(context, color);
    CGContextFillRect(context, rect);
    
    //告警从头开始判断
    _upperIndex = 0;
    _lowerIndex = 0;
    
    //开启反锯齿和字体平滑
//    CGContextSetShouldAntialias(context, true);
//    CGContextSetShouldSmoothFonts(context, true);
    CGContextSetAllowsAntialiasing(context, true);
}

- (void)drawRect:(CGRect)rect
{

    [self initXAxis];
    [self initYAxis];
    
    [self drawStart:rect];
    [self drawXAxis];
    [self drawYAxis];
    [self drawChart];
    [self drawValue];
    return;
}

-(void)handleLongPressGesture:(UILongPressGestureRecognizer *)gR
{
    switch (gR.state)
    {
        case UIGestureRecognizerStateBegan:
        {
            _showValue = YES;
            [self handleLongRecognizerStateChanged:gR];
            break;
        }
        
        case UIGestureRecognizerStateChanged:
        {
            [self handleLongRecognizerStateChanged:gR];
            break;
        }
        
        case UIGestureRecognizerStateEnded:
        {
            _showValue = NO;
            [self setNeedsDisplay];
            break;
        }
        default:
            break;
    }
}

-(IBAction)handleLongRecognizerStateChanged:(UILongPressGestureRecognizer*)gR
{
    CGPoint pt = [gR locationInView:self];
    if (self.capacity == 0) {
        return;
    }
    CGRect frame = self.bounds;
    
    CGFloat x = frame.origin.x + _xLayerBound;
    
    CGFloat width = (frame.size.width - _xLayerBound - _xLayerBound)/self.capacity;
    
    NSUInteger index = (pt.x - x)/width;
    
    if ([_xValues count] > 0) {
        if (index < [[_xValues objectAtIndex:0] count]) {
            _valueIndex = index;
            [self setNeedsDisplay];
        }
    }
}

-(IBAction)handlePan:(UIPanGestureRecognizer*)recognizer
{
    CGPoint pt = [recognizer locationInView:self];
    
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        _startPoint = pt;
    }
    if ((recognizer.state == UIGestureRecognizerStateChanged) || (recognizer.state == UIGestureRecognizerStateEnded)) {
        if ([_plots historyCnt] == 0) {
            return;
        }
        
        //正在加载中，不接受处理
        if (_status == GTPlotsStatusLoading) {
            return;
        }
        
        CGRect frame = self.bounds;
        CGFloat width = (frame.size.width - _xLayerBound - _xLayerBound)/self.capacity;
        int N = 3; //加快滑动系数
        int offset = (_startPoint.x - pt.x) * N/width;
        _startPoint = pt;
        
        NSUInteger historyCnt = [_plots historyCnt];
        
        _newStartIndex = _startIndex + offset;
        
        if (_newStartIndex < 0) {
            //超出左边界，调整
            _newStartIndex = 0;
        } else if ((historyCnt > self.capacity) && (_newStartIndex + (int)self.capacity > historyCnt)) {
            //超出右边界，调整
            _newStartIndex = historyCnt - _capacity;
        }
        
        NSInteger endIndex = _newStartIndex + self.capacity;
        if ((endIndex + 2) >= historyCnt) {
            _showMode = GTPlotsShowLatest;
        } else {
            _showMode = GTPlotsShowHistory;
        }
        
        [self setNeedsDisplay];
    }
}


- (void)setLabelNum:(NSUInteger)num
{
    NSInteger colorNum = _lineColors.count;
    
    if (_labels) {
        [_labels removeAllObjects];
    }
    
    for (int i = 0; i < num; i++) {
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, M_GT_POP_LABEL_HEIGHT*i, 40, M_GT_POP_LABEL_HEIGHT*(i+1))];
        [label setTextAlignment:NSTextAlignmentLeft];
        [label setBackgroundColor:[UIColor clearColor]];
        [label setFont:[UIFont systemFontOfSize:11]];
        if (colorNum > i) {
            [label setTextColor:[_lineColors objectAtIndex:i]];
        }
        else {
            [label setTextColor:M_GT_LABEL_VALUE_COLOR];
        }
        [_labels addObject:label];
        [_popView addSubview:label];
        [label release];
    }
}

- (void)updateData:(GTPlotsData *)data
{
    [self setPlots:data];
    _status = GTPlotsStatusNormal;
}

- (void)reloadData
{
    if (_dataSource) {
        if ([_dataSource respondsToSelector:@selector(chartDatas)]) {
            [self setPlots:[_dataSource chartDatas]];
        }
        
        if ([_dataSource respondsToSelector:@selector(upperWarningList)]) {
            [self setUpperWarningList:[_dataSource upperWarningList]];
        }
        
        if ([_dataSource respondsToSelector:@selector(lowerWarningList)]) {
            [self setLowerWarningList:[_dataSource lowerWarningList]];
        }
        
    }
    
    [self setNeedsDisplay];
}

@end
#endif
