//
//  GTInputList.m
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

#import "GTInputList.h"

@implementation GTInputList

M_GT_DEF_SINGLETION(GTInputList);
@synthesize acArray = _acArray;
@synthesize normalArray = _normalArray;
@synthesize disabledArray = _disabledArray;


- (id) init
{
    self = [super init];
    if (self) {
        _acArray = [[NSMutableArray alloc] init];
        _normalArray = [[NSMutableArray alloc] init];
        _disabledArray = [[NSMutableArray alloc] init];
    }
    
    return self;
}

- (void) dealloc
{
    [_acArray removeAllObjects];
    [_acArray release];
    
    [_normalArray removeAllObjects];
    [_normalArray release];
    
    [_disabledArray removeAllObjects];
    [_disabledArray release];
    [super dealloc];
}

#pragma mark - 对象维护基本操作

- (NSString *)dataValueForKey:(NSString *)key
{
    id obj = [super objectForKey:key];
    if (obj == nil) {
        return nil;
    }
    
    return [[obj dataInfo] value];
}


- (BOOL)isEnableForKey:(NSString *)key
{
    GTInputObject *obj = [self objectForKey:key];
    if (obj == nil) {
        return NO;
    }
    
    if ([obj status] == GTParaOnDisabled) {
        return NO;
    }
    return YES;
}

- (void)defaultOnAC:(NSString*)key1 key2:(NSString*)key2 key3:(NSString*)key3
{
    //清除之前设置在悬浮框上的数据
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTInputObject *obj = [_objs objectForKey:key];
        if ([obj status] == GTParaOnAc) {
            [[GTInputList sharedInstance] setStatus:GTParaOnNormal forKey:key];
        }
    }
    
    // 保证顺序为key1 key2 key3
    [[GTInputList sharedInstance] setStatus:GTParaOnAc forKey:key1];
    [[GTInputList sharedInstance] setStatus:GTParaOnAc forKey:key2];
    [[GTInputList sharedInstance] setStatus:GTParaOnAc forKey:key3];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    return;
}


- (void)defaultOnDisabled:(NSArray *)array
{
    //清除之前设置的数据
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTInputObject *obj = [_objs objectForKey:key];
        
        if ([obj status] == GTParaOnDisabled) {
            [[GTInputList sharedInstance] setStatus:GTParaOnNormal forKey:key];
        }
    }
    
    //按顺序设置在Disabled的数据
    for (int i = 0; i < [array count]; i++) {
        id key = [array objectAtIndex:i];
        [[GTInputList sharedInstance] setStatus:GTParaOnDisabled forKey:key];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    return;
}


- (NSMutableArray *)arrayForStatus:(NSUInteger)status
{
    if (status == GTParaOnNormal) {
        return _normalArray;
    } else if (status == GTParaOnAc) {
        return _acArray;
    } else if (status == GTParaOnDisabled) {
        return _disabledArray;
    } else {
        return nil;
    }
}

//设置状态后，会将key放在队尾
- (void)setStatus:(NSUInteger)status forKey:(NSString *)key
{
    GTInputObject *obj = [self objectForKey:key];
    if (obj == nil) {
        return;
    }
    if ([obj status] != status) {
        //删除旧的状态
        [[self arrayForStatus:[obj status]] removeObject:key];
    }
    
    //保存新的状态
    [[self arrayForStatus:status] removeObject:key];
    [[self arrayForStatus:status] addObject:key];
    
    [obj setStatus:status];
}

- (void)insertKey:(NSString *)key2 atKey:(NSString *)key1
{
    NSUInteger index = 0;
    GTInputObject *obj = [self objectForKey:key1];
    NSMutableArray *array = [self arrayForStatus:[obj status]];
    
    index = [array indexOfObject:key1];
    if (index < [array count]) {
        [array removeObject:key2];
        [array insertObject:key2 atIndex:index];
    }
}

- (void)insertBackForKey:(NSString *)key2 atKey:(NSString *)key1
{
    NSUInteger index = 0;
    GTInputObject *obj = [self objectForKey:key1];
    NSMutableArray *array = [self arrayForStatus:[obj status]];
    
    index = [array indexOfObject:key1];
    index++;
    if (index < [array count]) {
        [array removeObject:key2];
        [array insertObject:key2 atIndex:index];
    }
}

- (void)updateSectionArray
{
    [_disabledArray removeAllObjects];
    [_acArray removeAllObjects];
    [_normalArray removeAllObjects];
    
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        if ([[_objs objectForKey:key] status] == GTParaOnDisabled) {
            [_disabledArray addObject:key];
        } else if ([[_objs objectForKey:key] status] == GTParaOnAc) {
            [_acArray addObject:key];
        } else if ([[_objs objectForKey:key] status] == GTParaOnNormal) {
            [_normalArray addObject:key];
        }
    }
}

#pragma mark - 常用的接口实现

- (void)addInput:(NSString*)key alias:(NSString*)alias valueArray:(id)valueArray
{
    GTInputObject *obj = [[GTInputObject alloc] initWithKey:key alias:alias valueArray:valueArray];
    [self setObject:obj forKey:key];
    [obj release];
    
    [[GTInputList sharedInstance] setStatus:GTParaOnNormal forKey:key];
}

- (void)setInput:(id)value forKey:(NSString*)key
{
    GTInputObject *obj = [self objectForKey:key];
    [[obj dataInfo] addDataValue:value];
}

@end


#pragma mark - User Interface

#define M_GT_INPUT_LOG(op,k,v) if (writeToLog) {\
GT_OC_LOG_D(M_GT_TAG, @"%@ Input K:%@ V:%@", op, k, v);\
}


#pragma mark - OC
void func_addInputForOC(NSString* key, NSString *alias, NSArray *array)
{
    @autoreleasepool {
        [[GTInputList sharedInstance] addInput:key alias:alias valueArray:array];
    }
    
}

void func_setInputForOC(NSString* key, bool writeToLog, id value)
{
    @autoreleasepool {
        if(![[GTInputList sharedInstance] isEnableForKey:key]) {
            return;
        }
        M_GT_INPUT_LOG(@"SET", key, value);
        [[GTInputList sharedInstance] setInput:value forKey:key];
    }
    
}

id func_getInputForOC(NSString* key, BOOL writeToLog, id value)
{
    @autoreleasepool {
        if (!key) {
            return value;
        }
        if(![[GTInputList sharedInstance] isEnableForKey:key]) {
            return value;
        }
        id dataValue = [[GTInputList sharedInstance] dataValueForKey:key];
        if ([dataValue isEqualToString: M_GT_NIL]) {
            return nil;
        }
        M_GT_INPUT_LOG(@"GET", key, dataValue);
        return dataValue;
    }
    
}

void func_defaultInputOnACForOC(NSString *key1, NSString *key2, NSString *key3)
{
    @autoreleasepool {
        [[GTInputList sharedInstance] defaultOnAC:key1 key2:key2 key3:key3];
    }
    
    
}

void func_defaultInputOnDisabledForOC(NSString * format,...)
{
    @autoreleasepool {
        NSMutableArray *array = [NSMutableArray array];
        
        va_list args;
        va_start(args, format);
        if (format)
        {
            [array addObject:format];
            NSString *otherString;
            while ((otherString = va_arg(args, NSString *)))
            {
                [array addObject:otherString];
            }
        }
        va_end(args);
        
        [[GTInputList sharedInstance] defaultOnDisabled:array];
    }
    
}


void func_defaultInputAllOnDisabled()
{
    @autoreleasepool {
        for (int i = 0; i < [[GTInputList sharedInstance].keys count]; i++) {
            id key = [[GTInputList sharedInstance].keys objectAtIndex:i];
            [[GTInputList sharedInstance] setStatus:GTParaOnDisabled forKey:key];
        }
    }
    
}

#pragma mark - C
void func_addInputForString(const char *key, const char *alias, const char * format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(alias);
        M_GT_PTR_NULL_CHECK(format);
        
        NSString * aliasStr = [NSString stringWithCString:alias encoding:NSUTF8StringEncoding];
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        
        M_GT_FORMAT_INIT;
        NSString * valueStr = M_GT_FORMAT_STR;
        NSMutableArray *array = [NSMutableArray arrayWithCapacity:1];
        [array addObject:valueStr];
        
        [[GTInputList sharedInstance] addInput:keyStr alias:aliasStr valueArray:array];
    }
    
}

void func_addInputForArrayStr(const char *key, const char *alias, char* a[], int n)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(alias);
        M_GT_PTR_NULL_CHECK(a);
        
        NSString * aliasStr = [NSString stringWithCString:alias encoding:NSUTF8StringEncoding];
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        NSMutableArray *array = [NSMutableArray arrayWithCapacity:n];
        
        if (n <= 0) {
            return;
        }
        
        for (int i = 0; i < n; i++) {
            NSString * str = [NSString stringWithFormat:@"%s", a[i]];
            [array addObject:str];
        }
        
        [[GTInputList sharedInstance] addInput:keyStr alias:aliasStr valueArray:array];
    }
    
}

void func_setInputForString(const char *key, bool writeToLog, const char * format,...)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(format);
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTInputList sharedInstance] isEnableForKey:keyStr]) {
            return;
        }
        M_GT_FORMAT_INIT;
        NSString * valueStr = M_GT_FORMAT_STR;
        NSMutableArray *array = [NSMutableArray arrayWithCapacity:1];
        [array addObject:valueStr];
        
        M_GT_INPUT_LOG(@"SET", keyStr, valueStr);
        [[GTInputList sharedInstance] setInput:valueStr forKey:keyStr];
    }
    
}

bool func_getInputForBool(const char *key, bool writeToLog, bool value)
{
    @autoreleasepool {
        if (!key) {
            return value;
        }
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        
        if(![[GTInputList sharedInstance] isEnableForKey:keyStr]) {
            return value;
        }
        NSString * valueStr = [[GTInputList sharedInstance] dataValueForKey:keyStr];
        if ([valueStr isEqualToString: M_GT_NIL]) {
            valueStr = nil;
        }
        
        M_GT_INPUT_LOG(@"GET", keyStr, valueStr);
        
        if ([valueStr boolValue]) {
            return true;
        } else {
            return false;
        }
    }
    
}

double func_getInputForDouble(const char *key, bool writeToLog, double value)
{
    @autoreleasepool {
        if (!key) {
            return value;
        }
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTInputList sharedInstance] isEnableForKey:keyStr]) {
            return value;
        }
        NSString * valueStr = [[GTInputList sharedInstance] dataValueForKey:keyStr];
        
        if ([valueStr isEqualToString: M_GT_NIL]) {
            valueStr = nil;
        }
        
        M_GT_INPUT_LOG(@"GET", keyStr, valueStr);
        return [valueStr doubleValue];
    }
    
}

int func_getInputForInt(const char *key, bool writeToLog, int value)
{
    @autoreleasepool {
        if (!key) {
            return value;
        }
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTInputList sharedInstance] isEnableForKey:keyStr]) {
            return value;
        }
        NSString * valueStr = [[GTInputList sharedInstance] dataValueForKey:keyStr];
        
        if ([valueStr isEqualToString: M_GT_NIL]) {
            valueStr = nil;
        }
        
        M_GT_INPUT_LOG(@"GET", keyStr, valueStr);
        return [valueStr intValue];
    }
    
}

float func_getInputForFloat(const char *key, bool writeToLog, float value)
{
    @autoreleasepool {
        if (!key) {
            return value;
        }
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTInputList sharedInstance] isEnableForKey:keyStr]) {
            return value;
        }
        NSString * valueStr = [[GTInputList sharedInstance] dataValueForKey:keyStr];
        
        if ([valueStr isEqualToString: M_GT_NIL]) {
            valueStr = nil;
        }
        
        M_GT_INPUT_LOG(@"GET", keyStr, valueStr);
        return [valueStr floatValue];
    }
    
}

const char* func_getInputForString(const char *key, bool writeToLog, const char *value)
{
    @autoreleasepool {
        if (!key) {
            return value;
        }
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTInputList sharedInstance] isEnableForKey:keyStr]) {
            return value;
        }
        NSString * valueStr = [[GTInputList sharedInstance] dataValueForKey:keyStr];
        
        if ([valueStr isEqualToString: M_GT_NIL]) {
            return nil;
        }
        
        M_GT_INPUT_LOG(@"GET", keyStr, valueStr);
        return [valueStr UTF8String];
    }
    
}

void func_defaultInputOnAC(const char *key1, const char *key2, const char *key3)
{
    @autoreleasepool {
        NSString * keyStr1 = [NSString stringWithCString:key1 encoding:NSUTF8StringEncoding];
        NSString * keyStr2 = [NSString stringWithCString:key2 encoding:NSUTF8StringEncoding];
        NSString * keyStr3 = [NSString stringWithCString:key3 encoding:NSUTF8StringEncoding];
        
        [[GTInputList sharedInstance] defaultOnAC:keyStr1 key2:keyStr2 key3:keyStr3];
    }
    
}

void func_defaultInputOnDisabled(const char * format,...)
{
    @autoreleasepool {
        NSMutableArray *array = [NSMutableArray array];
        
        va_list args;
        va_start( args, format );
        if (format)
        {
            [array addObject:[NSString stringWithCString:format encoding:NSUTF8StringEncoding]];
            
            char *otherString;
            while ((otherString = va_arg(args, char *)))
            {
                [array addObject:[NSString stringWithCString:otherString encoding:NSUTF8StringEncoding]];
            }
        }
        va_end( args );
        
        [[GTInputList sharedInstance] defaultOnDisabled:array];
    }
    
}
#endif
