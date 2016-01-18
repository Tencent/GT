//
//  GTParaOutApiForOC.h
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

#define M_GT_LOWER_WARNING_INVALID 0xFFFFFF
#define M_GT_UPPER_WARNING_INVALID 0xFFFFFF

#ifdef GT_DEBUG_DISABLE

//------------------------ DISABLE GT BEGIN ------------------------

#define GT_OC_OUT_REGISTER(key,alias)
#define GT_OC_OUT_GET(key,writeToLog)
#define GT_OC_OUT_SET(key,writeToLog,...)
#define GT_OC_OUT_WRITE_TO_LOG(key,writeToLog)
#define GT_OC_OUT_VC_SET(key,vc)
#define GT_OC_OUT_HISTORY_CHECKED_SET(key,selected)
#define GT_OC_OUT_WARNING_OUT_OF_RANGE_SET(key,lastingTime,lowerValue,upperValue)
#define GT_OC_OUT_HISTORY_CLEAR(key)
#define GT_OC_OUT_HISTORY_SAVE(key,fileName)
#define GT_OC_OUT_HISTORY_SAVE_ALL(dirName)
#define GT_OC_OUT_DEFAULT_ON_AC(key1,key2,key3)
#define GT_OC_OUT_DEFAULT_ON_DISABLED(...)
#define GT_OC_OUT_DELEGATE_SET(key,delegate)
//------------------------ DISABLE GT END ------------------------

#else

#import <GT/GTParaDelegate.h>

//------------------------ FOR OC Language BEGIN ------------------------
/**
 * @brief   注册输出参数(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param alias [NSString *] key的别名(缩写), 要求控制在四个字符以内,超过则内部自动截取
 * @return
 *
 * Example Usage:
 * @code
 *    //注册输出参数
 *    GT_OC_OUT_REGISTER(@"fileTransferResult", @"REST");
 * @endcode
 */
#define GT_OC_OUT_REGISTER(key,alias) func_addOutputForOC(key,alias)
FOUNDATION_EXPORT void func_addOutputForOC(NSString *key, NSString *alias);

/**
 * @brief   获取输出参数值(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param writeToLog [BOOL] 该操作是否需要记录日志 YES:记录一条日志, NO:不记录日志
 * @return
 *
 * Example Usage:
 * @code
 *    //获取输出参数
 *    NSString* result = GT_OC_OUT_GET(@"fileTransferResult", YES);
 * @endcode
 */
#define GT_OC_OUT_GET(key,writeToLog) func_getOutputForOC(key,writeToLog)
FOUNDATION_EXPORT NSString* func_getOutputForOC(NSString *key, BOOL writeToLog);

/**
 * @brief   设置输出参数值(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param writeToLog [BOOL] 该操作是否需要记录日志 YES:记录一条日志, NO:不记录日志
 * @param ... [NSString *]
 * @return
 *
 * Example Usage:
 * @code
 *    //设置输出参数
 *    GT_OC_OUT_SET(@"fileTransferResult", YES, @"%s: success", __FUNCTION__);
 * @endcode
 */
#define GT_OC_OUT_SET(key,writeToLog,...) func_setOutputForOC(key,writeToLog,__VA_ARGS__)
FOUNDATION_EXPORT void func_setOutputForOC(NSString *key, BOOL writeToLog, NSString * format,...);

/**
 * @brief   设置输出参数值是否在日志展示
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @return
 *
 * Example Usage:
 * @code
 *    //设置输出参数展示在LOG上
 *    GT_OUT_WRITE_TO_LOG("App Smoothness", true);
 * @endcode
 */
#define GT_OC_OUT_WRITE_TO_LOG(key,writeToLog) func_setOutputWriteToLogForOC(key,writeToLog)
extern void func_setOutputWriteToLogForOC(NSString *key, BOOL writeToLog);


/**
 * @brief   设置输出参数值(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param vc  [NSString *] 点击输出参数进入自定义的UIViewController的类名，nsstring类型
 * @return
 *
 * Example Usage:
 * @code
 *    //设置输出参数的二级页面，NEWUIViewController为UIViewController的类名
 *    GT_OC_OUT_VC_SET(@"fileTransferResult", @"NEWUIViewController");
 * @endcode
 */
#define GT_OC_OUT_VC_SET(key,vc) func_setOutputVCForOC(key,vc)
FOUNDATION_EXPORT void func_setOutputVCForOC(NSString *key, NSString *vc);

/**
 * @brief   设置是否选择改项记录输出参数历史信息(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param selected [BOOL] 是否选择改项记录输出参数历史信息 YES:选择, NO:不选择
 * @return
 *
 * Example Usage:
 * @code
 *    //设置是否选择改项记录输出参数历史信息
 *    GT_OC_OUT_HISTORY_CHECKED_SET(@"fileTransferResult", YES);
 * @endcode
 */
#define GT_OC_OUT_HISTORY_CHECKED_SET(key,selected) func_setOutputHistoryCheckedForOC(key,selected)
FOUNDATION_EXPORT void func_setOutputHistoryCheckedForOC(NSString *key, BOOL selected);

/**
 * @brief   设置输出参数的告警的正常区间(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param lastingTime [NSTimeInterval] 连续时间，单位秒，连续lastingTime时间值上下限值区间外则认为一次告警
 * @param lowerValue [double] 下限值
 * @param upperValue [double] 上限值
 * @return
 *
 * Example Usage:
 * @code
 *    //设置告警的正常区间，连续5秒达都不在区间[20,60]内则记录一次告警
 *    GT_OC_OUT_WARNING_OUT_OF_RANGE_SET(@"App Smoothness", 5, 20, 60);
 * @endcode
 */

#define GT_OC_OUT_WARNING_OUT_OF_RANGE_SET(key,lastingTime,lowerValue,upperValue) func_setWarningOutOfRangeForOC(key,lastingTime,lowerValue,upperValue)
FOUNDATION_EXPORT void func_setWarningOutOfRangeForOC(NSString *key, NSTimeInterval lastingTime, double lowerValue, double upperValue);

/**
 * @brief   清除输出参数历史记录(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @return
 *
 * Example Usage:
 * @code
 *    //清除出参数据
 *    GT_OC_OUT_HISTORY_CLEAR(@"App Smoothness");
 * @endcode
 */
#define GT_OC_OUT_HISTORY_CLEAR(key) func_clearOutputHistoryForOC(key)
FOUNDATION_EXPORT void func_clearOutputHistoryForOC(NSString *key);

/**
 * @brief   保存输出参数历史记录(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param fileName [NSString *] 保存文件名
 * @return
 *
 * Example Usage:
 * @code
 *    //保存出参数据
 *    GT_OC_OUT_HISTORY_SAVE(@"App Smoothness", @"SM");
 * @endcode
 */
#define GT_OC_OUT_HISTORY_SAVE(key,fileName) func_saveOutputHistoryForOC(key,fileName)
FOUNDATION_EXPORT void func_saveOutputHistoryForOC(NSString *key, NSString *fileName);

/**
 * @brief   保存输出参数历史记录(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param dirName [NSString *] 保存目录名
 * @return
 *
 * Example Usage:
 * @code
 *    //保存出参数据
 *    GT_OC_OUT_HISTORY_SAVE_ALL(@"testCase");
 * @endcode
 */
#define GT_OC_OUT_HISTORY_SAVE_ALL(dirName) func_saveOutputHistoryAllForOC(dirName)
FOUNDATION_EXPORT void func_saveOutputHistoryAllForOC(NSString *dirName);

/**
 * @brief   默认在悬浮框上显示的输出参数(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key1 [NSString *] 标识输出参数的key
 * @param key2 [NSString *] 标识输出参数的key
 * @param key3 [NSString *] 标识输出参数的key
 * @return
 *
 * Example Usage:
 * @code
 *    //设置fileTransferResult在悬浮框显示
 *    GT_OC_OUT_DEFAULT_ON_AC(@"fileTransferResult", nil, nil);
 * @endcode
 */
#define GT_OC_OUT_DEFAULT_ON_AC(key1,key2,key3) func_defaultOutputOnACForOC(key1,key2,key3)
FOUNDATION_EXPORT void func_defaultOutputOnACForOC(NSString *key1, NSString *key2, NSString *key3);

/**
 * @brief   key对应的输出参数生效或失效(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param ... [NSString *] 标识输出参数的key列表, 末尾必须填nil
 * @return
 *
 * Example Usage:
 * @code
 *    GT_OC_OUT_DEFAULT_ON_DISABLED(@"ResendCount", nil); //设置ResendCount失效
 * @endcode
 */
#define GT_OC_OUT_DEFAULT_ON_DISABLED(...) func_defaultOutputOnDisabledForOC(__VA_ARGS__)
FOUNDATION_EXPORT void func_defaultOutputOnDisabledForOC(NSString * format,...);


/**
 * @brief   设置输出参数的delegate(支持OC语法)
 * @ingroup GT输出参数使用说明
 *
 * @param key [NSString *] 标识输出参数的key
 * @param delegate [id<GTParaDelegate>] 需要实现GTParaDelegate协议
 * @return
 *
 * Example Usage:
 * @code
 *    //设置输出参数的delegate，用于切换enabled区和disabled区的处理
 *    GT_OC_OUT_DELEGATE_SET("App Smoothness", nil);
 * @endcode
 */

#define GT_OC_OUT_DELEGATE_SET(key,delegate) func_setParaDelegateForOC(key,delegate)
FOUNDATION_EXPORT void func_setParaDelegateForOC(NSString *key, id<GTParaDelegate> delegate);

//------------------------ FOR OC Language END ------------------------

#endif
