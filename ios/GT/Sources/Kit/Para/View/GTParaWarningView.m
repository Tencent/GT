//
//  GTParaWarningView.m
//  GTKit
//
//  Created   on 13-11-20.
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

#import "GTParaWarningView.h"
#import "GTDebugDef.h"
#import "GTLang.h"
#import "GTLangDef.h"

//#define M_GT_WARNING_TITLE_DISABLED @"Warning(Disabled)"
//#define M_GT_WARNING_TITLE_ENABLE   @"Warning(Enable)"
//#define M_GT_CORE_WARNING_CNT @"Warning Count"

@implementation GTParaWarningView

@synthesize delegate = _delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self load];
        [self viewLayout];
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
    self.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    self.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    
    _warningTitle = [[UILabel alloc] init];
    _warningTitle.text = M_GT_LOCALSTRING(M_GT_WARNING_TITLE_DISABLED_KEY);
    _warningTitle.font = [UIFont boldSystemFontOfSize:15.0];
    _warningTitle.textColor = M_GT_WARNING_COLOR;
    _warningTitle.textAlignment = NSTextAlignmentLeft;
    _warningTitle.lineBreakMode = NSLineBreakByWordWrapping;
    _warningTitle.numberOfLines = 0;
    _warningTitle.backgroundColor = [UIColor clearColor];
    [self addSubview:_warningTitle];
    
    _extendBtn = [[UIButton alloc] init];
    [_extendBtn setBackgroundColor:[UIColor clearColor]];
    [_extendBtn addTarget:self action:@selector(extendButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_extendBtn];
    
    _lastingTime = [[UILabel alloc] init];
    _lastingTime.text = M_GT_LOCALSTRING(M_GT_WARNING_TIME_KEY);
    _lastingTime.font = [UIFont systemFontOfSize:12.0];
    _lastingTime.textColor = M_GT_LABEL_COLOR;
    _lastingTime.textAlignment = NSTextAlignmentLeft;
    _lastingTime.lineBreakMode = NSLineBreakByWordWrapping;
    _lastingTime.numberOfLines = 0;
    _lastingTime.backgroundColor = [UIColor clearColor];
    [self addSubview:_lastingTime];
    
    _outOfRange = [[UILabel alloc] init];
    _outOfRange.text = M_GT_LOCALSTRING(M_GT_WARNING_RANGE_KEY);
    _outOfRange.font = [UIFont systemFontOfSize:12.0];
    _outOfRange.textColor = M_GT_LABEL_COLOR;
    _outOfRange.textAlignment = NSTextAlignmentLeft;
    _outOfRange.lineBreakMode = NSLineBreakByWordWrapping;
    _outOfRange.numberOfLines = 0;
    _outOfRange.backgroundColor = [UIColor clearColor];
    [self addSubview:_outOfRange];
    
    _thresholdInterval = [[GTUITextField alloc] init];
    [_thresholdInterval setBorderStyle:UITextBorderStyleNone];
    
    
    _thresholdInterval.textColor = [UIColor whiteColor];
    _thresholdInterval.backgroundColor = [UIColor blackColor];
    _thresholdInterval.font = [UIFont systemFontOfSize:15];
    _thresholdInterval.placeholder = @"";
    _thresholdInterval.clearButtonMode = UITextFieldViewModeAlways;
    _thresholdInterval.textAlignment = NSTextAlignmentLeft;
    _thresholdInterval.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _thresholdInterval.delegate = self;
    _thresholdInterval.autocorrectionType = UITextAutocorrectionTypeNo;
    _thresholdInterval.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _thresholdInterval.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _thresholdInterval.returnKeyType = UIReturnKeyDone;
    _thresholdInterval.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _thresholdInterval.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [_thresholdInterval resignFirstResponder];
    
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 30)];
    _thresholdInterval.leftView = view;
    _thresholdInterval.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [self addSubview:_thresholdInterval];
    
    _leftBound = [[UILabel alloc] init];
    _leftBound.text = @"[";
    _leftBound.font = [UIFont systemFontOfSize:12.0];
    _leftBound.textColor = M_GT_LABEL_COLOR;
    _leftBound.textAlignment = NSTextAlignmentRight;
    _leftBound.lineBreakMode = NSLineBreakByWordWrapping;
    _leftBound.numberOfLines = 0;
    _leftBound.backgroundColor = [UIColor clearColor];
    [self addSubview:_leftBound];
    
    _lowerThresholdValue = [[GTUITextField alloc] init];
    [_lowerThresholdValue setBorderStyle:UITextBorderStyleNone];
    
    
    _lowerThresholdValue.textColor = [UIColor whiteColor];
    _lowerThresholdValue.backgroundColor = [UIColor blackColor];
    _lowerThresholdValue.font = [UIFont systemFontOfSize:15];
    _lowerThresholdValue.placeholder = @"min";
    _lowerThresholdValue.clearButtonMode = UITextFieldViewModeAlways;
    _lowerThresholdValue.textAlignment = NSTextAlignmentLeft;
    _lowerThresholdValue.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _lowerThresholdValue.delegate = self;
    _lowerThresholdValue.autocorrectionType = UITextAutocorrectionTypeNo;
    _lowerThresholdValue.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _lowerThresholdValue.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _lowerThresholdValue.returnKeyType = UIReturnKeyDone;
    _lowerThresholdValue.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _lowerThresholdValue.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [_lowerThresholdValue resignFirstResponder];
    
    view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 30)];
    _lowerThresholdValue.leftView = view;
    _lowerThresholdValue.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [self addSubview:_lowerThresholdValue];
    
    
    _connector = [[UILabel alloc] init];
    _connector.text = @"-";
    _connector.font = [UIFont systemFontOfSize:12.0];
    _connector.textColor = M_GT_LABEL_COLOR;
    _connector.textAlignment = NSTextAlignmentCenter;
    _connector.lineBreakMode = NSLineBreakByWordWrapping;
    _connector.numberOfLines = 0;
    _connector.backgroundColor = [UIColor clearColor];
    [self addSubview:_connector];
    
    _upperThresholdValue = [[GTUITextField alloc] init];
    [_upperThresholdValue setBorderStyle:UITextBorderStyleNone];
    
    
    _upperThresholdValue.textColor = [UIColor whiteColor];
    _upperThresholdValue.backgroundColor = [UIColor blackColor];
    _upperThresholdValue.font = [UIFont systemFontOfSize:15];
    _upperThresholdValue.placeholder = @"max";
    _upperThresholdValue.clearButtonMode = UITextFieldViewModeAlways;
    _upperThresholdValue.textAlignment = NSTextAlignmentLeft;
    _upperThresholdValue.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _upperThresholdValue.delegate = self;
    _upperThresholdValue.autocorrectionType = UITextAutocorrectionTypeNo;
    _upperThresholdValue.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _upperThresholdValue.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    _upperThresholdValue.returnKeyType = UIReturnKeyDone;
    _upperThresholdValue.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    _upperThresholdValue.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    [_upperThresholdValue resignFirstResponder];
    
    view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 30)];
    _upperThresholdValue.leftView = view;
    _upperThresholdValue.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [self addSubview:_upperThresholdValue];
    
    _rightBound = [[UILabel alloc] init];
    _rightBound.text = @"]";
    _rightBound.font = [UIFont systemFontOfSize:12.0];
    _rightBound.textColor = M_GT_LABEL_COLOR;
    _rightBound.textAlignment = NSTextAlignmentLeft;
    _rightBound.lineBreakMode = NSLineBreakByWordWrapping;
    _rightBound.numberOfLines = 0;
    _rightBound.backgroundColor = [UIColor clearColor];
    [self addSubview:_rightBound];
    
    _warningCount = [[UILabel alloc] init];
    _warningCount.font = [UIFont systemFontOfSize:12.0];
    _warningCount.textColor = M_GT_LABEL_COLOR;
    _warningCount.textAlignment = NSTextAlignmentRight;
    _warningCount.backgroundColor = [UIColor clearColor];
    [self addSubview:_warningCount];
    
    _warningCountValue = [[UILabel alloc] init];
    _warningCountValue.font = [UIFont systemFontOfSize:12.0];
    _warningCountValue.textColor = M_GT_LABEL_VALUE_COLOR;
    _warningCountValue.textAlignment = NSTextAlignmentLeft;
    _warningCountValue.backgroundColor = [UIColor clearColor];
    [self addSubview:_warningCountValue];
}

- (void)unload
{
    M_GT_SAFE_FREE( _warningTitle );
    M_GT_SAFE_FREE( _extendBtn );
    M_GT_SAFE_FREE( _lastingTime );
    M_GT_SAFE_FREE( _outOfRange );
    M_GT_SAFE_FREE( _leftBound );
    M_GT_SAFE_FREE( _connector );
    M_GT_SAFE_FREE( _rightBound );
    
    M_GT_SAFE_FREE( _thresholdInterval );
    M_GT_SAFE_FREE( _upperThresholdValue );
    M_GT_SAFE_FREE( _thresholdInterval );
    M_GT_SAFE_FREE( _lowerThresholdValue );
    
    M_GT_SAFE_FREE( _warningCount );
    M_GT_SAFE_FREE( _warningCountValue );
}


- (void)viewLayout
{
    CGRect frame = self.frame;
    
    //iphone和ipad尺寸适配，去除两边的20像素
    CGFloat scale = (M_GT_SCREEN_WIDTH - 40)/(320-40);
    
    frame.origin.x = 5.0f;
    frame.origin.y += 5.0f;
    frame.size.height = 20.0f;
    frame.size.width = 200 * scale;
    [_warningTitle setFrame:frame];
    
    
    frame.origin.x += (320-40)*scale - 44;
    frame.origin.y = 0;
    frame.size.height = 40.0f;
    frame.size.width = 44.0f;
    [_extendBtn setFrame:frame];
    
    frame.origin.x = 5.0f;
    frame.origin.y += frame.size.height;
    frame.size.height = 15.0f;
    frame.size.width = 85 * scale;
    [_lastingTime setFrame:frame];
    
    frame.origin.x += frame.size.width + 10.0f * scale;
    frame.size.width = 180 * scale;
    [_outOfRange setFrame:frame];
    
    frame.origin.y += frame.size.height + 1.0f * scale;
    frame.origin.x = 5.0f;
    frame.size.height = 30.0f;
    frame.size.width = 85 * scale;
    [_thresholdInterval setFrame:frame];
    
    frame.origin.x += frame.size.width + 5.0f * scale;
    frame.size.width = 3 * scale;
    [_leftBound setFrame:frame];
    
    frame.origin.x += frame.size.width + 2.0f * scale;
    frame.size.width = 86 * scale;
    [_lowerThresholdValue setFrame:frame];
    
    frame.origin.x += frame.size.width + 2.0f * scale;
    frame.size.width = 5 * scale;
    [_connector setFrame:frame];
    
    frame.origin.x += frame.size.width + 2.0f * scale;
    frame.size.width = 86 * scale;
    [_upperThresholdValue setFrame:frame];
    
    frame.origin.x += frame.size.width + 2.0f * scale;
    frame.size.width = 3 * scale;
    [_rightBound setFrame:frame];
    
    frame.origin.y += frame.size.height + 1.0f * scale;
    frame.origin.x = 5.0f;
    frame.size.height = 25.0f;
    frame.size.width = (320-40) * scale / 2;
    [_warningCount setFrame:frame];
    
    frame.origin.x += frame.size.width + 2.0f * scale;
    [_warningCountValue setFrame:frame];
}

- (void)updateData
{
    if ([_data switchForWarning]) {
        
        [_warningCount setText:[NSString stringWithFormat:@"%@ : ", M_GT_LOCALSTRING(M_GT_WARNING_COUNT_KEY)]];
        [_warningCountValue setText:[NSString stringWithFormat:@"%lu", (unsigned long)([[[_data lowerWarningList] keys]count] + [[[_data upperWarningList] keys]count])]];
        
    }
}

- (void)bindData:(GTOutputObject *)data
{
    _data = data;
    
    [self updateExtendBtn];
    
    if ([_data thresholdInterval] != 0) {
        [_thresholdInterval setText:[NSString stringWithFormat:@"%.0f", [_data thresholdInterval]]];
    }
    if ([_data lowerThresholdValue] != M_GT_LOWER_WARNING_INVALID) {
        [_lowerThresholdValue setText:[NSString stringWithFormat:@"%.1f", [_data lowerThresholdValue]]];
    }
    if ([_data upperThresholdValue] != M_GT_UPPER_WARNING_INVALID) {
        [_upperThresholdValue setText:[NSString stringWithFormat:@"%.1f", [_data upperThresholdValue]]];
    }
    
}

#pragma mark - UITextFieldDelegate

- (void)dismissKeyboard
{
    [_thresholdInterval resignFirstResponder];
    [_lowerThresholdValue resignFirstResponder];
    [_upperThresholdValue resignFirstResponder];
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self dismissKeyboard];
    
    if ([_thresholdInterval.text length] != 0) {
        [_data setThresholdInterval:[_thresholdInterval.text doubleValue]];
    } else {
        [_data setThresholdInterval:0];
    }
    
    if ([_lowerThresholdValue.text length] != 0) {
        [_data setLowerThresholdValue:[_lowerThresholdValue.text doubleValue]];
    } else {
        [_data setLowerThresholdValue:M_GT_LOWER_WARNING_INVALID];
    }
    
    if ([_upperThresholdValue.text length] != 0) {
        [_data setUpperThresholdValue:[_upperThresholdValue.text doubleValue]];
    } else {
        [_data setUpperThresholdValue:M_GT_UPPER_WARNING_INVALID];
    }
    
    //调整lower和upper
    if (([_data lowerThresholdValue] != M_GT_LOWER_WARNING_INVALID) && ([_data lowerThresholdValue] != M_GT_LOWER_WARNING_INVALID)) {
        if ([_data lowerThresholdValue] > [_data upperThresholdValue]) {
            double value = [_data lowerThresholdValue];
            [_data setLowerThresholdValue:[_data upperThresholdValue]];
            [_data setUpperThresholdValue:value];
            
            [_lowerThresholdValue setText:[NSString stringWithFormat:@"%.1f", [_data lowerThresholdValue]]];
            [_upperThresholdValue setText:[NSString stringWithFormat:@"%.1f", [_data upperThresholdValue]]];
        }
    }
    
    //更新告警列表
    [_data updateWarningList];
    
    return YES;
}

#pragma mark - Button

- (void)updateExtendBtn
{
    if ([_data switchForWarning]) {
        _lastingTime.hidden = NO;
        _outOfRange.hidden = NO;
        _leftBound.hidden = NO;
        _connector.hidden = NO;
        _rightBound.hidden = NO;
        _warningCount.hidden = NO;
        _warningCountValue.hidden = NO;
        _thresholdInterval.hidden = NO;
        _lowerThresholdValue.hidden = NO;
        _upperThresholdValue.hidden = NO;
        
        _warningTitle.text = M_GT_LOCALSTRING(M_GT_WARNING_TITLE_ENABLE_KEY);
        [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_up" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        _lastingTime.hidden = YES;
        _outOfRange.hidden = YES;
        _leftBound.hidden = YES;
        _connector.hidden = YES;
        _rightBound.hidden = YES;
        _warningCount.hidden = YES;
        _warningCountValue.hidden = YES;
        _thresholdInterval.hidden = YES;
        _lowerThresholdValue.hidden = YES;
        _upperThresholdValue.hidden = YES;
        
        _warningTitle.text = M_GT_LOCALSTRING(M_GT_WARNING_TITLE_DISABLED_KEY);
        [_extendBtn setImage:[GTImage imageNamed:@"gt_ac_down" ofType:@"png"] forState:UIControlStateNormal];
    }
}

- (void)extendButtonClicked:(id)sender
{
    BOOL warning = [_data switchForWarning];
    [_data setSwitchForWarning:!warning];
    [self updateExtendBtn];
    
    if (_delegate && [_delegate respondsToSelector:@selector(didClickExtend)])
    {
        [_delegate didClickExtend];
    }
}

@end
#endif
