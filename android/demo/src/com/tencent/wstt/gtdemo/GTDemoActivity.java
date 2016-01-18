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
package com.tencent.wstt.gtdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.tencent.wstt.gt.client.GT;

public class GTDemoActivity extends Activity implements UserStrings {

	private Button showImg;
	private GridView gridview;
	private ArrayList<Map<String, Object>> lstImageItem = new ArrayList<Map<String, Object>>();
	private Map<String, Object> map;
	private SimpleAdapter saImageItems;
	
	private final int CLEAR_GRIDVIEW = 0;
	private final int LOAD_IMG_OK = 1;
	private final int LOAD_IMG_ERROR = -1;
	
	// 本demo显示的图片数目
	private static final int img_Count = 9;
	
	/*
	 * 需要输出参数动态调整的属性
	 */
	private int max_thread_num = 1;
	private int con_timeOut = 60 * 1000;
	private int read_timeOut = 60 * 1000;
	private boolean flag_keepAliave = true;
	
	// 图片解码操作的线程池
	private ExecutorService decodePool = Executors.newFixedThreadPool(img_Count);
	
	private Bitmap[] bitmaps = new Bitmap[img_Count];
	private String[] urls = new String[img_Count];
	
	/*
	 * GT观察用变量
	 */
	AtomicInteger completeSum = new AtomicInteger(); // 已完成下载的图片数，需并发安全
	long startTime; // 本次下载图片的开始时间
	long endTime; // 本次下载完所有图片的结束时间
	long totalTime; // 已完成下载的图片的总耗时
	AtomicLong totalSize = new AtomicLong(); // 已完成下载的图片的总大小

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_demo);
		
		showImg = (Button)findViewById(R.id.showimg);
		showImg.setOnClickListener(showImage);
		gridview = (GridView)findViewById(R.id.gridview);
		saImageItems = new SimpleAdapter(getApplicationContext(), lstImageItem, 
				R.layout.gt_gridview_item, new String[]{"img"}, new int[]{R.id.ItemImage});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		gridview.setAdapter(saImageItems);

		// 10k大小左右的图片
		urls[0] = "http://111.161.48.64/sosopic/0/1420666048153445210/320";
		urls[1] = "http://111.161.48.64/sosopic/0/9869660902850290294/320";
		urls[2] = "http://111.161.48.64/sosopic/0/7906532888606229180/320";
		urls[3] = "http://111.161.48.64/sosopic/0/9416045979901772820/320";
		urls[4] = "http://111.161.48.64/sosopic/0/10614843762795705578/320";
		urls[5] = "http://111.161.48.64/sosopic/0/8025199124411859454/320";
		urls[6] = "http://111.161.48.64/sosopic/0/5054744870201991274/320";
		urls[7] = "http://111.161.48.64/sosopic/0/12417944983283013250/320";
		urls[8] = "http://111.161.48.64/sosopic/0/14359830428608672234/320";
//		urls[9] = "http://111.161.48.64/sosopic/0/5476654313994007188/320";
	}
	
	OnClickListener showImage = new OnClickListener() {
		@Override
		public void onClick(View v) {
			handler.sendEmptyMessage(CLEAR_GRIDVIEW);
			
			/*
			 * GT usage
			 * GT init start 每次执行前，初始化统计用的变量与输出参数值
			 */
			completeSum.set(0);
			totalTime = 0;
			totalSize.set(0);
			startTime = endTime = 0;
			
			GT.setOutPara(下载耗时, "");
			GT.setOutPara(NumberOfDownloadedPics, "");
			GT.setOutPara(实际带宽, "");
			GT.setOutPara(singlePicSpeed, "");
			/*GT init end*/
			
			Thread thread = new Thread(new LoadPics());
			thread.start();
		}
	};
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			GT.setOutPara(NumberOfDownloadedPics, completeSum.get());
			
			switch(msg.what){
			case CLEAR_GRIDVIEW:
				lstImageItem.clear();
				for(int i = 0 ; i < img_Count ; i++){
					map = new HashMap<String, Object>();
					map.put("img", R.drawable.background);
					lstImageItem.add(map);
				}
				gridview.setAdapter(saImageItems);
				break;
			case LOAD_IMG_OK:
				int picId = msg.getData().getInt("picId");
				for(int i = 0 ; i < img_Count ; i++){
					if(picId == i){
						map = lstImageItem.get(i);
						map.put("img", bitmaps[picId]);
						break;
					}
				}
				
				saImageItems.setViewBinder(new ViewBinder() {
					@Override
					public boolean setViewValue(View view, Object data,
							String textRepresentation) {
						if(view instanceof ImageView && data instanceof Bitmap){
							ImageView iv = (ImageView) view;
							iv.setImageBitmap((Bitmap)data);
							return true;
						}else{
							return false;
						}
					}
				});
				
				gridview.setAdapter(saImageItems);
				/*GT Usage start*/
				GT.endTime(下载完成后到UI展示, String.valueOf(picId));
				if (completeSum.get() == img_Count && totalTime > 0) {
					long tempTime = (endTime - startTime)/1000000; // 纳秒转毫秒
					if (tempTime > 0) {
						double second = div(tempTime, 1000L, 3);
						GT.setOutPara(下载耗时, second + "s");

						long speed = totalSize.get() / tempTime;
						GT.setOutPara(实际带宽, speed + "k/s");
					}
				}
				GT.logI(UI处理图片, "按成功情况处理图片完成:" + picId);
				/*GT Usage end*/
				break;
			case LOAD_IMG_ERROR:
				int erro_picId = msg.getData().getInt("picId");
				
				for(int i = 0 ; i < img_Count ; i++){
					if(erro_picId == i){
						map = lstImageItem.get(i);
						map.put("img", R.drawable.error);
					}
				}
				gridview.setAdapter(saImageItems);
				Toast.makeText(getApplicationContext(),
						"Download Error，picId:" + erro_picId, Toast.LENGTH_SHORT)
						.show();
				GT.logE(UI处理图片, "按失败情况处理图片完成:" + erro_picId);
				break;
			}
		};
	};
	
	class LoadPics implements Runnable{

		@Override
		public void run() {
			/*
			 * GT usage
			 * 使用输入参数"开启线程数"初始化线程池，默认线程数为代码逻辑的原值max_thread_num
			 * 当输入参数设置失效时，默认值取max_thread_num就不会改变原有代码业务逻辑
			 */
			max_thread_num = GT.getInPara(并发线程数, max_thread_num);
			
			ExecutorService t = Executors.newFixedThreadPool(max_thread_num);
			
			/*GT start*/
			if (startTime == 0)
			{
				startTime = System.nanoTime();
			}
			/*GT end*/
			
			for (int i = 0; i < urls.length; i++)
			{
				t.execute(new Task(urls[i], i));
			}
			t.shutdown();
		}
		
	}
	
	class Task implements Runnable {
		private String url;
		private int picId;
		Task(String url, int picId){
			this.url = url;
			this.picId = picId;
		}
		@Override
		public void run() {
			// GT usage
			GT.startTimeInThread(线程内统计, 图片下载);
			InputStream is = getInputStream(url);
				
			try
			{
				// 下载
				final byte[] data = getBytesFromIS(is); 
				
				/*GT usage start*/
				endTime = System.nanoTime();
				totalSize.addAndGet(data.length);
				GT.logI(速度统计, "length=" + data.length + "Byte totalSize=" + totalSize + "Byte");
				long et = GT.endTimeInThread(线程内统计, 图片下载);
				GT.logI(图片下载, "完成图片下载并渲染步骤（失败或成功），id:" + picId);
				et = et/1000000; // endTime取回来的是纳秒级，转成毫秒级别
				totalTime += et;
				GT.logI(速度统计, "pic" + picId + "=" + et + "ms" + " total=" + totalTime + "ms");

				if (et > 0) {
					long speed = data.length / et;
					GT.setOutPara(singlePicSpeed, speed + "k/s");
				}
				
				completeSum.incrementAndGet();
				/*GT usage end*/
				
				// 独立线程解码图片
				decodePool.execute(new Runnable() {

					@Override
					public void run() {
						try{
							bitmaps[picId] = BitmapFactory.decodeByteArray(data, 0, data.length);
							Message msg = handler.obtainMessage();
							Bundle bundle = new Bundle();
							bundle.putInt("picId", picId);
							msg.setData(bundle);
							msg.what = LOAD_IMG_OK;
							
							/*GT start*/
							GT.startTime(下载完成后到UI展示, String.valueOf(picId));
							/*GT end*/
							
							msg.sendToTarget();
						}
						catch(Exception e)
						{
							GT.logE(图片下载, "图片解码失败:" + picId);
							e.printStackTrace();
						}
						
					}});
			}
			catch(Exception e)
			{
				handleLoadPicException(picId);
			}
		}
	}
	
	private byte[] getBytesFromIS(InputStream is) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = 0;
		while ((b = is.read()) != -1)
		{
			baos.write(b);
		}
		return baos.toByteArray();
	}
	
	
	private void handleLoadPicException(int picId)
	{
		/*GT start*/
		endTime = System.nanoTime();
		long et = GT.endTimeInThread(线程内统计, 图片下载);
		et = et/1000000; // endTime取回来的是纳秒级，转成毫秒级别
		totalTime += et;
		GT.logE(速度统计, "下载图片失败:" + picId);

		if (et > 0) {
			GT.setOutPara(singlePicSpeed, "--");
		}
		
		completeSum.incrementAndGet();
		/*GT end*/
		
		Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("picId", picId);
		msg.setData(bundle);
		msg.what = LOAD_IMG_ERROR;
		msg.sendToTarget();
	}
	
	private InputStream getInputStream(String imagePath){
		URL url;
		try {
			url = new URL(imagePath);
			HttpURLConnection conn;
			// 对每个图片的连接速度进行性能统计
			conn = (HttpURLConnection) url.openConnection();
			// 使用输入参数"超时时间"确定是连接的超时时间
			con_timeOut = GT.getInPara(连接超时, 60000);
			conn.setConnectTimeout(con_timeOut);
			read_timeOut = GT.getInPara(读超时, 60000);
			conn.setReadTimeout(read_timeOut);
			
			// 使用输入参数"维持长链接"确定是长连接还是短连接
			flag_keepAliave = GT.getInPara(KeepAlive, true);
			if(!flag_keepAliave){
				conn.setRequestProperty("Connection", "close");
				System.setProperty("http.keepAlive", "false");
			}
			conn.setRequestMethod("GET");
			
			/*GT start*/
			GT.startTimeInThread(线程内统计, Http响应耗时);
			int resCode = -1;
			try{
				resCode = conn.getResponseCode();
			}
			catch(Exception e)
			{
				GT.logE(速度统计, "Http连接异常：" + e.getMessage());
			}
			GT.endTimeInThread(线程内统计, Http响应耗时);
			/*GT end*/
			
			if(200 == resCode){
				InputStream inputStream = conn.getInputStream();
				return inputStream;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	/**
	 * double 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public static double div(double d1, double d2, int scale) {
		// 当然在此之前，你要判断分母是否为0，
		// 为0你可以根据实际需求做相应的处理

		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
//		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		// 直接向下取整，保持和UI展示一致
		return bd1.divide(bd2, scale, BigDecimal.ROUND_DOWN).doubleValue();
	}
}
