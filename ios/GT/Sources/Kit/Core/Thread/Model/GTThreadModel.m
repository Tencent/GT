//
//  GTProcesserModel.m
//  GTKit
//
//  Created   on 12-10-18.
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

#import "GTThreadModel.h"
#import "GT.h"
#import "GTOutputList.h"

#include <mach/mach.h>
#include <malloc/malloc.h>

#import <sys/sysctl.h>
#import <sys/types.h>
#import <sys/param.h>
#import <sys/mount.h>
#import <mach/processor_info.h>
#import <mach/mach_host.h>


@implementation GTThreadModel

M_GT_DEF_SINGLETION(GTThreadModel);

-(id) init
{
    self = [super init];
    if (self) {
        GT_OUT_REGISTER("App CPU", "CPU");
        GT_OUT_HISTORY_CHECKED_SET("App CPU", true);
        GT_OC_OUT_DELEGATE_SET(@"App CPU", self);
    }
    
    return self;
}

-(void) dealloc
{
    [super dealloc];
}


- (void)handleTick
{
    [self getCpuUsage];
    GT_OUT_SET("App CPU", false, "%0.2f%%", cpu_usage);
}

- (float)getCpuUsage
{
    kern_return_t           kr;
    thread_array_t          thread_list;
    mach_msg_type_number_t  thread_count;
    thread_info_data_t      thinfo;
    mach_msg_type_number_t  thread_info_count;
    thread_basic_info_t     basic_info_th;
    
    kr = task_threads(mach_task_self(), &thread_list, &thread_count);
    if (kr != KERN_SUCCESS) {
        return -1;
    }
    cpu_usage = 0;
    
    for (int i = 0; i < thread_count; i++)
    {
        thread_info_count = THREAD_INFO_MAX;
        kr = thread_info(thread_list[i], THREAD_BASIC_INFO,(thread_info_t)thinfo, &thread_info_count);
        if (kr != KERN_SUCCESS) {
            return -1;
        }
        
        basic_info_th = (thread_basic_info_t)thinfo;

        if (!(basic_info_th->flags & TH_FLAGS_IDLE))
        {
            cpu_usage += basic_info_th->cpu_usage;
        }
    }
    
    cpu_usage = cpu_usage / (float)TH_USAGE_SCALE * 100.0;
    
    vm_deallocate(mach_task_self(), (vm_offset_t)thread_list, thread_count * sizeof(thread_t));
    
    return cpu_usage;
}


#pragma mark - GTParaDelegate

- (void)switchEnable
{
    [[GTCoreModel sharedInstance] enableMonitor:[self class] withInterval:0];
}

- (void)switchDisable
{
    [[GTCoreModel sharedInstance] disableMonitor:[self class]];
}
- (NSString *)yDesc
{
    return @"%";
}

@end

double func_cpuUsage()
{
    return [[GTThreadModel sharedInstance] getCpuUsage];
}

#endif
