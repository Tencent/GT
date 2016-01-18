//
//  GTPlotsView.h
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

#import <UIKit/UIKit.h>
#import "GTList.h"

typedef enum {
    GTPlotsShowLatest = 0,  //实时刷新最新值
	GTPlotsShowHistory,     //展示历史值
} GTPlotsShowMode;

typedef enum {
    GTPlotsStatusNormal = 0,      //正常显示
	GTPlotsStatusLoading,         //加载数据中
} GTPlotsStatus;

@interface GTPlotsData : NSObject
{
    NSMutableArray  *_dates;        //对应的时间列表
    NSMutableArray  *_curves;       //对应的曲线数据列表，为支持多曲线，故这里plots里每个对象都是NSArray类型
    NSUInteger       _historyIndex; //_plots数据里第一条数据对应整个历史记录的坐标
    NSUInteger       _historyCnt;   //总的历史记录数
}

@property (nonatomic, retain) NSMutableArray    *dates;
@property (nonatomic, retain) NSMutableArray    *curves;
@property (nonatomic, assign) NSUInteger         historyIndex;
@property (nonatomic, assign) NSUInteger         historyCnt;


@end

@protocol GTPlotsViewDataSource <NSObject>

@required
- (GTPlotsData *)chartDatas;

@optional
- (void)loadHistroyDatas:(NSInteger)startIndex;
- (GTList *)upperWarningList;
- (GTList *)lowerWarningList;
- (NSArray*)popValueStrs:(NSInteger) index;

@end


@interface GTPlotsView : UIView <UIGestureRecognizerDelegate>
{
    NSArray         *_lineColors;       //曲线的颜色
    UIColor         *_warningColor;     //告警颜色
    CGFloat          _lineWidth;        //曲线宽度
    CGFloat          _xLayerBound;      //视图X方向到纵坐标预留的空间，用于写纵坐标数值
	CGFloat          _yLayerBound;      //视图Y方向到纵坐标预留的空间，用于写横坐标数值
	BOOL             _autoCalBound;     //是否需要自动调整纵坐标的高度,默认为YES
	CGFloat          _lowerBound;       //纵坐标对应的下限值，若_autoCalBound为NO，则使用用户输入值
	CGFloat          _upperBound;       //纵坐标对应的上限值，若_autoCalBound为NO，则使用用户输入值
    BOOL             _showAvg;          //NO:不画平均线 YES:画平均线
    NSMutableArray  *_avgValues;        //计算前曲线可视区域的平均值
	NSInteger        _status;           //当前显示状态，枚举定义类型：GTPlotsStatus
    NSUInteger       _capacity;         //曲线可视的记录个数
    NSInteger        _newStartIndex;    //曲线展示对应的需要偏移开始坐标，包括_startIndex对应的数据
    NSInteger        _startIndex;       //曲线展示对应的开始坐标，包括_startIndex对应的数据
    NSMutableArray  *_xValues;          //曲线展示对应的X轴上的值，根据开始和结束坐标计算得出
    NSMutableArray  *_dateValues;       //曲线展示对应的X轴上的时间值，根据开始和结束坐标计算得出
    NSMutableArray  *_yValues;          //曲线展示对应的Y轴上的值，根据开始和结束坐标计算得出
    NSInteger        _showMode;         //曲线展示模式 枚举定义类型：GTPlotsShowMode
    BOOL             _showValue;        //用户长按曲线时展示点击区域对应的数值
    NSUInteger       _valueIndex;       //用户长按对应_xValues数组的下标，便于获取数据并展示
    CGPoint          _startPoint;       //记录用户手势的开始坐标，用于计算滑动偏移
	GTPlotsData     *_plots;            //曲线展示对应的数据区，通过_dataSource调用获取
    GTList          *_lowerWarningList; //下限告警对应的时间段列表
    NSUInteger       _lowerIndex;       //时间列表对应时顺序的，这里记录对应已判断过的下标，用于节省重复的判断
    GTList          *_upperWarningList; //上限告警对应的时间段列表
    NSUInteger       _upperIndex;       //时间列表对应时顺序的，这里记录对应已判断过的下标，用于节省重复的判断
    
    NSString        *_xDesc;            //X坐标描述，默认为Date
    NSString        *_yDesc;            //Y坐标描述，默认为空
    
    id<GTPlotsViewDataSource> _dataSource;
}
//横竖轴显示标签
@property (nonatomic, strong) NSMutableArray *xValues;
@property (nonatomic, strong) NSMutableArray *yValues;

@property (nonatomic, assign) id<GTPlotsViewDataSource> dataSource;

@property (nonatomic, retain) NSArray *		lineColors;
@property (nonatomic, retain) UIColor *		warningColor;
@property (nonatomic, assign) CGFloat 		xLayerBound;
@property (nonatomic, assign) CGFloat 		yLayerBound;
@property (nonatomic, assign) CGFloat		lineWidth;
@property (nonatomic, assign) CGFloat		lowerBound;
@property (nonatomic, assign) CGFloat		upperBound;
@property (nonatomic, assign) BOOL          autoCalBound;
@property (nonatomic, assign) BOOL          showAvg;
@property (nonatomic, assign) NSInteger     status;
@property (nonatomic, assign) NSUInteger	capacity;
@property (nonatomic, assign) NSInteger     startIndex;
@property (nonatomic, assign) NSInteger     showMode;
@property (nonatomic, retain) GTPlotsData 	*plots;
@property (nonatomic, retain) GTList        *lowerWarningList;
@property (nonatomic, retain) GTList 		*upperWarningList;
@property (nonatomic, retain) NSString      *xDesc;
@property (nonatomic, retain) NSString      *yDesc;

- (void)reloadData;

@end


#endif
