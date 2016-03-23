//
//  GT.h
//  GTKit
//
//  Created by  on 13-11-15.
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

#include <GT/GTInitApi.h>
#include <GT/GTParaOutApi.h>
#include <GT/GTParaInApi.h>
#include <GT/GTLogApi.h>
#include <GT/GTProfilerApi.h>
#include <GT/GTCoreApi.h>
#include <GT/GTStyleDef.h>

#ifdef __OBJC__
#import <GT/GTPluginApiForOC.h>
#import <GT/GTPluginViewController.h>
#import <GT/UIScreen+Bounds.h>
#endif


/**
 * @addtogroup GT启动使用说明
 * @addtogroup GT日志使用说明
 * @addtogroup GTprofiler使用说明
 * @addtogroup GT输入参数使用说明
 * @addtogroup GT输出参数使用说明
 * @addtogroup GT工具能力使用说明
 * @addtogroup GT插件使用说明
 */


/*------------------------------------------------------------------------------
 Introduction - ADD GT
 
 1. drag GT.embeddedframework to project.
 
 2. include GT.h in file which want to use GT Kit and init GT.
 a1. if .m or .c file, use as follow:
 #include <GT/GT.h>
 
 a2. if .mm or .cpp file, use as follow:
 extern "C"
 {
 #include <GT/GT.h>
 }
 
 b. init GT as follow:
 GT_DEBUG_INIT;
 
 ------------------------------------------------------------------------------*/