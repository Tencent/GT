/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.tencent.wstt.apt.console.APTConsoleFactory;
import com.tencent.wstt.apt.data.Constant;
import com.tencent.wstt.apt.file.APTLogFileParse.DataUnit;
import com.tencent.wstt.apt.file.APTLogFileParse.JfreeChartDatas;
import com.tencent.wstt.apt.file.APTLogFileParse.TSData;



/**
* @Description 测试数据曲线的抽象类
* @date 2013年11月10日 下午5:12:03 
*
 */
public abstract class AbstractRealTimeLineChart extends JPanel {

	private static final long serialVersionUID = 7533233863493417650L;
	protected JFreeChart chart;
	protected Title lastSubTile = null;
	
	protected TimeSeriesCollection dataset = new TimeSeriesCollection();
	protected List<TimeSeries> tsList = new ArrayList<TimeSeries>();
	//控制显示每种数据的开关
	protected List<Boolean> tsEnableList = new ArrayList<Boolean>();
		
		
	//对象（进程）的数量
	protected int objectNum= 0;
	//数据种类
	protected int dataNum = 0;
	/**
	 * 生成jfreechat，并添加panel中
	 */
	public AbstractRealTimeLineChart() {
		super(new BorderLayout());
		chart = createChart();
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		this.add(chartPanel);
	}
	


	/**
	 * 创建绘图用的JFreeChart对象.
	 * 确保数据源使用该类的数据成员dataset
	 * @return 绘图用的JFreeChart对象.
	 */
	abstract public JFreeChart createChart();
	
	/**
	 * 创建一个二维图表的数据源.
	 * 
	 * @return 数据源对象.
	 */
	public boolean initDataset(String[] objects, String[] datas)
	{
		if(objects == null || datas == null || objects.length == 0 || datas.length == 0)
		{
			return false;
		}
		
		objectNum = objects.length;
		dataNum = datas.length;
		dataset.removeAllSeries();
		
		if(datas.length == 1)
		{
			for(int i = 0; i < objectNum; i++)
			{
				// 设置曲线的宽度在这里感觉并不太合适
				chart.getXYPlot()
						.getRenderer()
						.setSeriesStroke(i,
								new BasicStroke(Constant.LINE_WIDTH));
				tsList.add(new TimeSeries(objects[i]));
				tsEnableList.add(true);
				dataset.addSeries(tsList.get(i));

			}
		}
		else
		{
			for(int i = 0; i < objectNum; i++)
			{
				for(int j = 0; j < dataNum; j++)
				{
					int index = dataNum*i+j;
					//设置曲线的宽度在这里感觉并不太合适
					chart.getXYPlot().getRenderer().setSeriesStroke(index, new BasicStroke(Constant.LINE_WIDTH));
					tsList.add(new TimeSeries(objects[i] + "_" + datas[j]));
					tsEnableList.add(true);
					dataset.addSeries(tsList.get(index));
				}
			}
		}

		return true;
	}
	/**
	 * 插入一种数据，返回该数据的编号
	 * 插入多次数据的时候需要保证每种数据的测试进程都是相同的
	* @Title: addOneDataset  
	* @Description:   
	* @param pkgNames
	* @param dataName
	* @return 
	* int 返回数据编号，失败返回-1
	* @throws
	 */
	public int addOneDataset(String[] objects, String dataName)
	{
		if(objects == null || objects.length == 0 || dataName == null)
		{
			return -1;
		}
		
		objectNum = objects.length;
		dataNum++;
		for(int i = 0; i < objectNum; i++)
		{
			int index = i*dataNum+(dataNum-1);
			chart.getXYPlot().getRenderer().setSeriesStroke(index, new BasicStroke(Constant.LINE_WIDTH));
			tsList.add(index, new TimeSeries(objects[i] + "_" + dataName));
			tsEnableList.add(index, true);
			dataset.addSeries(tsList.get(index));
		}
		return dataNum-1;
	}
	
	/**
	 * 清空数据
	* @Title: clearAllData  
	* @Description:    
	* void 
	* @throws
	 */
	public void clearAllData()
	{
		APTConsoleFactory.getInstance().APTPrint("clearAllData:1");
		objectNum = 0;
		dataNum = 0;
		dataset.removeAllSeries();
		APTConsoleFactory.getInstance().APTPrint("clearAllData:2");
		//当使用增加模式时，最好进行下面的处理
		tsList.clear();
		APTConsoleFactory.getInstance().APTPrint("clearAllData:3");
		tsEnableList.clear();
		APTConsoleFactory.getInstance().APTPrint("clearAllData:4");
	}

	/**
	 * 所有的曲线添加一次数据
	* @Title: update  
	* @Description:   
	* @param time
	* @param datas 
	* void 
	* @throws
	 */
	//TODO 这里有可能存在数组越界crash
	public void update(Date time, Number[] datas) {
		for(int i = 0; i < tsList.size(); i++)
		{
			tsList.get(i).addOrUpdate(new Millisecond(time), datas[i]);
		}
	}
	
	/**
	 * 仅仅更新所有进程的某一种数据
	* @Title: update  
	* @Description:   
	* @param time
	* @param dataIndex
	* @param datas 
	* void 
	* @throws
	 */
	public void update(Date time, int dataIndex, Number[] datas )
	{
		for(int i = 0; i < objectNum; i++)
		{
			int index = i*dataNum + dataIndex;
			tsList.get(index).add(new Millisecond(), datas[i]);
		}
	}
	

	public boolean fillData(JfreeChartDatas datas)
	{	
		//清除原有数据
		clearAllData();
		
		//初始化曲线
		if(!initDataset(datas.pkgNames, datas.dataItems))
		{
			return false;
		}
		//给每个曲线添加数据
		for(int i = 0; i < objectNum; i++)
		{
			for(int j = 0; j < dataNum; j++)
			{
				//tsList.get(dataNum*i+j).add(datas., value);
				int curveIndex = dataNum*i+j;
				TSData tsData = datas.tsDataList.get(curveIndex);
				
				for(DataUnit item : tsData.dataUnitList)
				{
					tsList.get(curveIndex).add(item.time, item.value);
				}
			}
		}	
		//更新开关状态
	
		
		// TODO 在好多情况下禁止UI操作
		return true;
	}
	
	/**
	 * 
	* @Title: setTimeSeriesEnable  
	* @Description:   控制是否显示某一条曲线
	* @param objectIndex
	* @param dataIndex
	* @param state 
	* void 
	* @throws
	 */
	public void setTimeSeriesEnable(int objectIndex, int dataIndex, boolean state)
	{
		int index = objectIndex*dataNum + dataIndex;
		//APTConsoleFactory.getInstance().APTPrint("Pkg:" + objectIndex + "data:" + dataIndex + "num:" + dataNum + "index:" + index);
		//首先判断参数是否合法
		if(objectIndex >= 0 && objectIndex < this.objectNum && dataIndex >= 0 && dataIndex < this.dataNum)
		{
			if(tsEnableList.get(index) != state)
			{
				//更新
				if(state)
				{
					dataset.addSeries(tsList.get(index));
				}
				else
				{
					dataset.removeSeries(tsList.get(index));
				}
				tsEnableList.set(index, state);
			}
		}
	}
	
	/**
	 * 设置主标题
	* @Title: setTitle  
	* @Description:   
	* @param title 
	* void 
	* @throws
	 */
	public void setTitle(String title)
	{
		chart.setTitle(title);
	}
	
	/**
	 * 添加文字副标题
	* @Title: setSubTitle  
	* @Description:   
	* @param title 
	* void 
	* @throws
	 */
	public void setSubTitle(String title)
	{
		Title curSubTitle = new TextTitle(title);
		if(lastSubTile == null)
		{
			chart.addSubtitle(curSubTitle);
		}
		else
		{
			chart.removeSubtitle(lastSubTile);
			chart.addSubtitle(curSubTitle);
		}
		lastSubTile = curSubTitle;
	}
	

	/**
	 * 保存图片
	* @Title: saveToJPEG  
	* @Description:   
	* @param imageName
	* @return 
	* boolean 
	* @throws
	 */
	public boolean saveToJPEG(String imageName)
	{
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imageName);
			ChartUtilities.writeChartAsJPEG(fos, chart, this.getSize().width, this.getSize().height);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	* @Description 保存测试曲线为PNG格式图片 
	* @param @param imageName 图片名称
	* @param @return   
	* @return boolean 是否成功
	* @throws
	 */
	public boolean saveToPNG(String imageName)
	{
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imageName);
			ChartUtilities.writeChartAsPNG(fos, chart, this.getSize().width, this.getSize().height);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}

