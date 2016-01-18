//
//  StatConfig.h
//  TA-SDK
//
//  Created by WQY on 12-11-5.
//  Copyright (c) 2012年 WQY. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    GTMTA_STRATEGY_INSTANT = 1,            //实时上报
    GTMTA_STRATEGY_BATCH = 2,              //批量上报，达到缓存临界值时触发发送
    GTMTA_STRATEGY_APP_LAUNCH = 3,         //应用启动时发送
    GTMTA_STRATEGY_ONLY_WIFI = 4,          //仅在WIFI网络下发送
    GTMTA_STRATEGY_PERIOD = 5,             //每间隔一定最小时间发送，默认24小时
    GTMTA_STRATEGY_DEVELOPER = 6,          //开发者在代码中主动调用发送行为
    GTMTA_STRATEGY_ONLY_WIFI_NO_CACHE = 7  //仅在WIFI网络下发送, 发送失败以及非WIFI网络情况下不缓存数据
} GTMTAStatReportStrategy;

@interface GTMTAConfig : NSObject
@property BOOL debugEnable;                     //debug开关
@property uint32_t sessionTimeoutSecs;          //Session超时时长，默认30秒
@property GTMTAStatReportStrategy reportStrategy;    //统计上报策略
@property (nonatomic, retain) NSString* appkey; //应用的统计AppKey
@property (nonatomic, retain) NSString* channel;//渠道名，默认为"appstore"
@property uint32_t maxStoreEventCount;          //最大缓存的未发送的统计消息，默认1024
@property uint32_t maxLoadEventCount;           //一次最大加载未发送的缓存消息，默认30
@property uint32_t minBatchReportCount;         //统计上报策略为BATCH时，触发上报时最小缓存消息数，默认30
@property uint32_t maxSendRetryCount;           //发送失败最大重试数，默认3
@property uint32_t sendPeriodMinutes;           //上报策略为PERIOD时发送间隔，单位分钟，默认一天（1440分钟）
@property uint32_t maxParallelTimingEvents;     //最大并行统计的时长事件数，默认1024
@property BOOL  smartReporting;                 //智能上报开关：在WIFI模式下实时上报，默认TRUE
@property BOOL  autoExceptionCaught;            //智能捕获未catch的异常，默认TRUE；设置为False需要在startWithAppkey前调用
@property uint32_t maxReportEventLength;        //最大上报的单条event长度，超过不上报
@property (nonatomic, retain) NSString* qq;           //QQ号或者帐号
@property (nonatomic, retain) NSString* account;      //帐号
@property int8_t accountType;                       //帐号类型
@property (nonatomic, retain) NSString* accountExt;   //帐号的扩展信息
@property BOOL statEnable;

@property (nonatomic, retain) NSString* customerUserID;
@property (nonatomic, retain) NSString* customerAppVersion;
@property (nonatomic, retain) NSString* ifa;
@property (nonatomic, retain) NSString* pushDeviceToken;

@property (nonatomic, retain) NSString* statReportURL; //自定义的上报url
@property int32_t maxSessionStatReportCount;

@property (nonatomic,retain) NSString* op;          //运营商
@property (nonatomic,retain) NSString* cn;          //网络类型
@property (nonatomic,retain) NSString* sp;   //测速结果
typedef void (^GTerrorCallback)(NSString *);
@property (nonatomic,copy) GTerrorCallback crashCallback; //用于crash日志删除前回调， param为crash JSON数据
-(id) init;
-(NSString*)getCustomProperty:(NSString*) key default:(NSString*) v;
+(id) getInstance;
@end
