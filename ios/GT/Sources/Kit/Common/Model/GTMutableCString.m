//
//  GTMutableCString.m
//  GTKit
//
//  Created   on 13-12-16.
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

#import "GTMutableCString.h"
#import "GTDebugDef.h"
#import "GTConfig.h"

#define M_GT_CSTR_SEGMENT (4.0 * M_GT_KB)

@interface GTMutableCString()
@end

@implementation GTMutableCString

@synthesize bytes = _bytes;
@synthesize allocedLen = _allocedLen;
@synthesize bytesLen = _bytesLen;

- (id)init
{
    self = [super init];
    if (self) {
        _allocedLen = M_GT_CSTR_SEGMENT;
        _bytes = (char * ) malloc(_allocedLen);
        memset(_bytes, 0, _allocedLen);
        _bytesLen = 0;
    }
    
    return self;
}


- (void)dealloc
{
    [self releaseString];
    
    [super dealloc];
}

- (void)releaseString
{
    if (_bytes) {
        free(_bytes);
        _bytes = NULL;
    }
}



- (void)appendCString:(const char *)bytes length:(NSUInteger)length
{
    if (_bytes == NULL) {
        return;
    }
    
    if (_bytesLen + length > _allocedLen) {
         //每块空间以M为单位，防止分配过于碎片
         NSUInteger newLen = _allocedLen + (length/M_GT_CSTR_SEGMENT+1)*M_GT_CSTR_SEGMENT;
         
         //空间不够，分配更大的空间
         char *newStr = (char * )malloc(newLen);
         if (newStr == NULL) {
             return;
         }
         memset(newStr, 0, newLen);
         
         //拷贝之前的数据
         memcpy(newStr, _bytes, _allocedLen);
         
         //释放老空间
         free(_bytes);
         
         //保存新数据
         _bytes = newStr;
         _allocedLen = newLen;
    }
    
    
    if (_bytesLen + length < _allocedLen) {
        char *dest = _bytes + _bytesLen;
        
        strncat(dest, bytes, length);
        _bytesLen += length;
    }
    
}

//保存时间格式HH:mm:ss.SSS
- (void)appendCStringWithTimeEx:(NSTimeInterval)time {
    NSInteger timeInt;
    
    //换算本地时间，增加时差
    time += [[GTConfig sharedInstance] secondsFromGMT];
    
    NSInteger secs = round(time);
    NSInteger days = secs/(3600*24);
    
    //去除年月日对应的秒数
    time -= days*3600*24;
    timeInt = (NSInteger)time;
    
    NSInteger hour = timeInt/3600;
    NSInteger minute = (timeInt%3600)/60;
    NSInteger second = (timeInt%3600)%60;
    NSInteger microSec = (time - timeInt)*1000; //毫秒显示三位数
    
    char str[M_GT_SIZE_32] = {0};
    memset(str, 0, M_GT_SIZE_32);
    snprintf(str, M_GT_SIZE_32 - 1, "%.2ld:%.2ld:%.2ld.%.3ld", (long)hour, (long)minute, (long)second, (long)microSec);
    
    [self appendCString:str length:strlen(str)];
}


@end
#endif
