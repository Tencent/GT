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
package com.tencent.wstt.gt.data;

import java.util.concurrent.LinkedBlockingQueue;

import com.tencent.wstt.gt.AidlTask;

public class PerfTaskCache {
	// 出参入参设置任务直接送队列,TODO 后面将和本地性能合并，缓存历史数据
	// 本地性能任务列表，直接送统计数据，可以异步,在完成Reduce计算后即加进来
	// 全局性能任务容器，需要送计算源数据，可以异步，但要在控制台侧做支持乱时序，时间戳需要
	private LinkedBlockingQueue<AidlTask> aidlPerfQueue;

	// TODO 日志在日志模块，暂时不移动过来，考虑做集中处理
	
	public PerfTaskCache()
	{
		aidlPerfQueue = new LinkedBlockingQueue<AidlTask>();
	}
	
	public void clear()
	{
		aidlPerfQueue.clear();
	}
	
	public void add(AidlTask v)
	{
		aidlPerfQueue.offer(v);
	}
	
	public AidlTask take() throws InterruptedException
	{
		return aidlPerfQueue.take();
	}
}
