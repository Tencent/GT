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
package com.tencent.wstt.gt.views;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.ui.model.TagTimeEntry;
import com.tencent.wstt.gt.ui.model.ThresholdEntry;
import com.tencent.wstt.gt.ui.model.TimeEntry;
import com.tencent.wstt.gt.utils.DoubleUtils;
import com.tencent.wstt.gt.utils.GTUtils;

public class GTPerfDetailView extends View {
	static final String TAG = "--GTPerfDetailView--";

	private TagTimeEntry dataSet; // 保持数据源的引用

	private static final int xMin = 10;
	public static final int xMax = 50;
	private static final int yMax = 999999;

	private static boolean measured = false;

	private static int devW = 0; // 手机屏幕宽度，作为特殊适配的判断
	private static int devH = 0; // 手机屏幕宽度，作为特殊适配的判断

	private static int canvasW = 674; // 控件画布整体宽度
	private static int canvasH = 550; // 控件画布整体高度

	private static int absX = (int) (canvasW / 11.23f); // 相对于控件画布x轴的起始位置
	private static int absXMax = (int) (canvasW - canvasW / 19.82f); // 相对于控件画布x轴的结束位置
	private static int absY = (int) (canvasH - canvasH / 4.51f); // 相对于控件画布y轴的起始位置
	private static int absYMax = (int) (canvasH / 7.43f); // 相对于控件画布y轴的结束位置

	private static int w = absXMax - absX;
	private static int h = absY - absYMax;
	private static int middle = (absXMax - absX) / 2;

	private static final int yGridMinMax = 10;
	private static final int yMaxGridNum = 10;

	private static final int scaleY720 = 7; // y轴的数字显示精度，算上小数点7位，加上上限999.999正合适
	private static final int scaleY480 = 5; // 480p分辨率，算上小数点5位，上限999.9
	private static final int scaleY320 = 5; // 320p分辨率，算上小数点5位，上限999.9
	private static int curScale = scaleY720;

	// 声明Paint对象
	private Paint mPaint = null;

	// 当前页面显示条数的平均值位置
	private long curAve = absY;

	private int xGrid = 1;
	private int yGrid = 100;
	long curYMax = 0;
	long curYMin = 0;

	private DrawEntry[] cache;
	private int curSize;

	float anchorX = middle;
	float anchorY = 0;
	long anchorValue = 0; // 锚点的微秒级别时间值
	long anchorTime = 0; // // 锚点数据生成的时间
	int anchorSeq = 0; // // 锚点数据的序号
	
	float singleTextInterval = 6; // 单个字间距,默认字号是12,单字间距是6

	/*刷新和拖动图表的相关属性*/
	private boolean isReachDataSetEnd = false; // 判定是否本次滑动滑动到数据源的尾部
	private boolean isAutoRefresh = true; // 如果正在拖动或数据没在最新位置，则不需要自动刷
	private boolean isInLongFlip; // 是否本次划动进入有效长按状态
	private boolean isInLongFlipJudged; // 是否本次划动进入有效长按状态的判断完成
	private boolean isSingleOffsetOverFlow; // 是否有一次单次偏移溢出
	private float curLCOffset; // 当前的长按事件中判断的偏移量
	private static final float OFFSET_LIMIT_SUM_UPPER_BOUND = 10.0f; // 长按统计中的允许偏移上限
	private static final float OFFSET_LIMIT_SINGLE_UPPER_BOUND = 2.0f; // 单次允许偏移上限

	private float lastX; // 每回touch图表记录当前的位置
	private int start; // 当前显示在数据源的起始位置
	private int end; // 当前显示在数据源的结束位置，在拖动图表中实际计算确定

	public GTPerfDetailView(Context activity, TagTimeEntry dataSet) {
		super(activity);
		this.dataSet = dataSet;
		
		if (0 == devW || 0 == devH)
		{
			devW = DeviceUtils.getDevWidth();
			devH = DeviceUtils.getDevHeight();
		}
		
		cache = new DrawEntry[xMax];
		for (int i = 0; i < cache.length; i++) {
			DrawEntry entry = new DrawEntry();
			cache[i] = entry;
		}
		mPaint = new Paint();
		
		this.setOnLongClickListener(onLongClickListener);
	}

	public GTPerfDetailView(Context activity, AttributeSet aSet) {
		super(activity, aSet);
		mPaint = new Paint();
	}
	
	/**
	 * 设置数据源，参数是数据源的起点
	 * 有三种情况会进入该方法：
	 * 1.GTPerfDetailView初始化时，参数会传入preStart=0,preEnd=0
	 * 2.Activity自动更新数据
	 * 3.划动图表刷新数据
	 * 
	 * @param preStart
	 *            预先估算的在数据源中的起始位置，实际位置需要在本方法中修正
	 */
	public void setInput(int preStart) {
		start = preStart;
		end = preStart + xMax;
		isReachDataSetEnd = false;
		// 滑动图表或进入时出现预期结束位置大于总数据长度情况,说明划到尾部以后了，需修正
		if (end >= dataSet.getRecordSize())
		{
			end = dataSet.getRecordSize();
			start = Math.max(end - xMax, 0); // 用修正后的end更新start
			isReachDataSetEnd = true;
		}
		List<TimeEntry> tempList = dataSet.getRecordList(start, end);
		setInput(tempList);
	}

	/**
	 * 需要传入微秒单位的数据
	 * 
	 * @param input
	 *            页面显示的数据源
	 * @param ave
	 *            整体的平均值， 现在页面显示的不是这个数，是页面显示那几条的平均值
	 */
	private void setInput(List<TimeEntry> input) {
		
		curSize = input.size();
		for (int i = 0; i < curSize; i++) {
			this.cache[i].time = input.get(i).time;
			this.cache[i].value = input.get(i).reduce / dataSet.getCarry();
			this.cache[i].i = i;
			this.cache[i].y = 0;
		}
		
		// 找出y最大值，并给出每个点的坐标
		curYMax = 0;
		curYMin = 0;
		if (curSize > 0)
		{
			curYMin = cache[0].value; // 随便取一个值作为初始最小值
		}
		
		long aveCount = 0;
		long tempAve = 0;
		for (int i = 0; i < curSize; i++) {
			curYMax = Math.max(curYMax, cache[i].value);
			curYMin = Math.min(curYMin, cache[i].value);
			// 顺便算页面显示的平均值
			aveCount += cache[i].value;
		}
		if (curSize > 0)
		{
			tempAve = Math.round((float)aveCount / (float)curSize);
		}
		
		curYMax = Math.max(curYMax, yGridMinMax);
		if (curYMax > yGridMinMax) // 纵坐标要求是100的整数倍
		{
			curYMax = (curYMax / yGridMinMax + 1) * yGridMinMax;
		}
		if (curYMax > yMax)
		{
			curYMax = yMax;
		}
		
		if (curYMin > 0)
		{
			curYMin = (curYMin / yGridMinMax) * yGridMinMax;
		}
		if (curYMin > 0)
		{
			curYMin = curYMin - (curYMax - curYMin) / yMaxGridNum;
			curYMin = Math.max(0, curYMin); // 别减成负数了
		}
		if (curYMin > yMax)
		{
			curYMin = yMax - 1; // 要保证curYMin比curYMax小，因为这个减数会做分母
		}

		// 注意分母
		if (tempAve > curYMin)
		{
			this.curAve = calcY(tempAve);
		}
		
		// 手动触发一次onDrow
		postInvalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		mPaint.setTextSize(12); // 恢复默认的字号
		mPaint.setStrokeWidth(0); // 恢复默认线条粗细
		singleTextInterval = 6; // 恢复默认字间距
		if (!measured)
		{
			canvasW = getMeasuredWidth();
			canvasH = getMeasuredHeight();
			
			absX = (int) (canvasW / 11.23f); // 相对于控件画布x轴的起始位置
			absXMax = (int) (canvasW - canvasW / 19.82f); // 相对于控件画布x轴的结束位置
			absY = (int) (canvasH - canvasH / 4.51f); // 相对于控件画布y轴的起始位置
			absYMax = (int) (canvasH / 7.43f); // 相对于控件画布y轴的结束位置
			
			// y轴数字对屏幕分辨率的适配
			if (devW == 720)
			{
				curScale = scaleY720;
			}
			else if (devW == 320)
			{
				absX = absX + 16;
				absXMax = absXMax - 18; // 不减一下锚点线数值会出界
				curScale = scaleY320;
			}
			else if (devW == 480)
			{
				absX = absX + 10;
				curScale = scaleY480;
			}

			w = absXMax - absX;
			h = absY - absYMax;
			middle = (absXMax - absX) / 2 + absX;
			anchorX = middle;
			
			curAve = absY;
			
			measured = true;
		}

		// 设置画布为黑色背景
		// canvas.drawColor(Color.BLACK);

		// 消除锯齿
		mPaint.setAntiAlias(true);

		// 设置图形为空心
		mPaint.setStyle(Paint.Style.STROKE);

		// 绘制x,y轴
		mPaint.setColor(Color.argb(0xff, 0x87, 0x8c, 0x98));
		mPaint.setStrokeWidth(2); // 设置线条粗细
		canvas.drawLine(absX, absY, absXMax, absY, mPaint);
		canvas.drawLine(absX, absYMax - 5, absX, absY, mPaint);

		// x最少显示10个数据，小于10个x坐标长度为10，从左开始显示
		if (curSize <= 5)
		{
			xGrid = 1;
		}
		else if (curSize > 5 && curSize <= xMin)
		{
			xGrid = 2; // 因为x轴显示时间了，最多显示10个1会展示不下
		}
		else
		{
			xGrid = 10;
		}

		// 根据传入的参数绘制点折线
		long preX = 0;
		float preY = 0;
		long preValue = 0;
		
		// 阈值对象
		ThresholdEntry thresholdEntry = dataSet.getThresholdEntry();
		double upper = thresholdEntry.getUpperValue();
		double lower = thresholdEntry.getLowerValue();
		double dUpper = DoubleUtils.mul(upper, dataSet.getCarry_l2d());
		double dLower = DoubleUtils.mul(lower, dataSet.getCarry_l2d());
		long realUpper = (long)dUpper;
		long realLower = (long)dLower;
		
		for (int i = 0; i < curSize; i++) {
			DrawEntry entry = cache[i];
			long point = entry.value;
			long x = absX + w * i / curSize;
			float y = calcY(point);
			entry.y = y;
			canvas.drawPoint(x, y, mPaint);
			
			if (i > 0) // 画从前一点到当前点的线
			{
				// 对于超出阈值的，需要标红线，而且需要是有效阈值
				if (dataSet.getThresholdEntry().isEnable()
						&& (realUpper > realLower
						&& (preValue > realUpper && point > realUpper
								|| preValue < realLower && point < realLower)))
				{
//					mPaint.setColor(Color.argb(0xff, 0xda, 0x7b, 0x2f));
					mPaint.setColor(Color.argb(0xff, 0xff, 0x00, 0x00));
				}
				else
				{
					mPaint.setColor(Color.argb(0xff, 0xd2, 0x90, 0x29));
				}

				mPaint.setStrokeWidth(2); // 设置线条粗细
				canvas.drawLine(x, y, preX, preY, mPaint);
			}
			
			// 单点, 而且需要是有效阈值;但是时间区间的情况下是否加描单点不一定
			if (dataSet.getThresholdEntry().isEnable()
					&& (realUpper > realLower
					&& (point > realUpper || point < realLower)))
			{
				mPaint.setColor(Color.argb(0xff, 0xff, 0x00, 0x00));
			}
			else
			{
				mPaint.setColor(Color.argb(0xff, 0xd2, 0x90, 0x29));
			}
			mPaint.setStrokeWidth(3);
			canvas.drawPoint(x, y, mPaint);
			
			preX = x;
			preY = y;
			preValue = point;
			
			// 要将x位置的数字画在横坐标上，显示内容数字：i，内容位置 x, y是absY
			// add on 20131123 将时间画在x轴上
			mPaint.setColor(Color.argb(0xff, 0x87, 0x8c, 0x98));
			mPaint.setStrokeWidth(0); // 设置线条粗细
			if (i % xGrid == 0)
			{
				canvas.drawText(Integer.toString(start + i), x, absY + 30, mPaint);
				canvas.drawText(GTUtils.getSystemTime(entry.time), x, absY + 45, mPaint);
			}
			if (curSize == xMax && i == curSize - 1)
			{
				canvas.drawText(Integer.toString(start + i), x, absY + 30, mPaint);
			}
			
			// 画垂直分割线
			mPaint.setColor(Color.argb(0x3f, 0x87, 0x8c, 0x98));
			mPaint.setStrokeWidth(1); // 设置线条粗细
			if (i % xGrid == 0)
			{
				canvas.drawLine(x, absY, x, absYMax - 5, mPaint);
			}
			if (curSize == xMax && i == curSize - 1)
			{
				canvas.drawLine(x, absY, x, absYMax - 5, mPaint);
			}
		}

		// TODO 绘制单位
		mPaint.setColor(Color.argb(0xff, 0x87, 0x8c, 0x98));
		mPaint.setStrokeWidth(0); // 设置线条粗细
		canvas.drawText(dataSet.getUnit(), absX - 10, absYMax - 15, mPaint);

		// 循环绘制y轴坐标数字,顺便画y轴间隔线
		mPaint.setColor(Color.argb(0xff, 0x87, 0x8c, 0x98));
		mPaint.setStrokeWidth(0); // 设置线条粗细
		yGrid = (int) ((curYMax - curYMin) / yMaxGridNum);

		for (int i = 0; i < yMaxGridNum + 1; i++)
		{
			long g = curYMin + i * yGrid;
			float y = calcY(g);
			double dg = DoubleUtils.div(g, dataSet.getCarry_l2d(), dataSet.getScale());
			String sdg = Double.toString(dg);
			
			mPaint.setColor(Color.argb(0xff, 0x87, 0x8c, 0x98));
			mPaint.setStrokeWidth(0); // 设置线条粗细
			if (sdg.length() > curScale)
			{
				canvas.drawText(Double.toString(dg).substring(0, curScale), absX - 40, y, mPaint);
			}
			else
			{
				canvas.drawText(Double.toString(dg), absX - 40, y, mPaint);
			}
			
			mPaint.setColor(Color.argb(0x3f, 0x87, 0x8c, 0x98));
			mPaint.setStrokeWidth(1); // 设置线条粗细
			canvas.drawLine(absX, y, absXMax, y, mPaint);
		}

		// 画平均值
		if (curYMax != 0 && curSize > 0)
		{
			mPaint.setColor(Color.argb(0xff, 0x14, 0x8d, 0xc0));
			mPaint.setStrokeWidth(2); // 设置线条粗细
			canvas.drawLine(absX, curAve, absXMax, curAve, mPaint);
		}
		
		mPaint.setColor(Color.argb(0xff, 0x87, 0x8c, 0x98));
		mPaint.setStrokeWidth(1); // 设置线条粗细
		
		// 在长按中，需画锚点线
		if (isInLongFlip)
		{
//			canvas.drawLine(anchorX, 0, anchorX, canvasH, mPaint);
			canvas.drawLine(anchorX, absY, anchorX, absYMax - 14, mPaint);
		}
		mPaint.setStrokeWidth(0); // 恢复线条粗细
		
		// 显示当前y值
		if (anchorY != 0) {
			// 时间文字显示的位置，默认小屏下取middle - 80
			float timeTextLocation = middle - 80;

			//设置字体大小，需要为大颗粒手机适配
			if (devW >= 480)
			{
				mPaint.setTextSize(24);
				
				singleTextInterval = 15f;
				timeTextLocation = middle - 155;
			}

			double dY = DoubleUtils.div(
					anchorValue, dataSet.getCarry_l2d(), dataSet.getScale());
			canvas.drawText(GTUtils.getSystemTime(anchorTime), timeTextLocation, absYMax - 15, mPaint);
			
			// 黄色显示当前次数
			mPaint.setColor(Color.argb(0xff, 0xd2, 0x90, 0x29));
			String sAnchorSeq = Integer.toString(anchorSeq + start);
			float al = sAnchorSeq.length() * singleTextInterval; // 修正位置，因为sAnchorSeq长度不定
			canvas.drawText(sAnchorSeq, middle, absYMax - 15, mPaint);

			// 绿色显示值
			float middleAl = middle + al;
			mPaint.setColor(Color.argb(0xff, 0x38, 0xad, 0x29));
			canvas.drawText(Double.toString(dY), middleAl + 10, absYMax - 15, mPaint);
		}
	}

	OnLongClickListener onLongClickListener = new OnLongClickListener(){

		@Override
		public boolean onLongClick(View v) {
			/*
			 * 如果是有效的长按，则按照长按滑动锚线显示值处理，否则不响应长按事件
			 */
			if (!judgeEffectiveLongClick())
			{
				return false;
			}

			anchorX = lastX;
			if (anchorX < absX) {
				anchorX = absX;
			}
			if (anchorX > absXMax) {
				anchorX = absXMax;
			}

			// 显示当前的x位置最近的对象，要修正显示x和y值
			int i = (int) ((anchorX - absX) * curSize / w);
			if (i >= 50) {
				i = 49;
			}
			if (i < 0) {
				i = 0;
			}
			DrawEntry nearest = cache[i];
			// 注意分母
			if (curSize != 0)
			{
				anchorX = absX + w * i / curSize;
			}
			
			anchorY = nearest.y;
			anchorValue = nearest.value;
			anchorTime = nearest.time;
			anchorSeq = nearest.i;

			postInvalidate();
			return true;
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {  
		case MotionEvent.ACTION_DOWN: 
			lastX = event.getX(); // 一定要在这里初始化，后面长按判断的起始位置依据
			isAutoRefresh = false; // 触屏即不允许自动刷新
			isInLongFlip = false;
			isInLongFlipJudged  = false;
			isSingleOffsetOverFlow = false;
			curLCOffset = 0;
			
			break;
		
		case MotionEvent.ACTION_MOVE:
			float dx = lastX - event.getX(); // 相比上次移动的距离
			lastX = event.getX();
			
			/*
			 * 长按有效性判定
			 */
			if (! isInLongFlipJudged)
			{
				if (Math.abs(dx) > OFFSET_LIMIT_SINGLE_UPPER_BOUND) // 单次偏移判定
				{
					isSingleOffsetOverFlow = true;
					isInLongFlipJudged = true; // 如果单次偏移爆了，直接结束判定
				}
				curLCOffset += dx;
			}
			
			// 如果进入长按状态，滑动方式是滑动锚点线显示数值
			if (isInLongFlip)
			{
				anchorX = lastX;
				if (anchorX < absX) {
					anchorX = absX;
				}
				if (anchorX > absXMax) {
					anchorX = absXMax;
				}

				// 显示当前的x位置最近的对象，要修正显示x和y值
				int i = (int) ((anchorX - absX) * curSize / w);
				if (i >= 50) {
					i = 49;
				}
				if (i < 0) {
					i = 0;
				}
				DrawEntry nearest = cache[i];
				// 注意分母
				if (curSize != 0)
				{
					anchorX = absX + w * i / curSize;
				}
				
				anchorY = nearest.y;
				anchorValue = nearest.value;
				anchorTime = nearest.time;
				anchorSeq = nearest.i;

				postInvalidate();
			}
			// 非长按状态是滑动图表显示历史数据，并且要保证当前图表数据等于xMax
			else if (end >= xMax)
			{
				// 计算本次滑动闪过多少个x轴位置
				if (dx > 0) 
				{
					// 向新数据方向，后面可以考虑根据速度进行放大倍数的计算
					int d = (int) (dx * curSize / w);
					
					// 如果已结束长按判定，可以提高滑动灵敏度
					if (isInLongFlipJudged)
					{
//						d = Math.max(d, 1); // 先屏蔽，小屏手机上有意外现象
					}

					// 预估的滑动后的开始值
					int preStart = Math.min(start + d, end - xMax);
					preStart = start + d;
					
					// 更新数据源
					setInput(preStart);
				}
				else 
				{
					// 向历史数据方向，后面可以考虑根据速度进行放大倍数的计算
					int d = (int) (dx * curSize / w);
					
					// 如果已结束长按判定，可以提高滑动灵敏度
					if (isInLongFlipJudged)
					{
//						d = Math.min(d, -1); // 先屏蔽，小屏手机上有意外现象
					}
					
					// 预估的滑动后的开始值
					int preStart  = Math.max(start + d, 0);
					
					// 更新数据源
					setInput(preStart);
				}
			}
			break;
			
		case MotionEvent.ACTION_UP:
			lastX = 0; // 此时lastX2不清理，要给长按用的
			isInLongFlip = false;
			isInLongFlipJudged  = false;
			isSingleOffsetOverFlow = false;
			curLCOffset = 0;
			
			// 如果松手时，正好划到最尾再过一点，这时可以自动刷新
			if (isReachDataSetEnd)
			{
				isAutoRefresh = true;
			}
			
			// 清理掉长按的锚点数值
			anchorY = 0;
			break;
		}
		
		return super.onTouchEvent(event);
	}

	/*
	 * 进行本次划动是否进入有效长按状态的判定
	 * @return
	 */
	private boolean judgeEffectiveLongClick()
	{
		// 长按响应期间有一次单次偏移大于单次偏移上限，或总偏移大于总偏移上限，判定为非有效长按
		if (isSingleOffsetOverFlow ||
				Math.abs(curLCOffset) > OFFSET_LIMIT_SUM_UPPER_BOUND)
		{
			isInLongFlip = false;
		}
		else
		{
			isInLongFlip = true;
		}
		isInLongFlipJudged  = true;
		return isInLongFlip;
	}

	static class DrawEntry {
		long time;
		long value;
		int i; // x轴坐标是序号，0-50先
		float y;
	}

	private long calcY(long timeY)
	{
		return curYMax - curYMin == 0 ? 0 : absY - (h * (timeY - curYMin) / (curYMax - curYMin));
	}

	public boolean isAutoRefresh()
	{
		return isAutoRefresh;
	}

	public void setAutoRefresh(boolean flag)
	{
		isAutoRefresh = flag;
	}
}
