//
//  GTBattery.m
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

#import "GTBattery.h"
#import <dlfcn.h>
#import <mach/port.h>
#import <mach/kern_return.h>
#import <objc/runtime.h>
#import "GTConfig.h"
#import "GTUtility.h"

@implementation GTBatteryInfo

@synthesize batteryLevel = _batteryLevel;
@synthesize current = _current;
@synthesize currentCapacity = _currentCapacity;
@synthesize maxCapacity = _maxCapacity;
@synthesize voltage = _voltage;
@synthesize temperature = _temperature;
@synthesize designCapacity = _designCapacity;

@end

@implementation GTBatteryHistory

@synthesize current = _current;
@synthesize currentCapacity = _currentCapacity;

- (id)init
{
    self = [super init];
    if (self) {
        
    }
    
    return self;
}

- (void)dealloc
{
    [super dealloc];
}

#pragma mark -


- (void)appendRowWithCString:(GTMutableCString *)cString
{
    char buffer[M_GT_SIZE_64] = {0};
    
    [cString appendCString:M_GT_HISTORY_ROW_HEADER_CSTR length:strlen(M_GT_HISTORY_ROW_HEADER_CSTR)];
    [cString appendCStringWithTimeEx:_date];
    memset(buffer, 0, M_GT_SIZE_64);
    snprintf(buffer, M_GT_SIZE_64 - 1, ",%.1f,%ld", [self current], (long)[self currentCapacity]);
    [cString appendCString:buffer length:strlen(buffer)];
    [cString appendCString:M_GT_HISTORY_ROW_TAIL_CSTR length:strlen(M_GT_HISTORY_ROW_TAIL_CSTR)];
    
}

- (NSString *)rowStr
{
    return [[[NSString alloc] initWithFormat:@"%@%@,%.1f,%ld\r\n", M_GT_HISTORY_ROW_HEADER, [NSString stringWithTimeEx:_date], [self current], (long)[self currentCapacity]] autorelease];
}

+ (NSString *)rowTitle
{
    return [[[NSString alloc] initWithFormat:@"%@time,current(mA),CostCapacity(mAh)\r\n", M_GT_HISTORY_ROW_HEADER] autorelease];
}


@end


@implementation GTBattery

M_GT_DEF_SINGLETION( GTBattery )

@synthesize lastCapacityDate = _lastCapacityDate;
@synthesize info = _info;
- (id)init
{
    self = [super init];
    if (self) {
        _prepared = NO;
        _lastCapacity = 0;
        _current = 0;
        _startCapacity = 0;
        _item = [[GTBatteryHistory alloc] init];
        _info = [[GTBatteryInfo alloc] init];
        
        GT_OUT_REGISTER("Battery", "BT");
        GT_OUT_HISTORY_CHECKED_SET("Battery", true);
        GT_OC_OUT_VC_SET(@"Battery", @"GTBatteryDetailBoard");
        GT_OC_OUT_DELEGATE_SET(@"Battery", self);
    }
    
    return self;
}

- (void)dealloc
{
    [self powerMgrRelease];
    
    M_GT_SAFE_FREE(_item);
    M_GT_SAFE_FREE(_info);
    [super dealloc];
}

- (void)powerMgrRelease
{
    if (_IOKitHandle) {
        dlclose(_IOKitHandle);
    }
}

- (BOOL)powerMgrInit
{
    _IOKitHandle = dlopen(M_GT_IOKIT_DYLIB_PATH, RTLD_NOW);
    if (!_IOKitHandle) {
        return NO;
    }
    
    _kIOMasterPortDefault = (mach_port_t *)dlsym(_IOKitHandle, "kIOMasterPortDefault");
    _IOServiceMatching = (GT_PFN_IOSERVICEMATCHING)dlsym(_IOKitHandle, "IOServiceMatching");
    _IOServiceGetMatchingService = (GT_PFN_IOSERVICEGETMATCHINGSERVICE)dlsym(_IOKitHandle, "IOServiceGetMatchingService");
    _IORegistryEntryCreateCFProperties = (GT_PFN_IOREGISTRYENTRYCREATECFPROPERTIES)dlsym(_IOKitHandle, "IORegistryEntryCreateCFProperties");
    _IOObjectRelease = (GT_PFN_IOOBJECTRELEASE)dlsym(_IOKitHandle, "IOObjectRelease");
    
    if (_kIOMasterPortDefault &&
        _IOServiceMatching &&
        _IOServiceGetMatchingService &&
        _IORegistryEntryCreateCFProperties &&
        _IOObjectRelease) {
        return YES;
    }
    
    return NO;
}

- (void)updateBatteryInfo
{
    CFMutableDictionaryRef matching , properties = NULL;
    mach_port_t entry = 0;
    matching = _IOServiceMatching( "IOPMPowerSource" );
    if (!matching) {
        return;
    }
    
    entry = _IOServiceGetMatchingService( *_kIOMasterPortDefault , matching );
    if (!entry) {
        return;
    }
    
    kern_return_t ret = _IORegistryEntryCreateCFProperties( entry , &properties , NULL , 0 );
    if (ret) {
        return;
    }
    
    [self updateBatteryItem:(NSDictionary *)properties];
    CFRelease( properties );
    _IOObjectRelease( entry );
    
    return;
}


- (int)batteryLevel
{
    @try {
        UIApplication *app = [UIApplication sharedApplication];
        if (app.applicationState == UIApplicationStateActive) {
            void *result = nil;
            object_getInstanceVariable(app, "_statusBar", &result);
            id status  = result;
            for (id a in [status subviews]) {
                for (id b in [a subviews]) {
                    if ([NSStringFromClass([b class]) caseInsensitiveCompare:@"UIStatusBarBatteryPercentItemView"] == NSOrderedSame) {
                        int ret = 0;
                        if ([[GTUtility sharedInstance] systemVersion] >= 6.0) {
                            object_getInstanceVariable(b, "_percentString", &result);
                            ret = (int)[(NSString *)result integerValue];
                        } else {
                            object_getInstanceVariable(b, "_capacity", &result);
                            ret = (int)result;
                        }
                        if (ret > 0 && ret <= 100) {
                            return ret;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
        
        return 0;
    }
    @catch (...) {
        return 0;
    }
}

- (void)updateBatteryItem:(NSDictionary *)dict
{
    _item.date = [GTUtility timeIntervalSince1970];
    NSNumber *value = [dict objectForKey:@"CurrentCapacity"];
    [_item setCurrentCapacity:[value integerValue]];
    
    [_item setCurrent:_current];
    _info.current = _current;
    _info.currentCapacity = [value integerValue];
    
    value = [dict objectForKey:@"Voltage"];
    _info.voltage = [value integerValue];
    
    value = [dict objectForKey:@"MaxCapacity"];
    _info.maxCapacity = [value integerValue];
    
    value = [dict objectForKey:@"Temperature"];
    _info.temperature = [value integerValue];
    _info.batteryLevel = [self batteryLevel];
    
    value = [dict objectForKey:@"CycleCount"];
    _info.cycleCount = [value integerValue];
    
    value = [dict objectForKey:@"BootVoltage"];
    _info.bootVoltage = [value integerValue];
    
    value = [dict objectForKey:@"DesignCapacity"];
    _info.designCapacity = [value integerValue];
    
    if (_lastCapacity == 0) {
        _current = 0;
        _lastCapacity = [_item currentCapacity];
        _startCapacity = _lastCapacity;
        _lastCapacityDate = _item.date;
//        GT_OC_OUT_SET(@"Battery", NO, [NSString stringWithFormat:@"C: %dmAh I: %.1fmA", _startCapacity-[_item currentCapacity], [_item current]]);
        
        GT_OUT_SET("Battery", false, "C: %dmAh I: %.1fmA", _startCapacity-[_item currentCapacity], [_item current]);
    } else {
        NSInteger costCapacity = [_item currentCapacity] - _lastCapacity;
        if (costCapacity != 0) {
            NSTimeInterval interval = _item.date - _lastCapacityDate;
            if (interval > 0) {
                _current = -costCapacity/interval*3600.0;
                [_item setCurrent:_current];
            }
            
            _lastCapacity = [_item currentCapacity];
            _lastCapacityDate = _item.date;
//            GT_OC_OUT_SET(@"Battery", NO, [NSString stringWithFormat:@"C: %dmAh I: %.1fmA", _startCapacity-[_item currentCapacity], [_item current]]);
            GT_OUT_SET("Battery", false, "C: %dmAh I: %.1fmA", _startCapacity-[_item currentCapacity], [_item current]);
        }
    }
}

- (void)handleTick
{
    if (_prepared == NO) {
        _prepared = [self powerMgrInit];
    }
    
    if (_prepared) {
        [self updateBatteryInfo];
    }
}


#pragma mark - GTParaDelegate

- (void)switchEnable
{
    [[GTCoreModel sharedInstance] enableMonitor:[self class] withInterval:10];
}

- (void)switchDisable
{
    [[GTCoreModel sharedInstance] disableMonitor:[self class]];
}

- (GTHistroyValue *)objForHistory
{
    GTBatteryHistory *data = [[GTBatteryHistory alloc] init];
    [data setDate:[_item date]];
    [data setCurrentCapacity:(_startCapacity - [_item currentCapacity])];
    [data setCurrent:[_item current]];
    
    return [data autorelease];
}

- (NSString *)descriptionForObj
{
    NSMutableString *description = [[NSMutableString alloc] initWithCapacity:1024];
    
    [description appendFormat:@"Temperature,%.2f℃\r\n", _info.temperature/100.0];
    [description appendFormat:@"CurrentCapacity,%ldmAh\r\n", (long)_info.currentCapacity];
    [description appendFormat:@"MaxCapacity,%ldmAh\r\n", (long)_info.maxCapacity];
    [description appendFormat:@"DesignCapacity,%ldmAh\r\n", (long)_info.designCapacity];
    [description appendFormat:@"Battery Level,%ld%%\r\n", (long)_info.batteryLevel];
    [description appendFormat:@"CycleCount,%ld\r\n", (long)_info.cycleCount];
    [description appendFormat:@"Voltage,%ldmV\r\n", (long)_info.voltage];
    [description appendFormat:@"BootVoltage,%ldmV\r\n", (long)_info.bootVoltage];
    [description appendFormat:@"\r\n"];
    return [description autorelease];
}

- (void)resetData
{
    _startCapacity = _info.currentCapacity;
//    GT_OC_OUT_SET(@"Battery", NO, [NSString stringWithFormat:@"CostCapacity: %dmAh Current:%.1fmA", _startCapacity-[_item currentCapacity], [_item current]]);
    GT_OUT_SET("Battery", false, "C: %dmAh I: %.1fmA", _startCapacity-[_item currentCapacity], [_item current]);
}

int func_currentCapacity()
{
    return (int)[[[GTBattery sharedInstance] info] currentCapacity];
}

@end
#endif
