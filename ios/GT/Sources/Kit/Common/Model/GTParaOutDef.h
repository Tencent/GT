//
//  GTParaOutDef.h
//  GTKit
//
//  Created   on 13-11-22.
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

#import "GTParaDelegate.h"


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

#endif
