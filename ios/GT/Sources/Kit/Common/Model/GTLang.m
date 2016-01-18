//
//  GTLang.m
//  GTKit
//
//  Created  on 14-8-27.
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

#import "GTLang.h"
#import "GTLangDef.h"

@implementation GTLang

M_GT_DEF_SINGLETION(GTLang);

@synthesize curLanguage = _curLanguage;
@synthesize zhDStringList = _zhDStringList;
@synthesize enDStringList = _enDStringList;

- (id)init
{
    self = [super init];
    if (self) {
        if (nil == _curLanguage) {
            _curLanguage  = [self getCurLanguage];
        }
        _zhDStringList = [[GTList alloc] init];
        _enDStringList = [[GTList alloc] init];
        [self initLanguage];
    }
    return self;
}

- (void)dealloc
{
    [_zhDStringList release];
    [_enDStringList release];

    [super dealloc];
}

- (void)initLanguage
{
    //para
    [self setDString:M_GT_PARA_KEY forStringEn:M_GT_PARA_EN forStringZh:M_GT_PARA_ZH];
    [self setDString:M_GT_PROFILER_KEY forStringEn:M_GT_PROFILER_EN forStringZh:M_GT_PROFILER_ZH];
    [self setDString:M_GT_LOG_KEY forStringEn:M_GT_LOG_EN forStringZh:M_GT_LOG_ZH];
    [self setDString:M_GT_PLUGIN_KEY forStringEn:M_GT_PLUGIN_EN forStringZh:M_GT_PLUGIN_ZH];
    [self setDString:M_GT_SETTING_KEY forStringEn:M_GT_SETTING_EN forStringZh:M_GT_SETTING_ZH];
    [self setDString:M_GT_TIME_KEY forStringEn:M_GT_TIME_EN forStringZh:M_GT_TIME_ZH];
    [self setDString:M_GT_SECOND_KEY forStringEn:M_GT_SECOND_EN forStringZh:M_GT_SECOND_ZH];
    
    
    
    //para
    
    [self setDString:M_GT_PARA_IN_KEY forStringEn:M_GT_PARA_IN_EN forStringZh:M_GT_PARA_IN_ZH];
    [self setDString:M_GT_PARA_OUT_KEY forStringEn:M_GT_PARA_OUT_EN forStringZh:M_GT_PARA_OUT_ZH];
    
    [self setDString:M_GT_PARA_DONE_KEY forStringEn:M_GT_PARA_DONE_EN forStringZh:M_GT_PARA_DONE_ZH];
    [self setDString:M_GT_PARA_EDIT_KEY forStringEn:M_GT_PARA_EDIT_EN forStringZh:M_GT_PARA_EDIT_ZH];
    
    [self setDString:M_GT_PARA_IN_ITEMS_KEY forStringEn:M_GT_PARA_IN_ITEMS_EN forStringZh:M_GT_PARA_IN_ITEMS_ZH];
    [self setDString:M_GT_PARA_OUT_ITEMS_KEY forStringEn:M_GT_PARA_OUT_ITEMS_EN forStringZh:M_GT_PARA_OUT_ITEMS_ZH];
    
    [self setDString:M_GT_PARA_TOP_KEY forStringEn:M_GT_PARA_TOP_EN forStringZh:M_GT_PARA_TOP_ZH];
    [self setDString:M_GT_PARA_DRAG_KEY forStringEn:M_GT_PARA_DRAG_EN forStringZh:M_GT_PARA_DRAG_ZH];
    [self setDString:M_GT_PARA_OUT_GW_KEY forStringEn:M_GT_PARA_OUT_GW_EN forStringZh:M_GT_PARA_OUT_GW_ZH];
    
    //para floating
    [self setDString:M_GT_PARA_FLOATING_KEY forStringEn:M_GT_PARA_FLOATING_EN forStringZh:M_GT_PARA_FLOATING_ZH];
    [self setDString:M_GT_PARA_UNFLOATING_KEY forStringEn:M_GT_PARA_UNFLOATING_EN forStringZh:M_GT_PARA_UNFLOATING_ZH];
    [self setDString:M_GT_PARA_UNFLOATING_KEY forStringEn:M_GT_PARA_UNFLOATING_EN forStringZh:M_GT_PARA_UNFLOATING_ZH];
    [self setDString:M_GT_PARA_DISABLE_KEY forStringEn:M_GT_PARA_DISABLE_EN forStringZh:M_GT_PARA_DISABLE_ZH];
    [self setDString:M_GT_PARA_FLOATING_ZERO_KEY forStringEn:M_GT_PARA_FLOATING_ZERO_EN forStringZh:M_GT_PARA_FLOATING_ZERO_ZH];
    
    
    //alert
    [self setDString:M_GT_ALERT_CLEAR_TITLE_KEY forStringEn:M_GT_ALERT_CLEAR_TITLE_EN forStringZh:M_GT_ALERT_CLEAR_TITLE_ZH];
    [self setDString:M_GT_ALERT_CLEAR_INFO_KEY forStringEn:M_GT_ALERT_CLEAR_INFO_EN forStringZh:M_GT_ALERT_CLEAR_INFO_ZH];
    [self setDString:M_GT_ALERT_SAVE_TITLE_KEY forStringEn:M_GT_ALERT_SAVE_TITLE_EN forStringZh:M_GT_ALERT_SAVE_TITLE_ZH];
    [self setDString:M_GT_ALERT_UPLOAD_TITLE_KEY forStringEn:M_GT_ALERT_UPLOAD_TITLE_EN forStringZh:M_GT_ALERT_UPLOAD_TITLE_ZH];
    
    [self setDString:M_GT_ALERT_INPUT_SAVED_FILE_KEY forStringEn:M_GT_ALERT_INPUT_SAVED_FILE_EN forStringZh:M_GT_ALERT_INPUT_SAVED_FILE_ZH];
    
    [self setDString:M_GT_ALERT_INPUT_FILE_KEY forStringEn:M_GT_ALERT_INPUT_FILE_EN forStringZh:M_GT_ALERT_INPUT_FILE_ZH];
    [self setDString:M_GT_ALERT_OK_KEY forStringEn:M_GT_ALERT_OK_EN forStringZh:M_GT_ALERT_OK_ZH];
    [self setDString:M_GT_ALERT_CANCEL_KEY forStringEn:M_GT_ALERT_CANCEL_EN forStringZh:M_GT_ALERT_CANCEL_ZH];
    [self setDString:M_GT_ALERT_BACK_KEY forStringEn:M_GT_ALERT_BACK_EN forStringZh:M_GT_ALERT_BACK_ZH];
    [self setDString:M_GT_ALERT_SEND_KEY forStringEn:M_GT_ALERT_SEND_EN forStringZh:M_GT_ALERT_SEND_ZH];
    [self setDString:M_GT_ALERT_SEND_SUCCESS_KEY forStringEn:M_GT_ALERT_SEND_SUCCESS_EN forStringZh:M_GT_ALERT_SEND_SUCCESS_ZH];
    [self setDString:M_GT_ALERT_SEND_ERROR_KEY forStringEn:M_GT_ALERT_SEND_ERROR_EN forStringZh:M_GT_ALERT_SEND_ERROR_ZH];
    [self setDString:M_GT_ALERT_SENDING_KEY forStringEn:M_GT_ALERT_SENDING_EN forStringZh:M_GT_ALERT_SENDING_ZH];
    
    [self setDString:M_GT_ALERT_START_KEY forStringEn:M_GT_ALERT_START_EN forStringZh:M_GT_ALERT_START_ZH];
    [self setDString:M_GT_ALERT_STOP_KEY forStringEn:M_GT_ALERT_STOP_EN forStringZh:M_GT_ALERT_STOP_ZH];
    
    [self setDString:M_GT_ALERT_PREPARING_KEY forStringEn:M_GT_ALERT_PREPARING_EN forStringZh:M_GT_ALERT_PREPARING_ZH];
    [self setDString:M_GT_ALERT_FILE_EXIT_KEY forStringEn:M_GT_ALERT_FILE_EXIT_EN forStringZh:M_GT_ALERT_FILE_EXIT_ZH];
    [self setDString:M_GT_ALERT_EXIT_KEY forStringEn:M_GT_ALERT_EXIT_EN forStringZh:M_GT_ALERT_EXIT_ZH];
    
    [self setDString:M_GT_ALERT_PW_KEY forStringEn:M_GT_ALERT_PW_EN forStringZh:M_GT_ALERT_PW_ZH];
    [self setDString:M_GT_ALERT_INPUT_ROOT_PW_KEY forStringEn:M_GT_ALERT_INPUT_ROOT_PW_EN forStringZh:M_GT_ALERT_INPUT_ROOT_PW_ZH];
    
    [self setDString:M_GT_ALERT_REMAINDER_KEY forStringEn:M_GT_ALERT_REMAINDER_EN forStringZh:M_GT_ALERT_REMAINDER_ZH];
    [self setDString:M_GT_ALERT_INPUT_1000_WORD_KEY forStringEn:M_GT_ALERT_INPUT_1000_WORD_EN forStringZh:M_GT_ALERT_INPUT_1000_WORD_ZH];
    [self setDString:M_GT_ALERT_I_KNOW_KEY forStringEn:M_GT_ALERT_I_KNOW_EN forStringZh:M_GT_ALERT_I_KNOW_ZH];
    [self setDString:M_GT_ALERT_NEW_GT_KEY forStringEn:M_GT_ALERT_NEW_GT_EN forStringZh:M_GT_ALERT_NEW_GT_ZH];
    
    
   
    
    

    //profiler
    [self setDString:M_GT_TIME_TITLE_KEY forStringEn:M_GT_TIME_TITLE_EN forStringZh:M_GT_TIME_TITLE_ZH];
     [self setDString:M_GT_TIME_COUNT_KEY forStringEn:M_GT_TIME_COUNT_EN forStringZh:M_GT_TIME_COUNT_ZH];
    [self setDString:M_GT_TIME_TOTAL_KEY forStringEn:M_GT_TIME_TOTAL_EN forStringZh:M_GT_TIME_TOTAL_ZH];
    [self setDString:M_GT_TIME_AVG_KEY forStringEn:M_GT_TIME_AVG_EN forStringZh:M_GT_TIME_AVG_ZH];
    [self setDString:M_GT_TIME_MAX_KEY forStringEn:M_GT_TIME_MAX_EN forStringZh:M_GT_TIME_MAX_ZH];
    [self setDString:M_GT_TIME_MIN_KEY forStringEn:M_GT_TIME_MIN_EN forStringZh:M_GT_TIME_MIN_ZH];
    [self setDString:M_GT_TIME_DETAIL_TITLE_KEY forStringEn:M_GT_TIME_DETAIL_TITLE_EN forStringZh:M_GT_TIME_DETAIL_TITLE_ZH];
    
    [self setDString:M_GT_PROFILER_NOT_START_KEY forStringEn:M_GT_PROFILER_NOT_START_EN forStringZh:M_GT_PROFILER_NOT_START_ZH];
    [self setDString:M_GT_PROFILER_START_KEY forStringEn:M_GT_PROFILER_START_EN forStringZh:M_GT_PROFILER_START_ZH];
    [self setDString:M_GT_PROFILER_STOP_KEY forStringEn:M_GT_PROFILER_STOP_EN forStringZh:M_GT_PROFILER_STOP_ZH];
    
    [self setDString:M_GT_PROFILER_COUNTING_KEY forStringEn:M_GT_PROFILER_COUNTING_EN forStringZh:M_GT_PROFILER_COUNTING_ZH];


    
    //core
    [self setDString:M_GT_CORE_COUNT_KEY forStringEn:M_GT_CORE_COUNT_EN forStringZh:M_GT_CORE_COUNT_ZH];
    [self setDString:M_GT_CORE_TOTAL_KEY forStringEn:M_GT_CORE_TOTAL_EN forStringZh:M_GT_CORE_TOTAL_ZH];
    [self setDString:M_GT_CORE_AVG_KEY forStringEn:M_GT_CORE_AVG_EN forStringZh:M_GT_CORE_AVG_ZH];
    [self setDString:M_GT_CORE_MAX_KEY forStringEn:M_GT_CORE_MAX_EN forStringZh:M_GT_CORE_MAX_ZH];
    [self setDString:M_GT_CORE_MIN_KEY forStringEn:M_GT_CORE_MIN_EN forStringZh:M_GT_CORE_MIN_ZH];
    
    //LOG
    [self setDString:M_GT_LOG_FILTER_BY_MSG_KEY forStringEn:M_GT_LOG_FILTER_BY_MSG_EN forStringZh:M_GT_LOG_FILTER_BY_MSG_ZH];
    [self setDString:M_GT_LOG_FILTER_ALL_KEY forStringEn:M_GT_LOG_FILTER_ALL_EN forStringZh:M_GT_LOG_FILTER_ALL_ZH];
    [self setDString:M_GT_LOG_FILTER_BY_TAG_KEY forStringEn:M_GT_LOG_FILTER_BY_TAG_EN forStringZh:M_GT_LOG_FILTER_BY_TAG_ZH];
    [self setDString:M_GT_LOG_SEARCH_KW_INFO_KEY forStringEn:M_GT_LOG_SEARCH_KW_INFO_EN forStringZh:M_GT_LOG_SEARCH_KW_INFO_ZH];
    
    
    //setting
    [self setDString:M_GT_SETTING_AC_KEY forStringEn:M_GT_SETTING_AC_EN forStringZh:M_GT_SETTING_AC_ZH];
    [self setDString:M_GT_SETTING_LOG_KEY forStringEn:M_GT_SETTING_LOG_EN forStringZh:M_GT_SETTING_LOG_ZH];
    [self setDString:M_GT_SETTING_PARA_KEY forStringEn:M_GT_SETTING_PARA_EN forStringZh:M_GT_SETTING_PARA_ZH];
    
    [self setDString:M_GT_SETTING_ABOUT_KEY forStringEn:M_GT_SETTING_ABOUT_EN forStringZh:M_GT_SETTING_ABOUT_ZH];
    
    
    [self setDString:M_GT_SETTING_AC_SHOW_KEY forStringEn:M_GT_SETTING_AC_SHOW_EN forStringZh:M_GT_SETTING_AC_SHOW_ZH];
    [self setDString:M_GT_SETTING_AC_Q_SWITCH_KEY forStringEn:M_GT_SETTING_AC_Q_SWITCH_EN forStringZh:M_GT_SETTING_AC_Q_SWITCH_ZH];
    [self setDString:M_GT_SETTING_AC_GW_KEY forStringEn:M_GT_SETTING_AC_GW_EN forStringZh:M_GT_SETTING_AC_GW_ZH];
    
    
    
    [self setDString:M_GT_SETTING_LOG_SWITCH_KEY forStringEn:M_GT_SETTING_LOG_SWITCH_EN forStringZh:M_GT_SETTING_LOG_SWITCH_ZH];
    
    [self setDString:M_GT_SETTING_LOG_AUTO_SAVE_KEY forStringEn:M_GT_SETTING_LOG_AUTO_SAVE_EN forStringZh:M_GT_SETTING_LOG_AUTO_SAVE_ZH];
    
     [self setDString:M_GT_SETTING_ABOUT_FEEDBACK_KEY forStringEn:M_GT_SETTING_ABOUT_FEEDBACK_EN forStringZh:M_GT_SETTING_ABOUT_FEEDBACK_ZH];
    //WARNING
    
    
    [self setDString:M_GT_WARNING_TITLE_DISABLED_KEY forStringEn:M_GT_WARNING_TITLE_DISABLED_EN forStringZh:M_GT_WARNING_TITLE_DISABLED_ZH];
    
    [self setDString:M_GT_WARNING_TITLE_ENABLE_KEY forStringEn:M_GT_WARNING_TITLE_ENABLE_EN forStringZh:M_GT_WARNING_TITLE_ENABLE_ZH];
    
    [self setDString:M_GT_WARNING_COUNT_KEY forStringEn:M_GT_WARNING_COUNT_EN forStringZh:M_GT_WARNING_COUNT_ZH];
    [self setDString:M_GT_SETTING_ABOUT_SCORE_KEY forStringEn:M_GT_SETTING_ABOUT_SCORE_EN forStringZh:M_GT_SETTING_ABOUT_SCORE_ZH];
    [self setDString:M_GT_WARNING_TIME_KEY forStringEn:M_GT_WARNING_TIME_EN forStringZh:M_GT_WARNING_TIME_ZH];
    [self setDString:M_GT_WARNING_RANGE_KEY forStringEn:M_GT_WARNING_RANGE_EN forStringZh:M_GT_WARNING_RANGE_ZH];
    

    //plugin
    [self setDString:M_GT_PLUGIN_SANDBOX_KEY forStringEn:M_GT_PLUGIN_SANDBOX_EN forStringZh:M_GT_PLUGIN_SANDBOX_ZH];
    [self setDString:M_GT_PLUGIN_CAP_KEY forStringEn:M_GT_PLUGIN_CAP_EN forStringZh:M_GT_PLUGIN_CAP_ZH];
    [self setDString:M_GT_PLUGIN_CAP_INFO_KEY forStringEn:M_GT_PLUGIN_CAP_INFO_EN forStringZh:M_GT_PLUGIN_CAP_INFO_ZH];
    [self setDString:M_GT_PLUGIN_CAP_CAPTURE_KEY forStringEn:M_GT_PLUGIN_CAP_CAPTURE_EN forStringZh:M_GT_PLUGIN_CAP_CAPTURE_ZH];
    [self setDString:M_GT_PLUGIN_CAP_CAPTURE_ERROR_KEY forStringEn:M_GT_PLUGIN_CAP_CAPTURE_ERROR_EN forStringZh:M_GT_PLUGIN_CAP_CAPTURE_ERROR_ZH];
    [self setDString:M_GT_PLUGIN_CAP_JAILBREAK_INFO_KEY forStringEn:M_GT_PLUGIN_CAP_JAILBREAK_INFO_EN forStringZh:M_GT_PLUGIN_CAP_JAILBREAK_INFO_ZH];
    
    [self setDString:M_GT_PARA_MONITOR_INTERVAL_KEY forStringEn:M_GT_PARA_MONITOR_INTERVAL_EN forStringZh:M_GT_PARA_MONITOR_INTERVAL_ZH];
    [self setDString:M_GT_PARA_GATHER_DURATION_KEY forStringEn:M_GT_PARA_GATHER_DURATION_EN forStringZh:M_GT_PARA_GATHER_DURATION_ZH];

}


- (NSString *)getCurLanguage
{
    NSString *sysLang =  [[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] objectAtIndex:0];
    if ([sysLang compare:@"zh-Hans" options:NSCaseInsensitiveSearch]== NSOrderedSame || [sysLang compare:@"zh-Hant" options:NSCaseInsensitiveSearch]==NSOrderedSame) {
        return @"zh";
    }
    else{
        return @"en";
    }
}
- (GTList *)getLanguageList:(NSString *)language
{
    if ([language isEqualToString:@"zh"]) {
        return _zhDStringList;
    }
    else {
        return _enDStringList;
    }
}

- (void)setDString:(NSString *)key forStringEn:(NSString *)enString forStringZh:(NSString *)zhString
{
    [_enDStringList setObject:enString forKey:key];
    [_zhDStringList setObject:zhString forKey:key];
    return;
}

- (NSString *)getDString:(NSString *)key
{
    GTList *curList = [self getLanguageList:_curLanguage];
    if (nil == curList) {
        return nil;
    }
    return [curList objectForKey:key];
}
@end

void func_setDstring(NSString * key, NSString * stringEn, NSString * stringZh)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(stringEn);
        M_GT_PTR_NULL_CHECK(stringZh);

        [[GTLang sharedInstance] setDString:key forStringEn:stringEn forStringZh:stringZh];
    }
}


#endif
