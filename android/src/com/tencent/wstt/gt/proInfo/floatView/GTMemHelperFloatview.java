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
package com.tencent.wstt.gt.proInfo.floatView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.plugin.BaseService;
import com.tencent.wstt.gt.utils.GTUtils;

public class GTMemHelperFloatview extends BaseService implements OnTouchListener {

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private Context mContext = null;
	public static View net_switch_view;

	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private float StartX;
	private float StartY;

	public boolean floatview_flag = true;
	public boolean net_state_flag = true;

	private final int SCREEN_HORIZONTALLY = 0;
	private final int SCREEN_VERTICAL = 1;
	private TextView showInfo;
	public static int tagTimes = 0;

	private Drawable draw;
	private Drawable drawSelect;
	private Handler handler;
	private String pName = "";

	private TextView tv_mem;
	private TextView tv_dump;
	private TextView tv_gc;
	
	private boolean isGc;
	private boolean isDump;
	private boolean isMem;
	private boolean isMoved; // 一次touch操作中是否有移动过

	public static ArrayList<MemInfo> memInfoList = new ArrayList<MemInfo>();

	private static GTMemHelperFloatview INSTANCE;

	public static GTMemHelperFloatview getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTMemHelperFloatview();
		}
		return INSTANCE;
	}

	@Override
	public void onCreate(Context context) {
		super.onCreate(context);

		mContext = context;
		net_switch_view = LayoutInflater.from(context).inflate(
				R.layout.gt_float_getproinfo, null);

		handler = new DrawHandler();

		draw = context.getResources().getDrawable(R.drawable.memfloatview);
		drawSelect = context.getResources().getDrawable(
				R.drawable.selected_Blue);

		createView();
		netswitch();
	}

	private void netswitch() {

		showInfo = (TextView) net_switch_view.findViewById(R.id.show_info);

		tv_mem = (TextView) net_switch_view.findViewById(R.id.tv_mobile);
		tv_dump = (TextView) net_switch_view.findViewById(R.id.tv_dump);
		tv_gc = (TextView) net_switch_view.findViewById(R.id.tv_GC);

		tv_mem.setOnTouchListener(this);
		tv_dump.setOnTouchListener(this);
		tv_gc.setOnTouchListener(this);
	}

	private void getProInfo(String pName) {
		tagTimes++;
		long time = System.currentTimeMillis();

		if (!pName.equals("")) {
			MemInfo mi = MemInfoByCMD.getMemInfo(pName);
			mi.time = time;

			if (mi.dalvikHeapSize != 0) {
				showInfo.setText(mi.toString());

				memInfoList.add(mi);
			}
		}
	}

	private void createView() {
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		try
		{
			wm.addView(net_switch_view, wmParams);
		}
		catch (Exception e)
		{
			/*
			 * 有的Android6会报permission denied for this window type问题
			 * https://github.com/intercom/intercom-android/issues/116
			 * 在这种系统上直接屏蔽悬浮窗
			 */
			stopSelf();
			return;
		}

		net_switch_view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				if (SCREEN_VERTICAL == checkScreenOrientation()) {
					y = event.getRawY()
							- DeviceUtils.getStatusBarHeight(mContext);
				} else {
					y = event.getRawY();
				}

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					StartX = x;
					StartY = y;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					isMoved = false;
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					updateViewPosition();
					mTouchStartX = mTouchStartY = 0;
					if (!isMoved)
					{
						if (isMem)
						{
							tv_mem.setBackgroundDrawable(drawSelect);
							handler.sendEmptyMessage(1);
							isMem = false;
						}
						if (isDump)
						{
							tv_dump.setBackgroundDrawable(drawSelect);
							handler.sendEmptyMessage(2);
							isDump = false;
						}
						if (isGc)
						{
							tv_gc.setBackgroundDrawable(drawSelect);
							handler.sendEmptyMessage(3);
							isGc = false;
						}
						isMoved = false;
					}
					break;
				}
				return true;
			}
		});
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		if (Math.abs(x - StartX) > 10 || Math.abs(y - StartY) > 10) {
			wmParams.x = (int) (x - mTouchStartX);
			wmParams.y = (int) (y - mTouchStartY);
			isMoved = true;
			wm.updateViewLayout(net_switch_view, wmParams);
		}
	}

	private void gc() {
		String pid = String.valueOf(ProcessUtils
				.getProcessPID(AUTManager.pkn.toString()));

		if (!pid.equals("-1")) {
			boolean isSucess = true;
			ProcessBuilder pb = null;

			String cmd = "kill -10 " + pid;
			pb = new ProcessBuilder("su", "-c", cmd);

			Process exec = null;

			pb.redirectErrorStream(true);
			try {
				exec = pb.start();

				InputStream is = exec.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((reader.readLine()) != null) {
					isSucess = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				isSucess = false;
			}
			// 至此命令算是执行成功
			if (isSucess)
			{
				handler.sendEmptyMessage(5);
			}
			
		} else {
			Log.d("gc error", "pid not found!");
		}
	}
	
	private void dumpHeap() {
		String pid = String.valueOf(ProcessUtils
				.getProcessPID(AUTManager.pkn.toString()));

		if (!pid.equals("-1")) {
			boolean isSucess = true;
			ProcessBuilder pb = null;
			
			String sFolder = Env.S_ROOT_DUMP_FOLDER + AUTManager.pkn.toString() + "/";
			File folder = new File(sFolder);
			if (!folder.exists())
			{
				folder.mkdirs();
			}

			String cmd = "am dumpheap " + pid + " "// 命令
					+ Env.S_ROOT_DUMP_FOLDER + AUTManager.pkn.toString() + "/"// 输出路径
					+ "dump_" + pid + "_" + GTUtils.getSaveDate() + ".hprof"; // 输出文件名
			pb = new ProcessBuilder("su", "-c", cmd);

			Process exec = null;

			pb.redirectErrorStream(true);
			try {
				exec = pb.start();

				InputStream is = exec.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((reader.readLine()) != null) {
					isSucess = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				isSucess = false;
			}
			// 至此命令算是执行成功
			if (isSucess)
			{
				handler.sendEmptyMessage(6);
			}
			
		} else {
			Log.d("dump error", "pid not found!");
		}
	}

	private int checkScreenOrientation() {
		int dev_width = DeviceUtils.getDevWidth();
		int dev_height = DeviceUtils.getDevHeight();
		int orientation = SCREEN_VERTICAL; // 0:竖屏 1:横屏
		if (dev_width > dev_height) {
			orientation = SCREEN_HORIZONTALLY;
		}
		return orientation;
	}

	@Override
	public void onStart(Intent intent) {
		pName = intent.getStringExtra("pName");
		super.onStart(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		floatview_flag = false;
		net_state_flag = false;

		try{
			wm.removeView(net_switch_view);
		}
		catch (Exception e)
		{
		}

		INSTANCE = null;
	}

	@Override
	public IBinder onBind() {
		return null;
	}

	@SuppressLint("HandlerLeak")
	class DrawHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int type = msg.what;
			if (type == 1)
			{
				getProInfo(pName);
				tv_mem.setText("refresh(" + tagTimes + ")");
				tv_mem.setBackgroundDrawable(draw);
			}
			else if (type == 2)
			{
				dumpHeap();
				tv_dump.setBackgroundDrawable(draw);
			}
			else if (type == 3)
			{
				gc();
				tv_gc.setBackgroundDrawable(draw);
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (v.getId() == tv_mem.getId())
			{
				isMem = true;
			}
			else if (v.getId() == tv_gc.getId())
			{
				isGc = true;
			}
			else if (v.getId() == tv_dump.getId())
			{
				isDump = true;
			}
		}
		return false; // 消息继续向下传递
	}
}
