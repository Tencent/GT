//
//  GTOutputObject.m
//  GTKit
//
//  Created   on 12-5-30.
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

#import "GTOutputObject.h"
#import "GTProfilerValue.h"
#import "GTLogConfig.h"
#import "GTConfig.h"

@implementation NSString (FileSort)
//按文件名的数字排序，最小到最大
- (NSComparisonResult)fileNumCompare:(NSString *)other {
    return ([other integerValue] > [self integerValue]);
}
@end

@implementation GTOutputObject

@synthesize dataInfo = _dataInfo;
@synthesize vcForDetail = _vcForDetail;
@synthesize paraDelegate = _paraDelegate;
@synthesize showDelegate = _showDelegate;
@synthesize selFloatingDesc = _selFloatingDesc;
@synthesize status = _status;
@synthesize writeToLog = _writeToLog;

@synthesize switchForHistory = _switchForHistory;
@synthesize switchForWarning = _switchForWarning;
@synthesize history = _history;
@synthesize recordClassStr = _recordClassStr;
@synthesize fileName = _fileName;
@synthesize historyCnt = _historyCnt;
@synthesize totalTime = _totalTime;
@synthesize avgTime = _avgTime;
@synthesize maxTime = _maxTime;
@synthesize minTime = _minTime;

@synthesize showWarning = _showWarning;
@synthesize thresholdInterval = _thresholdInterval;

@synthesize lowerThresholdValue = _lowerThresholdValue;
@synthesize lowerWarningList = _lowerWarningList;
@synthesize lowerStartWarning = _lowerStartWarning;
@synthesize lowerStartWarningStr = _lowerStartWarningStr;

@synthesize upperThresholdValue = _upperThresholdValue;
@synthesize upperWarningList = _upperWarningList;
@synthesize upperStartWarning = _upperStartWarning;
@synthesize upperStartWarningStr = _upperStartWarningStr;

- (id)initWithKey:(NSString*)key alias:(NSString*)alias value:(GTOutputValue *)value
{
    self = [super init];
    if (self) {
        _dataInfo    = [[GTOutputDataInfo alloc] initWithKey:key alias:alias value:value];
        _selFloatingDesc = nil;
        _paraDelegate = nil;
        _showDelegate = nil;
        
        _status = GTParaInvalid;
        _writeToLog = NO;
        _switchForHistory = GTParaHistroyDisabled;
        _switchForWarning = NO;
        
        _history = [[NSMutableArray alloc] initWithCapacity:M_GT_PARA_AUTOSAVE_RECORD_CNT];
        _historyCnt = 0;
        _totalTime = 0;
        _avgTime = 0;
        _maxTime = 0;
        _minTime = 0;
        
        _showWarning = NO;
        _thresholdInterval = 0;
        _upperThresholdValue = M_GT_UPPER_WARNING_INVALID;
        _upperStartWarning = 0;
        _upperWarningList = [[GTList alloc] init];
        
        _lowerThresholdValue = M_GT_LOWER_WARNING_INVALID;
        _lowerStartWarning = 0;
        _lowerWarningList = [[GTList alloc] init];
        
        self.recordClassStr = nil;
        [self setFileName:key];
    }
    
    return self;
}

- (void)dealloc
{
    [_dataInfo release];
    [_history removeAllObjects];
    [_history release];
    
    [_upperWarningList release];
    self.upperStartWarning = 0;
    self.upperStartWarningStr = nil;
    
    [_lowerWarningList release];
    self.lowerStartWarning = 0;
    self.lowerStartWarningStr = nil;
    
    self.fileName = nil;
    self.recordClassStr = nil;
    self.paraDelegate = nil;
    self.showDelegate = nil;
    [super dealloc];
}

#pragma mark - 告警逻辑处理

- (void)upperWarningTime:(NSTimeInterval)time withDate:(NSTimeInterval)date
{
    if (_thresholdInterval == 0) {
        return;
    }
    
    if (_upperThresholdValue == M_GT_UPPER_WARNING_INVALID) {
        return;
    }
    
    //没达到告警上限
    if (time <= _upperThresholdValue) {
        self.upperStartWarning = 0;
        self.upperStartWarningStr = nil;
    } else {
        //记录告警开始时间
        if (_upperStartWarning == 0) {
            self.upperStartWarning = date;
            self.upperStartWarningStr = [NSString stringWithDateEx:_upperStartWarning];
        }
        
        NSTimeInterval interval = date - self.upperStartWarning;
        
        //达到告警设置时长，记录该区间信息
        if (interval >= _thresholdInterval) {
            
            GTWarningSegment *segment = [_upperWarningList objectForKey:self.upperStartWarningStr];
            if (segment == nil) {
                segment = [[GTWarningSegment alloc] init];
                [segment setStartDate:_upperStartWarning];
                [_upperWarningList setObject:segment forKey:self.upperStartWarningStr];
                [segment release];
                
                //产生一个新的告警，发送一个通知
                [self setShowWarning:YES];
                [self postWarningNotification];
            }
            [segment setEndDate:date];
        }
    }
}

- (void)lowerWarningTime:(NSTimeInterval)time withDate:(NSTimeInterval)date
{
    if (_thresholdInterval == 0) {
        return;
    }
    
    if (_lowerThresholdValue == M_GT_LOWER_WARNING_INVALID) {
        return;
    }
    
    //没达到告警下限
    if (time >= _lowerThresholdValue) {
        self.lowerStartWarning = 0;
        self.lowerStartWarningStr = nil;
    } else {
        //记录告警开始时间
        if (_lowerStartWarning == 0) {
            self.lowerStartWarning = date;
            self.lowerStartWarningStr = [NSString stringWithDateEx:_lowerStartWarning];
        }
        
        NSTimeInterval interval = date - self.lowerStartWarning;
        
        //达到告警设置时长，记录该区间信息
        if (interval >= _thresholdInterval) {
            
            GTWarningSegment *segment = [_lowerWarningList objectForKey:self.lowerStartWarningStr];
            if (segment == nil) {
                segment = [[GTWarningSegment alloc] init];
                [segment setStartDate:_lowerStartWarning];
                [_lowerWarningList setObject:segment forKey:self.lowerStartWarningStr];
                [segment release];
                
                //产生一个新的告警，发送一个通知
                [self setShowWarning:YES];
                [self postWarningNotification];
            }
            [segment setEndDate:date];
        }
    }
}

- (void)setShowWarning:(BOOL)showWarning
{
    _showWarning = showWarning;
    
    //如果是清除操作，则发送通知
    if (_showWarning == NO) {
        [self postWarningNotification];
    }
}

- (void)postWarningNotification
{
    NSDictionary* dic = [[[NSMutableDictionary alloc] init] autorelease];
    [dic setValue:[_dataInfo key] forKey:@"key"];
    
    if (_showWarning) {
        [dic setValue:@"warning" forKey:@"result"];
    } else {
        [dic setValue:@"normal" forKey:@"result"];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_OBJ_WARNING object:nil userInfo:dic];
}

- (void)warningTime:(NSTimeInterval)time withDate:(NSTimeInterval)date
{
    //上限判断
    [self upperWarningTime:time withDate:date];
    
    //下限判断
    [self lowerWarningTime:time withDate:date];
}

//更新告警列表
- (void)updateWarningList
{
    //这里告警没有记录历史数据，所以更新告警列表的操作为删除原先告警数据，重新计数
    [self setShowWarning:NO];
    [_upperWarningList deleteAll];
    [_lowerWarningList deleteAll];
}

#pragma mark - 出参基本信息设置及历史记录清除保存

//返回TRUE：有记录到历史队列中，FALSE：没有记录到历史队列中
- (BOOL)setDataValue:(GTOutputValue *)value
{
    BOOL writeToHistory = NO;
    
    [_dataInfo setValue:value];
    
    if ((_switchForHistory == GTParaHistroyOn) && ([[GTConfig sharedInstance] gatherSwitch] == YES)) {
        // 若有用户自定义的对象，则history添加用户自定义结构
        if ((_paraDelegate) && [_paraDelegate respondsToSelector:@selector(objForHistory)]) {
            GTHistroyValue *obj = [_paraDelegate objForHistory];
            if (obj) {
                [_history addObject:obj];
                _historyCnt++;
            }
        } else {
            // 默认出参的处理结构
            GTOutputValue *output = (GTOutputValue *)value;
            NSTimeInterval time = [[output content] doubleValue];
            [self addTime:time withDate:[output date]];
            
            // 只有设置开关告警打开时才处理
            if (_switchForWarning == YES) {
                [self warningTime:time withDate:[output date]];
                
            }
        }
        
        writeToHistory = YES;
    }
    
    return writeToHistory;
}


- (void)addTime:(NSTimeInterval)time withDate:(NSTimeInterval)date
{
    _totalTime += time;
    _historyCnt++;
    if (_historyCnt) {
        _avgTime = _totalTime/_historyCnt;
    }
    
    if (_historyCnt == 1) {
        _maxTime = _minTime = time;
    } else if (time > _maxTime) {
        _maxTime = time;
    } else if (time < _minTime) {
        _minTime = time;
    }
    
    GTProfilerValue *value = [[GTProfilerValue alloc] initWithDate:date time:time];
    [_history addObject:value];
    [value release];
    
}

- (void)decTime:(NSTimeInterval)time
{
    _totalTime -= time;
    _historyCnt--;
    if (_historyCnt) {
        _avgTime = _totalTime/_historyCnt;
    }
}

- (void)clearHistroy
{
    [self setShowWarning:NO];
    [_history removeAllObjects];
    [_upperWarningList deleteAll];
    [_lowerWarningList deleteAll];
    
    _historyCnt = 0;
    _totalTime = 0;
    _avgTime = 0;
    _maxTime = 0;
    _minTime = 0;
    
    [self fileClearThreadStart];
}

- (void)notifyParaDelegate
{
    if (_paraDelegate != nil) {
        if (_status == GTParaOnDisabled) {
            if ([_paraDelegate respondsToSelector:@selector(switchDisable)]) {
                [_paraDelegate switchDisable];
            }
        } else {
            if ([_paraDelegate respondsToSelector:@selector(switchEnable)]) {
                [_paraDelegate switchEnable];
            }
        }
    }
}

- (void)setStatus:(NSUInteger)status
{
    _status = status;    
    [self notifyParaDelegate];
}


- (NSMutableDictionary *)dictionaryForSave
{
    NSMutableDictionary* dic = [[[NSMutableDictionary alloc] init] autorelease];
    
    [dic setValue:[[_history copy] autorelease] forKey:@"history"];
    [dic setValue:[_dataInfo key] forKey:@"key"];
    [dic setValue:[_dataInfo alias] forKey:@"alias"];
    
    GTOutputValue *value = [_dataInfo value];
    [dic setValue:[value content] forKey:@"value"];
    
    if ([_history count] > 0) {
        GTHistroyValue *value = [_history objectAtIndex:0];
        [dic setValue:[NSString stringWithDate:[value date]] forKey:@"begin date"];
        
        //取第一条数据，记录对应的类名
        [self setRecordClassStr:NSStringFromClass([value class])];
        
        value = [_history objectAtIndex:([_history count] - 1)];
        [dic setValue:[NSString stringWithDate:[value date]] forKey:@"end date"];
    }
    
    [dic setValue:[NSString stringWithFormat:@"%lu", (unsigned long)_historyCnt] forKey:@"count"];
    
    //对于用户自定义的类型则对应min，max，avg无效，不需要保存
    if ((_paraDelegate) && [_paraDelegate respondsToSelector:@selector(objForHistory)]) {
        [dic setValue:@"false" forKey:@"infoEx"];
    } else {
        [dic setValue:@"true" forKey:@"infoEx"];
        
        [dic setValue:[NSString stringWithFormat:@"%.f", _minTime] forKey:@"min"];
        [dic setValue:[NSString stringWithFormat:@"%.f", _maxTime] forKey:@"max"];
        [dic setValue:[NSString stringWithFormat:@"%.f", _avgTime] forKey:@"avg"];
    }
    
    
    return dic;
}


- (void)saveFile:(NSString *)fileName inThread:(BOOL)inThread
{
    [self setFileName:fileName];
    
    NSMutableDictionary *dic = [self dictionaryForSave];
    
    NSDateFormatter * formatter = [[GTConfig sharedInstance] formatter];
	[formatter setDateFormat:@"MM-dd HH:mm:ss"];
    NSString *dateStr = [formatter stringFromDate:[NSDate date]];
    
    //保存文件时需要时间信息
    [dic setValue:dateStr forKey:@"date"];
    
    if (inThread) {
        NSThread *thread = [[[NSThread alloc] initWithTarget:self selector:@selector(saveFile:) object:dic] autorelease];
        thread.name = @"saveFile";
        [thread start];
    } else {
        //直接调用
        [self saveFile:dic];
    }
}

- (void)saveFile:(NSDictionary *)dic
{
    @autoreleasepool {
        NSString *fileName = [NSString stringWithFormat:@"%@_%@", [dic objectForKey:@"date"], _fileName];
        NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:M_GT_PARA_OUT_DIR fileName:fileName ofType:M_GT_FILE_TYPE_CSV];
        
        [self exportCSV:filePath param:dic];
    }
    
}


- (void)createFile:(NSString *)filePath {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    [fileManager removeItemAtPath:filePath error:nil];
    
    if (![fileManager createFileAtPath:filePath contents:nil attributes:nil]) {
        NSLog(@"Create file error!");
    }
}


//保存CSV文件格式
- (void)exportCSV:(NSString *)filePath param:(NSDictionary *)dic {
    [self createFile:filePath];

    NSString *row = nil;
    NSOutputStream *output = [[NSOutputStream alloc] initToFileAtPath:filePath append:YES];
    [output open];
    
    if (![output hasSpaceAvailable]) {
        NSLog(@"Not enough space");
    } else {
        // 组装头部信息
//        NSMutableString *body = [[NSMutableString alloc] initWithCapacity:M_GT_MB];
//        
//        [body appendFormat:@"key,%@\r\n", [dic objectForKey:@"key"]];
//        [body appendFormat:@"alias,%@\r\n", [dic objectForKey:@"alias"]];
//        [body appendFormat:@"value,%@\r\n", [dic objectForKey:@"value"]];
//        [body appendFormat:@"begin date,%@\r\n", [dic objectForKey:@"begin date"]];
//        [body appendFormat:@"end date,%@\r\n", [dic objectForKey:@"end date"]];
//        [body appendFormat:@"count,%@\r\n", [dic objectForKey:@"count"]];
//        
//        [body appendFormat:@"\r\n"];
        
        GTMutableCString *cString = [[GTMutableCString alloc] init];
        char buffer[M_GT_SIZE_1024];
        memset(buffer, 0, M_GT_SIZE_1024);
        
        snprintf(buffer, M_GT_SIZE_1024 - 1, "key,%s\r\nalias,%s\r\nvalue,%s\r\nbegin date,%s\r\nend date,%s\r\ncount,%s\r\n\r\n", [[dic objectForKey:@"key"] UTF8String], [[dic objectForKey:@"alias"] UTF8String], [[dic objectForKey:@"value"] UTF8String], [[dic objectForKey:@"begin date"] UTF8String], [[dic objectForKey:@"end date"] UTF8String], [[dic objectForKey:@"count"] UTF8String]);
        [cString appendCString:buffer length:strlen(buffer)];
        
        NSString *infoEx = [dic objectForKey:@"infoEx"];
        if ([infoEx isEqualToString:@"true"]) {
//            [body appendFormat:@"min,%@\r\n", [dic objectForKey:@"min"]];
//            [body appendFormat:@"max,%@\r\n", [dic objectForKey:@"max"]];
//            [body appendFormat:@"avg,%@\r\n", [dic objectForKey:@"avg"]];
//            [body appendFormat:@"\r\n"];
            
            snprintf(buffer, M_GT_SIZE_1024 - 1, "min,%s\r\nmax,%s\r\navg,%s\r\n\r\n", [[dic objectForKey:@"min"] UTF8String], [[dic objectForKey:@"max"] UTF8String], [[dic objectForKey:@"avg"] UTF8String]);
            [cString appendCString:buffer length:strlen(buffer)];
        }
        
        
        const char *str = nil;
        if ((_paraDelegate) && [_paraDelegate respondsToSelector:@selector(descriptionForObj)]) {
//            [body appendString:[_paraDelegate descriptionForObj]];
            str = [[_paraDelegate descriptionForObj] UTF8String];
            [cString appendCString:str length:strlen(str)];
        }
        
        // 组装历史数据对应的标题
        if (self.recordClassStr) {
//            row = [NSClassFromString(self.recordClassStr) rowTitle];
//            [body appendString:row];
            str = [[NSClassFromString(self.recordClassStr) rowTitle] UTF8String];
            [cString appendCString:str length:strlen(str)];
        }
        
        // 组装历史数据，读取对应的disk上的内存数据
        NSString *dirPath = [NSString stringWithFormat:@"%@%@/%@/", [[GTConfig sharedInstance] usrDir], M_GT_SYS_PARA_DIR, [dic objectForKey:@"key"]];
        
        NSMutableArray *fileNameList = [NSMutableArray arrayWithCapacity:10];
        NSArray *tmpList = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:dirPath error:nil];
        
        for (NSString *fileName in tmpList) {
            NSString *fullPath = [dirPath stringByAppendingPathComponent:fileName];
            if ([[NSFileManager defaultManager] fileExistsAtPath:fullPath]) {
                //这里校验是否是TXT文本文件
                if ([[fileName pathExtension] isEqualToString:M_GT_FILE_TYPE_TXT]) {
                    [fileNameList addObject:fileName];
                }
            }
        }
        
        NSArray*sortedArray = [fileNameList sortedArrayUsingSelector:@selector(fileNumCompare:)];
        for (int i = 0; i < [sortedArray count]; i++) {
            //按顺序将每个文件的数据写入用户需要保存的文件
            NSString *filePath = [dirPath stringByAppendingPathComponent:[sortedArray objectAtIndex:i]];
            row = [NSString stringWithContentsOfFile:filePath usedEncoding:nil error:nil];
            if (row != nil) {
//                [body appendString:row];
                str = [row UTF8String];
                [cString appendCString:str length:strlen(str)];
            }
        }
        
        
        // 组装历史数据，dic对应的是当前的内存数据
        NSArray *array = [dic objectForKey:@"history"];
        
        for (int i = 0; i < [array count]; i++) {
            GTHistroyValue *value = [array objectAtIndex:i];
            [value appendRowWithCString:cString];
//            row = [value rowStr];
//            [body appendString:row];
        }
        
        // 写入历史数据
//        const uint8_t *bodyString = (const uint8_t *)[body cStringUsingEncoding:NSUTF8StringEncoding];
//        NSInteger bodyLength = [body lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
//        [body release];
        
//        NSInteger result = [output write:bodyString maxLength:bodyLength];
        NSInteger result = [output write:(const uint8_t *)[cString bytes] maxLength:[cString bytesLen]];
        if (result <= 0) {
            NSLog(@"write error.");
        }
        [cString release];
        [output close];
    }
    
    [output release];
}

#pragma mark - 自动保存磁盘处理

//保存到系统文件
+ (void)exportDisk:(NSDictionary *)dic {
    NSString *key = [dic objectForKey:@"key"];
    
    NSString *dirPath = [NSString stringWithFormat:@"%@%@/%@/", [[GTConfig sharedInstance] usrDir], M_GT_SYS_PARA_DIR, key];
    //对应目录不存在则创建一个新的目录
    [[GTConfig sharedInstance] dirCreateIfNotExists:dirPath];
    
    NSUInteger histroyIndex = [[dic objectForKey:@"historyIndex"] integerValue];
    
    NSArray *array = [dic objectForKey:@"history"];
    
//    NSUInteger fileMaxRecordCnt = [GT_OC_IN_GET(@"单个文件记录数", NO, 0) integerValue];
    NSUInteger fileMaxRecordCnt = M_GT_PARA_AUTOSAVE_FILE_RECORD_CNT_MAX;

    for (NSUInteger j = histroyIndex/fileMaxRecordCnt; j <= (histroyIndex+[array count])/fileMaxRecordCnt; j++) {
        
        //固定记录数保存一个文件名
//        NSString *fileName = [NSString stringWithFormat:@"%@_%.3u", key, j];
        char filePath[M_GT_SIZE_256] = {0};
        memset(filePath, 0, M_GT_SIZE_256);
        snprintf(filePath, M_GT_SIZE_256 - 1, "%s%s_%.3lu.txt", [dirPath UTF8String], [key UTF8String], (unsigned long)j);
//        NSString *filePath = [NSString stringWithFormat:@"%@%@_%.3u.%@", dirPath, key, j, M_GT_FILE_TYPE_TXT];
//        [[GTConfig sharedInstance] pathForDir:dirName fileName:fileName ofType:M_GT_FILE_TYPE_TXT];
        
        [NSThread sleepForTimeInterval:0.01];
        
        //追加的方式打开文件
        FILE *file = fopen(filePath, "a+");
        if (file) {
            //计算是第几段
            NSUInteger segment = j - histroyIndex/fileMaxRecordCnt;
            NSInteger startIndex;
            if (segment == 0) {
                startIndex = 0;
            } else {
                startIndex = segment*fileMaxRecordCnt - histroyIndex%fileMaxRecordCnt;
            }
            
            NSInteger endIndex = (segment+1)*fileMaxRecordCnt - histroyIndex%fileMaxRecordCnt;
            endIndex = MIN([array count], endIndex);
            
            // 写入历史数据
            NSArray *array = [dic objectForKey:@"history"];
            
//            GT_OC_LOG_D(@"GTSys", @"写入[%u-%u]%u条记录到[%@]", histroyIndex + startIndex, histroyIndex + endIndex - 1, (endIndex - startIndex), fileName);
//            NSLog(@"写入[%u-%u]%u条记录到[%@]", histroyIndex + startIndex, histroyIndex + endIndex - 1, (endIndex - startIndex), fileName);
            GTMutableCString *cString = [[GTMutableCString alloc] init];
            
            for (NSUInteger i = startIndex; i < endIndex; i++) {
                GTHistroyValue *value = [array objectAtIndex:i];
                
                //写入每行的数据信息
//                NSString *row = [value rowStr];
//                fprintf(file, "%s", [row UTF8String]);
//                for (int i = 0; i < 10; i++) {
//                    [value appendRowWithCString:cString];
//                }
                [value appendRowWithCString:cString];
                
            }
            
            fprintf(file, "%s", [cString bytes]);
            [cString release];
            fflush(file);
            fclose(file);
        }
    }
    
}

- (void)notifyUI
{
    if (_showDelegate && [self respondsToSelector:@selector(notifyWillSaveDisk:)]) {
        NSMutableDictionary *dict = [[[NSMutableDictionary alloc] init] autorelease];
        [dict setObject:_history forKey:@"history"];
        [dict setObject:[NSNumber numberWithInteger:_historyCnt] forKey:@"historyCnt"];
        [self performSelectorOnMainThread:@selector(notifyWillSaveDisk:) withObject:dict waitUntilDone:YES];
    }
}

- (void)notifyWillSaveDisk:(NSDictionary *)dict
{
    if (_showDelegate && [_showDelegate respondsToSelector:@selector(willSaveDisk:)]) {
        [_showDelegate willSaveDisk:dict];
    }
}

#pragma mark 删除文件

- (void)threadEnd
{
    if (_fileOpThread != nil) {
        [_fileOpThread cancel];
        [_fileOpThread release];
        _fileOpThread = nil;
    }
}

//删除对应目录
- (BOOL)fileClearThreadStart
{
    if (_fileOpThread == nil) {
        NSMutableDictionary* dic = [[[NSMutableDictionary alloc] init] autorelease];
        
        [dic setValue:[_dataInfo key] forKey:[_dataInfo key]];
        
        _fileOpThread = [[NSThread alloc] initWithTarget:self selector:@selector(clearThreadProc:) object:dic];
        _fileOpThread.name = [NSString stringWithFormat:@"GTClear_%@", NSStringFromClass([self class])];
        [_fileOpThread start];
        return YES;
    }
    return NO;
}

- (void)clearThreadProc:(NSDictionary *)dic
{
    @autoreleasepool {
        if ([[dic allKeys] count] > 0) {
            //有指定的key，删除对应key文件
            for (int i = 0; i < [[dic allKeys] count]; i++) {
                NSString *key = [[dic allKeys] objectAtIndex:i];
                NSString *dirPath = [NSString stringWithFormat:@"%@%@/%@/", [[GTConfig sharedInstance] usrDir], M_GT_SYS_PARA_DIR, key];
                
                //删除目录
                NSFileManager *fileManager = [NSFileManager defaultManager];
                [fileManager removeItemAtPath:dirPath error:nil];
            }
        }
        
        //删除完毕，通知主线程
        [self performSelectorOnMainThread:@selector(threadEnd) withObject:nil waitUntilDone:NO];
        
        //    [NSThread exit];
    }
    
}

@end


#endif
