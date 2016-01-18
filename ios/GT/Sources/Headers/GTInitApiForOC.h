//
//  GTInitApiForOC.h
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

#ifdef __OBJC__

#define GT_DEBUG_SET_AUTOROTATE(autorotate)
#define GT_DEBUG_SET_SUPPORT_ORIENTATIONS(interfaceOrientation)
#endif

//------------------------ DISABLE GT END -------------------------------

#else
#import <Foundation/Foundation.h>

//------------------------ FOR OC Language BEGIN ------------------------

/**
 * @brief   设置logo是否旋转，在iOS6上使用
 * @ingroup GT启动使用说明
 *
 * @param autorotate [BOOL] 默认为true true:可旋转 false:不可旋转
 * @return
 *
 * Example Usage:
 * @code
 *    //设置logo是否旋转(iOS6使用)
 *    GT_DEBUG_SET_AUTOROTATE(false);
 * @endcode
 */
#define GT_DEBUG_SET_AUTOROTATE(autorotate) func_setGTAutorotate(autorotate)
FOUNDATION_EXPORT void func_setGTAutorotate(BOOL autorotate);

/**
 * @brief   设置logo支持的方向
 * @ingroup GT启动使用说明
 *
 * @param interfaceOrientation [NSUInteger] logo支持的方向，默认为UIInterfaceOrientationMaskAll
 * @return
 *
 * Example Usage:
 * @code
 *    //设置logo仅支持竖屏
 *    GT_DEBUG_SET_SUPPORT_ORIENTATIONS(UIInterfaceOrientationMaskPortrait);
 * @endcode
 */
#define GT_DEBUG_SET_SUPPORT_ORIENTATIONS(interfaceOrientation) func_setGTSupportedOrientations(interfaceOrientation)
FOUNDATION_EXPORT void func_setGTSupportedOrientations(NSUInteger interfaceOrientation);

//------------------------ FOR OC Language END ------------------------

#endif
