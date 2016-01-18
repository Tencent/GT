//
//  GTLang.h
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
#import "GTList.h"

#define M_GT_DSTRING(key)\
[[GTLang sharedInstance]  getDString:key]

//#define M_GT_SET_DSTRING(key,enString,zhString) func_setDstring(key,enString,zhString)
//FOUNDATION_EXPORT void func_setDstring(NSString * key, NSString * stringEn, NSString * stringZh);
#define M_GT_SET_DSTRING(key,enString,zhString)\
[self setDString:key forStringEn:stringEn forStringZh:stringZh]


@interface GTLang : NSObject
{
    NSString *_curLanguage; //语言，目前只支持zh（包括简体和繁体－统一为简体）和en
    GTList *_zhDStringList;
    GTList *_enDStringList;
}
M_GT_AS_SINGLETION(GTLang)


@property (nonatomic, retain) NSString *curLanguage;
@property (nonatomic, retain) GTList *zhDStringList;
@property (nonatomic, retain) GTList *enDStringList;

- (NSString *)getCurLanguage;
- (NSString *)getDString:(NSString *)key;
- (void)setDString:(NSString *)key forStringEn:(NSString *)enString forStringZh:(NSString *)zhString;
@end
#endif
