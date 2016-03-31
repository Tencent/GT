package com.tencent.wstt.gt;
import com.tencent.wstt.gt.AidlTask;
import com.tencent.wstt.gt.PerfDigitalEntry;
import com.tencent.wstt.gt.PerfStringEntry;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.BooleanEntry;

interface IService
{
	// 连接
	int checkIsCanConnect(String cur_pkgName, int versionId);
	void initConnectGT(String pkgName, int pid);
	boolean disconnectGT(String cur_pkgName);
	
	// 出参操作
	oneway void registerOutPara(in OutPara outPara); // 注册
	oneway void registerGlobalOutPara(in OutPara outPara); // 注册
	String getOutPara(String key); // 获取
	String getGlobalOutPara(String key); // 获取
	void setOutPara(String key, String value); // 设置
	void setGlobalOutPara(String key, String value); // 设置
	void setTimedOutPara(String key, long time, String value); // 设置用户指定时间的
	
	// 入参操作
	oneway void registerInPara(in InPara inPara); // 注册
	oneway void registerGlobalInPara(in InPara inPara); // 注册
	InPara getInPara(String key); // 获取
	InPara getGlobalInPara(String key); // 获取
	void setInPara(String key, String value); // 设置
	void setGlobalInPara(String key, String value); // 设置
	
	// 日志
	void log(long tid, int level, String tag, String msg);
	
	// 异步传输性能数据到控制台，字符串类型的暂无需求
	void setPerfDigitalEntry(in PerfDigitalEntry task); // 数字类型
	void setPerfStringEntry(in PerfStringEntry task); // 字符串类型
	
	// 传boolean开关
	void setBooleanEntry(in BooleanEntry task);
	
	// 传通用命令，receiver是插件名字，也支持GT控制本身作为命令接受者
	void setCommond(in Bundle bundle);
	
	// 传通用命令，receiver是插件名字，也支持GT控制本身作为命令接受者,同步方法
	void setCommondSync(inout Bundle bundle);
}