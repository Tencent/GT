//
//  GTDebugDef.h
//  GTKit
//
//  Created   on 13-10-29.
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

#import "GT.h"
#import "GTImage.h"
#import "GTNotificationDef.h"
#import "GTAlertDef.h"

/************************释放相关*************************/
#undef	M_GT_SAFE_RELEASE_VIEW
#define M_GT_SAFE_RELEASE_VIEW( __x ) if (__x)\
{ \
[__x release]; \
__x = nil; \
}

#undef	M_GT_SAFE_RELEASE_SUBVIEW
#define M_GT_SAFE_RELEASE_SUBVIEW( __x ) if (__x)\
{ \
[__x removeFromSuperview]; \
[__x release]; \
__x = nil; \
}

#undef	M_GT_SAFE_FREE
#define M_GT_SAFE_FREE(p) if (p) {\
[p release];\
p = nil;\
}

/************************校验相关*************************/
#define M_GT_LOG_SWITCH_CHECK_EX(err) if ([[GTLogConfig sharedInstance] logSwitch] == NO) {\
return err;\
}

#define M_GT_PTR_NULL_CHECK_EX(ptr, err) if ( !ptr ) {\
return err;\
}

#define M_GT_LOG_SWITCH_CHECK if ([[GTLogConfig sharedInstance] logSwitch] == NO) {\
return;\
}

#define M_GT_PTR_NULL_CHECK(ptr) if ( !ptr ) {\
return;\
}

/************************fomat相关*************************/
#define M_GT_OC_FORMAT_INIT \
va_list args;\
va_start(args,format);

#define M_GT_OC_FORMAT_STR \
[[[NSString alloc] initWithFormat:format arguments:args] autorelease];\
va_end(args);

#define M_GT_FORMAT_INIT \
static char buffer[1024];\
va_list args;\
va_start( args, format );\
vsnprintf( buffer, 1023, format, args );

#define M_GT_FORMAT_STR \
[NSString stringWithCString:buffer encoding:NSUTF8StringEncoding];\
va_end( args );


#pragma mark -

/************************屏幕尺寸相关*************************/
#if 0
//Portrait状态下屏幕宽度
#define M_GT_FULL_SCREEN_WIDTH ([UIScreen mainScreen].bounds.size.width)

//Portrait状态下屏幕高度
#define M_GT_FULL_SCREEN_HEIGHT ([UIScreen mainScreen].bounds.size.height)

//Portrait状态下屏幕宽度（不含电池条）
#define M_GT_SCREEN_WIDTH ([[UIScreen mainScreen] applicationFrame].size.width)

//Portrait状态下屏幕高度（不含电池条）
#define M_GT_SCREEN_HEIGHT ([[UIScreen mainScreen] applicationFrame].size.height)

#else // navy modified

//Portrait状态下屏幕宽度
#define M_GT_FULL_SCREEN_WIDTH ([UIScreen mainScreen].fullScreenBounds.size.width)

//Portrait状态下屏幕高度
#define M_GT_FULL_SCREEN_HEIGHT ([UIScreen mainScreen].fullScreenBounds.size.height)

//Portrait状态下屏幕宽度（不含电池条）
#define M_GT_SCREEN_WIDTH ([[UIScreen mainScreen] screenBounds].size.width)

//Portrait状态下屏幕高度（不含电池条）
#define M_GT_SCREEN_HEIGHT ([[UIScreen mainScreen] screenBounds].size.height)
#endif

//顶部标题栏的高度
#define M_GT_HEADER_HEIGHT 44
#define M_GT_NAVBAR_HEIGHT 43

//底部选项卡的高度
#define M_GT_TARBAR_HEIGHT 44

//二级页面 TAB不做隐藏
#define M_GT_BOARD_HEIGHT (M_GT_SCREEN_HEIGHT - M_GT_HEADER_HEIGHT - M_GT_TARBAR_HEIGHT)
#define M_GT_BOARD_FRAME CGRectMake(0, M_GT_HEADER_HEIGHT, M_GT_SCREEN_WIDTH, M_GT_BOARD_HEIGHT)

#define M_GT_BOARD_FRAME_6_0 CGRectMake(0, M_GT_HEADER_HEIGHT, M_GT_SCREEN_WIDTH, M_GT_SCREEN_HEIGHT - M_GT_TARBAR_HEIGHT)
#define M_GT_BOARD_FRAME_7_0 CGRectMake(0, M_GT_HEADER_HEIGHT, M_GT_SCREEN_WIDTH, M_GT_FULL_SCREEN_HEIGHT - M_GT_TARBAR_HEIGHT)

//一级页面 去除HEADER和TARBAR
#define M_GT_APP_HEIGHT (M_GT_SCREEN_HEIGHT - M_GT_HEADER_HEIGHT - M_GT_TARBAR_HEIGHT)
#define M_GT_APP_FRAME CGRectMake(0, M_GT_HEADER_HEIGHT, M_GT_SCREEN_WIDTH, M_GT_APP_HEIGHT)
/***********************************************************/


/************************屏幕显示相关*************************/
#define M_GT_UIDEVICE_IS_PORTRAIT(orientation) ((orientation == UIInterfaceOrientationPortrait) || (orientation == UIInterfaceOrientationPortraitUpsideDown))

/***********************************************************/

#define M_GT_K_B 1000.0
#define M_GT_KB 1024.0
#define M_GT_MB (1024.0 * M_GT_KB)
#define M_GT_GB (1024.0 * M_GT_MB)


#define M_GT_TAG @"GTsys"
#define M_GT_NIL @"(nil)"

#define M_GT_SIZE_32        32
#define M_GT_SIZE_64        64
#define M_GT_SIZE_128       128
#define M_GT_SIZE_256       256
#define M_GT_SIZE_1024      1024

#endif
