/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;


import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;


/**
* @Description 内存曲线图 
* @date 2013年11月10日 下午5:14:10 
*
 */
public class MemoryRealTimeChart extends AbstractRealTimeLineChart {

	public MemoryRealTimeChart() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6638737463455176144L;
	
	@Override
	public JFreeChart createChart() {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "内存实时曲线图", 
            "时间", 
            "内存值（kB）",
            dataset, 
            true, 
            true,
			false);

        //设置，不然中文会存在乱码
		chart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));

		XYPlot xyPlot = (XYPlot)chart.getXYPlot();
		ValueAxis domainAxis =  xyPlot.getDomainAxis();
		ValueAxis rangeAxis = xyPlot.getRangeAxis();

		domainAxis.setLabelFont(new Font("宋体", Font.BOLD, 14));
		domainAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));
		
		rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 14));
		rangeAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));
		//rangeAxis.setAutoRange(true);

		//rangeAxis.setAutoTickUnitSelection(true);
		
        return chart;
    }	

}
