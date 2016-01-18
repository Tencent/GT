//
//  GTParaInApiForOC.h
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

#define GT_OC_IN_REGISTER(key,alias,valueArray)
#define GT_OC_IN_SET(key, writeToLog, value)
#define GT_OC_IN_GET(key, writeToLog, value) value
#define GT_OC_IN_DEFAULT_ON_AC(key1,key2,key3)
#define GT_OC_IN_DEFAULT_ON_DISABLED(...)

//------------------------ DISABLE GT END ------------------------------

#else

//------------------------ FOR OC Language BEGIN ------------------------


/**
 * @brief   注册输入参数(支持OC语法)
 * @ingroup GT输入参数使用说明
 *
 * @param key [NSString *] 标识输入参数的key
 * @param alias [NSString *] key的别名(缩写), 要求控制在四个字符以内,超过则内部自动截取
 * @param valueArray [NSArray *] 输入参数对应初始值,要求类型为NSArray
 * @return
 *
 * Example Usage:
 * @code
 *    //注册一个"重发次数"的输入参数
 *    NSArray *array = [NSArray arrayWithObjects:@"1", @"2", @"3", nil];
 *    GT_OC_IN_REGISTER(@"ResendCount", @"CNT", array);
 * @endcode
 */
#define GT_OC_IN_REGISTER(key,alias,valueArray) func_addInputForOC(key,alias,valueArray)
FOUNDATION_EXPORT void func_addInputForOC(NSString* key, NSString *alias, NSArray *array);

/**
 * @brief   设置输入参数值(支持OC语法)
 * @ingroup GT输入参数使用说明
 *
 * @param key [NSString *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [id] 输入参数值
 * @return
 *
 * Example Usage:
 * @code
 *    //设置输入参数值
 *    GT_OC_IN_SET(@"ResendCount", YES, @"1");
 * @endcode
 */
#define GT_OC_IN_SET(key, writeToLog, value) func_setInputForOC(key, writeToLog, value)
FOUNDATION_EXPORT void func_setInputForOC(NSString* key, bool writeToLog, id value);

/**
 * @brief   获取输入参数值(支持OC语法)
 * @ingroup GT输入参数使用说明
 *
 * @param key [NSString *] 标识输入参数的key
 * @param writeToLog [bool] 该操作是否需要记录日志 true:记录一条日志, false:不记录日志
 * @param value [id] 为用户输入值，该值在用户关闭输入参数功能时生效
 * @return 返回bool值
 *
 * Example Usage:
 * @code
 *    //获取当前输入参数的值
 *    NSString *count = GT_OC_IN_GET(@"ResendCount", YES, @"3");
 * @endcode
 */
#define GT_OC_IN_GET(key, writeToLog, value) func_getInputForOC(key, writeToLog, value)
FOUNDATION_EXPORT id func_getInputForOC(NSString* key, BOOL writeToLog, id value);

/**
 * @brief   默认在悬浮框上显示的输入参数(支持OC语法)
 * @ingroup GT输入参数使用说明
 *
 * @param key1 [NSString *] 标识输入参数的key
 * @param key2 [NSString *] 标识输入参数的key
 * @param key3 [NSString *] 标识输入参数的key
 * @return
 *
 * Example Usage:
 * @code
 *    GT_OC_IN_DEFAULT_ON_AC(@"ResendCount", nil, nil); //设置ResendCount在悬浮框显示
 * @endcode
 */
#define GT_OC_IN_DEFAULT_ON_AC(key1,key2,key3) func_defaultInputOnACForOC(key1,key2,key3)
FOUNDATION_EXPORT void func_defaultInputOnACForOC(NSString *key1, NSString *key2, NSString *key3);

/**
 * @brief   key对应的输入参数生效或失效(支持OC语法)
 * @ingroup GT输入参数使用说明
 *
 * @param ... [NSString *] 标识输入参数的key列表, 末尾必须填nil
 * @return
 *
 * Example Usage:
 * @code
 *    GT_OC_IN_DEFAULT_ON_DISABLED(@"ResendCount", nil); //设置ResendCount失效
 * @endcode
 */
#define GT_OC_IN_DEFAULT_ON_DISABLED(...) func_defaultInputOnDisabledForOC(__VA_ARGS__)
FOUNDATION_EXPORT void func_defaultInputOnDisabledForOC(NSString * format,...);

//------------------------ FOR OC Language END ------------------------


#endif
