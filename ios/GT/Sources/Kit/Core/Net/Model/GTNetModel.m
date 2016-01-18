//
//  GTNetModel.m
//  GTKit
//
//  Created   on 12-10-24.
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

#import <ifaddrs.h>
#import <sys/socket.h>
#import <net/if.h>
#import <sys/types.h>
#import <sys/sysctl.h>
#import <sys/mman.h>
#import <mach/mach.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import "GTNetModel.h"
#import "GTOutputList.h"
#import "GTConfig.h"

// 网络切换回调函数
static void GTReachabilityChanged(SCNetworkReachabilityRef target,
                                  SCNetworkConnectionFlags flags,
                                  void *info);

@implementation GTNetData

@synthesize wifiSent    = _wifiSent;
@synthesize wifiRev     = _wifiRev;
@synthesize WWANSent    = _WWANSent;
@synthesize WWANRev     = _WWANRev;

- (id)init
{
    self = [super init];
    if (self) {
        
    }
    
    return self;
}

- (void)dealloc
{
    self.wifiSent = 0;
    self.wifiRev = 0;
    self.WWANSent = 0;
    self.WWANRev = 0;
    [super dealloc];
}

- (void)appendRowWithCString:(GTMutableCString *)cString
{
    char buffer[M_GT_SIZE_1024] = {0};
    
    [cString appendCString:M_GT_HISTORY_ROW_HEADER_CSTR length:strlen(M_GT_HISTORY_ROW_HEADER_CSTR)];
    [cString appendCStringWithTimeEx:_date];
    memset(buffer, 0, M_GT_SIZE_1024);
    snprintf(buffer, M_GT_SIZE_1024 - 1, ",%.3f,%.3f,%.3f,%.3f",
            _wifiSent/M_GT_K_B, _wifiRev/M_GT_K_B,
            _WWANSent/M_GT_K_B, _WWANRev/M_GT_K_B);
    [cString appendCString:buffer length:strlen(buffer)];
    [cString appendCString:M_GT_HISTORY_ROW_TAIL_CSTR length:strlen(M_GT_HISTORY_ROW_TAIL_CSTR)];
}

- (NSString *)rowStr
{
    return [[[NSString alloc] initWithFormat:@"%@%@,%.3f,%.3f,%.3f,%.3f\r\n",
        M_GT_HISTORY_ROW_HEADER, [NSString stringWithTimeEx:_date],
        _wifiSent/M_GT_K_B, _wifiRev/M_GT_K_B,
        _WWANSent/M_GT_K_B, _WWANRev/M_GT_K_B] autorelease];
}


+ (NSString *)rowTitle
{
    return [[[NSString alloc] initWithFormat:@"%@time,wifiSent,wifiReceived,WWANSent,WWANReceived\r\n", M_GT_HISTORY_ROW_HEADER] autorelease];
}

@end

@implementation GTNetModel

M_GT_DEF_SINGLETION(GTNetModel);

@synthesize netStatus = _networkStatus;

@synthesize date = _date;
@synthesize prevWiFiSent = _prevWiFiSent;
@synthesize prevWiFiReceived = _prevWiFiReceived;
@synthesize prevWWANSent = _prevWWANSent;
@synthesize prevWWANReceived = _prevWWANReceived;
@synthesize WiFiSent = _WiFiSent;
@synthesize WiFiReceived = _WiFiReceived;
@synthesize WWANSent = _WWANSent;
@synthesize WWANReceived = _WWANReceived;

-(id)init
{
    self = [super init];
    if (self) {
        _prevValid        = false;
        _prevWiFiSent     = 0;
        _prevWiFiReceived = 0;
        _prevWWANSent     = 0;
        _prevWWANReceived = 0;
        
        _WiFiSent     = 0;
        _WiFiReceived = 0;
        _WWANSent     = 0;
        _WWANReceived = 0;
        
        GT_OUT_REGISTER("Device Network", "NET");
        GT_OUT_HISTORY_CHECKED_SET("Device Network", true);
        GT_OC_OUT_VC_SET(@"Device Network", @"GTNetDetailBoard");
        GT_OC_OUT_DELEGATE_SET(@"Device Network", self);
        _networkStatus = [self currentNetStatus];
        
        memset(_netInfo, 0, sizeof(_netInfo));
    }
    
    return self;
}

-(void)dealloc
{
    [super dealloc];
}

- (void)handleTick
{
    [[GTNetModel sharedInstance] updateNetData];
}

- (GTNetStatus) currentNetStatus
{
    _networkStatus = GTNotReachable;
	SCNetworkReachabilityFlags flags;
    
    struct sockaddr_in zeroAddress;
    bzero(&zeroAddress, sizeof(zeroAddress));
    zeroAddress.sin_len = sizeof(zeroAddress);
    zeroAddress.sin_family = AF_INET;
    _reachability = SCNetworkReachabilityCreateWithAddress(kCFAllocatorDefault, (const struct sockaddr*)&zeroAddress);
    
	if (SCNetworkReachabilityGetFlags(_reachability, &flags))
	{
        [self updateNetworkStatusForFlags:flags];
	}
    
    if (_reachability != NULL) {
		CFRelease(_reachability);
    }
    
	return _networkStatus;
}

- (void)updateNetData
{
    BOOL   success;
    struct ifaddrs *addrs;
    const struct ifaddrs *cursor;
    const struct if_data *networkStatisc;
    
    int64_t WiFiSent = 0;
    int64_t WiFiReceived = 0;
    int64_t WWANSent = 0;
    int64_t WWANReceived = 0;
    
    success = getifaddrs(&addrs) == 0;
    
    if (success)
    {
        cursor = addrs;
        while (cursor != NULL)
        {
            // names of interfaces: en0 is WiFi ,pdp_ip0 is WWAN
            if (cursor->ifa_addr->sa_family == AF_LINK)
            {
                if (strcmp(cursor->ifa_name, "en0") == 0)
                {
                    networkStatisc = (const struct if_data *) cursor->ifa_data;
                    WiFiSent+=networkStatisc->ifi_obytes;
                    WiFiReceived+=networkStatisc->ifi_ibytes;
                }
                if (strcmp(cursor->ifa_name, "pdp_ip0") == 0)
                {
                    networkStatisc = (const struct if_data *) cursor->ifa_data;
                    WWANSent+=networkStatisc->ifi_obytes;
                    WWANReceived+=networkStatisc->ifi_ibytes;
                }
            }
            cursor = cursor->ifa_next;
        }
        freeifaddrs(addrs);
    }
    
    if (_prevValid == true) {
        _WiFiSent = WiFiSent - _prevWiFiSent;
        _WiFiReceived = WiFiReceived - _prevWiFiReceived;
        _WWANSent = WWANSent - _prevWWANSent;
        _WWANReceived = WWANReceived - _prevWWANReceived;
        
    } else {
        //记录第一次启动监控的流量
        _prevValid = true;
        _prevWiFiSent = WiFiSent;
        _prevWiFiReceived = WiFiReceived;
        _prevWWANSent = WWANSent;
        _prevWWANReceived = WWANReceived;
    }
    
    char info[M_GT_SIZE_128] = {0};
    memset(info, 0, M_GT_SIZE_128);
    snprintf(info, M_GT_SIZE_128 - 1, "Wifi T:%.3fKB R:%.3fKB\rWWAN T:%.3fKB R:%.3fKB", _WiFiSent/M_GT_K_B,_WiFiReceived/M_GT_K_B,_WWANSent/M_GT_K_B,_WWANReceived/M_GT_K_B);
    
    GT_OUT_SET("Device Network", false, info);
    
//    NSString *netInfo = [NSString stringWithFormat:@"Wifi T:%.3fKB R:%.3fKB\rWWAN T:%.3fKB R:%.3fKB",_WiFiSent/M_GT_K_B,_WiFiReceived/M_GT_K_B,_WWANSent/M_GT_K_B,_WWANReceived/M_GT_K_B];
//    GT_OC_OUT_SET(@"Device Network", NO, netInfo);
    
    //和之前的信息对比判断是否有变化
    if (!strcmp(_netInfo, info)) {
        _newRecord = NO;
    } else {
        _newRecord = YES;
        
        _date = [GTUtility timeIntervalSince1970];
        memset(_netInfo, 0, sizeof(_netInfo));
        //记录本次信息，用于下次采集时判断是否有变化
        memcpy(_netInfo, info, sizeof(info));
    }
    
    return;
}

- (void)resetData
{
    _prevValid = false;
    _prevWiFiSent = 0;
    _prevWiFiReceived = 0;
    _prevWWANSent = 0;
    _prevWWANReceived = 0;
    
    _WiFiSent     = 0;
    _WiFiReceived = 0;
    _WWANSent     = 0;
    _WWANReceived = 0;
    [self updateNetData];
}

// 更新网络状态
- (void)updateNetworkStatusForFlags:(SCNetworkReachabilityFlags)flags
{
    if ((flags & kSCNetworkReachabilityFlagsReachable) == 0)
	{
		_networkStatus = GTNotReachable;
		return;
	}
	
	if ((flags & kSCNetworkReachabilityFlagsConnectionRequired) == 0)
	{
		_networkStatus = GTReachableViaWiFi;
	}
	
	if ((((flags & kSCNetworkReachabilityFlagsConnectionOnDemand ) != 0) ||
		 (flags & kSCNetworkReachabilityFlagsConnectionOnTraffic) != 0))
	{
		if ((flags & kSCNetworkReachabilityFlagsInterventionRequired) == 0)
		{
			_networkStatus = GTReachableViaWiFi;
		}
	}
	
	if ((flags & kSCNetworkReachabilityFlagsIsWWAN) == kSCNetworkReachabilityFlagsIsWWAN)
	{
		_networkStatus = GTReachableViaWWAN;
	}
	return;
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

- (GTHistroyValue *)objForHistory
{
    GTNetData *data = nil;
    if (_newRecord) {
        data = [[[GTNetData alloc] init] autorelease];
        [data setDate:_date];
        
        [data setWifiSent:_WiFiSent];
        [data setWifiRev:_WiFiReceived];
        [data setWWANSent:_WWANSent];
        [data setWWANRev:_WWANReceived];
        
    }
    
    return data;
}


@end

static void GTReachabilityChanged(SCNetworkReachabilityRef target,
                                  SCNetworkConnectionFlags flags,
                                  void *info)
{
    //记下状态
    [[GTNetModel sharedInstance] updateNetworkStatusForFlags:flags];
    
}


void func_resetNetData()
{
    return [[GTNetModel sharedInstance] resetData];
}

#endif
