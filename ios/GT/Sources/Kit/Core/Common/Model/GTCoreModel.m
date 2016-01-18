//
//  GTCoreModel.m
//  GTKit
//
//  Created   on 13-7-30.
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

#import "GTCoreModel.h"
#import "GTConfig.h"

@implementation GTMonitorObj

@synthesize aClass = _aClass;
@synthesize on = _on;
@synthesize interval = _interval;
@synthesize lastMonitor = _lastMonitor;

- (id)initMonitor:(BOOL)on withInterval:(NSTimeInterval)interval
{
    self = [super init];
    if (self) {
        _aClass = nil;
        _on = on;
        _interval = interval;
        _lastMonitor = 0;
    }
    
    return self;
}

-(void) dealloc
{
    [super dealloc];
}


- (BOOL)needMonitor
{
    if (_on == YES) {
        if (_interval == 0) {
            //0：默认，表示需要监控
            return YES;
        } else {
            if (_lastMonitor == 0) {
                _lastMonitor = [GTUtility timeIntervalSince1970];
                return YES;
            }
            
            if (([GTUtility timeIntervalSince1970] - _lastMonitor) > _interval) {
                _lastMonitor = [GTUtility timeIntervalSince1970];
                return YES;
            }
            
        }
        
    }
    
    return NO;
    
}

@end

@implementation GTCoreModel

M_GT_DEF_SINGLETION( GTCoreModel );

- (id)init
{
    self = [super init];
    if (self) {
        _monitorDict = [[NSMutableDictionary alloc] init];
        _monitorThread = nil;
        [self threadStart];
    }
    
    return self;
}

-(void) dealloc
{
    [self threadEnd];
    [super dealloc];
}

- (void)threadStart
{
    if (_monitorThread == nil) {
        _monitorThread = [[NSThread alloc] initWithTarget:self selector:@selector(threadProc:) object:nil];
        _monitorThread.name = [NSString stringWithFormat:@"GTCore_%@", NSStringFromClass([self class])];
        [_monitorThread start];
    }
}

- (void)threadEnd
{
    if (_monitorThread != nil) {
        [_monitorThread cancel];
        [_monitorThread release];
        _monitorThread = nil;
    }
}

- (void)enableMonitor:(Class )aClass withInterval:(NSTimeInterval)interval
{
    NSString *classString = NSStringFromClass(aClass);
    GTMonitorObj *obj = [_monitorDict objectForKey:classString];
    if (obj == nil) {
        obj = [[[GTMonitorObj alloc] initMonitor:YES withInterval:interval] autorelease];
    }
    [obj setOn:YES];
    [obj setAClass:aClass];
    @synchronized (_monitorDict) {
        [obj setLastMonitor:0];
        [_monitorDict setObject:obj forKey:classString];
    }
    //如果没有启动线程，则启动
    [self threadStart];
}

- (void)disableMonitor:(Class )aClass
{
    NSString *classString = NSStringFromClass(aClass);
    GTMonitorObj *obj = [_monitorDict objectForKey:classString];
    if (obj == nil) {
        return;
    }
    [obj setOn:NO];
    @synchronized (_monitorDict) {
        [obj setLastMonitor:0];
        [_monitorDict setObject:obj forKey:classString];
    }
    
    
    //如果没有监控项，则停止线程
    if (![self hasMonitorItem]) {
        [self threadEnd];
    }
}

- (BOOL)hasMonitorItem
{
    NSArray *array = [_monitorDict allKeys];
    for (int i = 0; i < [array count]; i++) {
        id key = [array objectAtIndex:i];
        GTMonitorObj *obj = [_monitorDict objectForKey:key];
        if ([obj on] == YES) {
            return YES;
        }
    }
    return NO;
}

- (void)handleTick
{
    NSArray *array = nil;
    @synchronized (_monitorDict) {
        array = [[_monitorDict allKeys] copy];
    }
    
    //采集所有需要监控的指标
    for (int i = 0; i < [array count]; i++) {
        id key = [array objectAtIndex:i];
        GTMonitorObj *obj = [_monitorDict objectForKey:key];
        if ([obj needMonitor] == YES) {
//            [[NSClassFromString(key) sharedInstance] handleTick];
            [[[obj aClass] sharedInstance] handleTick];
        }
    }
    
    [array release];
}

- (void)threadProc:(id)obj
{
    while (TRUE) {
        
        @autoreleasepool {
            if ([[NSThread currentThread] isCancelled]) {
                [NSThread exit];
            }
            
            [self handleTick];
            [NSThread sleepForTimeInterval:[[GTConfig sharedInstance] monitorInterval]];
        }

    }
}

@end

#endif
