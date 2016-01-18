//
//  GTOutput.m
//  GTKit
//
//  Created   on 12-5-29.
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

#import "GTOutputList.h"
#import "GTConfig.h"
#import "GTLogConfig.h"
#import "GTProfilerValue.h"
#import "GTProgressHUD.h"

@implementation GTOutputList


M_GT_DEF_SINGLETION(GTOutputList);
@synthesize acArray = _acArray;
@synthesize normalArray = _normalArray;
@synthesize disabledArray = _disabledArray;
@synthesize dirName = _dirName;

#pragma mark -

- (id)init
{
    self = [super init];
    if (self) {
        _acArray = [[NSMutableArray alloc] init];
        _normalArray = [[NSMutableArray alloc] init];
        _disabledArray = [[NSMutableArray alloc] init];
        
        _showWarning = NO;
        self.dirName = @"GW_data";
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleOutWarning:) name:M_GT_NOTIFICATION_OUT_OBJ_WARNING object:nil];
        
        _fileOpThread = nil;
        _threadReady = YES;
        //清除老的历史数据 输入nil表示删除全部
        [self fileClearThreadStart:nil];
        
//        NSArray *array = [NSArray arrayWithObjects:@"1000", @"10000", @"100000", @"200000", nil];
//        GT_OC_IN_REGISTER(@"触发写的总记录数", @"WTC", array);
//        
//        array = [NSArray arrayWithObjects:@"100", @"1000", @"2000", @"3000", nil];
//        GT_OC_IN_REGISTER(@"触发写的单个记录数", @"WFC", array);
//        
//        array = [NSArray arrayWithObjects:@"2000", @"10000", @"20000", @"30000", nil];
//        GT_OC_IN_REGISTER(@"单个文件记录数", @"FCNT", array);
    }
    
    return self;
}

- (void)dealloc
{
    [_acArray removeAllObjects];
    [_acArray release];
    
    [_normalArray removeAllObjects];
    [_normalArray release];
    
    [_disabledArray removeAllObjects];
    [_disabledArray release];
    
    self.dirName = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:M_GT_NOTIFICATION_OUT_OBJ_WARNING object:nil];
    [super dealloc];
}


#pragma mark - 告警相关处理
- (void)handleOutWarning:(NSNotification *)n
{
    BOOL showWarning = NO;
    
    NSDictionary* dic = [n userInfo];
    
    NSString *result = [dic objectForKey:@"result"];
    
    //本次通知表示产生一次告警就通知一次
    if ([result isEqualToString:@"warning"]) {
        _showWarning = YES;
        [self postWarningNotification];
        return;
    }
    
    
    for (int  i = 0; i < [_keys count]; i++) {
        GTOutputObject *obj = [_objs objectForKey:[_keys objectAtIndex:i]];
        if ([obj showWarning] == YES) {
            showWarning = YES;
            break;
        }
    }
    
    //刷新值
    _showWarning = showWarning;
    
    //当前告警全部清除需要通知一次
    if (_showWarning == NO) {
        [self postWarningNotification];
    }
    
}

- (void)postWarningNotification
{
    NSMutableDictionary* dic = [[[NSMutableDictionary alloc] init] autorelease];
    
    if (_showWarning) {
        [dic setValue:@"warning" forKey:@"result"];
    } else {
        [dic setValue:@"normal" forKey:@"result"];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_OUT_LST_WARNING object:nil userInfo:dic];
}

#pragma mark - 对象维护基本操作

- (GTOutputValue *)dataValueForKey:(NSString *)key
{
    id obj = [super objectForKey:key];
    if (obj == nil) {
        return nil;
    }
    
    return [[obj dataInfo] value];
}


- (BOOL)isEnableForKey:(NSString *)key
{
    GTOutputObject *obj = [self objectForKey:key];
    if ([obj status] == GTParaOnDisabled) {
        return NO;
    }
    return YES;
}

- (void)defaultOnAC:(NSString*)key1 key2:(NSString*)key2 key3:(NSString*)key3
{
    //清除之前设置在悬浮框上的数据
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [_objs objectForKey:key];
        
        if ([obj status] == GTParaOnAc) {
            [self setStatus:GTParaOnNormal forKey:key];
        }
    }
    
    // 保证顺序为key1 key2 key3
    [self setStatus:GTParaOnAc forKey:key1];
    [self setStatus:GTParaOnAc forKey:key2];
    [self setStatus:GTParaOnAc forKey:key3];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    return;
}


- (void)defaultOnDisabled:(NSArray *)array
{
    //清除之前设置的数据
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [_objs objectForKey:key];
        
        if ([obj status] == GTParaOnDisabled) {
            [self setStatus:GTParaOnNormal forKey:key];
        }
    }
    
    //按顺序设置在Disabled的数据
    for (int i = 0; i < [array count]; i++) {
        id key = [array objectAtIndex:i];
        [self setStatus:GTParaOnDisabled forKey:key];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:M_GT_NOTIFICATION_LIST_UPDATE object:nil];
    return;
}

- (NSMutableArray *)arrayForStatus:(NSUInteger)status
{
    if (status == GTParaOnNormal) {
        return _normalArray;
    } else if (status == GTParaOnAc) {
        return _acArray;
    } else if (status == GTParaOnDisabled) {
        return _disabledArray;
    } else {
        return nil;
    }
}

- (void)setStatus:(NSUInteger)status forKey:(NSString *)key
{
    if (key == nil) {
        return;
    }
    
    GTOutputObject *obj = [self objectForKey:key];
    if (obj == nil) {
        NSLog(@"key:%@ obj nil", key);
        return;
    }
    if ([obj status] != status) {
        //删除旧的状态
        [[self arrayForStatus:[obj status]] removeObject:key];
    }
    
    //保存新的状态
    [[self arrayForStatus:status] removeObject:key];
    [[self arrayForStatus:status] addObject:key];
    
    [obj setStatus:status];

}

- (void)insertKey:(NSString *)key2 atKey:(NSString *)key1
{
    NSUInteger index = 0;
    GTOutputObject *obj = [self objectForKey:key1];
    NSMutableArray *array = [self arrayForStatus:[obj status]];
    
    index = [array indexOfObject:key1];
    if (index < [array count]) {
        [array removeObject:key2];
        [array insertObject:key2 atIndex:index];
    }
}

- (void)insertBackForKey:(NSString *)key2 atKey:(NSString *)key1
{
    NSUInteger index = 0;
    GTOutputObject *obj = [self objectForKey:key1];
    NSMutableArray *array = [self arrayForStatus:[obj status]];

    index = [array indexOfObject:key1];
    index++;
    if (index < [array count]) {
        [array removeObject:key2];
        [array insertObject:key2 atIndex:index];
    }
}


- (void)updateSectionArray
{
    [_disabledArray removeAllObjects];
    [_acArray removeAllObjects];
    [_normalArray removeAllObjects];
    
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        if ([[_objs objectForKey:key] status] == GTParaOnDisabled) {
            [_disabledArray addObject:key];
        } else if ([[_objs objectForKey:key] status] == GTParaOnAc) {
            [_acArray addObject:key];
        } else if ([[_objs objectForKey:key] status] == GTParaOnNormal) {
            [_normalArray addObject:key];
        }
    }
}

#pragma mark - 常用的接口实现
- (void)addKey:(NSString*)key alias:(NSString*)alias value:(GTOutputValue *)value
{
    GTOutputObject *obj = nil;
    obj = [[GTOutputObject alloc] initWithKey:key alias:alias value:value];
    [self setObject:obj forKey:key];
    [obj release];
    
    [[GTOutputList sharedInstance] setStatus:GTParaOnNormal forKey:key];
}

- (void)setDataValue:(GTOutputValue *)value forKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    
    if([obj status] == GTParaOnDisabled) {
        return;
    }
    
    BOOL result = [obj setDataValue:value];
    
//    NSUInteger recordCnt = [GT_OC_IN_GET(@"触发写的总记录数", NO, 0) integerValue];
    NSUInteger recordCnt = M_GT_PARA_AUTOSAVE_RECORD_CNT;

    //如果有保存到历史记录中，则计数加1
    if ((result == YES) && (recordCnt != 0)){
        _recordCnt ++;
        if (_recordCnt >= recordCnt) {
            //启动自动保存数据，若没有启动线程，表示数据正在保存中，等待下次启动线程保存
            if ([self fileSaveThreadStart]) {
                //成功，计数清零
                _recordCnt = 0;
            }
        }
    }
    
    return;
}

- (void)clearAllHistroy
{
    NSMutableArray *array = [NSMutableArray array];
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [self objectForKey:key];
        
        //只处理非Disabled区并且被勾选的采集项
        if (([obj status] != GTParaOnDisabled)) {
            if ([obj switchForHistory] == GTParaHistroyOn) {
                [obj clearHistroy];
                [array addObject:[[obj dataInfo] key]];
            }
        }
    }
    
    //清除对应磁盘自动保存的数据
    if ([array count] > 0) {
        //计数清零
        _recordCnt = 0;
        
        //如果正在保存中线程没释放会导致清除线程没有启动，这里需要先取消线程
        [self threadEnd];
        
        
        if (![self fileClearThreadStart:array]) {
            NSLog(@"fileClearThreadStart error");
        }
    }
    
}

- (void)saveHistroyForDirName:(NSString *)dirName
{
    [self saveHistroyForDirName:dirName needClear:NO];
}


- (void)saveFileInThread:(NSDictionary *)dicAll
{
    @autoreleasepool {
        NSString *dir = [NSString stringWithFormat:@"%@/%@", M_GT_PARA_OUT_DIR, _dirName];
        
        // 如果需要清楚，则对应目录删除干净
//        if (needClear) {
//            
//        }
        
        for (int i = 0; i < [[dicAll allKeys] count]; i++) {
            id key = [[dicAll allKeys] objectAtIndex:i];
            
            NSDictionary* dic = [dicAll objectForKey:key];
            GTOutputObject *obj = [self objectForKey:key];
            
            NSString *fileName = [NSString stringWithFormat:@"%@_%@", [[obj dataInfo] key], [dic objectForKey:@"date"]];
            NSString *filePath = [[GTConfig sharedInstance] pathForDirByCreated:dir fileName:fileName ofType:M_GT_FILE_TYPE_CSV];
            
            [obj exportCSV:filePath param:dic];
        }
    }
    
}

- (void)clearHistroyForKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    
    [obj clearHistroy];
}

- (void)saveHistroyForKey:(NSString*)key fileName:(NSString *)fileName inThread:(BOOL)inThread
{
    GTOutputObject *obj = [self objectForKey:key];
    
    [obj saveFile:fileName inThread:inThread];
}

- (void)saveHistroyForDirName:(NSString *)dirName needClear:(BOOL)needClear
{
    [self setDirName:dirName];
    
    NSDateFormatter * formatter = [[GTConfig sharedInstance] formatter];
    [formatter setDateFormat:@"MM-dd HH-mm-ss"];
    NSString *dateStr = [formatter stringFromDate:[NSDate date]];
    
    NSDictionary *dicAll = [[[NSMutableDictionary alloc] init] autorelease];
    
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [self objectForKey:key];
        
        //只处理非Disabled区
        if (([obj status] != GTParaOnDisabled)) {
            //只保存需要采集项
            if ([obj switchForHistory] == GTParaHistroyOn) {
                NSMutableDictionary *dic = [obj dictionaryForSave];
                
                //保存文件时需要时间信息
                [dic setValue:dateStr forKey:@"date"];
                
                [dicAll setValue:dic forKey:[[obj dataInfo] key]];
            }
        }
        
    }
    
    //有采集项数据时才保存
    if ([[dicAll allKeys] count] > 0) {
        NSThread *thread = [[[NSThread alloc] initWithTarget:self selector:@selector(saveFileInThread:) object:dicAll] autorelease];
        thread.name = @"saveFileInThread";
        [thread start];
    }
}

- (void)setVC:(NSString *)vc forKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    
    [obj setVcForDetail:vc];
}

- (void)setParaDelegate:(id <GTParaDelegate>)delegate forKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    [obj setParaDelegate:delegate];
    //触发switchEnable或switchDisabled
    [obj notifyParaDelegate];
}

- (void)setOutOfRangeLower:(double)lowerValue upper:(double)upperValue lasting:(NSTimeInterval)lasting forKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    
    [obj setThresholdInterval:lasting];
    
    //设置告警默认为打开告警功能
    [obj setSwitchForWarning:YES];
    
    //如果有设置无上限或无下限，则直接赋值
    if ((lowerValue == M_GT_LOWER_WARNING_INVALID) || (upperValue == M_GT_UPPER_WARNING_INVALID)) {
        [obj setLowerThresholdValue:lowerValue];
        [obj setUpperThresholdValue:upperValue];
        return;
    }
    
    //若用户上限值小于下限值则自动纠正
    if (lowerValue < upperValue) {
        [obj setLowerThresholdValue:lowerValue];
        [obj setUpperThresholdValue:upperValue];
    } else {
        [obj setLowerThresholdValue:upperValue];
        [obj setUpperThresholdValue:lowerValue];
    }
    
}

- (void)setHistroyDisabledForKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    
    [obj setSwitchForHistory:GTParaHistroyDisabled];
}

- (void)setHistroyEnableChecked:(BOOL)on forKey:(NSString*)key
{
    GTOutputObject *obj = [self objectForKey:key];
    
    if (on == YES) {
        [obj setSwitchForHistory:GTParaHistroyOn];
    } else {
        [obj setSwitchForHistory:GTParaHistroyOff];
    }
}


//所有非Disabled区有选择
- (BOOL)hasItemHistoryOn
{
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [self objectForKey:key];
        
        //非Disabled区
        if (([obj status] != GTParaOnDisabled)) {
            //有采集项
            if ([obj switchForHistory] == GTParaHistroyOn) {
                return YES;
            }
        }
        
    }
    
    return NO;
}

//所有非Disabled区都打开History勾选功能
- (BOOL)itemsAllHistoryOn
{
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [self objectForKey:key];
        
        //非Disabled区
        if (([obj status] != GTParaOnDisabled)) {
            //没有采集项
            if ([obj switchForHistory] == GTParaHistroyOff) {
                return NO;
            }
        }
    }
    
    return YES;
}

- (void)setAllHistoryOn
{
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [self objectForKey:key];
        
        //非Disabled区
        if (([obj status] != GTParaOnDisabled)) {
        
            if ([obj switchForHistory] == GTParaHistroyOff) {
                //设置为打开History
                [obj setSwitchForHistory:GTParaHistroyOn];
            }
            
        }
    }
}

- (void)setAllHistoryOff
{
    for (int i = 0; i < [_keys count]; i++) {
        id key = [_keys objectAtIndex:i];
        GTOutputObject *obj = [self objectForKey:key];
        
        //非Disabled区
        if (([obj status] != GTParaOnDisabled)) {
            
            if ([obj switchForHistory] == GTParaHistroyOn) {
                //设置为关闭History
                [obj setSwitchForHistory:GTParaHistroyOff];
            }
            
        }
    }
}

#pragma mark - 线程保存文件相关操作

- (void)threadEnd
{
    if (_fileOpThread != nil) {
        [_fileOpThread cancel];
        [_fileOpThread release];
        _fileOpThread = nil;
        _threadReady = YES;
        
        //删除数据
        M_GT_SAFE_FREE(_dicAll);
        
    }
}

#pragma mark 保存文件
- (BOOL)fileSaveThreadStart
{
    @autoreleasepool {
        if ((_fileOpThread == nil) && _threadReady) {
            _threadReady = NO;
            if (_dicAll) {
                [_dicAll release];
            }
            _dicAll = [[NSMutableDictionary alloc] init];
            
            for (int i = 0; i < [_keys count]; i++) {
                id key = [_keys objectAtIndex:i];
                GTOutputObject *obj = [self objectForKey:key];
                
//                NSUInteger fileRecordMin = [GT_OC_IN_GET(@"触发写的单个记录数", NO, 0) integerValue];
                NSUInteger fileRecordMin = M_GT_PARA_AUTOSAVE_FILE_RECORD_CNT_MIN;

                //所有对象的历史数据达到记录条数的都全部保存到disk
                if ([[obj history] count] > fileRecordMin) {
//                    GT_OC_LOG_D(@"GTSys", @"触发[%@]写入[%u]条记录", key, [[obj history] count]);
//                    NSLog(@"触发[%@]写入[%u]条记录", key, [[obj history] count]);
                
                    NSMutableDictionary* dic = [[[NSMutableDictionary alloc] init] autorelease];
                    //取第一条数据，记录对应的类名
                    GTHistroyValue *value = [[obj history] objectAtIndex:0];
                    [obj setRecordClassStr:NSStringFromClass([value class])];
                    
                    //携带要保存的历史信息，和历史信息对应的全记录的起始下标
                    [dic setValue:[NSNumber numberWithInteger:MAX(0, ([obj historyCnt] - [[obj history] count]))] forKey:@"historyIndex"];
                    [dic setValue:[obj history] forKey:@"history"];
                    [dic setObject:key forKey:@"key"];
                    
                    [_dicAll setValue:dic forKey:key];
                    
                    //删除内存数据前先通知，便于UI曲线展示
                    [obj notifyUI];
                    
                    //重新分配历史队列，原先的队列保存在dic里
                    [obj setHistory:[[[NSMutableArray alloc] initWithCapacity:M_GT_PARA_AUTOSAVE_RECORD_CNT] autorelease]];
                }
            }
            
            if ([[_dicAll allKeys] count] > 0) {
                _fileOpThread = [[NSThread alloc] initWithTarget:self selector:@selector(saveThreadProc:) object:_dicAll];
                _fileOpThread.name = [NSString stringWithFormat:@"GTSave_%@", NSStringFromClass([self class])];
                _fileOpThread.threadPriority = 0.5;
                [_fileOpThread start];
            }
            
            _threadReady = YES;
            return YES;
        }
        
        return NO;
    }
}

- (void)saveThreadProc:(NSDictionary *)dicAll
{
    @autoreleasepool {
        for (int i = 0; i < [[dicAll allKeys] count]; i++) {
            id key = [[dicAll allKeys] objectAtIndex:i];
            
            NSDictionary* dic = [dicAll objectForKey:key];
            
            //追加方式保存历史数据
//            NSString *dir = [NSString stringWithFormat:@"%@%@/%@/", [[GTConfig sharedInstance] usrDir], M_GT_SYS_PARA_DIR, key];
//            NSString *filePath = [[GTConfig sharedInstance] pathForDir:dir fileName:key ofType:M_GT_FILE_TYPE_TXT];
            
            [GTOutputObject exportDisk:dic];
        }
        [NSThread sleepForTimeInterval:0.01];
        
        //保存完毕，通知主线程
        [self performSelectorOnMainThread:@selector(threadEnd) withObject:nil waitUntilDone:NO];
//        [NSThread exit];
    }
    
}

#pragma mark 删除文件
//array存储对应需要删除的key 若为nil表示删除对应的目录
- (BOOL)fileClearThreadStart:(NSArray *)array
{
    @autoreleasepool {
        if ((_fileOpThread == nil) && _threadReady) {
            _threadReady = NO;
            if (_dicAll) {
                [_dicAll release];
            }
            _dicAll = [[NSMutableDictionary alloc] init];
            
            if (array) {
                for (int i = 0; i < [array count]; i++) {
                    id key = [array objectAtIndex:i];
                    [_dicAll setValue:key forKey:key];
                }
            }
            
            _fileOpThread = [[NSThread alloc] initWithTarget:self selector:@selector(clearThreadProc:) object:_dicAll];
            _fileOpThread.name = [NSString stringWithFormat:@"GTClear_%@", NSStringFromClass([self class])];
            [_fileOpThread start];
            
            _threadReady = YES;
            return YES;
        }
        
        return NO;
    }
    
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
        } else {
            //没有指定的key，表示全部删除
            //删除M_GT_SYS_PARA_DIR目录
            NSString *filePath = [NSString stringWithFormat:@"%@%@/", [[GTConfig sharedInstance] usrDir], M_GT_SYS_PARA_DIR];
            NSFileManager *fileManager = [NSFileManager defaultManager];
            [fileManager removeItemAtPath:filePath error:nil];
        }
        
        //删除完毕，通知主线程
        [self performSelectorOnMainThread:@selector(threadEnd) withObject:nil waitUntilDone:NO];
        
        //    [NSThread exit];
    }
    
}

@end

#pragma mark - User Interface

#define M_GT_OUTPUT_LOG(op,k,v) \
GTOutputObject *obj = [[GTOutputList sharedInstance] objectForKey:k];\
if (writeToLog || [obj writeToLog]) {\
GT_OC_LOG_D(M_GT_TAG, @"%@ Output K:%@ V:%@", op, k, v);\
}


#pragma mark - OC

void func_addOutputForOC(NSString *key, NSString *alias)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(alias);
        
        [[GTOutputList sharedInstance] addKey:key alias:alias value:nil];
    }
    
}

NSString* func_getOutputForOC(NSString *key, BOOL writeToLog)
{
    @autoreleasepool {
        if (!key) {
            return nil;
        }
        if(![[GTOutputList sharedInstance] isEnableForKey:key]) {
            return nil;
        }
        GTOutputValue *value = [[GTOutputList sharedInstance] dataValueForKey:key];
        
        M_GT_OUTPUT_LOG(@"GET", key, [value content]);
        return [value content];
    }
    
}


void func_setOutputForOC(NSString *key, BOOL writeToLog, NSString * format,...)
{
    @autoreleasepool {
        if(![[GTOutputList sharedInstance] isEnableForKey:key]) {
            return;
        }
        NSTimeInterval now = [GTUtility timeIntervalSince1970];
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_OC_FORMAT_INIT;
        NSString * valueStr = M_GT_OC_FORMAT_STR;
        
        GTOutputValue *valueObj = [[GTOutputValue alloc] initWithDate:now content:valueStr];
        
        M_GT_OUTPUT_LOG(@"SET", key, valueStr);
        [[GTOutputList sharedInstance] setDataValue:valueObj forKey:key];
        [valueObj release];
    }
    
}

void func_setOutputWriteToLogForOC(NSString *key, BOOL writeToLog)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        GTOutputObject *obj = [[GTOutputList sharedInstance] objectForKey:key];
        [obj setWriteToLog:writeToLog];
    }
    
}

void func_setOutputVCForOC(NSString *key, NSString *vc)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        [[GTOutputList sharedInstance] setVC:vc forKey:key];
    }
    
}

void func_setOutputHistoryDisabledForOC(NSString *key)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        [[GTOutputList sharedInstance] setHistroyDisabledForKey:key];
    }
    
}

void func_setOutputHistoryCheckedForOC(NSString *key, BOOL selected)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        [[GTOutputList sharedInstance] setHistroyEnableChecked:selected forKey:key];
    }
    
}

void func_setWarningOutOfRangeForOC(NSString *key, NSTimeInterval lastingTime, double lowerValue, double upperValue)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        [[GTOutputList sharedInstance] setOutOfRangeLower:lowerValue upper:upperValue lasting:lastingTime forKey:key];
    }
    
}

void func_clearOutputHistoryForOC(NSString *key)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        [[GTOutputList sharedInstance] clearHistroyForKey:key];
    }
    
}

void func_saveOutputHistoryForOC(NSString *key, NSString *fileName)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(fileName);
        
        //开放API不启动线程保存
        [[GTOutputList sharedInstance] saveHistroyForKey:key fileName:fileName inThread:NO];
    }
    
}

void func_saveOutputHistoryAllForOC(NSString *dirName)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(dirName);
        
        //开放API不启动线程保存
        [[GTOutputList sharedInstance] saveHistroyForDirName:dirName];
    }
    
}


void func_setParaDelegateForOC(NSString *key, id<GTParaDelegate> delegate)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        [[GTOutputList sharedInstance] setParaDelegate:delegate forKey:key];
    }
    
}


void func_defaultOutputOnACForOC(NSString *key1, NSString *key2, NSString *key3)
{
    @autoreleasepool {
        [[GTOutputList sharedInstance] defaultOnAC:key1 key2:key2 key3:key3];
    }
    
}

void func_defaultOutputOnDisabledForOC(NSString * format,...)
{
    @autoreleasepool {
        NSMutableArray *array = [NSMutableArray array];
        
        va_list args;
        va_start(args, format);
        if (format)
        {
            [array addObject:format];
            NSString *otherString;
            while ((otherString = va_arg(args, NSString *)))
            {
                [array addObject:otherString];
            }
        }
        va_end(args);
        
        [[GTOutputList sharedInstance] defaultOnDisabled:array];
    }
    
}

#pragma mark - C

void func_addOutputForString(const char *key, const char *alias)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(alias);
        
        NSString * aliasStr = [NSString stringWithCString:alias encoding:NSUTF8StringEncoding];
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        
        [[GTOutputList sharedInstance] addKey:keyStr alias:aliasStr value:nil];
    }
    
}

const char* func_getOutputForString(const char *key, bool writeToLog)
{
    @autoreleasepool {
        if (!key) {
            return nil;
        }
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTOutputList sharedInstance] isEnableForKey:keyStr]) {
            return nil;
        }
        GTOutputValue * value = [[GTOutputList sharedInstance] dataValueForKey:keyStr];
        
        M_GT_OUTPUT_LOG(@"GET", keyStr, [value content]);
        return [[value content] UTF8String];
    }
    
}


void func_setOutputForString(const char *key, bool writeToLog, const char * format,...)
{
    NSTimeInterval now = [GTUtility timeIntervalSince1970];
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(format);
        
        M_GT_FORMAT_INIT;
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        if(![[GTOutputList sharedInstance] isEnableForKey:keyStr]) {
            return;
        }
        
        NSString * valueStr = M_GT_FORMAT_STR;
        GTOutputValue *valueObj = [[GTOutputValue alloc] initWithDate:now content:valueStr];
        
        M_GT_OUTPUT_LOG(@"SET", keyStr, valueStr);
        
        [[GTOutputList sharedInstance] setDataValue:valueObj forKey:keyStr];
        [valueObj release];
    }
    
}

void func_setOutputWriteToLog(const char *key, bool writeToLog)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        GTOutputObject *obj = [[GTOutputList sharedInstance] objectForKey:keyStr];
        [obj setWriteToLog:writeToLog];
    }
    
}

void func_setOutputHistoryDisabled(const char *key)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        [[GTOutputList sharedInstance] setHistroyDisabledForKey:keyStr];
    }
    
}

void func_setOutputHistoryChecked(const char *key, bool selected)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        [[GTOutputList sharedInstance] setHistroyEnableChecked:selected forKey:keyStr];
    }
    
}

void func_setWarningOutOfRange(const char *key, double lastingTime, double lowerValue, double upperValue)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        
        [[GTOutputList sharedInstance] setOutOfRangeLower:lowerValue upper:upperValue lasting:lastingTime forKey:keyStr];
    }
    
}

void func_clearOutputHistory(const char *key)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        [[GTOutputList sharedInstance] clearHistroyForKey:keyStr];
    }
    
}

void func_saveOutputHistory(const char *key, const char *fileName)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(key);
        M_GT_PTR_NULL_CHECK(fileName);
        NSString * keyStr = [NSString stringWithCString:key encoding:NSUTF8StringEncoding];
        NSString * fileNameStr = [NSString stringWithCString:fileName encoding:NSUTF8StringEncoding];
        
        [[GTOutputList sharedInstance] saveHistroyForKey:keyStr fileName:fileNameStr inThread:NO];
    }
    
}
void func_saveOutputHistoryAll(const char *dirName)
{
    @autoreleasepool {
        M_GT_PTR_NULL_CHECK(dirName);
        NSString * dirNameStr = [NSString stringWithCString:dirName encoding:NSUTF8StringEncoding];
        [[GTOutputList sharedInstance] saveHistroyForDirName:dirNameStr];
    }
    
}


void func_defaultOutputOnAC(const char *key1, const char *key2, const char *key3)
{
    @autoreleasepool {
        NSString * keyStr1 = nil;
        NSString * keyStr2 = nil;
        NSString * keyStr3 = nil;
        
        if (key1) {
            keyStr1 = [NSString stringWithCString:key1 encoding:NSUTF8StringEncoding];
        }
        
        if (key2) {
            keyStr2 = [NSString stringWithCString:key2 encoding:NSUTF8StringEncoding];
        }
        
        if (key3) {
            keyStr3 = [NSString stringWithCString:key3 encoding:NSUTF8StringEncoding];
        }
        
        [[GTOutputList sharedInstance] defaultOnAC:keyStr1 key2:keyStr2 key3:keyStr3];
    }
    
}

void func_defaultOutputOnDisabled(const char * format,...)
{
    @autoreleasepool {
        NSMutableArray *array = [NSMutableArray array];
        
        va_list args;
        va_start( args, format );
        if (format)
        {
            [array addObject:[NSString stringWithCString:format encoding:NSUTF8StringEncoding]];
            
            char *otherString;
            while ((otherString = va_arg(args, char *)))
            {
                [array addObject:[NSString stringWithCString:otherString encoding:NSUTF8StringEncoding]];
            }
        }
        va_end( args );
        
        [[GTOutputList sharedInstance] defaultOnDisabled:array];
    }
    
}

void func_defaultOutputAllOnDisabled()
{
    @autoreleasepool {
        for (int i = 0; i < [[GTOutputList sharedInstance].keys count]; i++) {
            id key = [[GTOutputList sharedInstance].keys objectAtIndex:i];
            [[GTOutputList sharedInstance] setStatus:GTParaOnDisabled forKey:key];
        }
    }
    
}
#endif
