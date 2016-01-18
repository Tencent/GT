//
//  GTLogCell.m
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

#import "GTLogCell.h"
#import "GTLog.h"
#import "GTLogConfig.h"
#import "GTConfig.h"

@implementation GTLogCell

+ (float)cellHeight:(NSObject *)data bound:(CGSize)bound
{
    GTLogRecord *obj = (GTLogRecord *)data;
    
    CGSize constrainedToSize = CGSizeMake(bound.width, 900);
    
    NSString *text = [NSString stringWithFormat:@" %@ %@|%@|%@ %@", [NSString stringWithTimeEx:[obj date]], [obj levelStr], [obj tag], [obj thread], [obj content]];
    
    CGSize size = [text sizeWithFont:[UIFont systemFontOfSize:14.0f]
                                  constrainedToSize:constrainedToSize
                                      lineBreakMode:NSLineBreakByWordWrapping];
    float height = size.height;
    height += 10;
    
    return height;
}

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, [GTLogCell cellHeight:data bound:bound] );
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier withFrame:(CGRect)frame
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier withFrame:frame];
    if (self) {

    }
    return self;
}

- (void)drawRect:(CGRect)rect
{
    GTLogRecord *obj = (GTLogRecord *)self.cellData;
    //    CGSize bound = CGSizeMake( self.bounds.size.width, 0.0f );
    //    CGFloat height = [GTLogCell cellHeight:self.cellData bound:bound];
    
    CGSize size;
    CGFloat width = 0;
    CGFloat viewWidth = self.bounds.size.width;
    CGFloat viewHeight = self.bounds.size.height;
    //    CGFloat viewHeight = height;
    
    width = 0;
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), [UIColor grayColor].CGColor);
    size = [[NSString stringWithTimeEx:[obj date]] drawInRect:CGRectMake( 0.0f, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
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
    width += size.width;
    size = [@" " drawInRect:CGRectMake( width, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    width += size.width;
    
    //    CGFloat spaceWidth = 0;
    NSMutableString *str = [NSMutableString stringWithCapacity:1];
    //一个空格占用四个width
    for (int i = 0; i < width/4 + 1; i++) {
        [str appendString:@" "];
    }
    [str appendString:[obj content]];
    [str drawInRect:CGRectMake( 0, 2.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];

}

- (void)cellLayout
{
//    _labelLog.frame = CGRectMake( 3, 2, self.bounds.size.width, self.bounds.size.height);

}


- (void)load
{
	[super load];
    
}

- (void)unload
{
    [super unload];
}

- (void)bindData:(NSObject *)data
{
    [super bindData:data];
    [self setNeedsDisplay];
    return;
}

- (void)clearData
{

}

@end


#endif
