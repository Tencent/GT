//
//  GTParaOutApi.h
//  GTKit
//
//  Created by  on 13-2-21.
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
#import "GTParaOutApiForOC.h"
#endif

#ifdef GT_DEBUG_DISABLE

//------------------------ DISABLE GT BEGIN --------------------------

#define GT_OUT_GATHER_SWITCH_SET(on)
#define GT_OUT_MONITOR_INTERVAL_SET(interval)
#define GT_OUT_REGISTER(key,alias)
#define GT_OUT_GET(key,writeToLog)
#define GT_OUT_SET(key,writeToLog,...)
#define GT_OUT_WRITE_TO_LOG(key,writeToLog)
#define GT_OUT_HISTORY_CHECKED_SET(key,selected)
#define GT_OUT_WARNING_OUT_OF_RANGE_SET(key,lastingTime,lowerValue,upperValue)
#define GT_OUT_HISTORY_CLEAR(key)
#define GT_OUT_HISTORY_SAVE(key,fileName)
#define GT_OUT_HISTORY_ALL_SAVE(dirName)

#define GT_OUT_DEFAULT_ON_AC(key1,key2,key3)
#define GT_OUT_DEFAULT_ON_DISABLED(...)
#define GT_OUT_DEFAULT_ALL_ON_DISABLED

//------------------------ DISABLE GT END ----------------------------

#else

//------------------------ FOR C Language BEGIN ------------------------
/**
 * @brief   设置所有输出参数历史统计开关
 * @ingroup GT输出参数使用说明
 *
 * @param on [bool] 默认为false, false:关闭output历史统计 true:打开output历史统计
 * @return
 *
 * Example Usage:
 * @code
 *    //打开output历史统计
 *    GT_OUT_GATHER_SWITCH_SET(true);
 * @endcode
 */
#define GT_OUT_GATHER_SWITCH_SET(on) func_setGatherSwitch(on)
extern void func_setGatherSwitch(bool on);

/**
 * @brief   设置监控（CPU，MEM，NET）间隔
 * @ingroup GT输出参数使用说明
 *
 * @param interval [double] 间隔，单位为s，最小值：0.1(0.1s) 最大值:10(10s)
 * @return
 *
 * Example Usage:
 * @code
 *    //打开output历史统计
 *    GT_OUT_MONITOR_INTERVAL_SET(5);
 * @endcode
 */
#define GT_OUT_MONITOR_INTERVAL_SET(interval) func_setMonitorInterval(interval)
extern void func_setMonitorInterval(double interval);

/**
 * @brief   注册输出参数
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param alias [const char *] key的别名(缩写), 要求控制在四个字符以内,超过则内部自动截取
 * @return
 *
 * Example Usage:
 * @code
 *    //注册输出参数
 *    GT_OUT_REGISTER("fileTransferResult", "REST");
 * @endcode
 */
#define GT_OUT_REGISTER(key,alias) func_addOutputForString(key,alias)
extern void func_addOutputForString(const char *key, const char *alias);

/**
 * @brief   获取输出参数值
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @return
 *
 * Example Usage:
 * @code
 *    //获取输出参数
 *    const char* result = GT_OUT_GET("fileTransferResult", true);
 * @endcode
 */
#define GT_OUT_GET(key,writeToLog) func_getOutputForString(key,writeToLog)
extern const char* func_getOutputForString(const char *key, bool writeToLog);


/**
 * @brief   设置输出参数值
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param ... [const char *]
 * @return
 *
 * Example Usage:
 * @code
 *    //设置输出参数
 *    GT_OUT_SET("fileTransferResult", true, "%s: success", __FUNCTION__);
 * @endcode
 */
#define GT_OUT_SET(key,writeToLog,...) func_setOutputForString(key,writeToLog,__VA_ARGS__)
extern void func_setOutputForString(const char *key, bool writeToLog, const char * format,...);

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
#define GT_OUT_WRITE_TO_LOG(key,writeToLog) func_setOutputWriteToLog(key,writeToLog)
extern void func_setOutputWriteToLog(const char *key, bool writeToLog);

/**
 * @brief   设置是否选择改项记录输出参数历史信息
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param selected [bool] 是否选择改项记录输出参数历史信息 true:选择, false:不选择
 * @return
 *
 * Example Usage:
 * @code
 *    //设置是否选择改项记录输出参数历史信息
 *    GT_OUT_HISTORY_CHECKED_SET("fileTransferResult", true);
 * @endcode
 */
#define GT_OUT_HISTORY_CHECKED_SET(key,selected) func_setOutputHistoryChecked(key,selected)
extern void func_setOutputHistoryChecked(const char *key, bool selected);

/**
 * @brief   设置输出参数的告警的正常区间
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param lastingTime [double] 连续时间，单位秒，连续lastingTime时间值上下限值区间外则认为一次告警
 * @param lowerValue [double] 下限值
 * @param upperValue [double] 上限值
 * @return
 *
 * Example Usage:
 * @code
 *    //设置告警的正常区间，连续5秒达都不在区间[20,60]内则记录一次告警
 *    GT_OUT_WARNING_OUT_OF_RANGE_SET("App Smoothness", 5, 20, 60);
 * @endcode
 */
#define GT_OUT_WARNING_OUT_OF_RANGE_SET(key,lastingTime,lowerValue,upperValue) func_setWarningOutOfRange(key,lastingTime,lowerValue,upperValue)
extern void func_setWarningOutOfRange(const char *key, double lastingTime, double lowerValue, double upperValue);

/**
 * @brief   清除输出参数历史记录
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @return
 *
 * Example Usage:
 * @code
 *    //清除出参数据
 *    GT_OUT_HISTORY_CLEAR("App Smoothness");
 * @endcode
 */
#define GT_OUT_HISTORY_CLEAR(key) func_clearOutputHistory(key)
extern void func_clearOutputHistory(const char *key);


/**
 * @brief   保存输出参数历史记录
 * @ingroup GT输出参数使用说明
 *
 * @param key [const char *] 标识输出参数的key
 * @param fileName [const char *] 保存文件名
 * @return
 *
 * Example Usage:
 * @code
 *    //保存出参数据
 *    GT_OUT_HISTORY_SAVE("App Smoothness", "SM");
 * @endcode
 */
#define GT_OUT_HISTORY_SAVE(key,fileName) func_saveOutputHistory(key,fileName)
extern void func_saveOutputHistory(const char *key, const char *fileName);


/**
 * @brief   保存输出所有参数历史记录
 * @ingroup GT输出参数使用说明
 *
 * @param dirName [const char *] 保存文件夹
 * @return
 *
 * Example Usage:
 * @code
 *    //保存出参数据
 *    GT_OUT_HISTORY_ALL_SAVE("dir");
 * @endcode
 */
#define GT_OUT_HISTORY_ALL_SAVE(dirName) func_saveOutputHistoryAll(dirName)
extern void func_saveOutputHistoryAll(const char *dirName);


/**
 * @brief   默认在悬浮框上显示的输出参数
 * @ingroup GT输出参数使用说明
 *
 * @param key1 [const char *] 标识输出参数的key
 * @param key2 [const char *] 标识输出参数的key
 * @param key3 [const char *] 标识输出参数的key
 * @return
 *
 * Example Usage:
 * @code
 *    //设置fileTransferResult在悬浮框显示
 *    GT_OUT_DEFAULT_ON_AC("fileTransferResult", NULL, NULL);
 * @endcode
 */
#define GT_OUT_DEFAULT_ON_AC(key1,key2,key3) func_defaultOutputOnAC(key1,key2,key3)
extern void func_defaultOutputOnAC(const char *key1, const char *key2, const char *key3);


/**
 * @brief   key对应的输出参数生效或失效
 * @ingroup GT输出参数使用说明
 *
 * @param ... [const char *] 标识输出参数的key列表, 末尾必须填NULL
 * @return
 *
 * Example Usage:
 * @code
 *    GT_OUT_DEFAULT_ON_DISABLED("ResendCount", NULL); //设置ResendCount失效
 * @endcode
 */
#define GT_OUT_DEFAULT_ON_DISABLED(...) func_defaultOutputOnDisabled(__VA_ARGS__)
extern void func_defaultOutputOnDisabled(const char * format,...);



/**
 * @brief   输出参数功能开关设置
 * @ingroup GT输出参数使用说明
 *
 * Example Usage:
 * @code
 *    //关闭输出参数设置功能
 *    GT_OUT_DEFAULT_ALL_ON_DISABLED;
 * @endcode
 */
#define GT_OUT_DEFAULT_ALL_ON_DISABLED func_defaultOutputAllOnDisabled()
extern void func_defaultOutputAllOnDisabled();


//------------------------ FOR C Language END ------------------------


#endif

