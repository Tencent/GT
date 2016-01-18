//
//  GTNotificationDef.h
//  GTKit
//
//  Created   on 13-11-20.
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

#define M_GT_NOTIFICATION_LIST_UPDATE       @"GT_LIST_UPDATE"       //对于影响到AC展示的数据需要发送该通知，用于AC初始化后列表更新
#define M_GT_NOTIFICATION_AC_UPDATE         @"GT_AC_UPDATE"         //AC定时刷新出参通知
#define M_GT_NOTIFICATION_LOG_MOD           @"GT_LOG_MOD"           //定时更新日志展示
#define M_GT_NOTIFICATION_PROFILER_MOD      @"GT_PROFILER_MOD"      //Profiler数据更新通知
#define M_GT_NOTIFICATION_NSLOG_MOD         @"GT_NSLOG_MOD"         //NSLog变化时通知
#define M_GT_NOTIFICATION_OUT_OBJ_WARNING   @"GT_OUT_OBJ_WARNING"   //出参对象告警变化信息通知
#define M_GT_NOTIFICATION_OUT_LST_WARNING   @"GT_OUT_LST_WARNING"   //出参队列告警信息变化通知

#define M_GT_NOTIFICATION_OUT_GW_UPDATE     @"GT_OUT_GW_UPDATE"     //采集状态变化时通知
#define M_GT_NOTIFICATION_OUT_PARA          @"GT_OUT_PARA"          //定期刷新出参通知
#define M_GT_NOTIFICATION_OUT_ALL_SEL       @"GT_OUT_ALL_SEL"       //全部勾选/不勾选点击时通知
#define M_GT_NOTIFICATION_OUT_CELL_SEL      @"GT_OUT_CELL_SEL"      //cell勾选/不勾选点击时通知

#endif
