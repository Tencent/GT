//
//  GTParaInApi.h
//  GTKit
//
//  Created   on 13-2-21.
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
#import "GTParaInApiForOC.h"
#endif


#ifdef GT_DEBUG_DISABLE

//------------------------ DISABLE GT BEGIN ----------------------------

#define GT_IN_REGISTER(key,alias,value)
#define GT_IN_REGISTER_ARRAY(key,alias,value, n)

#define GT_IN_SET(key, writeToLog, value)

#define GT_IN_GET_BOOL(key, writeToLog, value)
#define GT_IN_GET_INT(key, writeToLog, value)
#define GT_IN_GET_DOUBLE(key, writeToLog, value)
#define GT_IN_GET_FLOAT(key, writeToLog, value)
#define GT_IN_GET_STR(key, writeToLog, value)

#define GT_IN_DEFAULT_ON_AC(key1,key2,key3)
#define GT_IN_DEFAULT_ON_DISABLED(...)
#define GT_IN_DEFAULT_ALL_ON_DISABLED
//------------------------ DISABLE GT END ------------------------------

#else
#include <stdbool.h>


//------------------------ FOR C Language BEGIN ------------------------


/**
 * @brief   注册输入参数 字符串
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param alias [const char *] key的别名(缩写), 要求控制在四个字符以内,超过则内部自动截取
 * @param ... [const char *] 输入参数对应初始值,支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //注册一个"是否需要动画"的输入参数，根据取值范围，对应使用GT_IN_GET_BOOL获取
 *    GT_IN_REGISTER("animated", "ANI", "0"); 
 *
 *    //注册一个"分片个数"的输入参数，根据取值范围，对应使用GT_IN_GET_INT获取
 *    GT_IN_REGISTER("pkgCount", "CNT", "5");
 *
 *     //注册一个"分片速度"的输入参数，根据取值范围，对应使用GT_IN_GET_DOUBLE获取
 *    GT_IN_REGISTER("pkgSpeed", "V", "15.6");
 *
 *    //注册一个"图片压缩比例"的输入参数，根据取值范围，对应使用GT_IN_GET_FLOAT获取
 *    GT_IN_REGISTER("jpegCompressQuality", "QUA", "0.8");
 *
 *    //注册一个"版本选择"的输入参数，根据取值范围，对应使用GT_IN_GET_STR获取
 *    GT_IN_REGISTER("version", "VER", "V1.0");
 * @endcode
 */
#define GT_IN_REGISTER(key,alias,...) func_addInputForString(key,alias,__VA_ARGS__)
extern void func_addInputForString(const char *key, const char *alias, const char *format,...);

/**
 * @brief   注册输入参数 数组
 * @ingroup GT输入参数使用说明
 * 
 * @param key [const char *] 标识输入参数的key
 * @param alias [const char *] key的别名(缩写), 要求控制在四个字符以内,超过则内部自动截取
 * @param a [char *] 数组指针
 * @param n [int] 数组个数
 * @return
 *
 * Example Usage:
 * @code
 *    char* a[] = {"black", "white", "blue"};
 *    GT_IN_REGISTER_ARRAY("Color", "COL", a, 3); //注册颜色数组
 * @endcode
 */
#define GT_IN_REGISTER_ARRAY(key,alias,a,n) func_addInputForArrayStr(key,alias,a, n)
extern void func_addInputForArrayStr(const char *key, const char *alias, char* a[], int n);

/**
 * @brief   设置输入参数值
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param ... [const char *]
 * @return
 *
 * Example Usage:
 * @code
 *    GT_IN_SET("Color", true, "white"); //注册颜色数组
 * @endcode
 */
#define GT_IN_SET(key, writeToLog, ...) func_setInputForString(key, writeToLog, __VA_ARGS__)
extern void func_setInputForString(const char *key, bool writeToLog, const char * format,...);

/**
 * @brief   获取输入参数bool值
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [bool] 为用户输入值，该值在用户关闭输入参数功能时生效
 * @return 返回bool值
 *
 * Example Usage:
 * @code
 *    //注册一个"是否需要动画"的输入参数，根据取值范围，对应使用GT_IN_GET_BOOL获取
 *    GT_IN_REGISTER("animated", "ANI", "0");
 *
 *    //获取注册输入参数的当前值
 *    bool value = GT_IN_GET_BOOL("animated", true, true);
 * @endcode
 */
#define GT_IN_GET_BOOL(key, writeToLog, value) func_getInputForBool(key, writeToLog, value)
extern bool   func_getInputForBool(const char *key, bool writeToLog, bool value);

/**
 * @brief   获取输入参数int值
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [int] 为用户输入值，该值在用户关闭输入参数功能时生效
 * @return 返回bool值
 *
 * Example Usage:
 * @code
 *    //注册一个"分片个数"的输入参数，根据取值范围，对应使用GT_IN_GET_INT获取
 *    GT_IN_REGISTER("pkgCount", "CNT", "5");
 *
 *    //获取注册输入参数的当前值
 *    int pkgCount = GT_IN_GET_INT("pkgCount", true, 3);
 * @endcode
 */
#define GT_IN_GET_INT(key, writeToLog, value) func_getInputForInt(key, writeToLog, value)
extern int func_getInputForInt(const char *key, bool writeToLog, int value);

/**
 * @brief   获取输入参数double值
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [double] 为用户输入值，该值在用户关闭输入参数功能时生效
 * @return 返回bool值
 *
 * Example Usage:
 * @code
 *     //注册一个"分片速度"的输入参数，根据取值范围，对应使用GT_IN_GET_DOUBLE获取
 *    GT_IN_REGISTER("pkgSpeed", "V", "15.6");
 *
 *    //获取注册输入参数的当前值
 *    double pkgSpeed = GT_IN_GET_DOUBLE("pkgSpeed", true, 15.6);
 * @endcode
 */
#define GT_IN_GET_DOUBLE(key, writeToLog, value) func_getInputForDouble(key, writeToLog, value)
extern double func_getInputForDouble(const char *key, bool writeToLog, double value);

/**
 * @brief   获取输入参数float值
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [float] 为用户输入值，该值在用户关闭输入参数功能时生效
 * @return 返回bool值
 *
 * Example Usage:
 * @code
 *    //注册一个"图片压缩比例"的输入参数，根据取值范围，对应使用GT_IN_GET_FLOAT获取
 *    GT_IN_REGISTER("jpegCompressQuality", "QUA", "0.8");
 *
 *    //获取注册输入参数的当前值
 *    float = GT_IN_GET_FLOAT("jpegCompressQuality", true, 0.8);
 * @endcode
 */
#define GT_IN_GET_FLOAT(key, writeToLog, value) func_getInputForFloat(key, writeToLog, value)
extern float  func_getInputForFloat(const char *key, bool writeToLog, float value);

/**
 * @brief   获取输入参数字符串值,返回char*类型
 * @ingroup GT输入参数使用说明
 *
 * @param key [const char *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [const char *] 为用户输入值，该值在用户关闭输入参数功能时生效
 * @return 返回bool值
 *
 * Example Usage:
 * @code
 *    //注册一个"版本选择"的输入参数，根据取值范围，对应使用GT_IN_GET_STR获取
 *    GT_IN_REGISTER("version", "VER", "V1.0");
 *
 *    //获取注册输入参数的当前值
 *    const char * version = GT_IN_GET_STR("version", true, "V1.0");
 * @endcode
 */
#define GT_IN_GET_STR(key, writeToLog, value) func_getInputForString(key, writeToLog, value)
extern const char*  func_getInputForString(const char *key, bool writeToLog, const char *value);

/**
 * @brief   默认在悬浮框上显示的输入参数
 * @ingroup GT输入参数使用说明
 *
 * @param key1 [const char *] 标识输入参数的key
 * @param key2 [const char *] 标识输入参数的key
 * @param key3 [const char *] 标识输入参数的key
 * @return
 *
 * Example Usage:
 * @code
 *    GT_IN_DEFAULT_ON_AC("Version", "Color", NULL); //设置Version和Color两个在悬浮框显示
 * @endcode
 */
#define GT_IN_DEFAULT_ON_AC(key1,key2,key3) func_defaultInputOnAC(key1,key2,key3)
extern void   func_defaultInputOnAC(const char *key1, const char *key2, const char *key3);

/**
 * @brief   key对应的输入参数生效或失效
 * @ingroup GT输入参数使用说明
 *
 * @param ... [const char *] 标识输入参数的key列表, 末尾必须填NULL
 * @return
 *
 * Example Usage:
 * @code
 *    GT_IN_DEFAULT_ON_DISABLED("ResendCount", NULL); //设置ResendCount失效
 * @endcode
 */
#define GT_IN_DEFAULT_ON_DISABLED(...)  func_defaultInputOnDisabled(__VA_ARGS__)
extern void func_defaultInputOnDisabled(const char * format,...);


/**
 * @brief   输入参数功能开关设置
 * @ingroup GT输入参数使用说明
 *
 * Example Usage:
 * @code
 *    //关闭输入参数设置功能
 *    GT_IN_DEFAULT_ALL_ON_DISABLED;
 * @endcode
 */
#define GT_IN_DEFAULT_ALL_ON_DISABLED func_defaultInputAllOnDisabled()
extern void func_defaultInputAllOnDisabled();

//------------------------ FOR C Language END ------------------------

#endif

