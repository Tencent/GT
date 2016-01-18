//
//  GTLogApiForOC.h
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

//------------------------ DISABLE GT BEGIN ---------------------------
#define GT_OC_LOG_D(tag,...)
#define GT_OC_LOG_I(tag,...)
#define GT_OC_LOG_W(tag,...)
#define GT_OC_LOG_E(tag,...)

#define GT_OC_LOG_CLEAN(...)
#define GT_OC_LOG_START(...)
#define GT_OC_LOG_END(...)

//------------------------ DISABLE GT END ------------------------------

#else

//------------------------ FOR OC Language BEGIN ------------------------
/**
 * @brief   DEBUG级别日志输出(支持OC语法)
 * @ingroup GT日志使用说明
 *
 * @param tag [NSString *] 日志的分类
 * @param ... [NSString *] 日志的信息，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //输出日志
 *    GT_OC_LOG_D(@"tagFun", @"%s %u", __FUNCTION__, __LINE__);
 * @endcode
 */
#define GT_OC_LOG_D(tag,...) func_logDebugForOC(tag,__VA_ARGS__)
FOUNDATION_EXPORT void func_logDebugForOC( NSString* tag, NSString * format, ... );

/**
 * @brief   INFO级别日志输出(支持OC语法)
 * @ingroup GT日志使用说明
 *
 * @param tag [NSString *] 日志的分类
 * @param ... [NSString *] 日志的信息，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //输出日志
 *    GT_OC_LOG_I(@"tagFun", @"%s %u", __FUNCTION__, __LINE__);
 * @endcode
 */
#define GT_OC_LOG_I(tag,...) func_logInfoForOC(tag,__VA_ARGS__)
FOUNDATION_EXPORT void func_logInfoForOC( NSString* tag, NSString * format, ... );

/**
 * @brief   WARNING级别日志输出(支持OC语法)
 * @ingroup GT日志使用说明
 *
 * @param tag [NSString *] 日志的分类
 * @param ... [NSString *] 日志的信息，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //输出日志
 *    GT_OC_LOG_W(@"tagFun", @"%s %u", __FUNCTION__, __LINE__);
 * @endcode
 */
#define GT_OC_LOG_W(tag,...) func_logWarningForOC(tag,__VA_ARGS__)
FOUNDATION_EXPORT void func_logWarningForOC( NSString* tag, NSString * format, ... );

/**
 * @brief   ERROR级别日志输出(支持OC语法)
 * @ingroup GT日志使用说明
 *
 * @param tag [NSString *] 日志的分类
 * @param ... [NSString *] 日志的信息，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //输出日志
 *    GT_OC_LOG_E(@"tagFun", @"%s %u", __FUNCTION__, __LINE__);
 * @endcode
 */
#define GT_OC_LOG_E(tag,...) func_logErrorForOC(tag,__VA_ARGS__)
FOUNDATION_EXPORT void func_logErrorForOC( NSString* tag, NSString * format, ... );

/**
 * @brief   清除日志(支持OC语法)
 * @details 文件所在的目录对应为../Documents/GT/Log/
 * @ingroup GT日志使用说明
 *
 * @param ... [NSString *] 文件名，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //清除日志
 *    GT_OC_LOG_CLEAN(@"file1");
 * @endcode
 */
#define GT_OC_LOG_CLEAN(...) func_logCleanForOC(__VA_ARGS__)
FOUNDATION_EXPORT void func_logCleanForOC(NSString * format,...);

/**
 * @brief   开始记录日志(支持OC语法)
 * @details 文件所在的目录对应为../Documents/GT/Log/
 * @ingroup GT日志使用说明
 *
 * @param ... [NSString *] 文件名，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //开始记录日志
 *    GT_OC_LOG_START(@"file1");
 * @endcode
 */
#define GT_OC_LOG_START(...) func_logStartForOC(__VA_ARGS__)
FOUNDATION_EXPORT void func_logStartForOC(NSString * format,...);

/**
 * @brief   停止记录日志(支持OC语法)
 * @details 文件所在的目录对应为../Documents/GT/Log/
 * @ingroup GT日志使用说明
 *
 * @param ... [NSString *] 文件名，支持多参数输入
 * @return
 *
 * Example Usage:
 * @code
 *    //开始记录日志
 *    GT_OC_LOG_END(@"file1");
 * @endcode
 */
#define GT_OC_LOG_END(...)   func_logEndForOC(__VA_ARGS__)
FOUNDATION_EXPORT void func_logEndForOC(NSString * format,...);

//------------------------ FOR OC Language END ------------------------

#endif
