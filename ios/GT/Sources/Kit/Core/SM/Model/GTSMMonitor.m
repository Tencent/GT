//
//  GTSMMonitor.m
//  GTKit
//
//  Created   on 13-9-4.
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

#import "GTSMMonitor.h"
#import <QuartzCore/QuartzCore.h>

@interface GTSMMonitor()

@end

@implementation GTSMMonitor 

M_GT_DEF_SINGLETION(GTSMMonitor);

- (void)dealloc {
    [_displayLink setPaused:YES];
    [_displayLink removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
    [super dealloc];
}


- (id)init {
    self = [super init];
    if( self ){
        _updateInterval = 0.25f;
        _historyCount = 0;
        _historySum = 0;
        [[NSNotificationCenter defaultCenter] addObserver: self
                                                 selector: @selector(applicationWillResignActiveNotification)
                                                     name: UIApplicationWillResignActiveNotification
                                                   object: nil];
        [[NSNotificationCenter defaultCenter] addObserver: self
                                                 selector: @selector(applicationDidBecomeActiveNotification)
                                                     name: UIApplicationDidBecomeActiveNotification
                                                   object: nil];
        
        
        _displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(displayLinkProc)];
        [_displayLink setPaused:YES];
        [_displayLink addToRunLoop:[NSRunLoop mainRunLoop] forMode:NSRunLoopCommonModes];
        
        GT_OUT_REGISTER("App Smoothness", "SM");
        GT_OUT_HISTORY_CHECKED_SET("App Smoothness", true);
        GT_OC_OUT_DELEGATE_SET(@"App Smoothness", self);
        
    }
    return self;
}

// 切换前台
- (void)applicationDidBecomeActiveNotification {
    [_displayLink setPaused:NO];
}


// 切换后台
- (void)applicationWillResignActiveNotification {
    [_displayLink setPaused:YES];
}


- (void)displayLinkProc {
    _historyCount += _displayLink.frameInterval;
    
    CFTimeInterval interval = _displayLink.timestamp - _lastTime;
    if( interval >= _updateInterval ) {
        _lastTime = _displayLink.timestamp;
//        NSString *text = [NSString stringWithFormat:@"%.0f", _historyCount/interval];
//        GT_OC_OUT_SET(@"App Smoothness", NO, text);
        
        GT_OUT_SET("App Smoothness", false, "%.0f", _historyCount/interval);
        _historyCount = 0;
    }
}

#pragma mark - GTParaDelegate

- (void)switchEnable
{
    [_displayLink setPaused:NO];
}
- (void)switchDisable
{
    [_displayLink setPaused:YES];
}


@end
