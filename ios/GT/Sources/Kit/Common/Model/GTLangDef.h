//
//  GTLangDef.h
//  GTKit
//
//  Created   on 14-8-27.
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

#import <Foundation/Foundation.h>
#import "GTDebugDef.h"


//#define M_GT_GET_DSTRING(key)\
[[GTLang sharedInstance] getDString:key]

#define M_GT_LANG_EN @"cn"
#define M_GT_LANG_ZH @"zh"

#define M_GT_LOCALSTRING(key)\
[[NSBundle frameworkBundle] localizedStringForKey:(key) value:@"" table:@"GTStrings"]



#define M_GT_PARA_KEY       @"Para."
#define M_GT_PARA_EN        @"Para."
#define M_GT_PARA_ZH        @"参数"
//#define M_GT_PARA M_GT_DSTRING(M_GT_PARA_KEY)
//#define M_GT_PARA NSLocalizedStringFromTable(@"para",@"Localization",nil)
//#define M_GT_PARA NSLocalizedString(@"para",@"")

#define M_GT_PROFILER_KEY       @"Profiler"
#define M_GT_PROFILER_EN        @"Profiler"
#define M_GT_PROFILER_ZH        @"耗时"
//#define M_GT_PROFILER M_GT_DSTRING(M_GT_PROFILER_KEY)


#define M_GT_LOG_KEY       @"Log"
#define M_GT_LOG_EN        @"Log"
#define M_GT_LOG_ZH        @"日志"
//#define M_GT_LOG M_GT_DSTRING(M_GT_LOG_KEY)

#define M_GT_PLUGIN_KEY       @"Plugin"
#define M_GT_PLUGIN_EN        @"Plugin"
#define M_GT_PLUGIN_ZH        @"插件"
//#define M_GT_PLUGIN M_GT_DSTRING(M_GT_PLUGIN_KEY)

#define M_GT_SETTING_KEY       @"Setting"
#define M_GT_SETTING_EN        @"Setting"
#define M_GT_SETTING_ZH        @"设置"
//#define M_GT_SETTING M_GT_DSTRING(M_GT_SETTING_KEY)

#define M_GT_TIME_KEY       @"Time"
#define M_GT_TIME_EN        @"Time"
#define M_GT_TIME_ZH        @"时间"
//#define M_GT_TIME M_GT_DSTRING(M_GT_TIME_KEY)

#define M_GT_SECOND_KEY       @"s"
#define M_GT_SECOND_EN        @"s"
#define M_GT_SECOND_ZH        @"秒"
//#define M_GT_SECOND M_GT_DSTRING(M_GT_SECOND_KEY)


//PARA
#define M_GT_PARA_IN_KEY       @"In"
#define M_GT_PARA_IN_EN        @"In"
#define M_GT_PARA_IN_ZH        @"入参"
//#define M_GT_PARA_IN M_GT_DSTRING(M_GT_PARA_IN_KEY)

#define M_GT_PARA_OUT_KEY       @"Out"
#define M_GT_PARA_OUT_EN        @"Out"
#define M_GT_PARA_OUT_ZH        @"出参"
//#define M_GT_PARA_OUT M_GT_DSTRING(M_GT_PARA_OUT_KEY)


#define M_GT_PARA_DONE_KEY       @"Done"
#define M_GT_PARA_DONE_EN        @"Done"
#define M_GT_PARA_DONE_ZH        @"完成"
//#define M_GT_PARA_DONE M_GT_DSTRING(M_GT_PARA_DONE_KEY)

#define M_GT_PARA_EDIT_KEY       @"Edit"
#define M_GT_PARA_EDIT_EN        @"Edit"
#define M_GT_PARA_EDIT_ZH        @"编辑"
//#define M_GT_PARA_EDIT M_GT_DSTRING(M_GT_PARA_EDIT_KEY)

#define M_GT_PARA_IN_ITEMS_KEY       @"Input Para Items"
#define M_GT_PARA_IN_ITEMS_EN        @"Input Para Items"
#define M_GT_PARA_IN_ITEMS_ZH        @"入参列表"
//#define M_GT_PARA_IN_ITEMS M_GT_DSTRING(M_GT_PARA_IN_ITEMS_KEY)

#define M_GT_PARA_IN_CELL_TITLE_KEY       @"Input"
#define M_GT_PARA_IN_CELL_TITLE_EN        @"Input"
#define M_GT_PARA_IN_CELL_TITLE_ZH        @"入参"
//#define M_GT_PARA_IN_CELL_TITLE M_GT_DSTRING(M_GT_PARA_IN_CELL_TITLE_KEY)


#define M_GT_PARA_TOP_KEY       @"Top"
#define M_GT_PARA_TOP_EN        @"Top"
#define M_GT_PARA_TOP_ZH        @"置顶"
//#define M_GT_PARA_TOP M_GT_DSTRING(M_GT_PARA_TOP_KEY)


#define M_GT_PARA_DRAG_KEY       @"Drag"
#define M_GT_PARA_DRAG_EN        @"Drag"
#define M_GT_PARA_DRAG_ZH        @"拖拽"
//#define M_GT_PARA_DRAG M_GT_DSTRING(M_GT_PARA_DRAG_KEY)

//OUT PARA
#define M_GT_PARA_OUT_ITEMS_KEY       @"Output Para Items"
#define M_GT_PARA_OUT_ITEMS_EN        @"Output Para Items"
#define M_GT_PARA_OUT_ITEMS_ZH        @"出参列表"
//#define M_GT_PARA_OUT_ITEMS M_GT_DSTRING(M_GT_PARA_OUT_ITEMS_KEY)


#define M_GT_PARA_OUT_GW_KEY      @"Gather & Warning(G&W)"
#define M_GT_PARA_OUT_GW_EN       @"Gather & Warning(G&W)"
#define M_GT_PARA_OUT_GW_ZH       @"数据采集与告警操作栏(G&W)"
//#define M_GT_PARA_OUT_GW M_GT_DSTRING(M_GT_PARA_OUT_GW_KEY)



#define M_GT_PARA_FLOATING_KEY       @"Show on Air-console(≤3)"
#define M_GT_PARA_FLOATING_EN        @"Show on Air-console(≤3)"
#define M_GT_PARA_FLOATING_ZH        @"悬浮框展示的参数(≤3)"
//#define M_GT_PARA_FLOATING M_GT_DSTRING(M_GT_PARA_FLOATING_KEY)

#define M_GT_PARA_UNFLOATING_KEY       @"Optional Parameters"
#define M_GT_PARA_UNFLOATING_EN        @"Optional Parameters"
#define M_GT_PARA_UNFLOATING_ZH        @"已关注的参数"
//#define M_GT_PARA_UNFLOATING M_GT_DSTRING(M_GT_PARA_UNFLOATING_KEY)

#define M_GT_PARA_DISABLE_KEY       @"Disabled Parameters"
#define M_GT_PARA_DISABLE_EN        @"Disabled Parameters"
#define M_GT_PARA_DISABLE_ZH        @"已取消关注的参数"
//#define M_GT_PARA_DISABLE M_GT_DSTRING(M_GT_PARA_DISABLE_KEY)

#define M_GT_PARA_FLOATING_ZERO_KEY       @"Empty"
#define M_GT_PARA_FLOATING_ZERO_EN        @"Empty"
#define M_GT_PARA_FLOATING_ZERO_ZH        @"空"
//#define M_GT_PARA_FLOATING_ZERO M_GT_DSTRING(M_GT_PARA_FLOATING_ZERO_KEY)

#define M_GT_PARA_FLOATING_INFO_KEY       @"Please drag para here"
#define M_GT_PARA_FLOATING_INFO_EN        @"Please drag para here"
#define M_GT_PARA_FLOATING_INFO_ZH        @"请拖动此处"
//#define M_GT_PARA_FLOATING_INFO M_GT_DSTRING(M_GT_PARA_FLOATING_INFO_KEY)

//warning
#define M_GT_WARNING_TITLE_DISABLED_KEY       @"Warning(Disabled)"
#define M_GT_WARNING_TITLE_DISABLED_EN        @"Warning(Disabled)"
#define M_GT_WARNING_TITLE_DISABLED_ZH        @"告警区(停用)"
//#define M_GT_WARNING_TITLE_DISABLED M_GT_DSTRING(M_GT_WARNING_TITLE_DISABLED_KEY)


#define M_GT_WARNING_TITLE_ENABLE_KEY       @"Warning(Enable)"
#define M_GT_WARNING_TITLE_ENABLE_EN        @"Warning(Enable)"
#define M_GT_WARNING_TITLE_ENABLE_ZH        @"告警区(可用)"
//#define M_GT_WARNING_TITLE_ENABLE M_GT_DSTRING(M_GT_WARNING_TITLE_ENABLE_KEY)



#define M_GT_WARNING_COUNT_KEY       @"Warning Count"
#define M_GT_WARNING_COUNT_EN        @"Warning Count"
#define M_GT_WARNING_COUNT_ZH        @"告警次数"
//#define M_GT_WARNING_CNT M_GT_DSTRING(M_GT_WARNING_COUNT_KEY)

#define M_GT_WARNING_TIME_KEY       @"Lasting Time(s)"
#define M_GT_WARNING_TIME_EN        @"Lasting Time(s)"
#define M_GT_WARNING_TIME_ZH        @"时间(s)"
//#define M_GT_WARNING_TIME M_GT_DSTRING(M_GT_WARNING_TIME_KEY)

#define M_GT_WARNING_RANGE_KEY       @"Out Of Range"
#define M_GT_WARNING_RANGE_EN        @"Out Of Range"
#define M_GT_WARNING_RANGE_ZH        @"告警阈值"
//#define M_GT_WARNING_RANGE M_GT_DSTRING(M_GT_WARNING_RANGE_KEY)


//alert
#define M_GT_ALERT_CLEAR_TITLE_KEY       @"Clear"
#define M_GT_ALERT_CLEAR_TITLE_EN        @"Clear"
#define M_GT_ALERT_CLEAR_TITLE_ZH        @"清理"
//#define M_GT_ALERT_CLEAR_TITLE M_GT_DSTRING(M_GT_ALERT_CLEAR_TITLE_KEY)

#define M_GT_ALERT_CLEAR_INFO_KEY       @"Clear the datas"
#define M_GT_ALERT_CLEAR_INFO_EN        @"Clear the datas"
#define M_GT_ALERT_CLEAR_INFO_ZH        @"清理数据？"
//#define M_GT_ALERT_CLEAR_INFO M_GT_DSTRING(M_GT_ALERT_CLEAR_INFO_KEY)


#define M_GT_ALERT_SAVE_TITLE_KEY       @"Save"
#define M_GT_ALERT_SAVE_TITLE_EN        @"Save"
#define M_GT_ALERT_SAVE_TITLE_ZH        @"保存"
//#define M_GT_ALERT_SAVE_TITLE M_GT_DSTRING(M_GT_ALERT_SAVE_TITLE_KEY)

#define M_GT_ALERT_UPLOAD_TITLE_KEY       @"Upload"
#define M_GT_ALERT_UPLOAD_TITLE_EN        @"Upload"
#define M_GT_ALERT_UPLOAD_TITLE_ZH        @"上传"


#define M_GT_ALERT_INPUT_SAVED_FILE_KEY       @"Please input the saved file name"
#define M_GT_ALERT_INPUT_SAVED_FILE_EN        @"Please input the saved file name"
#define M_GT_ALERT_INPUT_SAVED_FILE_ZH        @"请输入您要保存的文件名"
//#define M_GT_ALERT_INPUT_SAVED_FILE M_GT_DSTRING(M_GT_ALERT_INPUT_SAVED_FILE_KEY)

#define M_GT_ALERT_FILE_EXIT_KEY       @"File Exist"
#define M_GT_ALERT_FILE_EXIT_EN        @"File Exist"
#define M_GT_ALERT_FILE_EXIT_ZH        @"文件已存在"
//#define M_GT_ALERT_FILE_EXIT M_GT_DSTRING(M_GT_ALERT_FILE_EXIT_KEY)


#define M_GT_ALERT_OK_KEY       @"OK"
#define M_GT_ALERT_OK_EN        @"OK"
#define M_GT_ALERT_OK_ZH        @"确定"
//#define M_GT_ALERT_OK M_GT_DSTRING(M_GT_ALERT_OK_KEY)

#define M_GT_ALERT_START_KEY       @"Start"
#define M_GT_ALERT_START_EN        @"Start"
#define M_GT_ALERT_START_ZH        @"开始"
//#define M_GT_ALERT_START M_GT_DSTRING(M_GT_ALERT_START_KEY)


#define M_GT_ALERT_STOP_KEY       @"Stop"
#define M_GT_ALERT_STOP_EN        @"Stop"
#define M_GT_ALERT_STOP_ZH        @"停止"
//#define M_GT_ALERT_STOP M_GT_DSTRING(M_GT_ALERT_STOP_KEY)


#define M_GT_ALERT_EXIT_KEY       @"Exit"
#define M_GT_ALERT_EXIT_EN        @"Exit"
#define M_GT_ALERT_EXIT_ZH        @"退出"
//#define M_GT_ALERT_EXIT M_GT_DSTRING(M_GT_ALERT_EXIT_KEY)



#define M_GT_ALERT_PREPARING_KEY       @"Preparing"
#define M_GT_ALERT_PREPARING_EN        @"Preparing"
#define M_GT_ALERT_PREPARING_ZH        @"准备"
//#define M_GT_ALERT_PREPARING M_GT_DSTRING(M_GT_ALERT_PREPARING_KEY)





#define M_GT_ALERT_CANCEL_KEY       @"Cancel"
#define M_GT_ALERT_CANCEL_EN        @"Cancel"
#define M_GT_ALERT_CANCEL_ZH        @"取消"
//#define M_GT_ALERT_CANCEL M_GT_DSTRING(M_GT_ALERT_CANCEL_KEY)


#define M_GT_ALERT_BACK_KEY       @"Back"
#define M_GT_ALERT_BACK_EN        @"Back"
#define M_GT_ALERT_BACK_ZH        @"返回"
//#define M_GT_ALERT_BACK M_GT_DSTRING(M_GT_ALERT_BACK_KEY)

#define M_GT_ALERT_SEND_KEY       @"Send"
#define M_GT_ALERT_SEND_EN        @"Send"
#define M_GT_ALERT_SEND_ZH        @"发送"
//#define M_GT_ALERT_SEND M_GT_DSTRING(M_GT_ALERT_SEND_KEY)

#define M_GT_ALERT_SENDING_KEY       @"Sending"
#define M_GT_ALERT_SENDING_EN        @"Sending"
#define M_GT_ALERT_SENDING_ZH        @"发送中"
//#define M_GT_ALERT_SENDING M_GT_DSTRING(M_GT_ALERT_SENDING_KEY)

#define M_GT_ALERT_SEND_SUCCESS_KEY       @"Send Success!"
#define M_GT_ALERT_SEND_SUCCESS_EN        @"Send Success!"
#define M_GT_ALERT_SEND_SUCCESS_ZH        @"发送成功"
//#define M_GT_ALERT_SEND_SUCCESS M_GT_DSTRING(M_GT_ALERT_SEND_SUCCESS_KEY)

#define M_GT_ALERT_SEND_ERROR_KEY       @"Send Error!"
#define M_GT_ALERT_SEND_ERROR_EN        @"Send Error!"
#define M_GT_ALERT_SEND_ERROR_ZH        @"发送失败"
//#define M_GT_ALERT_SEND_ERROR M_GT_DSTRING(M_GT_ALERT_SEND_ERROR_KEY)


#define M_GT_ALERT_FILE_NAME_EMPTY_KEY       @"File Name empty"
#define M_GT_ALERT_FILE_NAME_EMPTY_EN       @"File Name empty"
#define M_GT_ALERT_FILE_NAME_EMPTY_ZH        @"文件名为空"
//#define M_GT_ALERT_FILE_NAME_EMPTY M_GT_DSTRING(M_GT_ALERT_FILE_NAME_EMPTY_KEY)


#define M_GT_ALERT_INPUT_FILE_KEY       @"Please input file name"
#define M_GT_ALERT_INPUT_FILE_EN        @"Please input file name"
#define M_GT_ALERT_INPUT_FILE_ZH        @"请输入文件名"
//#define M_GT_ALERT_INPUT_FILE M_GT_DSTRING(M_GT_ALERT_INPUT_FILE_KEY)


#define M_GT_ALERT_PW_KEY       @"Passwd"
#define M_GT_ALERT_PW_EN        @"Passwd"
#define M_GT_ALERT_PW_ZH        @"密码"
//#define M_GT_ALERT_PW M_GT_DSTRING(M_GT_ALERT_PW_KEY)




#define M_GT_ALERT_INPUT_ROOT_PW_KEY       @"Please input root passwd"
#define M_GT_ALERT_INPUT_ROOT_PW_EN        @"Please input root passwd"
#define M_GT_ALERT_INPUT_ROOT_PW_ZH        @"请输入root密码"
//#define M_GT_ALERT_INPUT_ROOT_PW M_GT_DSTRING(M_GT_ALERT_INPUT_ROOT_PW_KEY)


#define M_GT_ALERT_REMAINDER_KEY       @"Reminder"
#define M_GT_ALERT_REMAINDER_EN        @"Reminder"
#define M_GT_ALERT_REMAINDER_ZH        @"密码"
//#define M_GT_ALERT_REMAINDER M_GT_DSTRING(M_GT_ALERT_REMAINDER_KEY)

#define M_GT_ALERT_INPUT_1000_WORD_KEY       @"You have input 1000 words"
#define M_GT_ALERT_INPUT_1000_WORD_EN        @"You have input 1000 words"
#define M_GT_ALERT_INPUT_1000_WORD_ZH        @"必须输入1000字"
//#define M_GT_ALERT_INPUT_1000_WORD M_GT_DSTRING(M_GT_ALERT_INPUT_1000_WORD_KEY)


#define M_GT_ALERT_I_KNOW_KEY       @"I know"
#define M_GT_ALERT_I_KNOW_EN        @"I know"
#define M_GT_ALERT_I_KNOW_ZH        @"确定"
//#define M_GT_ALERT_I_KNOW M_GT_DSTRING(M_GT_ALERT_I_KNOW_KEY)

#define M_GT_ALERT_NEW_GT_KEY       @"New GT available"
#define M_GT_ALERT_NEW_GT_EN        @"New GT available"
#define M_GT_ALERT_NEW_GT_ZH        @"有新版本可用"
//#define M_GT_ALERT_NEW_GT M_GT_DSTRING(M_GT_ALERT_NEW_GT_KEY)


//profiler
#define M_GT_TIME_TITLE_KEY       @"Detail Information"
#define M_GT_TIME_TITLE_EN        @"Detail Information"
#define M_GT_TIME_TITLE_ZH        @"详情"
//#define M_GT_TIME_TITLE M_GT_DSTRING(M_GT_TIME_TITLE_KEY)

#define M_GT_TIME_COUNT_KEY       @"Counts"
#define M_GT_TIME_COUNT_EN        @"Counts"
#define M_GT_TIME_COUNT_ZH        @"次数"
//#define M_GT_TIME_COUNT M_GT_DSTRING(M_GT_TIME_COUNT_KEY)

#define M_GT_TIME_TOTAL_KEY       @"Sum"
#define M_GT_TIME_TOTAL_EN        @"Sum"
#define M_GT_TIME_TOTAL_ZH        @"合计"
//#define M_GT_TIME_TOTAL M_GT_DSTRING(M_GT_TIME_TOTAL_KEY)

#define M_GT_TIME_AVG_KEY       @"Avg"
#define M_GT_TIME_AVG_EN        @"Avg"
#define M_GT_TIME_AVG_ZH        @"平均值"
//#define M_GT_TIME_AVG M_GT_DSTRING(M_GT_TIME_AVG_KEY)

#define M_GT_TIME_MAX_KEY       @"Max"
#define M_GT_TIME_MAX_EN        @"Max"
#define M_GT_TIME_MAX_ZH        @"最大值"
//#define M_GT_TIME_MAX M_GT_DSTRING(M_GT_TIME_MAX_KEY)

#define M_GT_TIME_MIN_KEY       @"Min"
#define M_GT_TIME_MIN_EN        @"Min"
#define M_GT_TIME_MIN_ZH        @"最小值"
//#define M_GT_TIME_MIN M_GT_DSTRING(M_GT_TIME_MIN_KEY)

#define M_GT_TIME_DETAIL_TITLE_KEY       @"Detail"
#define M_GT_TIME_DETAIL_TITLE_EN        @"Detail"
#define M_GT_TIME_DETAIL_TITLE_ZH        @"详情"
//#define M_GT_TIME_DETAIL_TITLE M_GT_DSTRING(M_GT_TIME_DETAIL_TITLE_KEY)

#define M_GT_PROFILER_NOT_START_KEY       @"Not Start Profiling"
#define M_GT_PROFILER_NOT_START_EN        @"Not Start Profiling"
#define M_GT_PROFILER_NOT_START_ZH        @"耗时统计未启动"
//#define M_GT_PROFILER_NOT_START M_GT_DSTRING(M_GT_PROFILER_NOT_START_KEY)

#define M_GT_PROFILER_START_KEY       @"Start"
#define M_GT_PROFILER_START_EN        @"Start"
#define M_GT_PROFILER_START_ZH        @"启动"
//#define M_GT_PROFILER_START M_GT_DSTRING(M_GT_PROFILER_START_KEY)

#define M_GT_PROFILER_STOP_KEY       @"End"
#define M_GT_PROFILER_STOP_EN        @"End"
#define M_GT_PROFILER_STOP_ZH        @"停止"
//#define M_GT_PROFILER_STOP M_GT_DSTRING(M_GT_PROFILER_STOP_KEY)


#define M_GT_PROFILER_COUNTING_KEY       @"Profiling..."
#define M_GT_PROFILER_COUNTING_EN        @"Profiling..."
#define M_GT_PROFILER_COUNTING_ZH        @"分析中"
//#define M_GT_PROFILER_COUNTING M_GT_DSTRING(M_GT_PROFILER_COUNTING_KEY)






//core

#define M_GT_CORE_COUNT_KEY       @"Counts"
#define M_GT_CORE_COUNT_EN        @"Counts"
#define M_GT_CORE_COUNT_ZH        @"次数"
//#define M_GT_CORE_COUNT M_GT_DSTRING(M_GT_CORE_COUNT_KEY)

#define M_GT_CORE_TOTAL_KEY       @"Sum"
#define M_GT_CORE_TOTAL_EN        @"Sum"
#define M_GT_CORE_TOTAL_ZH        @"合计"
//#define M_GT_CORE_TOTAL M_GT_DSTRING(M_GT_CORE_TOTAL_KEY)

#define M_GT_CORE_AVG_KEY       @"Avg"
#define M_GT_CORE_AVG_EN        @"Avg"
#define M_GT_CORE_AVG_ZH        @"平均值"
//#define M_GT_CORE_AVG M_GT_DSTRING(M_GT_CORE_AVG_KEY)

#define M_GT_CORE_MAX_KEY       @"Max"
#define M_GT_CORE_MAX_EN        @"Max"
#define M_GT_CORE_MAX_ZH        @"最大值"
//#define M_GT_CORE_MAX M_GT_DSTRING(M_GT_CORE_MAX_KEY)

#define M_GT_CORE_MIN_KEY       @"Min"
#define M_GT_CORE_MIN_EN        @"Min"
#define M_GT_CORE_MIN_ZH        @"最小值"
//#define M_GT_CORE_MIN M_GT_DSTRING(M_GT_CORE_MIN_KEY)



//log
#define M_GT_LOG_FILTER_BY_MSG_KEY       @"by message"
#define M_GT_LOG_FILTER_BY_MSG_EN        @"by message"
#define M_GT_LOG_FILTER_BY_MSG_ZH        @"按文本过滤"
//#define M_GT_LOG_FILTER_BY_MSG M_GT_DSTRING(M_GT_LOG_FILTER_BY_MSG_KEY)

#define M_GT_LOG_FILTER_ALL_KEY         @"ALL"
#define M_GT_LOG_FILTER_ALL_EN        @"ALL"
#define M_GT_LOG_FILTER_ALL_ZH        @"ALL"
//#define M_GT_LOG_FILTER_ALL M_GT_DSTRING(M_GT_LOG_FILTER_ALL_KEY)


#define M_GT_LOG_FILTER_BY_TAG_KEY         @"TAG"
#define M_GT_LOG_FILTER_BY_TAG_EN        @"TAG"
#define M_GT_LOG_FILTER_BY_TAG_ZH        @"TAG"
//#define M_GT_LOG_FILTER_BY_TAG M_GT_DSTRING(M_GT_LOG_FILTER_BY_TAG_KEY)

#define M_GT_LOG_SEARCH_KW_INFO_KEY          @"input keyword here"
#define M_GT_LOG_SEARCH_KW_INFO_EN        @"input keyword here"
#define M_GT_LOG_SEARCH_KW_INFO_ZH        @"请输入搜索关键字"
//#define M_GT_LOG_SEARCH_KW_INFO M_GT_DSTRING(M_GT_LOG_SEARCH_KW_INFO_KEY)


//setting
#define M_GT_SETTING_AC_KEY               @"AC Setting"
#define M_GT_SETTING_AC_EN        @"AC Setting"
#define M_GT_SETTING_AC_ZH        @"悬浮框"
//#define M_GT_SETTING_AC M_GT_DSTRING(M_GT_SETTING_AC_KEY)

#define M_GT_SETTING_LOG_KEY       @"Log Setting"
#define M_GT_SETTING_LOG_EN        @"Log Setting"
#define M_GT_SETTING_LOG_ZH        @"日志"
//#define M_GT_SETTING_LOG M_GT_DSTRING(M_GT_SETTING_LOG_KEY)

#define M_GT_SETTING_PARA_KEY       @"Para Setting"
#define M_GT_SETTING_PARA_EN        @"Para Setting"
#define M_GT_SETTING_PARA_ZH        @"参数"
//#define M_GT_SETTING_PARA M_GT_DSTRING(M_GT_SETTING_PARA_KEY)


#define M_GT_SETTING_ABOUT_KEY       @"About GT"
#define M_GT_SETTING_ABOUT_EN        @"About GT"
#define M_GT_SETTING_ABOUT_ZH        @"关于 GT"
//#define M_GT_SETTING_ABOUT M_GT_DSTRING(M_GT_SETTING_ABOUT_KEY)

#define M_GT_SETTING_AC_SHOW_KEY       @"AC Show"
#define M_GT_SETTING_AC_SHOW_EN       @"AC Show"
#define M_GT_SETTING_AC_SHOW_ZH        @"显示悬浮框"
//#define M_GT_SETTING_AC_SHOW M_GT_DSTRING(M_GT_SETTING_AC_SHOW_KEY)

#define M_GT_SETTING_AC_Q_SWITCH_KEY       @"AC Quick Switch"
#define M_GT_SETTING_AC_Q_SWITCH_EN       @"AC Quick Switch"
#define M_GT_SETTING_AC_Q_SWITCH_ZH        @"快速切换"
//#define M_GT_SETTING_AC_Q_SWITCH M_GT_DSTRING(M_GT_SETTING_AC_Q_SWITCH_KEY)

#define M_GT_SETTING_AC_GW_KEY       @"G&W"
#define M_GT_SETTING_AC_GW_EN       @"G&W"
#define M_GT_SETTING_AC_GW_ZH        @"G&W"
//#define M_GT_SETTING_AC_GW M_GT_DSTRING(M_GT_SETTING_AC_GW_KEY)



#define M_GT_SETTING_LOG_SWITCH_KEY       @"Switch"
#define M_GT_SETTING_LOG_SWITCH_EN       @"Switch"
#define M_GT_SETTING_LOG_SWITCH_ZH        @"开关"
//#define M_GT_SETTING_LOG_SWITCH M_GT_DSTRING(M_GT_SETTING_LOG_SWITCH_KEY)


#define M_GT_SETTING_LOG_AUTO_SAVE_KEY       @"Auto Save"
#define M_GT_SETTING_LOG_AUTO_SAVE_EN       @"Auto Save"
#define M_GT_SETTING_LOG_AUTO_SAVE_ZH        @"自动保存"
//#define M_GT_SETTING_LOG_AUTO_SAVE M_GT_DSTRING(M_GT_SETTING_LOG_AUTO_SAVE_KEY)


#define M_GT_SETTING_ABOUT_FEEDBACK_KEY       @"Feedback"
#define M_GT_SETTING_ABOUT_FEEDBACK_EN       @"Feedback"
#define M_GT_SETTING_ABOUT_FEEDBACK_ZH        @"反馈"
//#define M_GT_SETTING_ABOUT_FEEDBACK M_GT_DSTRING(M_GT_SETTING_ABOUT_FEEDBACK_KEY)


#define M_GT_SETTING_ABOUT_SCORE_KEY       @"Lightly drag stars to score"
#define M_GT_SETTING_ABOUT_SCORE_EN       @"Lightly drag stars to score"
#define M_GT_SETTING_ABOUT_SCORE_ZH        @"评分"
//#define M_GT_SETTING_ABOUT_SCORE M_GT_DSTRING(M_GT_SETTING_ABOUT_SCORE_KEY)


//plugin
#define M_GT_PLUGIN_SANDBOX_KEY       @"Sandbox"
#define M_GT_PLUGIN_SANDBOX_EN       @"Sandbox"
#define M_GT_PLUGIN_SANDBOX_ZH        @"沙箱"
//#define M_GT_PLUGIN_SANDBOX M_GT_DSTRING(M_GT_PLUGIN_SANDBOX_KEY)


#define M_GT_PLUGIN_UPLOAD_KEY       @"GTUpload"
#define M_GT_PLUGIN_UPLOAD_EN       @"GTUpload"
#define M_GT_PLUGIN_UPLOAD_ZH        @"数据上传"

#define M_GT_PLUGIN_UPLOAD_INFO_KEY       @"upload the data gathered by GT"
#define M_GT_PLUGIN_UPLOAD_INFO_EN       @"upload the data gathered by GT"
#define M_GT_PLUGIN_UPLOAD_INFO_ZH        @"上传GT采集的数据"

#define M_GT_PLUGIN_UPLOAD_CLEAR_INFO_KEY       @"Clear History Data"
#define M_GT_PLUGIN_UPLOAD_CLEAR_INFO_EN        @"Clear History Data"
#define M_GT_PLUGIN_UPLOAD_CLEAR_INFO_ZH        @"清除历史数据"

#define M_GT_PLUGIN_UPLOAD_CLEAR_KEY       @"Clear"
#define M_GT_PLUGIN_UPLOAD_CLEAR_EN        @"Clear"
#define M_GT_PLUGIN_UPLOAD_CLEAR_ZH        @"清除"

#define M_GT_PLUGIN_UPLOAD_PARA_KEY       @"Path Para."
#define M_GT_PLUGIN_UPLOAD_PARA_EN        @"Path Para."
#define M_GT_PLUGIN_UPLOAD_PARA_ZH        @"路径参数"


#define M_GT_PLUGIN_CAP_KEY       @"GTPcap"
#define M_GT_PLUGIN_CAP_EN       @"GTPcap"
#define M_GT_PLUGIN_CAP_ZH        @"抓包"
//#define M_GT_PLUGIN_CAP M_GT_DSTRING(M_GT_PLUGIN_CAP_KEY)


#define M_GT_PLUGIN_CAP_INFO_KEY       @"capture the network traffic by tcpdump"
#define M_GT_PLUGIN_CAP_INFO_EN       @"capture the network traffic by tcpdump"
#define M_GT_PLUGIN_CAP_INFO_ZH        @"通过tcpdump捕获网络流量"
//#define M_GT_PLUGIN_CAP_INFO M_GT_DSTRING(M_GT_PLUGIN_CAP_INFO_KEY)


#define M_GT_PLUGIN_CAP_CAPTURE_KEY       @"capture"
#define M_GT_PLUGIN_CAP_CAPTURE_EN        @"capture"
#define M_GT_PLUGIN_CAP_CAPTURE_ZH        @"抓取"
//#define M_GT_PLUGIN_CAP_CAPTURE M_GT_DSTRING(M_GT_PLUGIN_CAP_CAPTURE_KEY)


#define M_GT_PLUGIN_CAP_CAPTURE_ERROR_KEY       @"Capture Error"
#define M_GT_PLUGIN_CAP_CAPTURE_ERROR_EN       @"Capture Error"
#define M_GT_PLUGIN_CAP_CAPTURE_ERROR_ZH        @"抓取错误"
//#define M_GT_PLUGIN_CAP_CAPTURE_ERROR M_GT_DSTRING(M_GT_PLUGIN_CAP_CAPTURE_ERROR_KEY)

#define M_GT_PLUGIN_CAP_JAILBREAK_INFO_KEY       @"Need iOS6 jailbreak system."
#define M_GT_PLUGIN_CAP_JAILBREAK_INFO_EN       @"Need iOS6 jailbreak system."
#define M_GT_PLUGIN_CAP_JAILBREAK_INFO_ZH        @"需要越狱iOS6系统"
//#define M_GT_PLUGIN_CAP_JAILBREAK_INFO M_GT_DSTRING(M_GT_PLUGIN_CAP_JAILBREAK_INFO_KEY)

#define M_GT_PARA_MONITOR_INTERVAL_KEY       @"Monitor Interval"
#define M_GT_PARA_MONITOR_INTERVAL_EN       @"Monitor Interval"
#define M_GT_PARA_MONITOR_INTERVAL_ZH        @"监听间隔"
//#define M_GT_PARA_MONITOR_INTERVAL M_GT_DSTRING(M_GT_PARA_MONITOR_INTERVAL_KEY)

#define M_GT_PARA_GATHER_DURATION_KEY       @"Gather Duration"
#define M_GT_PARA_GATHER_DURATION_EN       @"Gather Duration"
#define M_GT_PARA_GATHER_DURATION_ZH        @"收集时间"
//#define M_GT_PARA_GATHER_DURATION M_GT_DSTRING(M_GT_PARA_GATHER_DURATION_KEY)



#endif
