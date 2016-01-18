//
//  GTStyleDef.h
//  GTKit
//
//  Created   on 13-8-13.
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


/************************GT样式相关*************************/

#define M_GT_COLOR_WITH_HEX(hex)\
[UIColor colorWithRed:((float)((hex & 0xFF0000) >> 16))/255.0 \
green:((float)((hex & 0xFF00) >> 8))/255.0 \
blue:((float)(hex & 0xFF))/255.0 alpha:1.0]

#define M_GT_BKGD_COLOR [[UIColor blackColor] colorWithAlphaComponent:0.75]

#define M_GT_PLOTS_LINE_COLOR M_GT_COLOR_WITH_HEX(0x26C8D5)
#define M_GT_PLOTS_LINE1_COLOR M_GT_COLOR_WITH_HEX(0xE0A025)
#define M_GT_PLOTS_LINE2_COLOR M_GT_COLOR_WITH_HEX(0xD74882)

#define M_GT_PLOTS_AXIS_TEXT_COLOR M_GT_COLOR_WITH_HEX(0x878C98)
#define M_GT_PLOTS_AXIS_COLOR M_GT_COLOR_WITH_HEX(0x878C98)
#define M_GT_PLOTS_AXIS_SHADOW_COLOR [M_GT_COLOR_WITH_HEX(0x212222) colorWithAlphaComponent:0.75]
#define M_GT_PLOTS_AXIS_AVG_COLOR M_GT_COLOR_WITH_HEX(0x38AD29)

#define M_GT_LABEL_COLOR M_GT_COLOR_WITH_HEX(0x878C98)
#define M_GT_LABEL_VALUE_COLOR M_GT_COLOR_WITH_HEX(0x38AD29)
#define M_GT_LABEL_RED_COLOR M_GT_COLOR_WITH_HEX(0xEC3A3B)

#define M_GT_WARNING_COLOR M_GT_COLOR_WITH_HEX(0xDD843F)

#define M_GT_CELL_BKGD_COLOR M_GT_COLOR_WITH_HEX(0x29292D)
#define M_GT_CELL_BORDER_COLOR M_GT_COLOR_WITH_HEX(0x3C3C42)
#define M_GT_CELL_BORDER_WIDTH 1.0f
#define M_GT_CELL_TEXT_COLOR M_GT_COLOR_WITH_HEX(0xB7BDCF)
#define M_GT_CELL_TEXT_DISABLE_COLOR M_GT_COLOR_WITH_HEX(0x666666)

#define M_GT_SELECTED_COLOR M_GT_COLOR_WITH_HEX(0x3C4A76)

#define M_GT_NAV_BAR_COLOR M_GT_COLOR_WITH_HEX(0x3C3C42)
#define M_GT_NAV_BARLINE_COLOR M_GT_COLOR_WITH_HEX(0x1f1f22)

#define M_GT_BTN_HEIGHT     35.0f
#define M_GT_BTN_WIDTH      54.0f
#define M_GT_BTN_FONTSIZE   14.0f

#define M_GT_BTN_BKGD_COLOR M_GT_COLOR_WITH_HEX(0x35353B)
#define M_GT_BTN_BORDER_COLOR M_GT_COLOR_WITH_HEX(0x1C1C21)
#define M_GT_BTN_BORDER_WIDTH 1.0f

#define M_GT_TXT_FIELD_COLOR M_GT_COLOR_WITH_HEX(0x181818)
/***********************************************************/
