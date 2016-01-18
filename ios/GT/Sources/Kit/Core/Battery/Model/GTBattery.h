//
//  GTBattery.h
//  GTKit
//
//  Created   on 13-7-23.
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
#import <Foundation/Foundation.h>
#import "GTDebugDef.h"
#import "GTOutputObject.h"
#import "GTCoreModel.h"
#import "GTParaOutDef.h"

#define M_GT_IOKIT_DYLIB_PATH "/System/Library/Frameworks/IOKit.framework/IOKit"
typedef CFMutableDictionaryRef (* GT_PFN_IOSERVICEMATCHING)(const char *name);
typedef mach_port_t (* GT_PFN_IOSERVICEGETMATCHINGSERVICE)(mach_port_t masterPort, CFDictionaryRef matching);
typedef kern_return_t (* GT_PFN_IOREGISTRYENTRYCREATECFPROPERTIES)(mach_port_t	entry, CFMutableDictionaryRef * properties, CFAllocatorRef allocator,int options);
typedef kern_return_t (* GT_PFN_IOOBJECTRELEASE)(mach_port_t object);

@interface GTBatteryInfo: NSObject
{
    NSInteger _batteryLevel;
    CGFloat   _current;
    NSInteger _currentCapacity;
    NSInteger _maxCapacity;
    NSInteger _voltage;
    CGFloat   _temperature;
    NSInteger _cycleCount;
    NSInteger _bootVoltage;
    NSInteger _designCapacity;
}

@property (nonatomic, assign) NSInteger batteryLevel;
@property (nonatomic, assign) CGFloat current;
@property (nonatomic, assign) NSInteger currentCapacity;
@property (nonatomic, assign) NSInteger maxCapacity;
@property (nonatomic, assign) NSInteger voltage;
@property (nonatomic, assign) CGFloat temperature;
@property (nonatomic, assign) NSInteger cycleCount;
@property (nonatomic, assign) NSInteger bootVoltage;
@property (nonatomic, assign) NSInteger designCapacity;

@end

@interface GTBatteryHistory : GTHistroyValue
{
    CGFloat   _current;
    NSInteger _currentCapacity;
}

@property (nonatomic, assign) CGFloat current;
@property (nonatomic, assign) NSInteger currentCapacity;

@end


@interface GTBattery : NSObject <GTParaDelegate>
{
    GT_PFN_IOSERVICEMATCHING                    _IOServiceMatching;
    GT_PFN_IOSERVICEGETMATCHINGSERVICE          _IOServiceGetMatchingService;
    GT_PFN_IOREGISTRYENTRYCREATECFPROPERTIES    _IORegistryEntryCreateCFProperties;
    GT_PFN_IOOBJECTRELEASE                      _IOObjectRelease;
    mach_port_t                                 *_kIOMasterPortDefault;
    void                                        *_IOKitHandle;
    
    BOOL                                        _prepared;
    NSInteger                                   _lastCapacity; //保存上一次currentCapacity
    NSInteger                                   _startCapacity; //保存开始时的currentCapacity
    NSTimeInterval                              _lastCapacityDate;
    CGFloat                                     _current;
    
    GTBatteryHistory                            *_item; //需要记录的电量信息
    GTBatteryInfo                               *_info; //查询到到电量相关信息保存
    
}

M_GT_AS_SINGLETION( GTBattery );

- (void)handleTick;
@property (nonatomic, assign) NSTimeInterval lastCapacityDate;
@property (nonatomic, retain) GTBatteryInfo *info;
- (void)resetData;
@end
#endif
