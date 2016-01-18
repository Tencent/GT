//
//  GTMTA.h
//  GTMTA-SDK
//
//  Created by WQY on 12-11-5.
//  Copyright (c) 2012年 WQY. All rights reserved.
//

#import <Foundation/Foundation.h>

#define MTA_SDK_VERSION @"1.4.6"
#define MTA_APP_USER_VERSION  @"MTA_USER_APP_VER" 

typedef enum {
    GTMTA_SUCCESS = 0,
    GTMTA_FAILURE = 1,
    GTMTA_LOGIC_FAILURE = 2
} GTMTAAppMonitorErrorType;

@interface GTMTAAppMonitorStat : NSObject
@property (nonatomic, retain) NSString* interface;  //监控业务接口名
@property uint32_t requestPackageSize;              //上传请求包量，单位字节
@property uint32_t responsePackageSize;             //接收应答包量，单位字节
@property uint64_t consumedMilliseconds;            //消耗的时间，单位毫秒
@property int32_t returnCode;                       //业务返回的应答码
@property GTMTAAppMonitorErrorType resultType;        //业务返回类型
@property uint32_t sampling;                        //上报采样率，默认0含义为无采样
@end


@interface GTMTA : NSObject
+(void) startWithAppkey:(NSString*) appkey;
+(BOOL) startWithAppkey:(NSString*) appkey checkedSdkVersion:(NSString*)ver;

+(void) trackPageViewBegin:(NSString*) page;
+(void) trackPageViewEnd:(NSString*) page;

+(void) trackError:(NSString*)error;
+(void) trackException:(NSException*)exception;

+(void) trackCustomEvent:(NSString*)event_id args:(NSArray*) array;
+(void) trackCustomEventBegin:(NSString*)event_id args:(NSArray*) array;
+(void) trackCustomEventEnd:(NSString*)event_id args:(NSArray*) array;

+(void) trackCustomKeyValueEvent:(NSString*)event_id props:(NSDictionary*) kvs;
+(void) trackCustomKeyValueEventBegin:(NSString*)event_id props:(NSDictionary*) kvs;
+(void) trackCustomKeyValueEventEnd:(NSString*)event_id props:(NSDictionary*) kvs;
+(void) trackCustomKeyValueEventDuration:(uint32_t)seconds withEventid:(NSString*)event_id props:(NSDictionary*) kvs;

+(void) commitCachedStats:(int32_t) maxStatCount;

+(void) startNewSession;
+(void) stopSession;

+(void) reportAppMonitorStat:(GTMTAAppMonitorStat*)stat;

+(void) reportQQ:(NSString*)qq;

+(void) reportAccount:(NSString *)account type:(uint32_t)type ext:(NSString *)ext;

+(void) trackGameUser:(NSString*)uid world:(NSString*)wd level:(NSString*)lev;

+(NSString *) getMtaUDID;

/*********************************************************************
 以下是需要自定义的增强接口,
 appkey:指定appkey进行上报
 isRealTime:如果为true，则进行实时上报。如果为false,则进行默认GTMTAConfig中的上报策略进行上报
*********************************************************************/
+(void) trackPageViewBegin:(NSString*) page appkey:(NSString *)appkey;
+(void) trackPageViewEnd:(NSString*) page appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) trackError:(NSString*)error appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;
+(void) trackException:(NSException*)exception appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) trackCustomEvent:(NSString*)event_id args:(NSArray*) array appkey:(NSString *)appkey  isRealTime:(BOOL)isRealTime;
+(void) trackCustomEventBegin:(NSString*)event_id args:(NSArray*) array appkey:(NSString *)appkey;
+(void) trackCustomEventEnd:(NSString*)event_id args:(NSArray*) array appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) trackCustomKeyValueEvent:(NSString*)event_id props:(NSDictionary*) kvs appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;
+(void) trackCustomKeyValueEventBegin:(NSString*)event_id props:(NSDictionary*) kvs appkey:(NSString *)appkey;
+(void) trackCustomKeyValueEventEnd:(NSString*)event_id props:(NSDictionary*) kvs appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;
+(void) trackCustomKeyValueEventDuration:(uint32_t) seconds withEventid:(NSString*)event_id props:(NSDictionary*) kvs appKey:(NSString*)appkey isRealTime:(BOOL)isRealTime;

+(void) reportAppMonitorStat:(GTMTAAppMonitorStat*)stat appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) reportQQ:(NSString*)qq appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) reportAccount:(NSString *)account type:(uint32_t)type ext:(NSString *)ext appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) trackGameUser:(NSString*)uid world:(NSString*)wd level:(NSString*)lev appkey:(NSString *)appkey isRealTime:(BOOL)isRealTime;

+(void) startNewSession:(BOOL)isRealTime;
@end
