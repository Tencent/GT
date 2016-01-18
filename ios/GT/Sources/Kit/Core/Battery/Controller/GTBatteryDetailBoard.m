//
//  GTBatteryDetailBoard.m
//  GTKit
//
//  Created   on 13-11-7.
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

#import "GTBatteryDetailBoard.h"
#import "GTBattery.h"
#import "GTConfig.h"

@interface GTBatteryDetailBoard ()

@end

@implementation GTBatteryDetailBoard

- (void)initHistoryUI
{
    [super initHistoryUI];
    
    [_plotView setYDesc:@"mAh"];
    
    _infoView = [[UIView alloc] init];
    _infoView.backgroundColor = M_GT_CELL_BKGD_COLOR;
    _infoView.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _infoView.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [_backgroundView addSubview:_infoView];
    
    _extendBtn = [[UIButton alloc] init];
    [_extendBtn setBackgroundColor:[UIColor clearColor]];
    [_extendBtn addTarget:self action:@selector(extendButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_up" ofType:@"png"] forState:UIControlStateNormal];
    [_infoView addSubview:_extendBtn];
    
    _infoTitle = [[UILabel alloc] init];
    _infoTitle.font = [UIFont systemFontOfSize:15.0];
    _infoTitle.textColor = M_GT_COLOR_WITH_HEX(0x598E9D);
    _infoTitle.textAlignment = NSTextAlignmentLeft;
    _infoTitle.backgroundColor = [UIColor clearColor];
    _infoTitle.lineBreakMode = NSLineBreakByCharWrapping;
    _infoTitle.numberOfLines = 0;
    [_infoTitle setText:@"BatteryInfo"];
    [_infoView addSubview:_infoTitle];
    
    _temperature = [[UILabel alloc] init];
    _temperature.font = [UIFont systemFontOfSize:12.0];
    _temperature.textColor = M_GT_LABEL_COLOR;
    _temperature.textAlignment = NSTextAlignmentRight;
    _temperature.backgroundColor = [UIColor clearColor];
    _temperature.lineBreakMode = NSLineBreakByCharWrapping;
    _temperature.numberOfLines = 0;
    [_temperature setText:@"Temperature : "];
    [_infoView addSubview:_temperature];
    
    _temperatureValue = [[UILabel alloc] init];
    _temperatureValue.font = [UIFont systemFontOfSize:12.0];
    _temperatureValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _temperatureValue.textAlignment = NSTextAlignmentLeft;
    _temperatureValue.backgroundColor = [UIColor clearColor];
    _temperatureValue.lineBreakMode = NSLineBreakByCharWrapping;
    _temperatureValue.numberOfLines = 0;
    [_infoView addSubview:_temperatureValue];
    
    _maxCapacity = [[UILabel alloc] init];
    _maxCapacity.font = [UIFont systemFontOfSize:12.0];
    _maxCapacity.textColor = M_GT_LABEL_COLOR;
    _maxCapacity.textAlignment = NSTextAlignmentRight;
    _maxCapacity.backgroundColor = [UIColor clearColor];
    _maxCapacity.lineBreakMode = NSLineBreakByCharWrapping;
    _maxCapacity.numberOfLines = 0;
    [_maxCapacity setText:@"MaxCapacity : "];
    [_infoView addSubview:_maxCapacity];
    
    _maxCapacityValue = [[UILabel alloc] init];
    _maxCapacityValue.font = [UIFont systemFontOfSize:12.0];
    _maxCapacityValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _maxCapacityValue.textAlignment = NSTextAlignmentLeft;
    _maxCapacityValue.backgroundColor = [UIColor clearColor];
    _maxCapacityValue.lineBreakMode = NSLineBreakByCharWrapping;
    _maxCapacityValue.numberOfLines = 0;
    [_infoView addSubview:_maxCapacityValue];
    
    _voltage = [[UILabel alloc] init];
    _voltage.font = [UIFont systemFontOfSize:12.0];
    _voltage.textColor = M_GT_LABEL_COLOR;
    _voltage.textAlignment = NSTextAlignmentRight;
    _voltage.backgroundColor = [UIColor clearColor];
    _voltage.lineBreakMode = NSLineBreakByCharWrapping;
    _voltage.numberOfLines = 0;
    [_voltage setText:@"Voltage : "];
    [_infoView addSubview:_voltage];
    
    _voltageValue = [[UILabel alloc] init];
    _voltageValue.font = [UIFont systemFontOfSize:12.0];
    _voltageValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _voltageValue.textAlignment = NSTextAlignmentLeft;
    _voltageValue.backgroundColor = [UIColor clearColor];
    _voltageValue.lineBreakMode = NSLineBreakByCharWrapping;
    _voltageValue.numberOfLines = 0;
    [_infoView addSubview:_voltageValue];
    
    _bootVoltage = [[UILabel alloc] init];
    _bootVoltage.font = [UIFont systemFontOfSize:12.0];
    _bootVoltage.textColor = M_GT_LABEL_COLOR;
    _bootVoltage.textAlignment = NSTextAlignmentRight;
    _bootVoltage.backgroundColor = [UIColor clearColor];
    _bootVoltage.lineBreakMode = NSLineBreakByCharWrapping;
    _bootVoltage.numberOfLines = 0;
    [_bootVoltage setText:@"BootVoltage : "];
    [_infoView addSubview:_bootVoltage];
    
    _bootVoltageValue = [[UILabel alloc] init];
    _bootVoltageValue.font = [UIFont systemFontOfSize:12.0];
    _bootVoltageValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _bootVoltageValue.textAlignment = NSTextAlignmentLeft;
    _bootVoltageValue.backgroundColor = [UIColor clearColor];
    _bootVoltageValue.lineBreakMode = NSLineBreakByCharWrapping;
    _bootVoltageValue.numberOfLines = 0;
    [_infoView addSubview:_bootVoltageValue];
    
    _date = [[UILabel alloc] init];
    _date.font = [UIFont systemFontOfSize:12.0];
    _date.textColor = M_GT_LABEL_COLOR;
    _date.textAlignment = NSTextAlignmentRight;
    _date.backgroundColor = [UIColor clearColor];
    _date.lineBreakMode = NSLineBreakByCharWrapping;
    _date.numberOfLines = 0;
    [_date setText:@"time : "];
    [_summaryView addSubview:_date];
    
    _dateValue = [[UILabel alloc] init];
    _dateValue.font = [UIFont systemFontOfSize:12.0];
    _dateValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _dateValue.textAlignment = NSTextAlignmentLeft;
    _dateValue.backgroundColor = [UIColor clearColor];
    _dateValue.lineBreakMode = NSLineBreakByCharWrapping;
    _dateValue.numberOfLines = 0;
    [_summaryView addSubview:_dateValue];
    
    _designCapacity = [[UILabel alloc] init];
    _designCapacity.font = [UIFont systemFontOfSize:12.0];
    _designCapacity.textColor = M_GT_LABEL_COLOR;
    _designCapacity.textAlignment = NSTextAlignmentRight;
    _designCapacity.backgroundColor = [UIColor clearColor];
    _designCapacity.lineBreakMode = NSLineBreakByCharWrapping;
    _designCapacity.numberOfLines = 0;
    [_designCapacity setText:@"DesignCapacity : "];
    [_infoView addSubview:_designCapacity];
    
    _designCapacityValue = [[UILabel alloc] init];
    _designCapacityValue.font = [UIFont systemFontOfSize:12.0];
    _designCapacityValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _designCapacityValue.textAlignment = NSTextAlignmentLeft;
    _designCapacityValue.backgroundColor = [UIColor clearColor];
    _designCapacityValue.lineBreakMode = NSLineBreakByCharWrapping;
    _designCapacityValue.numberOfLines = 0;
    [_infoView addSubview:_designCapacityValue];
    
    _currentCapacity = [[UILabel alloc] init];
    _currentCapacity.font = [UIFont systemFontOfSize:12.0];
    _currentCapacity.textColor = M_GT_LABEL_COLOR;
    _currentCapacity.textAlignment = NSTextAlignmentRight;
    _currentCapacity.backgroundColor = [UIColor clearColor];
    _currentCapacity.lineBreakMode = NSLineBreakByCharWrapping;
    _currentCapacity.numberOfLines = 0;
    [_currentCapacity setText:@"CurrentCapacity : "];
    [_infoView addSubview:_currentCapacity];
    
    _currentCapacityValue = [[UILabel alloc] init];
    _currentCapacityValue.font = [UIFont systemFontOfSize:12.0];
    _currentCapacityValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _currentCapacityValue.textAlignment = NSTextAlignmentLeft;
    _currentCapacityValue.backgroundColor = [UIColor clearColor];
    _currentCapacityValue.lineBreakMode = NSLineBreakByCharWrapping;
    _currentCapacityValue.numberOfLines = 0;
    [_infoView addSubview:_currentCapacityValue];
    
    _level = [[UILabel alloc] init];
    _level.font = [UIFont systemFontOfSize:12.0];
    _level.textColor = M_GT_LABEL_COLOR;
    _level.textAlignment = NSTextAlignmentRight;
    _level.backgroundColor = [UIColor clearColor];
    _level.lineBreakMode = NSLineBreakByCharWrapping;
    _level.numberOfLines = 0;
    [_level setText:@"BatteryLevel : "];
    [_infoView addSubview:_level];
    
    _levelValue = [[UILabel alloc] init];
    _levelValue.font = [UIFont systemFontOfSize:12.0];
    _levelValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _levelValue.textAlignment = NSTextAlignmentLeft;
    _levelValue.backgroundColor = [UIColor clearColor];
    _levelValue.lineBreakMode = NSLineBreakByCharWrapping;
    _levelValue.numberOfLines = 0;
    [_infoView addSubview:_levelValue];
    
    _cycleCount = [[UILabel alloc] init];
    _cycleCount.font = [UIFont systemFontOfSize:12.0];
    _cycleCount.textColor = M_GT_LABEL_COLOR;
    _cycleCount.textAlignment = NSTextAlignmentRight;
    _cycleCount.backgroundColor = [UIColor clearColor];
    _cycleCount.lineBreakMode = NSLineBreakByCharWrapping;
    _cycleCount.numberOfLines = 0;
    [_cycleCount setText:@"CycleCount : "];
    [_infoView addSubview:_cycleCount];
    
    _cycleCountValue = [[UILabel alloc] init];
    _cycleCountValue.font = [UIFont systemFontOfSize:12.0];
    _cycleCountValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _cycleCountValue.textAlignment = NSTextAlignmentLeft;
    _cycleCountValue.backgroundColor = [UIColor clearColor];
    _cycleCountValue.lineBreakMode = NSLineBreakByCharWrapping;
    _cycleCountValue.numberOfLines = 0;
    [_infoView addSubview:_cycleCountValue];

    _resetBtn = [[UIButton alloc] initWithFrame:CGRectMake(M_GT_SCREEN_WIDTH - 40, 5, 60, 30)];
    [_resetBtn addTarget:self action:@selector(onReset:) forControlEvents:UIControlEventTouchUpInside];
    [_resetBtn addTarget:self action:@selector(onSetColorIn:) forControlEvents:UIControlEventTouchDown];
    [_resetBtn addTarget:self action:@selector(onSetColorOut:) forControlEvents:UIControlEventTouchDragExit];
    [_resetBtn setTitle:@"reset" forState:UIControlStateNormal];
    [_resetBtn.titleLabel setFont:[UIFont systemFontOfSize:12]];
    [_resetBtn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    
    _resetBtn.layer.borderColor = [UIColor blackColor].CGColor;
    _resetBtn.layer.borderWidth = 1.0f;
    [_summaryView addSubview:_resetBtn];
    
    _switchBtn = [[UIButton alloc] initWithFrame:CGRectMake(M_GT_SCREEN_WIDTH - 40, 5, 60, 30)];
    [_switchBtn addTarget:self action:@selector(onSwitch:) forControlEvents:UIControlEventTouchUpInside];
    [_switchBtn addTarget:self action:@selector(onSetColorIn:) forControlEvents:UIControlEventTouchDown];
    [_switchBtn addTarget:self action:@selector(onSetSwitchColorOut:) forControlEvents:UIControlEventTouchDragExit];
    [_switchBtn setTitle:@"→ mA" forState:UIControlStateNormal];
    [_switchBtn.titleLabel setFont:[UIFont systemFontOfSize:12]];
    [_switchBtn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    
    _switchBtn.layer.borderColor = [UIColor blackColor].CGColor;
    _switchBtn.layer.borderWidth = 1.0f;
    [_backgroundView addSubview:_switchBtn];
    
    _isCurrent = false;
    _isShowInfo = true;
}

- (void)infoLayout
{
    int width = _infoView.frame.size.width;
    width = width/4;
    
    int height = 20, y = 5, x = 0;
    
    _infoTitle.frame = CGRectMake( 5, y, 2*width, height );
    _extendBtn.frame = CGRectMake( _infoView.frame.size.width-60, 0, 40, 44 );
    if (_isShowInfo) {
        y += 30;
        _temperature.frame = CGRectMake( x, y, width+20, height );
        _temperatureValue.frame = CGRectMake( x+width+20, y, width-20, height );
        _level.frame = CGRectMake( x+2*width, y, width+10, height );
        _levelValue.frame = CGRectMake( x+3*width+10, y, width-10, height );
        
        _currentCapacity.frame = CGRectMake( x, y+height, width+20, height );
        _currentCapacityValue.frame = CGRectMake( x+width+20, y+height, width-20, height );
        _maxCapacity.frame = CGRectMake( x+2*width, y+height, width+10, height );
        _maxCapacityValue.frame = CGRectMake( x+width*3+10, y+height, width-10, height );
        
        _designCapacity.frame = CGRectMake( x, y+2*height, width+20, height );
        _designCapacityValue.frame = CGRectMake( x+width+20, y+2*height, width-20, height );
        _cycleCount.frame = CGRectMake( x+2*width, y+2*height, width+10, height );
        _cycleCountValue.frame = CGRectMake( x+width*3+10, y+2*height, width-10, height );
        
        _voltage.frame = CGRectMake( x, y+3*height, width+20, height );
        _voltageValue.frame = CGRectMake( x+width+20, y+3*height, width-20, height );
        _bootVoltage.frame = CGRectMake( x+2*width, y+3*height, width+10, height );
        _bootVoltageValue.frame = CGRectMake( x+width*3+10, y+3*height, width-10, height );
    }

}

- (void)viewLayout
{
    [super viewLayout];
    CGFloat width = [self widthForOutDetail];
    CGRect frame;
    
    frame.origin.x = 0;
    frame.origin.y = 5;
    frame.size.height = 50;
    frame.size.width = width;
    
    //设置summary页的frame
    [_summaryView setFrame:frame];
    
    CGFloat x = frame.origin.x;
    CGFloat y = frame.origin.y;
    
    CGFloat height = 40;
    if (_isShowInfo) {
        height = 120;
    }
    
    _infoView.frame = CGRectMake( x, y, width, height );
    [self infoLayout];
    
    x = frame.origin.x;
    width = frame.size.width;
    y = _infoView.frame.origin.y + _infoView.frame.size.height+5;
    height = 50;
    _summaryView.frame = CGRectMake( x, y, width, height );
    
    width = (width-80)/4;
    height = 25;
    x = 0;
    y = 0;
    _count.frame = CGRectMake( x, y, width, height );
    _countValue.frame = CGRectMake( x + width, y, width, height );
    _date.frame = CGRectMake( x+2*width, y, width-20, height );
    _dateValue.frame = CGRectMake( x + 3*width-20, y, width+30, height );
    x = _summaryView.frame.origin.x+_summaryView.frame.size.width - 50;
    _resetBtn.frame = CGRectMake( x, y+5, 50, 30 );
    
    x = 5;
    y = y+25;
    width = frame.size.width-5;
    height = 25;
    _content.frame = CGRectMake( x, y, width, height );
    
    y = _summaryView.frame.origin.y + _summaryView.frame.size.height+5;
    x = frame.origin.x;
    width = frame.size.width;
    
    _plotView.frame = CGRectMake( x, y, width, _backgroundView.frame.size.height - y - 10 );
    x = _plotView.frame.origin.x + _plotView.frame.size.width - 50;
    y = _plotView.frame.origin.y + _plotView.frame.size.height/2 - 15;
    _switchBtn.frame = CGRectMake( x, y, 50, 30 );
}


- (void)unload
{
    M_GT_SAFE_FREE( _infoTitle );
    M_GT_SAFE_FREE( _temperature );
    M_GT_SAFE_FREE( _temperatureValue );
    M_GT_SAFE_FREE( _level );
    M_GT_SAFE_FREE( _levelValue );
    M_GT_SAFE_FREE( _currentCapacity );
    M_GT_SAFE_FREE( _currentCapacityValue );
    M_GT_SAFE_FREE( _designCapacity );
    M_GT_SAFE_FREE( _designCapacityValue );
    M_GT_SAFE_FREE( _maxCapacity );
    M_GT_SAFE_FREE( _maxCapacityValue );
    M_GT_SAFE_FREE( _voltage );
    M_GT_SAFE_FREE( _voltageValue );
    M_GT_SAFE_FREE( _bootVoltage );
    M_GT_SAFE_FREE( _bootVoltageValue );
    M_GT_SAFE_FREE( _cycleCount );
    M_GT_SAFE_FREE( _cycleCountValue );
    M_GT_SAFE_FREE( _date );
    M_GT_SAFE_FREE( _dateValue );
    
    M_GT_SAFE_FREE( _infoView );
    
    M_GT_SAFE_FREE( _resetBtn );
    M_GT_SAFE_FREE( _switchBtn );
    M_GT_SAFE_FREE( _extendBtn );
    [super unload];
}


- (void)updateData
{
    [super updateData];

    NSString *str = nil;
    GTBattery *battery = [GTBattery sharedInstance];
    
    str = [NSString stringWithFormat:@"%.2f℃", [[battery info] temperature]/100.0];
    [_temperatureValue setText:str];
    
    str = [NSString stringWithFormat:@"%ldmAh", (long)[[battery info] maxCapacity]];
    [_maxCapacityValue setText:str];
    
    str = [NSString stringWithFormat:@"%ldmV", (long)[[battery info] voltage]];
    [_voltageValue setText:str];
    
    str = [NSString stringWithFormat:@"%ldmV", (long)[[battery info] bootVoltage]];
    [_bootVoltageValue setText:str];
    
    str = [NSString stringWithFormat:@"%ld", (long)[[battery info] cycleCount]];
    [_cycleCountValue setText:str];
    
    str = [NSString stringWithFormat:@"%ld%%", (long)[[battery info] batteryLevel]];
    [_levelValue setText:str];
    
    str = [NSString stringWithFormat:@"%ldmAh", (long)[[battery info] currentCapacity]];
    [_currentCapacityValue setText:str];
    
    str = [NSString stringWithFormat:@"%ldmAh", (long)[[battery info] designCapacity]];
    [_designCapacityValue setText:str];

    [_dateValue setText:[NSString stringWithTimeEx:[battery lastCapacityDate]]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - GTMultiPlotsViewDataSource
- (NSArray*)multiChartDatas
{
    NSArray * result = nil;
    NSMutableArray *histroy = [[[_data history] copy] autorelease];
    
    NSUInteger count = [histroy count];
    if (count == 0) {
        return nil;
    }
    
    NSMutableArray * batteryLevels = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTBatteryHistory *obj = nil;
    
    if (_isCurrent) {
        for (int i = 0; i < count; i++) {
            obj = [histroy objectAtIndex:i];
            [batteryLevels addObject:[NSNumber numberWithFloat:[obj current]]];
        }
    }
    else {
        for (int i = 0; i < count; i++) {
            obj = [histroy objectAtIndex:i];
            [batteryLevels addObject:[NSNumber numberWithFloat:[obj currentCapacity]]];
        }
        
    }
    
    
    result = [[NSArray alloc] initWithObjects:batteryLevels, nil];
    [batteryLevels release];
    
    return [result autorelease];
}

- (NSArray*)multiChartDates
{
    NSMutableArray *histroy = [[[_data history] copy] autorelease];
    
    NSUInteger count = [histroy count];
    if (count == 0) {
        return nil;
    }
    
    NSMutableArray * dates = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTBatteryHistory *obj = nil;
    
    for (int i = 0; i < count; i++) {
        obj = [histroy objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
    }
    
    
    return [dates autorelease];
}

#pragma mark - 数据缓冲自定义曲线展示扩展

- (void)plotDataInitWithMemory:(NSArray *)array
{
    NSUInteger count = [array count];
    
    NSMutableArray *dates = [[NSMutableArray alloc] initWithCapacity:count];
    NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:count];
    
    GTBatteryHistory *obj = nil;
    
    if (_isCurrent) {
        for (int i = 0; i < count; i++) {
            obj = [array objectAtIndex:i];
            [dates addObject:[NSNumber numberWithDouble:[obj date]]];
            [values addObject:[NSNumber numberWithFloat:[obj current]]];
        }
    } else {
        for (int i = 0; i < count; i++) {
            obj = [array objectAtIndex:i];
            [dates addObject:[NSNumber numberWithDouble:[obj date]]];
            [values addObject:[NSNumber numberWithFloat:[obj currentCapacity]]];
        }
    }
    
    [_plotDataBuf setDates:dates];
    [dates release];
    
    //这里支持多曲线，所以输入对应为二维数组
    [_plotDataBuf setCurves:[NSMutableArray arrayWithObjects:values, nil]];
    [values release];
}

- (void)plotDataInitWithDisk:(NSArray *)array
{
    NSUInteger count = [array count];
    
    NSMutableArray *dates = [[NSMutableArray alloc] initWithCapacity:count];
    NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:count];
    
    if (_isCurrent) {
        //更新数据
        for (int i = 0; i < count; i++) {
            NSString *item = [array objectAtIndex:i];
            NSArray *itemArray = [item componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@","]];
            if ([itemArray count] != 3) {
                continue;
            }
            
            //日期格式转换为秒数
            [dates addObject:[NSNumber numberWithDouble:[(NSString *)[itemArray objectAtIndex:0] timeValue]]];
            [values addObject:[NSNumber numberWithDouble:[[itemArray objectAtIndex:1] doubleValue]]];
        }
    } else {
        for (int i = 0; i < count; i++) {
            NSString *item = [array objectAtIndex:i];
            NSArray *itemArray = [item componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@","]];
            if ([itemArray count] != 3) {
                continue;
            }
            
            //日期格式转换为秒数
            [dates addObject:[NSNumber numberWithDouble:[(NSString *)[itemArray objectAtIndex:0] timeValue]]];
            [values addObject:[NSNumber numberWithDouble:[[itemArray objectAtIndex:2] doubleValue]]];
        }
    }
    
    [_plotDataBuf setDates:dates];
    [dates release];
    
    //这里支持多曲线，所以输入对应为二维数组
    [_plotDataBuf setCurves:[NSMutableArray arrayWithObjects:values, nil]];
    [values release];
}


- (void)plotDataUpdateWithMemory:(NSArray *)array fromIndex:(NSInteger)index
{
    if ([[_plotDataBuf curves] count] == 0) {
        return;
    }
    
    NSMutableArray *dates = [_plotDataBuf dates];
    NSMutableArray *values = [[_plotDataBuf curves] objectAtIndex:0];
    
    GTBatteryHistory *obj = nil;
    
    for (NSUInteger i = index; i < [array count]; i++) {
        obj = [array objectAtIndex:i];
        [dates addObject:[NSNumber numberWithDouble:[obj date]]];
        if (_isCurrent) {
            [values addObject:[NSNumber numberWithFloat:[obj current]]];
        } else {
            [values addObject:[NSNumber numberWithFloat:[obj currentCapacity]]];
        }
    }
    
}

#pragma mark - GTPlotsViewDataSource

- (NSArray *)popValueStrs1:(NSInteger)index
{
    NSMutableArray *histroy = [_data history];
    NSUInteger count = [histroy count];
    
    if (index >= count) {
        return nil;
    }
    
    GTBatteryHistory *obj = [histroy objectAtIndex:index];
    
    NSMutableArray *array = [[[NSMutableArray alloc] initWithCapacity:1] autorelease];
    NSString *str = nil;
    if (_isCurrent) {
        str = [NSString stringWithFormat:@"%@,%.1fmA", [NSString stringWithTimeEx:[obj date]],[obj current]];
    }
    else {
        str = [NSString stringWithFormat:@"%@,%ldmAh", [NSString stringWithTimeEx:[obj date]], (long)[obj currentCapacity]];
    }
    [array addObject:str];
    
    return array;
    
}

#pragma mark - Button

- (void)onReset:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    
    [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    //清除累计流量
    [[GTBattery sharedInstance] resetData];
    [self updateData];
}

- (void)onSwitch:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    
    if(_isCurrent){
        _isCurrent = false;
        [_plotView setYDesc:@"mAh"];
        [_switchBtn setTitle:@"→ mA" forState:UIControlStateNormal];
        [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    }
    else {
        _isCurrent = true;
        [_plotView setYDesc:@"mA"];
        [_switchBtn setTitle:@"→ mAh" forState:UIControlStateNormal];
        [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x9F729D)];
    }
    
    [self resetData];
    
    [self updateData];
}


- (void)onSetColorIn:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    [btn setBackgroundColor:[UIColor grayColor]];
}

- (void)onSetColorOut:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
}

- (void)onSetSwitchColorOut:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    if(_isCurrent){
        [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x9F729D)];
    }
    else {
        [btn setBackgroundColor:M_GT_COLOR_WITH_HEX(0x598E9D)];
    }

}

#pragma mark - Button

- (void)updateExtendBtn
{
    if (_isShowInfo) {
        _temperature.hidden = NO;
        _temperatureValue.hidden = NO;
        _level.hidden = NO;
        _levelValue.hidden = NO;
        _currentCapacity.hidden = NO;
        _currentCapacityValue.hidden = NO;
        _maxCapacity.hidden = NO;
        _maxCapacityValue.hidden = NO;
        _designCapacity.hidden = NO;
        _designCapacityValue.hidden = NO;
        _voltage.hidden = NO;
        _voltageValue.hidden = NO;
        _bootVoltage.hidden = NO;
        _bootVoltageValue.hidden = NO;
        _cycleCount.hidden = NO;
        _cycleCountValue.hidden = NO;
        _infoTitle.text = @"BatteryInfo";
        [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_up" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        _temperature.hidden = YES;
        _temperatureValue.hidden = YES;
        _level.hidden = YES;
        _levelValue.hidden = YES;
        _currentCapacity.hidden = YES;
        _currentCapacityValue.hidden = YES;
        _maxCapacity.hidden = YES;
        _maxCapacityValue.hidden = YES;
        _designCapacity.hidden = YES;
        _designCapacityValue.hidden = YES;
        _voltage.hidden = YES;
        _voltageValue.hidden = YES;
        _bootVoltage.hidden = YES;
        _bootVoltageValue.hidden = YES;
        _cycleCount.hidden = YES;
        _cycleCountValue.hidden = YES;
        _infoTitle.text = @"BatteryInfo(hide)";
        [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_down" ofType:@"png"] forState:UIControlStateNormal];
    }
}

- (void)extendButtonClicked:(id)sender
{
    if (_isShowInfo) {
        _isShowInfo = false;
    }
    else
    {
        _isShowInfo = true;
    }
    [self updateExtendBtn];
    [self viewLayout];
     
     //收缩后坐标字体显示会有变形，这里重新绘图
     [_plotView setNeedsDisplay];
}




@end
