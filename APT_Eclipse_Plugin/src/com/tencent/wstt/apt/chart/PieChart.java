/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.tencent.wstt.apt.data.PieChartDataItem;

/**
* @Description 显示饼图数据的Jpanel 
* @date 2013年11月10日 下午5:34:28 
*
 */
public class PieChart extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultPieDataset dataset;
	private JFreeChart chart;
	public PieChart()
	{
		super(new BorderLayout());
		chart = createChart();
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		this.add(chartPanel);
	}

	private JFreeChart createChart()
	{
		this.dataset = new DefaultPieDataset();
		final JFreeChart chart = ChartFactory.createPieChart("内存构成分析", this.dataset,
				true, true, false);
		chart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1}"));
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		return chart;
	}
	
	/**
	 * 更新dataset
	 * @return
	 */
	public void setDataset(List<PieChartDataItem> sour)
	{
		dataset.clear();
		for(PieChartDataItem item : sour)
		{
			dataset.setValue(item.mapping, item.value);
		}
	}
}
