//
//  GTMemoryModel.m
//  GTKit
//
//  Created   on 12-10-13.
//
//

#ifndef GT_DEBUG_DISABLE

#import "GTMemoryModel.h"
#import "GTThreadModel.h"
#import "GTNetModel.h"
#import "GTUtility.h"
#import "GTOutputList.h"

#include <mach/mach.h>
#include <malloc/malloc.h>

#import <sys/types.h>
#import <sys/sysctl.h>
#import <sys/mman.h>

@interface GTMemoryModel()
{
    NSUInteger          _appMemory;
}

@end

@implementation GTMemoryModel


M_GT_DEF_SINGLETION(GTMemoryModel)


-(id) init
{
    self = [super init];
    if (self) {
        [self load];
        GT_OUT_REGISTER("App Memory", "MEM");
        GT_OUT_HISTORY_CHECKED_SET("App Memory", true);
        GT_OC_OUT_DELEGATE_SET(@"App Memory", self);
    }
    
    return self;
}

-(void) dealloc
{
    [self unload];
    [super dealloc];
}


- (void)load
{
	
}

- (void)unload
{
	
}

- (NSUInteger)getResidentMemory
{
    struct task_basic_info t_info;
	mach_msg_type_number_t t_info_count = TASK_BASIC_INFO_COUNT;
	
	int r = task_info(mach_task_self(), TASK_BASIC_INFO, (task_info_t)&t_info, &t_info_count);
	if (r == KERN_SUCCESS)
	{
		return t_info.resident_size;
	}
	else
	{
		return -1;
	}
}

- (void)handleTick
{
    [self updateMemoryData];
}

- (void)updateMemoryData
{
    _appMemory = [self getResidentMemory];
    [self updateOutputInfo];
    return;
}

- (void)updateOutputInfo
{
    //用M单位保存数据
    GT_OUT_SET("App Memory", false, "%.2fM", _appMemory / M_GT_MB);
    
//    NSMutableString * text = [NSMutableString string];
//    [text appendFormat:@"%@", [GTUtility number2String:_appMemory]];
//    GT_OC_OUT_SET(@"App Memory", NO, text);
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
    return @"MB";
}

@end


int64_t func_getAppMemory()
{
    return [[GTMemoryModel sharedInstance] getResidentMemory];
}

int64_t func_getUsedMemory()
{
    size_t length = 0;
    int mib[6] = {0};
    
    int pagesize = 0;
    mib[0] = CTL_HW;
    mib[1] = HW_PAGESIZE;
    length = sizeof(pagesize);
    if (sysctl(mib, 2, &pagesize, &length, NULL, 0) < 0)
    {
        return 0;
    }
    
    mach_msg_type_number_t count = HOST_VM_INFO_COUNT;
    
    vm_statistics_data_t vmstat;
    
    if (host_statistics(mach_host_self(), HOST_VM_INFO, (host_info_t)&vmstat, &count) != KERN_SUCCESS)
    {
		return 0;
    }
    
    int wireMem = vmstat.wire_count * pagesize;
	int activeMem = vmstat.active_count * pagesize;
    return wireMem + activeMem;
}

int64_t func_getFreeMemory()
{
    size_t length = 0;
    int mib[6] = {0};
    
    int pagesize = 0;
    mib[0] = CTL_HW;
    mib[1] = HW_PAGESIZE;
    length = sizeof(pagesize);
    if (sysctl(mib, 2, &pagesize, &length, NULL, 0) < 0)
    {
        return 0;
    }
    
    mach_msg_type_number_t count = HOST_VM_INFO_COUNT;
    
    vm_statistics_data_t vmstat;
    
    if (host_statistics(mach_host_self(), HOST_VM_INFO, (host_info_t)&vmstat, &count) != KERN_SUCCESS)
    {
		return 0;
    }
    
	int freeMem = vmstat.free_count * pagesize;
	int inactiveMem = vmstat.inactive_count * pagesize;
    
    return freeMem + inactiveMem;
}



#endif
