//
//  GTList.m
//  GTKit
//
//  Created   on 12-11-28.
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

#import "GTList.h"


@implementation GTList

@synthesize keys = _keys;
@synthesize objs = _objs;

#pragma mark -
#pragma mark Life Cycle

- (id) init
{
    self = [super init];
    if (self) {
        _keys = [[NSMutableArray alloc] init];
        _objs = [[NSMutableDictionary alloc] init];
    }
    
    return self;
}

- (void) dealloc
{
    [_keys removeAllObjects];
    [_keys release];
    [_objs removeAllObjects];
    [_objs release];
    [super dealloc];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@", _objs];
}

#pragma mark - List Operation

- (void)setObject:(id)object forKey:(NSString *)key
{
    if ( nil == key ) {
        return;
    }
	if ( nil == object ) {
		return;
	}
    id obj = [_objs objectForKey:key];
    if (!obj) {
        [_keys addObject:key];
    }
    
    [_objs setObject:object forKey:key];
    
    return;
}

- (id)objectForKey:(NSString *)key
{
    return [_objs objectForKey:key];
}

- (void)removeObjectForKey:(NSString *)key
{
    if ( [_objs objectForKey:key] )
	{
        //removeObjectIdenticalTo sometimes doesn't work
//		[_keys removeObjectIdenticalTo:key];
        [_keys removeObject:key];
		[_objs removeObjectForKey:key];
	}

    return;
}


// 在key1前插入key2
- (void)insertFrontForKey:(NSString *)key2 atKey:(NSString *)key1
{
    if ( nil == key1 ) {
        return;
    }
    if ( nil == key2 ) {
        return;
    }

    if ([key1 isEqualToString:key2]) {
        return;
    }
    
    if (( ![_objs objectForKey:key1] ) || ( ![_objs objectForKey:key2] )) {
        return;
    }
    
    //先删除
    [_keys removeObject:key2];
    
    NSUInteger index1 = [_keys indexOfObjectIdenticalTo:key1];
    if (index1 < [_keys count]) {
        [_keys insertObject:key2 atIndex:index1];
    }
}

// 在key1后插入key2
- (void)insertBackForKey:(NSString *)key2 atKey:(NSString *)key1
{
    if ( nil == key1 ) {
        return;
    }
    if ( nil == key2 ) {
        return;
    }
    
    if ([key1 isEqualToString:key2]) {
        return;
    }
    
    if (( ![_objs objectForKey:key1] ) || ( ![_objs objectForKey:key2] )) {
        return;
    }
    
    //先删除
    [_keys removeObject:key2];
    
    NSUInteger index1 = [_keys indexOfObjectIdenticalTo:key1];
    index1++;
    
    if (index1 < [_keys count]) {
        [_keys insertObject:key2 atIndex:index1];
    } else {
        [_keys addObject:key2];
    }
}

// 将key对应到object对象插入尾部
- (void)insertTailForKey:(NSString *)key
{
    if ( ![_objs objectForKey:key] ) {
        return;
    }
    
    //先删除
    [_keys removeObject:key];
    [_keys addObject:key];
}


- (void)deleteAll
{
	[_keys removeAllObjects];
	[_objs removeAllObjects];
}


#pragma mark NSCopying


- (id)copyWithZone:(NSZone *)zone
{
	GTList *copy = [[[self class] allocWithZone:zone] init];
    copy.keys = [[self.keys copy] autorelease];
    copy.objs = [[self.objs copy] autorelease];
	
    return copy;
}


@end
#endif
