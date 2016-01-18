//
//  GTNetModel.h
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

#import <Foundation/Foundation.h>
#import <SystemConfiguration/SystemConfiguration.h>
#import "GTDebugDef.h"
#import "GTCoreModel.h"
#import "GTOutputList.h"
#import "GTParaOutDef.h"

typedef enum {
	GTNotReachable = 0,
	GTReachableViaWiFi,
	GTReachableViaWWAN
} GTNetStatus;

@interface GTNetData : GTHistroyValue
{
    int64_t    _wifiSent;
    int64_t    _wifiRev;
    int64_t    _WWANSent;
    int64_t    _WWANRev;
}

@property (nonatomic, assign) int64_t wifiSent;
@property (nonatomic, assign) int64_t wifiRev;
@property (nonatomic, assign) int64_t WWANSent;
@property (nonatomic, assign) int64_t WWANRev;

@end

@interface GTNetModel : NSObject <GTParaDelegate>
{
    SCNetworkReachabilityRef _reachability;
	SCNetworkReachabilityContext _context;
    GTNetStatus          _networkStatus;
	
    NSTimeInterval      _date;
    
    BOOL         _prevValid;
    int64_t      _prevWiFiSent;
    int64_t      _prevWiFiReceived;
    int64_t      _prevWWANSent;
    int64_t      _prevWWANReceived;
    
    int64_t      _WiFiSent;
    int64_t      _WiFiReceived;
    int64_t      _WWANSent;
    int64_t      _WWANReceived;
    
    char         _netInfo[128];   //记录上一次的数据，对于NET历史数据，只保存变化的，如果没有变化，历史数据不保存
    BOOL         _newRecord; //标识是否为新记录，判断新记录的标准为当前netInfo是否和之前的netInfo有变化
}

M_GT_AS_SINGLETION( GTNetModel );

@property (nonatomic) GTNetStatus netStatus;

@property (nonatomic, assign) NSTimeInterval  date;

@property (nonatomic, readonly) int64_t prevWiFiSent;
@property (nonatomic, readonly) int64_t prevWiFiReceived;
@property (nonatomic, readonly) int64_t prevWWANSent;
@property (nonatomic, readonly) int64_t prevWWANReceived;

@property (nonatomic, readonly) int64_t WiFiSent;
@property (nonatomic, readonly) int64_t WiFiReceived;
@property (nonatomic, readonly) int64_t WWANSent;
@property (nonatomic, readonly) int64_t WWANReceived;

- (void)handleTick;

- (void)updateNetData;
- (void)resetData;
- (GTNetStatus)currentNetStatus;

@end
#endif
