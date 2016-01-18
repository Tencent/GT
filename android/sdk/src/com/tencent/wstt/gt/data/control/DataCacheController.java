/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.wstt.gt.data.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Bundle;

import com.tencent.wstt.gt.AidlTask;
import com.tencent.wstt.gt.BooleanEntry;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.data.CommandTaskCache;
import com.tencent.wstt.gt.data.LocalTimePerfCache;
import com.tencent.wstt.gt.data.LogCache;
import com.tencent.wstt.gt.data.ParaTaskCache;
import com.tencent.wstt.gt.data.PerfInParaCache;
import com.tencent.wstt.gt.data.PerfOutParaCache;
import com.tencent.wstt.gt.data.PerfTaskCache;
import com.tencent.wstt.gt.data.local.LocalNumberDataPerfEntry;

public class DataCacheController {
	
	private CommandTaskCache commandTaskCache;
	private PerfTaskCache perfTaskCache;
	private ParaTaskCache paraTaskCache;
	private LogCache logCache;
	private LocalTimePerfCache localTimePerfCache; // 目前只有这一种需要单独加工的缓存数据
	private PerfOutParaCache outParaCache; // 注意这个出参缓存只在Connecting状态下有效,应该在上层状态控制
	private PerfInParaCache inParaCache; // 注意这个出参缓存只在Connecting状态下有效,应该在上层状态控制
	
	public DataCacheController()
	{
		
	}
	
	/**
	 * 该方法应在GTInternal中状态Connecting时启动
	 */
	public void init()
	{
		commandTaskCache = new CommandTaskCache();
		perfTaskCache = new PerfTaskCache();
		paraTaskCache = new ParaTaskCache();
		logCache = new LogCache();
		localTimePerfCache = new LocalTimePerfCache();
		outParaCache = new PerfOutParaCache();
		inParaCache = new PerfInParaCache();
	}
	
	/*
	 * modify on 20150305 频繁使用下，localTimePerfCache等设置null在并发情况小概率
	 * 引起空指针，所以不再主动设置null，牺牲少量内存保证健壮性
	 */
	public void dispose()
	{
		if (null != localTimePerfCache)
		{
			localTimePerfCache.clear();
//			localTimePerfCache = null;
		}
		
		if (null != outParaCache)
		{
			outParaCache.clear();
//			outParaCache = null;
		}
		
		if (null != inParaCache)
		{
			inParaCache.clear();
//			inParaCache = null;
		}
		
		if (null != perfTaskCache)
		{
			perfTaskCache.clear();
//			perfTaskCache = null;
		}
		
		if (null != paraTaskCache)
		{
			paraTaskCache.clear();
//			paraTaskCache = null;
		}
		
		if (null != commandTaskCache)
		{
			commandTaskCache.clear();
//			commandTaskCache = null;
		}

		if (null != logCache)
		{
			logCache.clear();
//			logCache = null;
		}
	}
	
	/**
	 * 日志生产者
	 * @param content
	 * @return
	 */
	public boolean putLog(String[] content)
	{
		return logCache.offer(content);
	}
	
	/**
	 * 直接给日志消费者线程调用的方法
	 * @return
	 * @throws InterruptedException
	 */
	public String[] takeLog() throws InterruptedException
	{
		return logCache.take();
	}
	
	/**
	 * 加本地性能数据缓存
	 * @param dataEntry
	 */
	public void putPerfData(LocalNumberDataPerfEntry...dataEntry)
	{
		if (null == dataEntry || dataEntry.length == 0)
		{
			return;
		}
		for (LocalNumberDataPerfEntry e : dataEntry)
		{
			localTimePerfCache.put(e);
		}
	}

	public Bundle takeCommandTask() throws InterruptedException
	{
		return commandTaskCache.take();
	}

	public AidlTask takePerfTask() throws InterruptedException
	{
		return perfTaskCache.take();
	}
	
	public AidlTask takeParaTask() throws InterruptedException
	{
		return paraTaskCache.take();
	}

	/**
	 * 对于需要在控制台侧分析计算的，直接加消费队列准备送去控制台处理
	 * @param aidlTask
	 */
	public void putPerfTask(AidlTask aidlTask)
	{
		if (null != aidlTask)
		{
			// 加消费者队列
			perfTaskCache.add(aidlTask);
		}
	}
	
	public void putParaTask(AidlTask aidlTask)
	{
		if (null != aidlTask)
		{
			// 加消费者队列
			paraTaskCache.add(aidlTask);
		}
	}
	
	/**
	 * 在这个方法中计算本地性能分析值，并加消费者队列
	 * @param queryEntry
	 * @return
	 */
	public AidlTask profilerData(LocalNumberDataPerfEntry...endDataEntry)
	{
		AidlTask result = localTimePerfCache.profiler(endDataEntry);
		if (null != result)
		{
			// 加消费者队列
			perfTaskCache.add(result);
		}
		
		return result;
	}

	/**
	 * 只有在Connecting态才应该走缓存，Connected态应直接送队列
	 */
	public void registerOutParaToCache(OutPara outPara)
	{
		if (null == outPara || null == outPara.getKey())
		{
			return;
		}
		
		if (null == outPara.getValue())
		{
			outPara.setValue("");
		}
		
		outParaCache.register(outPara);
	}

	/**
	 * 只有在Connecting态才应该走缓存，Connected态应直接送队列
	 */
	public void setOutParaToCache(String paraName, Object value)
	{
		if (null != value)
		{
			outParaCache.put(paraName, value.toString());
		}
		outParaCache.put(paraName, "");
	}
	
	/**
	 * 获取缓存中的出参值，只有在Connecting态才应该走缓存
	 * @param paraName
	 * @return
	 */
	public String getOutParaFromCache(String paraName)
	{
		if (null == paraName || outParaCache == null)
		{
			return null;
		}
		OutPara result = outParaCache.get(paraName);

		return result == null ? "" : result.getValue();
	}
	
	/**
	 * 只有在Connecting态才应该走缓存，Connected态应直接送队列，这个是直接送队列
	 */
	public void setOutPara(String paraName, Object value, boolean isGlobal)
	{
		OutPara outPara = new OutPara();
		outPara.setKey(paraName);
		if (value == null)
		{
			value = "";
		}
		outPara.setValue(value.toString());
		outPara.setGlobal(isGlobal);
		paraTaskCache.add(outPara);
	}
	
	/**
	 * 只有在Connecting态才应该走缓存，Connected态应直接送队列，这个是直接送队列
	 */
	public void registerInParaToCache(InPara inPara)
	{
		if (null == inPara || null == inPara.getKey())
		{
			return;
		}
		
		if (null == inPara.getValues())
		{
			inPara.setValues(new ArrayList<String>());
		}
		
		inParaCache.register(inPara);
	}
	
	/**
	 * 只有在Connecting态才应该走缓存，Connected态应直接送队列
	 */
	public void setInParaToCache(String paraName, Object newValue)
	{
		if (null != newValue)
		{
			inParaCache.put(paraName, newValue.toString());
		}
	}
	
	/**
	 * 获取缓存中的出参值，只有在Connecting态才应该走缓存
	 * @param paraName
	 * @return
	 */
	public String getInParaFromCache(String paraName)
	{
		if (null == paraName || inParaCache == null)
		{
			return null;
		}
		InPara result = inParaCache.get(paraName);
		if(result == null || InPara.DISPLAY_DISABLE == result.getDisplayProperty()){
			return null;
		}
		
		List<String> values = result.getValues();
		if (values != null && values.size() > 0)
		{
			return values.get(0);
		}

		return null;
	}

	/**
	 * 只有在Connecting态才应该走缓存，Connected态应直接送队列，这个是直接送队列
	 */
	public void setInPara(String paraName, Object newValue, boolean isGlobal)
	{
		InPara inPara = new InPara();
		inPara.setKey(paraName);
		if (newValue == null)
		{
			newValue = "";
		}
		List<String> values = new ArrayList<String>();
		values.add(newValue.toString());
		inPara.setValues(values);
		inPara.setGlobal(isGlobal);

		paraTaskCache.add(inPara);
	}
	
	/**
	 * 将缓存中的出入参数据全部传给控制台(注册与设值)，
	 * 完成后将outParaCache这个一次性缓存清理掉
	 */
	public void transParasToConsole()
	{
		/*
		 * 1.入参
		 */
		if (inParaCache != null)
		{
			Collection<InPara> inParas = inParaCache.getAll();
			if (inParas != null)
			{
				for (InPara para : inParas)
				{
					// 加消费者队列
					paraTaskCache.add(para);
				}
			}
			
			// 完成后将outParaCache这个一次性缓存清理掉
			inParaCache.clear();
			inParaCache = null;
		}

		/*
		 * 2.出参
		 */
		if (outParaCache != null)
		{
			Collection<OutPara> outParas = outParaCache.getAll();
			if (outParas != null)
			{
				for (OutPara para : outParas)
				{
					// 加消费者队列
					paraTaskCache.add(para);
				}
				
				// 完成后将outParaCache这个一次性缓存清理掉
				outParaCache.clear();
				outParaCache = null;
			}
		}
	}
	
	public void putBooleanTask(BooleanEntry aidlTask)
	{
		if (null != aidlTask)
		{
			// 加消费者队列
			paraTaskCache.add(aidlTask);
		}
	}
	
	/**
	 * 对于需要在控制台侧处理的命令，直接放入队列准备送去控制台处理
	 * @param bundle 命令包
	 */
	public void putCommandTask(Bundle bundle)
	{
		if (null != bundle)
		{
			// 加消费者队列
			commandTaskCache.add(bundle);
		}
	}
}
