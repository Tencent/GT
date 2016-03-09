/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;


/**
* @Description 数据提供者抽象类（监听者模式） ，并继承TimerTask，可以定时的更新数据
* @date 2013年11月10日 下午5:18:50 
*
 */
public abstract class DataProvider extends TimerTask {

	protected List<Observer> observerList = new ArrayList<Observer>();

	@Override
	public void run() {
		Number []data = getData();
		if(data == null)
		{
			//这里主要为配合jiffies这种数据，第一次获取的没有意义
			return;
		}
		Date curDate = new Date(System.currentTimeMillis());
		
		for(Observer item : observerList)
		{
			item.update(curDate, data);
		}

	}
	
	/**
	* @Description 更新一次该数据提供者的数据 
	* @param @return   
	* @return Number[] 
	* @throws
	 */
	protected abstract Number[] getData();
	
	/**
	* @Description  添加一个该数据提供者的观察者
	* @param @param observer  观察者 
	* @return void 
	* @throws
	 */
	public void attach(Observer observer)
	{
		observerList.add(observer);
	}
	
	/**
	* @Description 从该数据提供者的观察者队列中删除观察者observer 
	* @param @param observer   
	* @return void 
	* @throws
	 */
	public void detach(Observer observer)
	{
		observerList.remove(observer);
	}

}
