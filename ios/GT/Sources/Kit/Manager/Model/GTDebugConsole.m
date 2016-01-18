//
//  GTDebugConsole.m
//  GTKit
//
//  Created   on 13-5-14.
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
#import "GTDebugConsole.h"
#import "GTUIManager.h"
#import "GTConfig.h"
#import "GTMemoryModel.h"
#import "GTNetModel.h"
#import "GTThreadModel.h"
#import "GTCrashFileHandler.h"
#import "GTNSLog.h"
#import "GTSMMonitor.h"
#import "GTBattery.h"
#import "GTLang.h"
#import "GTLangDef.h"
#import "GTVersionDef.h"
#import "GTMTA.h"
#import "GTMTAConfig.h"

@implementation GTDebugConsole

M_GT_DEF_SINGLETION(GTDebugConsole);

- (id)init
{
    if(self = [super init])
    {
        [self load];
    }
    
    return self;
}

- (void)load
{
}

- (void)dealloc
{
	[super dealloc];
}

#pragma mark -


- (void)loadDatas
{
    // 配置初始化
    [GTConfig sharedInstance];
    //[GTLang sharedInstance];

    
    // 监控初始化
    [GTSMMonitor sharedInstance];
    [GTThreadModel sharedInstance];
    [GTNetModel sharedInstance];
    [GTMemoryModel sharedInstance];
    [GTBattery sharedInstance];
    
    
    // 日志初始化
    [GTLog sharedInstance];
    
    GTPlugin *plugin;
    
    // 添加沙箱插件
    plugin = (GTPlugin *)[[[NSClassFromString(@"GTSandboxPlugin") alloc] init] autorelease];
    GT_PLUGIN_REGISTER(plugin);
    
    // 添加crash插件
    [GTCrashFileHandler sharedInstance];
    plugin = (GTPlugin *)[[[NSClassFromString(@"GTCrashPlugin") alloc] init] autorelease];
    GT_PLUGIN_REGISTER(plugin);
    
    // 添加NSLog插件
    [GTNSLog sharedInstance];
    plugin = (GTPlugin *)[[[NSClassFromString(@"GTNSLogPlugin") alloc] init] autorelease];
    GT_PLUGIN_REGISTER(plugin);
    
    // 初始化GTMTA，用于事件上报
    [GTMTA startWithAppkey:@"IP49TXV1MH9M"];
    
    // 调试使用
//    [[GTMTAConfig getInstance] setReportStrategy:GTMTA_STRATEGY_APP_LAUNCH];
    
}


- (NSString *)getFilePath
{
    return [[GTConfig sharedInstance] pathForDirByCreated:M_GT_SYS_DIR fileName:@"provision" ofType:@"txt"];
}

- (void)checkShowProvInThread:(id)sender
{
    @autoreleasepool {
        NSString *filePath = [self getFilePath];
        
        // 文件不存在则提示用户
        if ((![[NSFileManager defaultManager] fileExistsAtPath:filePath])&&([[GTConfig sharedInstance] showAlert])) {
            [self performSelectorOnMainThread:@selector(showProvisions) withObject:nil waitUntilDone:NO];
        } else {
            [self performSelectorOnMainThread:@selector(initGT) withObject:nil waitUntilDone:NO];
        }

    }
    
}

- (void)saveProvInThread:(id)sender
{
    NSString *filePath = [self getFilePath];
    FILE *file = fopen([filePath UTF8String], "w");
    
	if (file) {
        fprintf(file, "%u\r", [[GTConfig sharedInstance] useGT]);
		fflush(file);
        fclose(file);
	}
}


- (void)showProvisions
{
    GTUIAlertView * alertView = [[GTProvisionsAlertView alloc] initWithTitle:@"AGREEMENT"
                                                                  message:nil
                                                                 delegate:self
                                                        cancelButtonTitle:M_GT_LOCALSTRING(M_GT_ALERT_I_KNOW_KEY)
                                                        otherButtonTitles:nil];

    
    
    [alertView setTag:'s'+'m'];
    [alertView show];
    [alertView release];
    
}


- (void)checkShowGT
{
    //提前基本参数的配置，便于用户后续API调用时设置
//    GT_OUT_REGISTER("Battery", "BT");
//    GT_OUT_REGISTER("App Memory", "MEM");
//    GT_OUT_REGISTER("Device Network", "NET");
//    GT_OUT_REGISTER("App Smoothness", "SM");
//    GT_OUT_REGISTER("App CPU", "CPU");
//    
//    
//    GT_OC_OUT_DEFAULT_ON_AC(@"App CPU", @"App Memory", @"App Smoothness");
//    GT_OC_OUT_DEFAULT_ON_DISABLED(@"Battery", nil);
//    GT_OUT_DEFAULT_ON_DISABLED("App Smoothness", "Device Network", "Battery", nil);
    @autoreleasepool {
        [[GTDebugConsole sharedInstance] loadDatas];
    }

    NSThread *thread = [[[NSThread alloc] initWithTarget:self selector:@selector(checkShowProvInThread:) object:nil] autorelease];
    thread.name = @"checkShowProvInThread";
    [thread start];
}

- (void)initGT
{
//    @autoreleasepool {
//        [[GTDebugConsole sharedInstance] loadDatas];
//    }
    
    //在某些被测应用(如路宝)会在初始化时修改应用窗口的尺寸，故这里做延时的处理，确保窗口尺寸不被修改
    [[GTDebugConsole sharedInstance] performSelector:@selector(initGTUI) withObject:nil afterDelay:0.5];
}

- (void)initGTUI
{
    @autoreleasepool {
        // UI初始化
        [GTUIManager sharedInstance];
        BOOL showAC = [[GTConfig sharedInstance] showAC];
        if (showAC) {
            GT_AC_SHOW;
        }
        
    }
}

#pragma mark - GTUIAlertViewDelegate

- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {

    if ([alertView tag] == 's'+'m') {
        if (buttonIndex == 0) {
            //用户接受条款则初始化GT
            if ([(GTProvisionsAlertView *)alertView tipsSelected]) {
                NSThread *thread = [[[NSThread alloc] initWithTarget:self selector:@selector(saveProvInThread:) object:nil] autorelease];
                thread.name = @"saveProvInThread";
                [thread start];
                [self performSelectorOnMainThread:@selector(initGT) withObject:nil waitUntilDone:NO];
            } else {
                GT_DEBUG_SET_HIDDEN(YES);
                [[GTConfig sharedInstance] setUseGT:NO];
            }

        }
    }
}

@end


#pragma mark -

bool func_boolFromBOOL( BOOL value )
{
    if (value) {
        return true;
    } else {
        return false;
    }
}

BOOL func_BOOLFrombool( bool value )
{
    if (value) {
        return YES;
    } else {
        return NO;
    }
}

#pragma mark - User Interface


void func_initGT()
{
    [[GTDebugConsole sharedInstance] checkShowGT];
    return;

    @autoreleasepool {
        [[GTDebugConsole sharedInstance] loadDatas];
    }
    
    //在某些被测应用(如路宝)会在初始化时修改应用窗口的尺寸，故这里做延时的处理，确保窗口尺寸不被修改
    [[GTDebugConsole sharedInstance] performSelector:@selector(initGTUI) withObject:nil afterDelay:0.5];
}

void func_setGTHidden(bool hidden)
{
    if ([[GTConfig sharedInstance] useGT]) {
        [[GTUIManager sharedInstance] setGTHidden:hidden];
    }
    
}


bool func_getGTHidden()
{
    BOOL isShow = [[GTUIManager sharedInstance] hidden];
    return func_boolFromBOOL(isShow);
}

void func_setGTAutorotate(BOOL autorotate)
{
    [[GTConfig sharedInstance] setShouldAutorotate:autorotate];
}

void func_setGTSupportedOrientations(NSUInteger interfaceOrientation)
{
    [[GTConfig sharedInstance] setSupportedInterfaceOrientations:interfaceOrientation];
}

void func_setGTACHeaderHeight(float height)
{
    if ((height < 20) || (height > 40)) {
        return;
    }
    [[GTConfig sharedInstance] setAcHeaderHeight:height];
}

#endif
