//
//  GTProfilerApiForOC.h
//  GTKit
//
//  Created   on 13-10-12.
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

#ifdef GT_DEBUG_DISABLE

//------------------------ DISABLE GT BEGIN ----------------------------

#define GT_OC_TIME_START(group, ...)
#define GT_OC_TIME_END(group, ...) 0
#define GT_OC_TIME_GET(group, ...) 0

#define GT_OC_TIME_START_IN_THREAD(group, ...)
#define GT_OC_TIME_END_IN_THREAD(group, ...) 0

//------------------------ DISABLE GT END -------------------------------

#else


//------------------------ FOR OC Language BEGIN ------------------------

/**
 * @brief   开始时间统计(支持OC语法)
 * @ingroup GTprofiler使用说明
 *
 * @param group [NSString *] 时间统计项的分组
 * @param ... [NSString *] 时间统计项的描述信息key，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //时间统计的开始时刻调用
 *    GT_OC_TIME_START(@"URL", @"www.qq.com");
 * @endcode
 */
#define GT_OC_TIME_START(group, ...) func_startRecTimeForOC(group, __VA_ARGS__)
FOUNDATION_EXPORT void func_startRecTimeForOC(NSString * logKey, NSString * format,...);

/**
 * @brief   结束时间统计(支持OC语法)
 * @ingroup GTprofiler使用说明
 *
 * @param group [NSString *] 时间统计项的分组
 * @param ... [NSString *] 时间统计项的描述信息key，支持多参数输入
 * @return 返回从开始到结束的时间间隔，单位为秒
 *
 * Example Usage:
 * @code
 *    //时间统计的结束时刻调用, interval为从开始到结束的时间间隔，单位为秒
 *    NSTimeInterval interval = GT_OC_TIME_END(@"URL", @"www.qq.com");
 * @endcode
 */
#define GT_OC_TIME_END(group, ...) func_endRecTimeForOC(group, __VA_ARGS__)
FOUNDATION_EXPORT NSTimeInterval func_endRecTimeForOC(NSString * logKey, NSString * format,...);

/**
 * @brief   获取某时间统计项最新时间间隔值(支持OC语法)
 * @ingroup GTprofiler使用说明
 *
 * @param group [NSString *] 时间统计项的分组
 * @param ... [NSString *] 时间统计项的描述信息key，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //interval为统计项里最新时间间隔值，单位为秒
 *    NSTimeInterval interval = GT_OC_TIME_GET(@"URL", @"www.qq.com");
 * @endcode
 */
#define GT_OC_TIME_GET(group, ...) func_getRecTimeForOC(group, __VA_ARGS__)
FOUNDATION_EXPORT NSTimeInterval func_getRecTimeForOC(NSString* logKey, NSString* format,...);

/**
 * @brief   开始时间统计(区分线程)(支持OC语法)
 * @ingroup GTprofiler使用说明
 *
 * @param group [NSString *] 时间统计项的分组
 * @param ... [NSString *] 时间统计项的描述信息key，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //时间统计的开始时刻调用
 *    GT_OC_TIME_START_IN_THREAD(@"URL", @"www.qq.com");
 * @endcode
 */
#define GT_OC_TIME_START_IN_THREAD(group, ...) func_startRecTimeInThreadForOC(group, __VA_ARGS__)
FOUNDATION_EXPORT void func_startRecTimeInThreadForOC(NSString* logKey, NSString* format,...);

/**
 * @brief   结束时间统计(区分线程)(支持OC语法)
 * @ingroup GTprofiler使用说明
 *
 * @param group [NSString *] 时间统计项的分组
 * @param ... [NSString *] 时间统计项的描述信息key，支持多参数输入
 * @return 返回从开始到结束的时间间隔，单位为秒
 *
 * Example Usage:
 * @code
 *    //时间统计的结束时刻调用, interval为从开始到结束的时间间隔，单位为秒
 *    NSTimeInterval interval = GT_OC_TIME_END_IN_THREAD(@"URL", @"www.qq.com");
 * @endcode
 */
#define GT_OC_TIME_END_IN_THREAD(group, ...) func_endRecTimeInThreadForOC(group, __VA_ARGS__)
FOUNDATION_EXPORT NSTimeInterval func_endRecTimeInThreadForOC(NSString* logKey, NSString* format,...);

//------------------------ FOR OC Language END ------------------------



#endif
