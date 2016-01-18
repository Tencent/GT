//
//  GTMultiDetailBoard.m
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

#import "GTMultiDetailBoard.h"

#define M_GT_MULTI_COUNT @"Counts"

@interface GTMultiDetailBoard ()

@end

@implementation GTMultiDetailBoard

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)unload
{
    M_GT_SAFE_FREE( _content);
    [super unload];
}

- (void)initHistoryUI
{
    [super initHistoryUI];
    
    _content = [[UILabel alloc] init];
    _content.font = [UIFont systemFontOfSize:12.0];
    _content.textColor = M_GT_LABEL_VALUE_COLOR;
    _content.textAlignment = NSTextAlignmentLeft;
    _content.backgroundColor = [UIColor clearColor];
    _content.lineBreakMode = NSLineBreakByCharWrapping;
    _content.numberOfLines = 0;
    [_summaryView addSubview:_content];
    
    [_plotView setShowAvg:NO];
}

#pragma mark -

- (void)viewLayout
{
    [super viewLayout];
    CGFloat width = [self widthForOutDetail];
    CGRect frame;
    
    frame.origin.x = 0;
    frame.origin.y = 5;
    frame.size.height = 60;
    frame.size.width = width;
    
    //设置summary页的frame
    [_summaryView setFrame:frame];
    
    frame.origin.y += frame.size.height + 5;
    frame.size.height = _backgroundView.frame.size.height - frame.origin.y - 10;
    
    //设置曲线页的frame
    [_plotView setFrame:frame];
    
    width = width/4;
    CGFloat x = frame.origin.x;
    CGFloat y = 5;
    CGFloat height = 20;
    
    _count.frame = CGRectMake( x, y, width, height );
    _countValue.frame = CGRectMake( x + width, y, width, height );
    
    x = 5;
    y += 5;
    height = 50;
    width = [self widthForOutDetail] - 10;
    _content.frame = CGRectMake( x, y, width, height );
}

- (void)updateData
{
    [super updateData];
    
    NSString *str = nil;
    
    [_count setText:[NSString stringWithFormat:@"%@ : ", M_GT_MULTI_COUNT]];
    str = [NSString stringWithFormat:@"%lu", (unsigned long)[_data historyCnt] ];
    [_countValue setText:str];
    
    GTOutputValue *value = [[_data dataInfo] value];
    [_content setText:[value content]];
    
}


@end
