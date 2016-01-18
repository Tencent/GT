//
//  GTConfig.m
//  GTKit
//
//  Created   on 13-6-26.
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

#import "GTConfig.h"
#import "GTList.h"
#import "GTProgressHUD.h"
#import "GTOutputList.h"

#define M_GT_GATHER_PROMPT_INTERVAL 300
#define M_GT_GATHER_STOP_INTERVAL   60

@implementation NSString (TimeCategory)

//返回时间格式HH:mm:ss.SSS
+ (NSString *)stringWithTimeEx:(NSTimeInterval)time {
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
    
    return [[[NSString alloc] initWithFormat:@"%.2ld:%.2ld:%.2ld.%.3ld",
            (long)hour, (long)minute, (long)second, (long)microSec] autorelease];
}

//返回时间格式HH:mm:ss
+ (NSString *)stringWithTime:(NSTimeInterval)time {
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
    
    return [[[NSString alloc] initWithFormat:@"%.2ld:%.2ld:%.2ld",
            (long)hour, (long)minute, (long)second] autorelease];
}

//返回时间格式yyyy-MM-dd
+ (NSString *)stringWithDate:(NSTimeInterval)time {
    time += [[GTConfig sharedInstance] secondsFromGMT];
    
    static NSDateFormatter *formatter = nil;
    if (formatter == nil) {
        formatter = [[NSDateFormatter alloc] init];
    }
    
    [formatter setDateFormat:@"yyyy-MM-dd"];
    
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:time];
    
    return NSLocalizedString([formatter stringFromDate:date],);
}

//返回时间格式yyyy-MM-dd HH:mm:ss.SSS
+ (NSString *)stringWithDateEx:(NSTimeInterval)time {
    time += [[GTConfig sharedInstance] secondsFromGMT];
    
    static NSDateFormatter *formatter = nil;
    if (formatter == nil) {
        formatter = [[NSDateFormatter alloc] init];
    }
    
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss.SSS"];
    
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:time];
    
    return NSLocalizedString([formatter stringFromDate:date],);
}

- (NSTimeInterval)timeValue {
    NSInteger hour = 0, minute = 0, second = 0;
    NSTimeInterval microSec = 0;
    NSArray *array = [self componentsSeparatedByString:@"."];
    if ([array count] == 2) {
        microSec = [[array objectAtIndex:1] doubleValue]/1000;
    }
    
    NSArray *sections = [self componentsSeparatedByString:@":"];
    NSInteger count = [sections count];
    if (count == 3) {
        hour = [[sections objectAtIndex:0] integerValue];
        minute = [[sections objectAtIndex:1] integerValue];
        second = [[sections objectAtIndex:2] integerValue];
    }
    
    NSTimeInterval time = hour * 3600 + minute * 60 + second + microSec;
    time -= [[GTConfig sharedInstance] secondsFromGMT];
    
    return time;
}


//传入秒数，返回几分几秒
+ (NSString *)timeString:(NSTimeInterval)t
{
    int hour = 0;
    int minute = 0;
    int second = 0;
    int time = round(t); //四舍五入
    
    hour = t/3600;
    minute = (time % 3600) / 60;
    second = time % 3600 % 60;
    
    if (hour > 0) {
        return [[[NSString alloc] initWithFormat:@"%.2d:%.2d:%.2d", hour, minute, second] autorelease];
    } else {
        return [[[NSString alloc] initWithFormat:@"%.2d:%.2d", minute, second] autorelease];
    }
}


//返回时分秒信息
+ (NSString *)stringFromDate:(NSDate *)date
{
    NSDateFormatter * formatter = [[GTConfig sharedInstance] formatter];
	[formatter setDateFormat:@"HH:mm:ss.SSS"];
    
    return [formatter stringFromDate:date];
}


@end

@implementation GTConfig

M_GT_DEF_SINGLETION(GTConfig);

@synthesize useGT = _useGT;
@synthesize hasReported = _hasReported;
@synthesize startTime = _startTime;
@synthesize watchTime = _watchTime;
@synthesize appStatusBarHidden = _appStatusBarHidden;
@synthesize appStatusBarOrientation = _appStatusBarOrientation;
@synthesize appKeyWindow = _appKeyWindow;
@synthesize showAC = _showAC;
@synthesize userClicked = _userClicked;
@synthesize acInterval = _acInterval;
@synthesize monitorInterval = _monitorInterval;
@synthesize shouldAutorotate = _shouldAutorotate;
@synthesize supportedInterfaceOrientations = _supportedInterfaceOrientations;
@synthesize acHeaderHeight = _acHeaderHeight;
@synthesize gatherSwitch = _gatherSwitch;
@synthesize acSwtichIndex = _acSwtichIndex;
@synthesize formatter = _formatter;
@synthesize showAlert = _showAlert;
@synthesize secondsFromGMT = _secondsFromGMT;

#pragma mark 初始化

- (id)init
{
    self = [super init];
    if (self) {
        _watchTime = 0;
        _startTime = nil;
        _useGT = YES;
        _hasReported = NO;
        
        _appStatusBarHidden = [UIApplication sharedApplication].statusBarHidden;
        _appStatusBarOrientation = [UIApplication sharedApplication].statusBarOrientation;
        _appKeyWindow = nil;
        _showAC = NO;
        _acInterval = 1;
        
        //监控性能相关配置
        _monitorInterval = 1;
        _gatherSwitch = NO;
        
        _shouldAutorotate = YES;
        _supportedInterfaceOrientations = UIInterfaceOrientationMaskAll;
        _acHeaderHeight = 20;
        _acSwtichIndex = GTACSwitchGW;
        
        
        _formatter = [[NSDateFormatter alloc] init];
		[_formatter setTimeStyle:NSDateFormatterShortStyle];
		[_formatter setDateStyle:NSDateFormatterShortStyle];
        
        _showAlert = YES;
        _secondsFromGMT = [[NSTimeZone systemTimeZone] secondsFromGMT];
        _homeDirectory = [NSHomeDirectory() retain];
    }
    
    return self;
}

- (void)dealloc
{
    _appKeyWindow = nil;
    
    [_formatter release];
    _formatter = nil;
    
    [_homeDirectory release];
    
    [super dealloc];
}


#pragma mark - path

- (void)dirCreateIfNotExists:(NSString *)dirPath
{
    NSFileManager * manager = [NSFileManager defaultManager];
    BOOL result = YES;
	if (![manager fileExistsAtPath:dirPath isDirectory:&result]) {
        [manager createDirectoryAtPath:dirPath withIntermediateDirectories:YES attributes:nil error:nil];
	}
}


- (NSString *)sysDir
{
    return [NSString stringWithFormat:@"%@/Documents/GT/sys/", _homeDirectory];
}

- (NSString *)usrDir
{
    return [NSString stringWithFormat:@"%@/Documents/GT/", _homeDirectory];
}


- (NSString *)sysDirByCreated
{
    NSString *dirPath = [self sysDir];
    [self dirCreateIfNotExists:dirPath];
    
    return dirPath;
}

- (NSString *)usrDirByCreated
{
    NSString *dirPath = [self usrDir];
    [self dirCreateIfNotExists:dirPath];
    
    return dirPath;
}

- (NSString *)pathForDir:(NSString *)dir fileName:(NSString *)fileName ofType:(NSString *)ext
{
    if (dir == nil) {
        return [NSString stringWithFormat:@"%@/%@.%@", [self usrDir], fileName, ext];;
    }
    
    return [NSString stringWithFormat:@"%@%@/%@.%@", [self usrDir], dir, fileName, ext];
}


- (NSString *)pathForDirByCreated:(NSString *)dir fileName:(NSString *)fileName ofType:(NSString *)ext
{
    NSString *dirPath = nil;
    if (dir == nil) {
        dirPath = [NSString stringWithFormat:@"%@", [self usrDir]];
    } else {
        dirPath = [NSString stringWithFormat:@"%@%@/", [self usrDir], dir];
    }
    [self dirCreateIfNotExists:dirPath];
    
    return [NSString stringWithFormat:@"%@%@.%@", dirPath, fileName, ext];
}

#pragma mark - 磁盘空间大小
//单个文件的大小
- (unsigned long long)getFileSize:(NSString *)filePath{
    
    NSFileManager* manager = [NSFileManager defaultManager];
    
    if ([manager fileExistsAtPath:filePath]) {
        return [[manager attributesOfItemAtPath:filePath error:nil] fileSize];
    }
    
    return 0;
    
}

//获取文件夹大小
- (long long)getFolderSize:(NSString *)folderPath{
    
    NSFileManager *manager = [NSFileManager defaultManager];
    if (![manager fileExistsAtPath:folderPath]) {
        return 0;
    }
    NSEnumerator *filesEnumerator = [[manager subpathsAtPath:folderPath] objectEnumerator];
    unsigned long long folderSize = 0;
    NSString *fileName;
    
    while ((fileName = [filesEnumerator nextObject]) != nil) {
        NSString *fileAbsolutePath = [folderPath stringByAppendingPathComponent:fileName];
        folderSize += [self getFileSize:fileAbsolutePath];
    }
    
    return folderSize;
}

- (long long)paraSysDiskSize
{
    return _paraSysDiskSize;
}


- (void)threadEnd:(NSNumber *)folderSize
{
    if (_fileOpThread != nil) {
        [_fileOpThread cancel];
        [_fileOpThread release];
        _fileOpThread = nil;
        
        _paraSysDiskSize = [folderSize longLongValue];
        
        if (_paraSysDiskSize >= M_GT_PARA_OUT_DISK_PROMPT_SIZE) {
            [GTProgressHUD showWithString:@"Space will be full!\r\nPlease stop Gather and clear history!"];
            [self stopPromptTimer];
            [self startCloseTimer];
        } else if (_paraSysDiskSize >= M_GT_PARA_OUT_DISK_MAX_SIZE) {
            //如果当前磁盘空间超过阈值，则自动关闭采集功能
            [GTProgressHUD showWithString:@"No space! Gather is stopped!"];
            //关闭采集
            [self setGatherSwitch:NO];
            [self stopPromptTimer];
        }
    }
}

#pragma mark 保存文件
- (BOOL)dirSizeGetThreadStart
{
    if (!_gatherSwitch) {
        return NO;
    }
    
    @synchronized (self) {
        if (_fileOpThread == nil) {
            _fileOpThread = [[NSThread alloc] initWithTarget:self selector:@selector(dirSizeThreadProc) object:nil];
            _fileOpThread.name = [NSString stringWithFormat:@"GTDirSizeGet_%@", NSStringFromClass([self class])];
            [_fileOpThread start];
            return YES;
        }
        
        return NO;
    }
}

- (void)dirSizeThreadProc
{
    @autoreleasepool {
        NSString *filePath = [NSString stringWithFormat:@"%@%@/", [[GTConfig sharedInstance] usrDir], M_GT_SYS_PARA_DIR];
        float folderSize = [self getFolderSize:filePath];
        
        //保存完毕，通知主线程
        [self performSelectorOnMainThread:@selector(threadEnd:) withObject:[NSNumber numberWithLongLong:folderSize] waitUntilDone:NO];
    }
//    [NSThread exit];
}

#pragma mark - gather

- (void)setGatherSwitch:(BOOL)gatherSwitch
{
    if (_gatherSwitch == gatherSwitch) {
        return;
    }
    
    if (gatherSwitch) {
        //如果当前磁盘空间超过阈值，则自动关闭采集功能
        if (_paraSysDiskSize >= M_GT_PARA_OUT_DISK_MAX_SIZE) {
            [GTProgressHUD showWithString:@"No space! Please clear history!"];
            return;
        }
        
        if (![[GTOutputList sharedInstance] hasItemHistoryOn]) {
            [GTProgressHUD showWithString:@"No gather item!"];
            return;
        }
        
        //获取磁盘空间大小，用于更新
        [self dirSizeGetThreadStart];
        
        //如果开始则计算提示时间
        [self startTimer];
    } else {
        //关闭之前提示用户的定时器
        [self stopTimer];
    }
    
    _gatherSwitch = gatherSwitch;
    //发送通知，用于UI图标的刷新
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_GW_UPDATE object:nil];
}

- (void)stopTimer
{
    [self stopPromptTimer];
    [self stopCloseTimer];
}

- (void)startTimer
{
    [self startPromptTimer];
    
    //close Timer由prompt timer触发
}

#pragma mark - Prompt Timer Process
- (void)startPromptTimer
{
    if (_promptTimer == nil) {
        //每五分钟监控一次内存
        _promptTimer = [[NSTimer alloc] initWithFireDate:[NSDate date] interval:M_GT_GATHER_PROMPT_INTERVAL target:self selector:@selector(promptTimerNotify:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_promptTimer forMode:NSRunLoopCommonModes];
    }
    
}

- (void)stopPromptTimer
{
    if (_promptTimer) {
        [_promptTimer invalidate];
        [_promptTimer release];
        _promptTimer = nil;
    }
}

- (void)promptTimerNotify:(id)sender
{
    //启动线程更新当前磁盘空间大小
    [self dirSizeGetThreadStart];
}


#pragma mark - Close Timer Process

- (void)startCloseTimer
{
    if (_closeTimer == nil) {
        //每一分钟监控一次内存
        _closeTimer = [[NSTimer alloc] initWithFireDate:[NSDate date] interval:M_GT_GATHER_STOP_INTERVAL target:self selector:@selector(closeTimerNotify:) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_closeTimer forMode:NSRunLoopCommonModes];
    }
    
}

- (void)stopCloseTimer
{
    if (_closeTimer) {
        [_closeTimer invalidate];
        [_closeTimer release];
        _closeTimer = nil;
    }
}

- (void)closeTimerNotify:(id)sender
{
    //启动线程更新当前磁盘空间大小
    [self dirSizeGetThreadStart];
}


@end

#pragma mark - User Interface

void func_setGatherSwitch(bool on)
{
    @autoreleasepool {
        [[GTConfig sharedInstance] setGatherSwitch:on];
        [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    }
    
}

void func_setMonitorInterval(double interval)
{
    if ((interval < 0.1) || (interval > 10)) {
        return;
    }
    [[GTConfig sharedInstance] setMonitorInterval:interval];
}

void func_hideAlert()
{
    [[GTConfig sharedInstance] setShowAlert:NO];
}

#endif
