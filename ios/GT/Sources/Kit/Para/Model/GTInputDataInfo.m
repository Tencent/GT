//
//  GTInputDataInfo.m
//  GTKit
//
//  Created   on 12-5-29.
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

#import "GTInputDataInfo.h"

@implementation GTInputDataInfo

@synthesize key   = _key;
@synthesize valueArray   = _valueArray;
@synthesize valueIndex   = _valueIndex;

#pragma mark -

- (id)initWithKey:(NSString*)key alias:(NSString*)alias valueArray:(id)valueArray
{
    self = [super initWithAlias:alias];
    if (self) {
        _key   = [[NSString alloc] initWithString:key];
        _valueArray = [[NSMutableArray arrayWithArray:valueArray] retain];
        _valueIndex = [self defaultValueIndex];
    }
    
    return self;
}

- (void)dealloc
{
    [_key release];
    [_valueArray removeAllObjects];
    [_valueArray release];

    [super dealloc];
}

- (NSInteger)defaultValueIndex
{
    return 0;
}

- (id)value
{
    if (_valueIndex < [_valueArray count]) {
        return [_valueArray objectAtIndex:_valueIndex];
    } else {
        return nil;
    }
    
}

- (void)setDataValue:(id)value
{
    @synchronized (self) {
        if ([_valueArray count] == 0) {
            _valueIndex = 0;
            [_valueArray addObject:value];
        } else if (_valueIndex < [_valueArray count]) {
            [_valueArray replaceObjectAtIndex:_valueIndex withObject:value];
        }
    }
}

- (NSUInteger)addDataValue:(id)value
{
    for (int i = 0; i < [_valueArray count]; i++) {
        if ([value isEqualToString:[_valueArray objectAtIndex:i]]) {
            _valueIndex = i;
            return _valueIndex;
        }
    }
    
    //插到头部
    [_valueArray insertObject:value atIndex:0];
    _valueIndex = 0;
    
    return _valueIndex;
}

- (NSUInteger)addDataValueAtLast:(id)value
{
    for (int i = 0; i < [_valueArray count]; i++) {
        if ([value isEqualToString:[_valueArray objectAtIndex:i]]) {
            _valueIndex = i;
            return _valueIndex;
        }
    }
    
    //插到尾部
    [_valueArray addObject:value];
    
    return _valueIndex;
}

- (void)clearDataValue
{
    
}

- (NSString *)dataValueInfo
{
    if (_valueIndex < [_valueArray count]) {
        return [NSString stringWithFormat:@"%@", [_valueArray objectAtIndex:_valueIndex]];
    } else {
        return nil;
    }
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@", _valueArray];
}

@end

#endif
