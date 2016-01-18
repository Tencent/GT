//
//  GTCoreApi.h
//  GTKit
//
//  Created   on 13-2-28.
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

#ifdef __OBJC__
#import "GTCoreApiForOC.h"
#endif

#ifdef GT_DEBUG_DISABLE

//------------------------ DISABLE GT BEGIN ---------------------------

#define GT_UTIL_GET_CPU_USAGE
#define GT_UTIL_GET_USED_MEM
#define GT_UTIL_GET_FREE_MEM
#define GT_UTIL_GET_APP_MEM
#define GT_UTIL_RESET_NET_DATA

#define GT_UTIL_CURRENT_CAPACITY


//------------------------ DISABLE GT END -----------------------------


#else


#import <sys/types.h>

typedef enum {
    GTCaptureStatusPreparing = 0,
	GTCaptureStatusError,
    GTCaptureStatusing
} GTCaptureStatus;

//------------------------ FOR C Language BEGIN ------------------------

/**
 * @brief   获取当前CPU值
 * @ingroup GT工具能力使用说明
 *
 * @return 返回CPU占用值(<1) 返回0.45表示CPU占用45%
 *
 * Example Usage:
 * @code
 *    GT_LOG_D("UTIL","cpuUsage:%f", GT_UTIL_GET_CPU_USAGE);
 * @endcode
 */
#define GT_UTIL_GET_CPU_USAGE func_cpuUsage()
extern double func_cpuUsage();

/**
 * @brief   获取当前memory占用情况
 * @ingroup GT工具能力使用说明
 *
 * @return 返回memory占用值，单位为Byte
 *
 * Example Usage:
 * @code
 *    GT_LOG_D("UTIL","usedMemory:%u", GT_UTIL_GET_USED_MEM);
 * @endcode
 */
#define GT_UTIL_GET_USED_MEM func_getUsedMemory()
extern int64_t func_getUsedMemory();

/**
 * @brief   获取当前App memory占用情况
 * @ingroup GT工具能力使用说明
 *
 * @return 返回App memory占用值，单位为Byte
 *
 * Example Usage:
 * @code
 *    GT_LOG_D("UTIL","AppUsedMemory:%u", GT_UTIL_GET_APP_MEM);
 * @endcode
 */
#define GT_UTIL_GET_APP_MEM func_getAppMemory()
extern int64_t func_getAppMemory();

/**
 * @brief   获取当前memory空闲情况
 * @ingroup GT工具能力使用说明
 *
 * @return 返回memory空闲值，单位为Byte
 *
 * Example Usage:
 * @code
 *    GT_LOG_D("UTIL","freeMemory:%u", GT_UTIL_GET_FREE_MEM);
 * @endcode
 */
#define GT_UTIL_GET_FREE_MEM func_getFreeMemory()
extern int64_t func_getFreeMemory();


/**
 * @brief   清除网络数据
 * @ingroup GT工具能力使用说明
 * @return
*
 * Example Usage:
 * @code
 *    GT_UTIL_RESET_NET_DATA;
 * @endcode
 */
#define GT_UTIL_RESET_NET_DATA func_resetNetData()
extern void func_resetNetData();


/**
 * @brief   获取当前电量信息
 * @ingroup GT工具能力使用说明
 *
 * @return 返回currentCapacity,单位mAh
 *
 * Example Usage:
 * @code
 *    int currentCapacity = GT_UTIL_CURRENT_CAPACITY;
 * @endcode
 */
#define GT_UTIL_CURRENT_CAPACITY func_currentCapacity()
extern int func_currentCapacity();


//------------------------ FOR C Language END ------------------------


#endif

