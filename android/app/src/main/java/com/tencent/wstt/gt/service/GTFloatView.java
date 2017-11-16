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
package com.tencent.wstt.gt.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.InPara;
import com.tencent.wstt.gt.OutPara;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTACSettingActivity;
import com.tencent.wstt.gt.activity.GTInputParamSetActivity;
import com.tencent.wstt.gt.activity.GTMainActivity;
import com.tencent.wstt.gt.activity.SplashActivity;
import com.tencent.wstt.gt.api.base.GTTime;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.internal.GTMemoryDaemonHelper;
import com.tencent.wstt.gt.manager.IpUIManager;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.plugin.listener.PluginListener;

public class GTFloatView extends Service {

	public static View view_floatview;
	public static LinearLayout floatview;
	public static FrameLayout fm_floatview_top_layout;
	public static FrameLayout fm_floatview_bottom_layout;
	private FrameLayout view_floatview_bottom;
	private FrameLayout view_floatview_ip;
	private ImageView img_floatview_bottom_arrow;
	private TextView tv_floatview_title;
	// Title
	private boolean tv_floatview_title_down = false;
	private int tv_floatview_title_timer = 0;
	// Timer
	private TextView tv_timer;
	private final static int STATE_TIMER_INIT = 0;
	private final static int STATE_TIMER_START = 1;
	private final static int STATE_TIMER_END = 2;
	private static int state_timer = STATE_TIMER_START;
	private static long baseTime = 0;
	private double showTime = 0;
	private int timer_delaytime = 100;
	private Thread timer_thread;
	public static boolean refresh_timer_flag = false;
	// profiler
	private ImageView img_floatview_profiler_switch;
	public static boolean refresh_profiler_flag = false;
	private int alpha_count = 0;
	private static boolean profiler_already_run = false;
	// 输出参数
	private TextView tv_floatview_op1;
	private TextView tv_floatview_op1_value;
	private TextView tv_floatview_op2;
	private TextView tv_floatview_op2_value;
	private TextView tv_floatview_op3;
	private TextView tv_floatview_op3_value;
	private static boolean op1_fold = true;
	private static boolean op2_fold = true;
	private static boolean op3_fold = true;
	public static List<OutPara> ac_op;
	private int tv_floatview_op1_fold_height = 0;
	private int tv_floatview_op1_unfold_height = 0;
	private int tv_floatview_op2_fold_height = 0;
	private int tv_floatview_op2_unfold_height = 0;
	private int tv_floatview_op3_fold_height = 0;
	private int tv_floatview_op3_unfold_height = 0;
	private boolean tv_floatview_op1_down = false;
	private boolean tv_floatview_op2_down = false;
	private boolean tv_floatview_op3_down = false;
	private int tv_floatview_op1_timer = 0;
	private int tv_floatview_op2_timer = 0;
	private int tv_floatview_op3_timer = 0;
	private static int op_len_state = 0;
	private static int old_op_len = 0;
	private static boolean op_len_modify = false;
	private String alias1 = "";
	private String value1 = "";
	private String alias2 = "";
	private String value2 = "";
	private String alias3 = "";
	private String value3 = "";
	private OutPara op1 = null;
	private OutPara op2 = null;
	private OutPara op3 = null;
	// 输入参数
	private final static int STATE_FOLD = 0;
	private final static int STATE_UNFOLD = 1;
	private static int state_fold = STATE_UNFOLD; // 1 展开 0 收起
	private TextView tv_floatview_ip1;
	private TextView tv_floatview_ip1_value;
	private TextView tv_floatview_ip2;
	private TextView tv_floatview_ip2_value;
	private TextView tv_floatview_ip3;
	private TextView tv_floatview_ip3_value;
	private List<InPara> ac_ip;
	private static int len_ip = 0;
	private static int old_ip_len = 0;
	private boolean tv_floatview_ip1_down = false;
	private boolean tv_floatview_ip2_down = false;
	private boolean tv_floatview_ip3_down = false;
	private int tv_floatview_ip1_timer = 0;
	private int tv_floatview_ip2_timer = 0;
	private int tv_floatview_ip3_timer = 0;
	// Logo
	private ImageView img_left_top_logo;
	private ImageView img_right_top_logo;
	private ImageView img_left_bottom_logo;
	private ImageView img_right_bottom_logo;
	private boolean keyUpDown = false;
	private int timer = 0;
	private boolean move_event = false;
	private boolean up_event = false;
	private final int LOGO_BOTTOM_RIGHT = 0;
	private final int LOGO_BOTTOM_LEFT = 1;
	private final int LOGO_TOP_RIGHT = 2;
	private final int LOGO_TOP_LEFT = 3;
	// 悬浮窗的位置计算
	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private float move_oldX = -1000;
	private float move_oldY = -1000;
	public float StartX;
	public float StartY;
	private float ProX;
	private float ProY;
	private float moveX;
	private float moveY;
	private float mTouchStartX;
	private float mTouchStartY;
	private float touchX;
	private float touchY;
	private float x;
	private float y;
	private static int cur_logo_pos = 0; // 0:bottom_right 1:bottom_left
											// 2:top_right 3:top_left
	private final static int MSG_ANIMATION_DirectToRight = 0;
	private final static int MSG_ANIMATION_DirectToLeft = 1;
	private final static int MSG_ANIMATION_DirectToDown = 2;
	private final static int MSG_ANIMATION_DirectToTop = 3;
	private int last_x = 0;
	private int orig_x = 0;
	private int last_y = 0;
	private int orig_y = 0;
	private int h_last_y = 0;
	private int h_orig_y = 0;
	private int h_last_x = 0;
	private int h_orig_x = 0;
	private static int redirect_x = -5000; // 这里设这个初始值，是因为屏幕上不可能取道这个值
	private static int redirect_y = -5000;
	// 悬浮窗接收到的点击状态
	public int state;
	// 悬浮窗的刷新
	private int fv_delaytime = 500;
	private int fv_value_delaytime = 1000;
	public static boolean refresh_fv_flag;
	// 悬浮窗的运行状态
	public static boolean floatViewRunned = false;
	public static boolean floatview_run_flag = true;
	private final int ID_LOGO = 0;
	// 默认设置的屏幕密度
	private float dev_density = 2.0f;
	// 移动动画的移动步阶
	private int mAnimDistance = 1;

	@Override
	public void onCreate() {
		super.onCreate();

		floatViewRunned = true;

		view_floatview = LayoutInflater.from(this).inflate(
				R.layout.gt_floatview3, null);
		floatview = (LinearLayout) view_floatview
				.findViewById(R.id.gt_floatview);
		view_floatview_bottom = (FrameLayout) view_floatview
				.findViewById(R.id.fl_floatview_bottom);
		view_floatview_bottom.setOnClickListener(fold);
		TextView tv_floatview_bottom_border = (TextView) view_floatview
				.findViewById(R.id.floatview_bottom_border);
		tv_floatview_bottom_border.setOnClickListener(fold);
		view_floatview_ip = (FrameLayout) view_floatview
				.findViewById(R.id.fl_floatview_ip);
		img_floatview_bottom_arrow = (ImageView) view_floatview
				.findViewById(R.id.floatview_bottom_arrow);

		tv_timer = (TextView) view_floatview.findViewById(R.id.floatview_timer);
		tv_timer.setClickable(true);
		tv_timer.setOnClickListener(operatTimer);

		img_floatview_profiler_switch = (ImageView) view_floatview
				.findViewById(R.id.floatview_profiler_switch);

		dev_density = DeviceUtils.getDevDensity();

		createView();

		view_floatview.setVisibility(View.GONE);

		initOutParamLayout();
		initInParamLayout();
		initFloatViewBorder();
		// 新启一个线程去更新悬浮窗中的数据
		refresh_fv_flag = true;
		Thread thread = new Thread(new MyRunnable());
		thread.start();

		initParamsView();

		tv_floatview_title = (TextView) view_floatview
				.findViewById(R.id.tv_floatview_title);
		tv_floatview_title.setOnTouchListener(touchTitle);

		// GTServiceController.INSTANCE.setCurAviableService(1);

	}

	private void initParamsView() {
		tv_floatview_op1.setVisibility(View.GONE);
		tv_floatview_op1_value.setVisibility(View.GONE);
		tv_floatview_op2.setVisibility(View.GONE);
		tv_floatview_op2_value.setVisibility(View.GONE);
		tv_floatview_op3.setVisibility(View.GONE);
		tv_floatview_op3_value.setVisibility(View.GONE);
	}

	// 悬浮窗Title拖拽、点击处理
	OnTouchListener touchTitle = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				tv_floatview_title_clickHandler.sendEmptyMessage(0);
				break;
			}
			return false;
		}
	};

	Handler tv_floatview_title_clickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				tv_floatview_title_down = true;
				tv_floatview_title_MoveClickListener();
				break;
			}
		}
	};

	private void tv_floatview_title_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_title_down) {
					try {
						sleep(200);
						tv_floatview_title_timer++;
						if (up_event) {
							tv_floatview_title_down = false;
							if (tv_floatview_title_timer <= 2) {
								tv_floatview_title_onClick();
							}
							tv_floatview_title_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_title_onClick() {
		if (refresh_profiler_flag) {
			Message msg = floatview_handler.obtainMessage();
			msg.what = 5;
			msg.sendToTarget();

			if (null != PluginListener.mapPIListener) {
				for (Entry<Object, PluginListener> e : PluginListener.mapPIListener
						.entrySet()) {
					PluginListener listener = (PluginListener) e.getValue();
					listener.setFlag(false);
					listener.end();
				}
			}
		} else {
			Message msg = floatview_handler.obtainMessage();
			msg.what = 4;
			msg.sendToTarget();

			if (null != PluginListener.mapPIListener) {
				for (Entry<Object, PluginListener> e : PluginListener.mapPIListener
						.entrySet()) {
					PluginListener listener = (PluginListener) e.getValue();
					listener.setFlag(true);
					listener.start();
				}
			}
		}
	}

	// 处理悬浮窗刷新事件的handler（横竖屏位置的重新定位、参数值刷新、计时器刷新、性能刷新、logo显示位置重新确定、性能开关状态）
	Handler floatview_handler = new Handler() {
		public void handleMessage(Message msg) {

			if (floatview_run_flag) {
				if (msg.what == 10) {
					checkIsInGT();
					if (-5000 != redirect_x) {
						redirectFloatViewPos(redirect_x, redirect_y);
						redirect_x = -5000;
					}
				}
				if (!checkIsInGT()) {
					if (msg.what == 0) {
						refreshOutputParamsUI();
						refreshInputParamsUI();
					}
					if (msg.what == 1) {
						refreshTimer();
					}
					if (msg.what == 2) {
						refreshProfilerImg();
						// refreshProfilerImg_old();
						// refreshProfilerImg_drawDifPic();
					}
					if (msg.what == 3) {
						view_floatview.setVisibility(View.GONE);

						int[] loc = new int[2];
						view_floatview.getLocationOnScreen(loc);
						int abs_x = loc[0];
						int abs_y = loc[1];
						int floatview_width = view_floatview.getMeasuredWidth();
						int floatview_height = view_floatview
								.getMeasuredHeight();
						int logo_width = (int) (25 * dev_density);
						int logo_height = (int) (25 * dev_density);
						int statusBar_height = DeviceUtils
								.getStatusBarHeight(getApplicationContext());
						int logo_pos_x = 0;
						int logo_pos_y = 0;
						if ((ProX - abs_x) < logo_width) {
							if ((ProY - abs_y) < logo_height) {
								int view_top_left_x = abs_x;
								int view_top_left_y = abs_y - statusBar_height;
								logo_pos_x = view_top_left_x;
								logo_pos_y = view_top_left_y;
							} else {
								int view_bottom_left_x = abs_x;
								int view_bottom_left_y = (abs_y - statusBar_height)
										+ floatview_height;
								logo_pos_x = view_bottom_left_x;
								logo_pos_y = view_bottom_left_y - logo_height;
							}
						} else {
							if ((ProY - abs_y) < logo_height) {
								int view_top_right_x = abs_x + floatview_width;
								int view_top_right_y = (abs_y - statusBar_height);
								int view_logo_bottom_right_x = view_top_right_x;
								int view_logo_bottom_right_y = view_top_right_y
										+ logo_height;
								logo_pos_x = view_logo_bottom_right_x
										- logo_width;
								logo_pos_y = view_logo_bottom_right_y
										- logo_height;
							} else {
								int view_bottom_right_x = abs_x
										+ floatview_width;
								int view_bottom_right_y = (abs_y - statusBar_height)
										+ floatview_height;
								logo_pos_x = view_bottom_right_x - logo_width;
								logo_pos_y = view_bottom_right_y - logo_height;
							}
						}

						GTLogo.setReDirectXY(logo_pos_x, logo_pos_y);

					}
					if (msg.what == 4) { // 用来检测GT里面性能开关打开后，悬浮窗上的界面初次更新，也就是模拟性能快捷开关的click操作
						refresh_profiler_flag = true;
						profiler_already_run = true;
						alpha_count = 0;
						img_floatview_profiler_switch
								.setBackgroundResource(R.drawable.gw_pause);

						// AC设置的开关功能不同，逻辑不同
						switch (GTACSettingActivity.getSwitchType()) {
						case GTACSettingActivity.PROFILER:
							// 如果想开启，需要先校验
							if (!GTMemoryDaemonHelper.startGWOrProfValid()) {
								return;
							}

							GTTime.enable();
							TextView tv_title_pro = (TextView) view_floatview
									.findViewById(R.id.tv_floatview_title);
							tv_title_pro
									.setBackgroundResource(R.drawable.floatview_title_bar);
							break;
						case GTACSettingActivity.GW:
							// 如果想开启，需要先校验
							if (!GTMemoryDaemonHelper.startGWOrProfValid()) {
								return;
							}

							OpUIManager.gw_running = true;
							TextView tv_title = (TextView) view_floatview
									.findViewById(R.id.tv_floatview_title);
							tv_title.setBackgroundResource(R.drawable.floatview_title_bar_gw);
							break;
						}
					}
					if (msg.what == 5) { // 用来检测GT里面profiler性能开关关闭后，悬浮窗上的界面初次更新，也就是模拟性能快捷开关的click操作
						refresh_profiler_flag = false;
						profiler_already_run = false;
						img_floatview_profiler_switch
								.setBackgroundResource(R.drawable.gw_start);

						// AC设置的开关功能不同，逻辑不同
						switch (GTACSettingActivity.getSwitchType()) {
						case GTACSettingActivity.PROFILER:
							GTTime.disable();
							TextView tv_title_pro = (TextView) view_floatview
									.findViewById(R.id.tv_floatview_title);
							tv_title_pro
									.setBackgroundResource(R.drawable.floatview_title_bar);
							break;
						case GTACSettingActivity.GW:
							OpUIManager.gw_running = false;
							TextView tv_title = (TextView) view_floatview
									.findViewById(R.id.tv_floatview_title);
							tv_title.setBackgroundResource(R.drawable.floatview_title_bar_gw);
							break;
						}

					}
					checkIsVisiable();
				}
			}

			super.handleMessage(msg);

		}
	};

	private void refreshTimer() {
		long curTime = System.currentTimeMillis();
		double tempTime = (curTime - baseTime) / (1000 + 0.0);
		BigDecimal b = new BigDecimal(tempTime);
		showTime = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (showTime >= 999.9) {
			baseTime = System.currentTimeMillis();
		}
		tv_timer.setText(showTime + "''");
		tv_timer.postInvalidate();
	}

	private void refreshProfilerImg() {
		if (refresh_profiler_flag) {
			alpha_count += 20;
			if (alpha_count / 20 > 5) {
				alpha_count = 20;
			}
			img_floatview_profiler_switch
					.setBackgroundResource(R.drawable.gw_start);
		} else {

			img_floatview_profiler_switch
					.setBackgroundResource(R.drawable.gw_start);
		}
	}

	private void monitorProfilerStatus() {
		// AC设置的开关功能不同，逻辑不同
		switch (GTACSettingActivity.getSwitchType()) {
		case GTACSettingActivity.PROFILER:
			if (GTTime.isEnable()) {
				if (!profiler_already_run) {
					Message msg = floatview_handler.obtainMessage();
					msg.what = 4;
					msg.sendToTarget();
				}
			} else {
				profiler_already_run = false;
				Message msg = floatview_handler.obtainMessage();
				msg.what = 5;
				msg.sendToTarget();
			}
			break;
		case GTACSettingActivity.GW:
			if (OpUIManager.gw_running) {
				Message msg = floatview_handler.obtainMessage();
				msg.what = 4;
				msg.sendToTarget();
			} else {
				OpUIManager.gw_running = false;
				Message msg = floatview_handler.obtainMessage();
				msg.what = 5;
				msg.sendToTarget();
			}
			break;
		}
	}

	public class TimerRunnable implements Runnable {
		@Override
		public void run() {

			baseTime = System.currentTimeMillis();
			while (refresh_timer_flag) {
				try {
					Thread.sleep(timer_delaytime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Message msg = floatview_handler.obtainMessage();
				msg.what = 1;
				msg.sendToTarget();
			}
		}
	}

	public class FloatViewRunnable implements Runnable {

		@Override
		public void run() {
			while (refresh_fv_flag) {
				checkFloatViewPosEffective();
				try {
					Thread.sleep(fv_delaytime);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = floatview_handler.obtainMessage();
				msg.what = 10;
				msg.sendToTarget();

				if (GTServiceController.INSTANCE
						.getServiceControllerSwitchState()
						&& ID_LOGO == GTServiceController.INSTANCE
								.getCurAviableService()) {
					Message myMsg = floatview_handler.obtainMessage();
					myMsg.what = 3;
					myMsg.sendToTarget();
					GTServiceController.INSTANCE
							.setServiceControllerSwitchStateClose();
				}
			}
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		checkFloatViewPosEffective();
		Message msg = floatview_handler.obtainMessage();
		msg.what = 10;
		msg.sendToTarget();
	}

	public class MyRunnable implements Runnable {
		public void run() {
			while (refresh_fv_flag) {
				fv_value_delaytime = OpUIManager.delaytime;
				try {
					Thread.sleep(fv_value_delaytime);
					if (!OpUIManager.gw_running && OpUIManager.list_change) {
						GTServiceController.INSTANCE.setFloatViewFront(true);
						OpUIManager.list_change = false;
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dataRefresh();
				monitorProfilerStatus();
				Message msg = floatview_handler.obtainMessage();
				msg.what = 0;
				msg.sendToTarget();
			}
		}
	}

	private void checkFloatViewPosEffective() {
		int cur_x = wmParams.x;
		int cur_y = wmParams.y;

		int cur_fv_width = view_floatview.getMeasuredWidth();
		int cur_fv_height = view_floatview.getMeasuredHeight();

		int dev_width = DeviceUtils.getDevWidth();
		int dev_height = DeviceUtils.getDevHeight();

		int orientation = checkScreenOrientation(dev_width, dev_height);
		switch (orientation) {
		case 0:
			redirectVerticalPos(cur_x, cur_y, cur_fv_width, cur_fv_height,
					dev_width, dev_height);
			break;
		case 1:
			redirectHorizontalPos(cur_x, cur_y, cur_fv_width, cur_fv_height,
					dev_width, dev_height);
			break;
		}
	}

	private int checkScreenOrientation(int dev_width, int dev_height) {
		int orientation = 0; // 0:竖屏 1:横屏
		if (dev_width > dev_height) {
			orientation = 1;
		}
		return orientation;
	}

	Handler floatview_redirect_Vertical_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ANIMATION_DirectToRight:
				doAnimationDirectToRight();
				break;
			case MSG_ANIMATION_DirectToLeft:
				doAnimationDirectToLeft();
				break;
			case MSG_ANIMATION_DirectToDown:
				doAnimationDirectToDown();
				break;
			case MSG_ANIMATION_DirectToTop:
				doAnimationDirectToTop();
				break;
			}
		}
	};

	private void doAnimationDirectToRight() {
		if (orig_x < last_x) {
			orig_x += mAnimDistance;
			wmParams.x = orig_x;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Vertical_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToRight);
		}
	}

	private void doAnimationDirectToLeft() {
		if (orig_x > last_x) {
			orig_x -= mAnimDistance;
			wmParams.x = orig_x;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Vertical_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToLeft);
		}
	}

	private void doAnimationDirectToDown() {
		if (orig_y < last_y) {
			orig_y += mAnimDistance;
			wmParams.y = orig_y;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Vertical_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToDown);
		}
	}

	private void doAnimationDirectToTop() {
		if (orig_y > last_y) {
			orig_y -= mAnimDistance;
			wmParams.y = orig_y;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Vertical_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToTop);
		}
	}

	private void redirectVerticalPos(int cur_x, int cur_y, int cur_fv_width,
			int cur_fv_height, int dev_width, int dev_height) {
		int logo_width = (int) (30 * dev_density);
		int logo_height = (int) (30 * dev_density);
		// 在左边外面，往右边拉一个logo的位置出来
		if (cur_x < 0 && cur_x + cur_fv_width <= logo_width) {
			last_x = -(cur_fv_width - logo_width);
			orig_x = cur_x;
			Message msg = floatview_redirect_Vertical_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToRight;
			msg.sendToTarget();
		}
		// 在右边外面，往左边拉一个logo的位置出来
		if (dev_width != 0 && cur_x > dev_width - logo_width) {
			last_x = dev_width - logo_width;
			orig_x = cur_x;
			Message msg = floatview_redirect_Vertical_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToLeft;
			msg.sendToTarget();
		}
		// 在上边外面，往下边拉一个logo的位置出来
		if (cur_y < 0 && cur_y + cur_fv_height < logo_height) {
			last_y = logo_height - cur_fv_height;
			orig_y = cur_y;
			Message msg = floatview_redirect_Vertical_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToDown;
			msg.sendToTarget();
		}
		// 在下边外面，往上边拉一个logo的位置出来
		if (dev_height != 0 && dev_height - cur_y < logo_height) {
			last_y = dev_height - logo_height - 60; // 60是悬浮窗计时器那个栏的高度
			orig_y = cur_y;
			Message msg = floatview_redirect_Vertical_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToTop;
			msg.sendToTarget();
		}

	}

	Handler floatview_redirect_Horizontal_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ANIMATION_DirectToRight:
				doAnimationDirectToRightHorizontal();
				break;
			case MSG_ANIMATION_DirectToLeft:
				doAnimationDirectToLeftHorizontal();
				break;
			case MSG_ANIMATION_DirectToDown:
				doAnimationDirectToDownHorizontal();
				break;
			case MSG_ANIMATION_DirectToTop:
				doAnimationDirectToTopHorizontal();
				break;
			}
		}
	};

	private void doAnimationDirectToRightHorizontal() {
		if (h_orig_x < h_last_x) {
			h_orig_x += mAnimDistance;
			wmParams.x = h_orig_x;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Horizontal_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToRight);
		}
	}

	private void doAnimationDirectToLeftHorizontal() {
		if (h_orig_x > h_last_x) {
			h_orig_x -= mAnimDistance;
			wmParams.x = h_orig_x;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Horizontal_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToLeft);
		}
	}

	private void doAnimationDirectToDownHorizontal() {
		if (h_orig_y < h_last_y) {
			h_orig_y += mAnimDistance;
			wmParams.y = h_orig_y;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Horizontal_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToDown);
		}
	}

	private void doAnimationDirectToTopHorizontal() {

		if (h_orig_y > h_last_y) {
			h_orig_y -= mAnimDistance;
			wmParams.y = h_orig_y;
			wm.updateViewLayout(view_floatview, wmParams);
			floatview_redirect_Horizontal_handler
					.sendEmptyMessage(MSG_ANIMATION_DirectToTop);
		}

	}

	private void redirectHorizontalPos(int cur_x, int cur_y, int cur_fv_width,
			int cur_fv_height, int dev_width, int dev_height) {
		int logo_height = (int) (30 * dev_density);
		int logo_width = (int) (30 * dev_density);
		// 转过来后，直接跑到屏幕外面的情况
		if (cur_y + logo_height >= dev_height) {
			h_last_y = dev_height - logo_height - 60; // 60是悬浮窗计时器那个栏的高度
			h_orig_y = cur_y;
			Message msg = floatview_redirect_Horizontal_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToTop;
			msg.sendToTarget();
		}

		// 在左边外面，往右边拉一个logo的位置出来
		if (cur_x < 0 && cur_x + cur_fv_width <= logo_width) {
			h_last_x = -(cur_fv_width - logo_width);
			h_orig_x = cur_x;
			Message msg = floatview_redirect_Horizontal_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToRight;
			msg.sendToTarget();
		}
		// 在右边外面，往左边拉一个logo的位置出来
		if (dev_width != 0 && cur_x > dev_width - logo_width) {
			h_last_x = dev_width - logo_width;
			h_orig_x = cur_x;
			Message msg = floatview_redirect_Horizontal_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToLeft;
			msg.sendToTarget();
		}
		// 在上边外面，往下边拉一个logo的位置出来
		if (cur_y < 0 && cur_y + cur_fv_height < logo_height) {
			h_last_y = logo_height - cur_fv_height;
			h_orig_y = cur_y;
			Message msg = floatview_redirect_Horizontal_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToDown;
			msg.sendToTarget();
		}
		// 在下边外面，往上边拉一个logo的位置出来
		if (dev_height != 0 && dev_height - cur_y < logo_height) {
			h_last_y = dev_height - logo_height - 60; // 60是悬浮窗计时器那个栏的高度
			h_orig_y = cur_y;
			Message msg = floatview_redirect_Horizontal_handler.obtainMessage();
			msg.what = MSG_ANIMATION_DirectToTop;
			msg.sendToTarget();
		}
	}

	public class ProfilerRunnable implements Runnable {
		public void run() {
			while (refresh_profiler_flag) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Message msg = floatview_handler.obtainMessage();
				msg.what = 2;
				msg.sendToTarget();
			}
		}
	}

	private Handler tv_floatview_op_clickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				tv_floatview_op1_down = true;
				tv_floatview_op1_MoveClickListener();
				break;
			case 1:
				tv_floatview_op2_down = true;
				tv_floatview_op2_MoveClickListener();
				break;
			case 2:
				tv_floatview_op3_down = true;
				tv_floatview_op3_MoveClickListener();
				break;
			}
		};
	};

	private void tv_floatview_op1_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_op1_down) {
					try {
						sleep(200);
						tv_floatview_op1_timer++;
						if (up_event) {
							tv_floatview_op1_down = false;
							if (tv_floatview_op1_timer <= 2) {
								tv_floatview_op1_onClick();
							}
							tv_floatview_op1_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_op1_onClick() {
		FVIsClick = true;
		tv_floatview_op_Handler.sendEmptyMessage(0);
	}

	private Handler tv_floatview_op_Handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (op1_fold) {
					op1_fold = false;
					tv_floatview_op1_fold_height = tv_floatview_op1_value
							.getMeasuredHeight();
					int width_text = (int) tv_floatview_op1_value
							.getPaint()
							.measureText(
									tv_floatview_op1_value.getText().toString());
					int width_textView = tv_floatview_op1_value
							.getMeasuredWidth();

					if (tv_floatview_op1_value.getText().toString().equals("")) {
						return;
					}
					if (width_textView < width_text) {
						// 改底色
						tv_floatview_op1
								.setBackgroundResource(R.drawable.floatview_selected_bottom);
						tv_floatview_op1
								.setHeight(tv_floatview_op1_unfold_height);
						tv_floatview_op1_value
								.setBackgroundResource(R.drawable.floatview_selected_bottom);
						tv_floatview_op1_value
								.setHeight(tv_floatview_op1_unfold_height);

						tv_floatview_op1_value.setSingleLine(false);
						tv_floatview_op1_value.setMaxLines(5);
						tv_floatview_op1.setGravity(Gravity.TOP | Gravity.LEFT);
					} else {
						tv_floatview_op1.setGravity(Gravity.TOP | Gravity.LEFT);
					}
				} else {
					op1_fold = true;
					// 改底色
					tv_floatview_op1
							.setBackgroundResource(R.drawable.floatview_op_textview_left);
					tv_floatview_op1.setHeight(tv_floatview_op1_fold_height);
					tv_floatview_op1_value
							.setBackgroundResource(R.drawable.floatview_op_textview_right);
					tv_floatview_op1_value
							.setHeight(tv_floatview_op1_fold_height);
					tv_floatview_op1_unfold_height = tv_floatview_op1_value
							.getMeasuredHeight();

					tv_floatview_op1_value.setSingleLine(true);
					tv_floatview_op1.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				}

				checkOthersOPState(1);
				break;
			case 1:
				if (op2_fold) {
					op2_fold = false;
					tv_floatview_op2_fold_height = tv_floatview_op2_value
							.getMeasuredHeight();
					int width_text = (int) tv_floatview_op2_value
							.getPaint()
							.measureText(
									tv_floatview_op2_value.getText().toString());
					int width_textView = tv_floatview_op2_value
							.getMeasuredWidth();

					if (width_textView < width_text) {
						// 改底色
						tv_floatview_op2
								.setBackgroundResource(R.drawable.floatview_selected_bottom);
						tv_floatview_op2
								.setHeight(tv_floatview_op2_unfold_height);
						tv_floatview_op2_value
								.setBackgroundResource(R.drawable.floatview_selected_bottom);
						tv_floatview_op2_value
								.setHeight(tv_floatview_op2_unfold_height);
					} else {
						// 不需要改底色
					}

					tv_floatview_op2_value.setSingleLine(false);
					tv_floatview_op2_value.setMaxLines(5);
					tv_floatview_op2.setGravity(Gravity.TOP | Gravity.LEFT);

				} else {
					op2_fold = true;
					tv_floatview_op2_unfold_height = tv_floatview_op2_value
							.getMeasuredHeight();
					// 改底色
					tv_floatview_op2
							.setBackgroundResource(R.drawable.floatview_op_textview_left);
					tv_floatview_op2.setHeight(tv_floatview_op2_fold_height);
					tv_floatview_op2_value
							.setBackgroundResource(R.drawable.floatview_op_textview_right);
					tv_floatview_op2_value
							.setHeight(tv_floatview_op2_fold_height);

					tv_floatview_op2_value.setSingleLine(true);
					tv_floatview_op2.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				}
				checkOthersOPState(2);
				break;
			case 2:
				if (op3_fold) {
					op3_fold = false;
					tv_floatview_op3_fold_height = tv_floatview_op3_value
							.getMeasuredHeight();
					int width_text = (int) tv_floatview_op3_value
							.getPaint()
							.measureText(
									tv_floatview_op3_value.getText().toString());
					int width_textView = tv_floatview_op3_value
							.getMeasuredWidth();
					if (width_textView < width_text) {
						// 改底色
						tv_floatview_op3
								.setBackgroundResource(R.drawable.floatview_selected_bottom);
						tv_floatview_op3
								.setHeight(tv_floatview_op3_unfold_height);
						tv_floatview_op3_value
								.setBackgroundResource(R.drawable.floatview_selected_bottom);
						tv_floatview_op3_value
								.setHeight(tv_floatview_op3_unfold_height);
					} else {
						// 不需要改底色
					}

					tv_floatview_op3_value.setSingleLine(false);
					tv_floatview_op3_value.setMaxLines(5);
					tv_floatview_op3.setGravity(Gravity.TOP | Gravity.LEFT);
					// tv_floatview_op3.setPadding(24, 0, 0, 0);
					// tv_floatview_op3.setGravity(Gravity.TOP);
					// tv_floatview_op3.setPadding(tv_floatview_op3_left_orig,
					// tv_floatview_op3_top_orig, 0, 0);

				} else {
					op3_fold = true;
					tv_floatview_op3_unfold_height = tv_floatview_op3_value
							.getMeasuredHeight();
					// 改底色
					tv_floatview_op3
							.setBackgroundResource(R.drawable.floatview_op_textview_left);
					tv_floatview_op3.setHeight(tv_floatview_op3_fold_height);
					tv_floatview_op3_value
							.setBackgroundResource(R.drawable.floatview_op_textview_right);
					tv_floatview_op3_value
							.setHeight(tv_floatview_op3_fold_height);

					tv_floatview_op3_value.setSingleLine(true);
					tv_floatview_op3.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				}
				checkOthersOPState(3);
				break;
			}
		};
	};

	private void tv_floatview_op2_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_op2_down) {
					try {
						sleep(200);
						tv_floatview_op2_timer++;
						if (up_event) {
							tv_floatview_op2_down = false;
							if (tv_floatview_op2_timer <= 2) {
								tv_floatview_op2_onClick();
							}
							tv_floatview_op2_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_op2_onClick() {
		FVIsClick = true;
		tv_floatview_op_Handler.sendEmptyMessage(1);
	}

	private void tv_floatview_op3_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_op3_down) {
					try {
						sleep(200);
						tv_floatview_op3_timer++;
						if (up_event) {
							tv_floatview_op3_down = false;
							if (tv_floatview_op3_timer <= 2) {
								tv_floatview_op3_onClick();
							}
							tv_floatview_op3_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_op3_onClick() {
		FVIsClick = true;
		tv_floatview_op_Handler.sendEmptyMessage(2);
	}

	private void initOutParamLayout() {
		// 初始化出参布局
		tv_floatview_op1 = (TextView) view_floatview
				.findViewById(R.id.floatview_op1);
		tv_floatview_op1_value = (TextView) view_floatview
				.findViewById(R.id.floatview_op1_value);

		tv_floatview_op2 = (TextView) view_floatview
				.findViewById(R.id.floatview_op2);
		tv_floatview_op2_value = (TextView) view_floatview
				.findViewById(R.id.floatview_op2_value);

		tv_floatview_op3 = (TextView) view_floatview
				.findViewById(R.id.floatview_op3);
		tv_floatview_op3_value = (TextView) view_floatview
				.findViewById(R.id.floatview_op3_value);

		// 设置点击事件
		tv_floatview_op1_value.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_op_clickHandler.sendEmptyMessage(0);
					break;
				}
				return false;
			}
		});

		tv_floatview_op2_value.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_op_clickHandler.sendEmptyMessage(1);
					break;
				}
				return false;
			}
		});

		tv_floatview_op3_value.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_op_clickHandler.sendEmptyMessage(2);
					break;
				}
				return false;
			}
		});

	}

	private Handler tv_floatview_ip_clickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				tv_floatview_ip1_down = true;
				tv_floatview_ip1_MoveClickListener();
				break;
			case 1:
				tv_floatview_ip2_down = true;
				tv_floatview_ip2_MoveClickListener();
				break;
			case 2:
				tv_floatview_ip3_down = true;
				tv_floatview_ip3_MoveClickListener();
				break;
			}
		};
	};

	private void tv_floatview_ip1_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_ip1_down) {
					try {
						sleep(200);
						tv_floatview_ip1_timer++;
						if (up_event) {
							tv_floatview_ip1_down = false;
							if (tv_floatview_ip1_timer <= 2) {
								tv_floatview_ip1_onClick();
							}
							tv_floatview_ip1_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_ip1_onClick() {
		FVIsClick = true;
		tv_floatview_ip_Handler.sendEmptyMessage(0);
	}

	private void tv_floatview_ip2_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_ip2_down) {
					try {
						sleep(200);
						tv_floatview_ip2_timer++;
						if (up_event) {
							tv_floatview_ip2_down = false;
							if (tv_floatview_ip2_timer <= 2) {
								tv_floatview_ip2_onClick();
							}
							tv_floatview_ip2_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_ip2_onClick() {
		FVIsClick = true;
		tv_floatview_ip_Handler.sendEmptyMessage(1);
	}

	private void tv_floatview_ip3_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_floatview_ip3_down) {
					try {
						sleep(200);
						tv_floatview_ip3_timer++;
						if (up_event) {
							tv_floatview_ip3_down = false;
							if (tv_floatview_ip3_timer <= 2) {
								tv_floatview_ip3_onClick();
							}
							tv_floatview_ip3_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_floatview_ip3_onClick() {
		FVIsClick = true;
		tv_floatview_ip_Handler.sendEmptyMessage(2);
	}

	private Handler tv_floatview_ip_Handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				InPara iv = ac_ip.get(0);
				Bundle bundle = new Bundle();
				bundle.putString("ip_name", iv.getKey());
				bundle.putString("ip_client", iv.getClient());
				bundle.putStringArrayList("ip_values",
						(ArrayList<String>) iv.getValues());

				Intent intent = new Intent(GTFloatView.this,
						GTInputParamSetActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case 1:
				InPara iv_2 = ac_ip.get(1);
				Bundle bundle_2 = new Bundle();
				bundle_2.putString("ip_name", iv_2.getKey());
				bundle_2.putString("ip_client", iv_2.getClient());
				bundle_2.putStringArrayList("ip_values",
						(ArrayList<String>) iv_2.getValues());

				Intent intent_2 = new Intent(GTFloatView.this,
						GTInputParamSetActivity.class);
				intent_2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent_2.putExtras(bundle_2);
				startActivity(intent_2);
				break;
			case 2:
				InPara iv_3 = ac_ip.get(2);
				Bundle bundle_3 = new Bundle();
				bundle_3.putString("ip_name", iv_3.getKey());
				bundle_3.putString("ip_client", iv_3.getClient());
				bundle_3.putStringArrayList("ip_values",
						(ArrayList<String>) iv_3.getValues());

				Intent intent_3 = new Intent(GTFloatView.this,
						GTInputParamSetActivity.class);
				intent_3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent_3.putExtras(bundle_3);
				startActivity(intent_3);
				break;
			}
		};
	};

	private void initInParamLayout() {
		// 初始化出参布局
		tv_floatview_ip1 = (TextView) view_floatview
				.findViewById(R.id.floatview_ip1);
		tv_floatview_ip1_value = (TextView) view_floatview
				.findViewById(R.id.floatview_ip1_value);

		tv_floatview_ip2 = (TextView) view_floatview
				.findViewById(R.id.floatview_ip2);
		tv_floatview_ip2_value = (TextView) view_floatview
				.findViewById(R.id.floatview_ip2_value);

		tv_floatview_ip3 = (TextView) view_floatview
				.findViewById(R.id.floatview_ip3);
		tv_floatview_ip3_value = (TextView) view_floatview
				.findViewById(R.id.floatview_ip3_value);

		// 初始化相应点击事件的view

		// 设置点击事件
		tv_floatview_ip1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_ip_clickHandler.sendEmptyMessage(0);
					break;
				}
				return false;
			}
		});
		tv_floatview_ip1_value.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_ip_clickHandler.sendEmptyMessage(0);
					break;
				}
				return false;
			}
		});

		tv_floatview_ip2.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_ip_clickHandler.sendEmptyMessage(1);
					break;
				}
				return false;
			}
		});
		tv_floatview_ip2_value.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_ip_clickHandler.sendEmptyMessage(1);
					break;
				}
				return false;
			}
		});

		tv_floatview_ip3.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_ip_clickHandler.sendEmptyMessage(2);
					break;
				}
				return false;
			}
		});

		tv_floatview_ip3_value.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_floatview_ip_clickHandler.sendEmptyMessage(2);
					break;
				}
				return false;
			}
		});
	}

	private void initFloatViewBorder() {
		img_left_top_logo = (ImageView) view_floatview
				.findViewById(R.id.img_gt_logo_top_left);
		img_left_top_logo.setVisibility(View.GONE);
		img_right_top_logo = (ImageView) view_floatview
				.findViewById(R.id.img_gt_logo_top_right);
		img_right_top_logo.setVisibility(View.GONE);
		img_left_bottom_logo = (ImageView) view_floatview
				.findViewById(R.id.img_gt_logo_bottom_left);
		img_left_bottom_logo.setVisibility(View.GONE);
		img_right_bottom_logo = (ImageView) view_floatview
				.findViewById(R.id.img_gt_logo_bottom_right);
		img_right_bottom_logo.setVisibility(View.VISIBLE);

	}

	private void checkOthersOPState(int id) {
		switch (id) {
		case 1:
			if (!op2_fold) {
				op2_fold = true;
				tv_floatview_op2_unfold_height = tv_floatview_op2_value
						.getMeasuredHeight();
				// 改底色
				tv_floatview_op2
						.setBackgroundResource(R.drawable.floatview_op_textview_left);
				tv_floatview_op2.setHeight(tv_floatview_op2_fold_height);
				tv_floatview_op2_value
						.setBackgroundResource(R.drawable.floatview_op_textview_right);
				tv_floatview_op2_value.setHeight(tv_floatview_op2_fold_height);

				tv_floatview_op2_value.setSingleLine(true);
				tv_floatview_op2.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				// tv_floatview_op2.setPadding(24, 0, 0, 0);
			}
			if (!op3_fold) {
				op3_fold = true;
				tv_floatview_op3_unfold_height = tv_floatview_op3_value
						.getMeasuredHeight();
				// 改底色
				tv_floatview_op3
						.setBackgroundResource(R.drawable.floatview_op_textview_left);
				tv_floatview_op3.setHeight(tv_floatview_op3_fold_height);
				tv_floatview_op3_value
						.setBackgroundResource(R.drawable.floatview_op_textview_right);
				tv_floatview_op3_value.setHeight(tv_floatview_op3_fold_height);

				tv_floatview_op3_value.setSingleLine(true);
				tv_floatview_op3.setGravity(Gravity.BOTTOM | Gravity.LEFT);
			}
			break;
		case 2:
			if (!op1_fold) {
				op1_fold = true;
				tv_floatview_op1_unfold_height = tv_floatview_op1_value
						.getMeasuredHeight();
				// 改底色
				tv_floatview_op1
						.setBackgroundResource(R.drawable.floatview_op_textview_left);
				tv_floatview_op1.setHeight(tv_floatview_op1_fold_height);
				tv_floatview_op1_value
						.setBackgroundResource(R.drawable.floatview_op_textview_right);
				tv_floatview_op1_value.setHeight(tv_floatview_op1_fold_height);

				tv_floatview_op1_value.setSingleLine(true);
				tv_floatview_op1.setGravity(Gravity.BOTTOM | Gravity.LEFT);
			}
			if (!op3_fold) {
				op3_fold = true;
				tv_floatview_op3_unfold_height = tv_floatview_op3_value
						.getMeasuredHeight();
				// 改底色
				tv_floatview_op3
						.setBackgroundResource(R.drawable.floatview_op_textview_left);
				tv_floatview_op3.setHeight(tv_floatview_op3_fold_height);
				tv_floatview_op3_value
						.setBackgroundResource(R.drawable.floatview_op_textview_right);
				tv_floatview_op3_value.setHeight(tv_floatview_op3_fold_height);

				tv_floatview_op3_value.setSingleLine(true);
				tv_floatview_op3.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				// tv_floatview_op3.setPadding(24, 0, 0, 0);
			}
			break;
		case 3:
			if (!op1_fold) {
				op1_fold = true;
				tv_floatview_op1_unfold_height = tv_floatview_op1_value
						.getMeasuredHeight();
				// 改底色
				tv_floatview_op1
						.setBackgroundResource(R.drawable.floatview_op_textview_left);
				tv_floatview_op1.setHeight(tv_floatview_op1_fold_height);
				tv_floatview_op1_value
						.setBackgroundResource(R.drawable.floatview_op_textview_right);
				tv_floatview_op1_value.setHeight(tv_floatview_op1_fold_height);

				tv_floatview_op1_value.setSingleLine(true);
				// tv_floatview_op1_value.setTextSize(20);
				tv_floatview_op1.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				// tv_floatview_op1.setPadding(24, 0, 0, 0);
			}
			if (!op2_fold) {
				op2_fold = true;
				tv_floatview_op2_unfold_height = tv_floatview_op2_value
						.getMeasuredHeight();
				// 改底色
				tv_floatview_op2
						.setBackgroundResource(R.drawable.floatview_op_textview_left);
				tv_floatview_op2.setHeight(tv_floatview_op2_fold_height);
				tv_floatview_op2_value
						.setBackgroundResource(R.drawable.floatview_op_textview_right);
				tv_floatview_op2_value.setHeight(tv_floatview_op2_fold_height);

				tv_floatview_op2_value.setSingleLine(true);
				tv_floatview_op2.setGravity(Gravity.BOTTOM | Gravity.LEFT);
				// tv_floatview_op2.setPadding(24, 0, 0, 0);
			}
			break;
		}
	}

	private void dataRefresh() {

		refreshOutputParams();
		refreshInputParams();

	}

	private void refreshOutputParams() {
		ac_op = OpUIManager.getACOutputParams();
		
		int cur_size_list_op = ac_op.size();
		if (cur_size_list_op == old_op_len) {
			op_len_modify = false;
		} else {
			op_len_modify = true;
		}

		switch (cur_size_list_op) {
		case 0:
			op_len_state = 0;
			break;
		case 1:
			op_len_state = 1;
			op1 = ac_op.get(0);
			alias1 = ac_op.get(0).getAlias();
			value1 = op1.getValue();
			break;
		case 2:
			op_len_state = 2;
			op1 = ac_op.get(0);
			alias1 = ac_op.get(0).getAlias();
			value1 = op1.getValue();
			op2 = ac_op.get(1);
			alias2 = ac_op.get(1).getAlias();
			value2 = op2.getValue();
			break;
		case 3:
			op_len_state = 3;
			op1 = ac_op.get(0);
			alias1 = ac_op.get(0).getAlias();
			value1 = op1.getValue();
			op2 = ac_op.get(1);
			alias2 = ac_op.get(1).getAlias();
			value2 = op2.getValue();
			op3 = ac_op.get(2);
			alias3 = ac_op.get(2).getAlias();
			value3 = op3.getValue();
			break;
		}
	}

	private void refreshOutputParamsUI() {

		switch (op_len_state) {
		case 0:
			if (op_len_modify) {
				tv_floatview_op1.setVisibility(View.GONE);
				tv_floatview_op1_value.setVisibility(View.GONE);
				tv_floatview_op2.setVisibility(View.GONE);
				tv_floatview_op2_value.setVisibility(View.GONE);
				tv_floatview_op3.setVisibility(View.GONE);
				tv_floatview_op3_value.setVisibility(View.GONE);

				old_op_len = 0;
			}
			break;
		case 1:
			if (op_len_modify) {
				tv_floatview_op1.setVisibility(View.VISIBLE);
				tv_floatview_op1_value.setVisibility(View.VISIBLE);
				tv_floatview_op2.setVisibility(View.GONE);
				tv_floatview_op2_value.setVisibility(View.GONE);
				tv_floatview_op3.setVisibility(View.GONE);
				tv_floatview_op3_value.setVisibility(View.GONE);

				old_op_len = 1;

			}
			tv_floatview_op1.setText(alias1);
			tv_floatview_op1_value.setText(value1);
			break;
		case 2:
			if (op_len_modify) {
				tv_floatview_op1.setVisibility(View.VISIBLE);
				tv_floatview_op1_value.setVisibility(View.VISIBLE);
				tv_floatview_op2.setVisibility(View.VISIBLE);
				tv_floatview_op2_value.setVisibility(View.VISIBLE);
				tv_floatview_op3.setVisibility(View.GONE);
				tv_floatview_op3_value.setVisibility(View.GONE);

				old_op_len = 2;
			}

			tv_floatview_op1.setText(alias1);
			tv_floatview_op1_value.setText(value1);
			tv_floatview_op2.setText(alias2);
			tv_floatview_op2_value.setText(value2);
			break;
		case 3:
			if (op_len_modify) {
				tv_floatview_op1.setVisibility(View.VISIBLE);
				tv_floatview_op1_value.setVisibility(View.VISIBLE);
				tv_floatview_op2.setVisibility(View.VISIBLE);
				tv_floatview_op2_value.setVisibility(View.VISIBLE);
				tv_floatview_op3.setVisibility(View.VISIBLE);
				tv_floatview_op3_value.setVisibility(View.VISIBLE);

				old_op_len = 3;
			}

			tv_floatview_op1.setText(alias1);
			tv_floatview_op1_value.setText(value1);
			tv_floatview_op2.setText(alias2);
			tv_floatview_op2_value.setText(value2);
			tv_floatview_op3.setText(alias3);
			tv_floatview_op3_value.setText(value3);
			break;
		}
	}

	private void refreshInputParamsUI() {
		if (len_ip == 0) {
			view_floatview_ip.setVisibility(View.GONE);
			img_floatview_bottom_arrow.setVisibility(View.GONE);
			view_floatview_bottom.setVisibility(View.GONE);
			old_ip_len = 0;
		} else {
			if (old_ip_len == 0) {
				view_floatview_ip.setVisibility(View.VISIBLE);
				img_floatview_bottom_arrow.setVisibility(View.VISIBLE);
				view_floatview_bottom.setVisibility(View.VISIBLE);
				old_ip_len = len_ip;
			}
		}

		switch (len_ip) {
		case 0:
			break;
		case 1:
			if (ip_len_modify) {
				tv_floatview_ip2.setVisibility(View.GONE);
				tv_floatview_ip2_value.setVisibility(View.GONE);
				tv_floatview_ip3.setVisibility(View.GONE);
				tv_floatview_ip3_value.setVisibility(View.GONE);

				tv_floatview_ip1.setWidth((int) (dev_density * 173));
				tv_floatview_ip1_value.setWidth((int) (dev_density * 173));

				old_list_ip = 1;
			}

			tv_floatview_ip1.setText(ac_ip.get(0).getAlias());
			tv_floatview_ip1_value.setText(ac_ip.get(0).getValues().get(0));
			break;
		case 2:
			if (ip_len_modify) {
				tv_floatview_ip2.setVisibility(View.VISIBLE);
				tv_floatview_ip2_value.setVisibility(View.VISIBLE);
				tv_floatview_ip3.setVisibility(View.GONE);
				tv_floatview_ip3_value.setVisibility(View.GONE);

				tv_floatview_ip1.setWidth((int) (dev_density * (173 / 2)));
				tv_floatview_ip1_value
						.setWidth((int) (dev_density * (173 / 2)));
				tv_floatview_ip2.setWidth((int) (dev_density * (173 / 2)));
				tv_floatview_ip2_value
						.setWidth((int) (dev_density * (173 / 2)));

				old_list_ip = 2;
			}

			tv_floatview_ip1.setText(ac_ip.get(0).getAlias());
			tv_floatview_ip1_value.setText(ac_ip.get(0).getValues().get(0));
			tv_floatview_ip2.setText(ac_ip.get(1).getAlias());
			tv_floatview_ip2_value.setText(ac_ip.get(1).getValues().get(0));
			break;
		case 3:
			if (ip_len_modify) {
				tv_floatview_ip2.setVisibility(View.VISIBLE);
				tv_floatview_ip2_value.setVisibility(View.VISIBLE);
				tv_floatview_ip3.setVisibility(View.VISIBLE);
				tv_floatview_ip3_value.setVisibility(View.VISIBLE);

				tv_floatview_ip1.setWidth((int) (dev_density * (173 / 3)));
				tv_floatview_ip1_value
						.setWidth((int) (dev_density * (173 / 3)));
				tv_floatview_ip2.setWidth((int) (dev_density * (173 / 3)));
				tv_floatview_ip2_value
						.setWidth((int) (dev_density * (173 / 3)));
				tv_floatview_ip3.setWidth((int) (dev_density * (173 / 3)));
				tv_floatview_ip3_value
						.setWidth((int) (dev_density * (173 / 3)));

				old_list_ip = 3;
			}

			tv_floatview_ip1.setText(ac_ip.get(0).getAlias());
			tv_floatview_ip1_value.setText(ac_ip.get(0).getValues().get(0));
			tv_floatview_ip2.setText(ac_ip.get(1).getAlias());
			tv_floatview_ip2_value.setText(ac_ip.get(1).getValues().get(0));
			tv_floatview_ip3.setText(ac_ip.get(2).getAlias());
			tv_floatview_ip3_value.setText(ac_ip.get(2).getValues().get(0));
			break;
		}
	}

	private int old_list_ip = 0;
	private boolean ip_len_modify = false;

	private void refreshInputParams() {
		ac_ip = IpUIManager.getACInputParams();

		len_ip = ac_ip.size();
		if (old_list_ip == len_ip) {
			ip_len_modify = false;
		} else {
			ip_len_modify = true;
		}
	}

	private OnClickListener operatTimer = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (state_timer) {
			case STATE_TIMER_INIT:
				state_timer = STATE_TIMER_START;
				tv_timer.setText("0.0''");
				tv_timer.setTextColor(Color.WHITE);
				break;
			case STATE_TIMER_START:
				state_timer = STATE_TIMER_END;
				refresh_timer_flag = true;
				timer_thread = new Thread(new TimerRunnable());
				timer_thread.start();
				break;
			case STATE_TIMER_END:
				state_timer = STATE_TIMER_INIT;
				refresh_timer_flag = false;
				tv_timer.setTextColor(Color.GREEN);
				break;
			}
		}
	};

	private OnClickListener fold = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (state_fold) {
			case STATE_FOLD:
				view_floatview_ip.setVisibility(View.VISIBLE);
				img_floatview_bottom_arrow
						.setBackgroundResource(R.drawable.fold_arrow);
				state_fold = STATE_UNFOLD;
				break;
			case STATE_UNFOLD:
				view_floatview_ip.setVisibility(View.GONE);
				img_floatview_bottom_arrow
						.setBackgroundResource(R.drawable.fold_arrow);
				state_fold = STATE_FOLD;
				break;
			}
		}
	};

	private Handler clickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				keyUpDown = true;
				keyUpDownListener();
				break;
			case 1:
				if (keyUpDown) {
					keyUpDown = false;
					if (timer <= 1) {
						viewIsOnClick();
					}
					timer = 0;
				}
				break;
			}
		};
	};

	private int keyUpDownListener() {
		new Thread() {
			public void run() {
				while (keyUpDown) {
					try {
						sleep(200);
						timer++;
						if (!move_event) {
							if (timer >= 5) {
								keyUpDown = false;
								viewIsOnLongClick();
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		return timer;
	}

	private static boolean FVIsClick = false;

	private void viewIsOnClick() {

		timer = 0;
		FVIsClick = true;
		if (isInLogoArea(touchX, touchY)) {
			// 把点开的输出项收起来，为的是防止在出参界面交换位置后，回到悬浮窗打开收起有问题的bug
			if (!op1_fold) {
				tv_floatview_op1_onClick();
			}
			if (!op2_fold) {
				tv_floatview_op2_onClick();
			}
			if (!op3_fold) {
				tv_floatview_op3_onClick();
			}
			if (!GTMainActivity.dlgIsShow) {
				// Intent intent = new Intent(GTFloatView.this,
				// GTMainActivity.class);
				Intent intent = new Intent(GTFloatView.this,
						SplashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}
	}

	private void viewIsOnLongClick() {
		timer = 0;
		if (isInLogoArea(touchX, touchY)) {
			GTServiceController.INSTANCE.setCurAviableService(ID_LOGO);
			Message msg = floatview_handler.obtainMessage();
			msg.what = 3;
			msg.sendToTarget();

			if (!GTLogo.gtLogoRunned) {
				Intent intent = new Intent(GTFloatView.this, GTLogo.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startService(intent);
			}
		}
	}

	private void createView() {

		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角

		wmParams.x = 0;
		wmParams.y = 0;

		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;

		try
		{
			wm.addView(view_floatview, wmParams);
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

		final int sbar_height = DeviceUtils
				.getStatusBarHeight(getApplicationContext());

		view_floatview.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				x = event.getRawX();
				y = event.getRawY() - sbar_height;

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					move_event = false;
					up_event = false;
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					touchX = mTouchStartX;
					touchY = mTouchStartY;
					ProX = event.getRawX();
					ProY = event.getRawY();

					clickHandler.sendEmptyMessage(0);

					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					moveX = event.getRawX();
					moveY = event.getRawY();
					final ViewConfiguration configuration = ViewConfiguration
							.get(getApplicationContext());
					int mTouchSlop = configuration.getScaledTouchSlop();
					// 第一次move
					if (move_oldX == -1000 && move_oldY == -1000) {
						move_oldX = moveX;
						move_oldY = moveY;
						if (Math.abs(moveX - ProX) < mTouchSlop * 2
								&& Math.abs(moveY - ProY) < mTouchSlop * 2) {
							move_event = false;
						} else {
							move_event = true;
							updateViewPosition();
						}
					} else {
						if (move_event == false) {
							if (Math.abs(moveX - move_oldX) < mTouchSlop * 2
									&& Math.abs(moveY - move_oldY) < mTouchSlop * 2) {
								move_event = false;
							} else {
								move_event = true;
								updateViewPosition();
							}
						} else {
							updateViewPosition();
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;
					updateViewPositionEnd();
					move_oldX = -1000;
					move_oldY = -1000;
					mTouchStartX = mTouchStartY = 0;
					up_event = true;
					clickHandler.sendEmptyMessage(1);

					break;
				}

				return true;
			}
		});

	}

	private boolean isInLogoArea(float touchX, float touchY) {

		boolean result = false;

		int cur_logo_float_view_width = view_floatview.getMeasuredWidth();
		int cur_logo_float_view_height = view_floatview.getMeasuredHeight();

		int logo_width = (int) (25 * dev_density);
		int logo_height = (int) (25 * dev_density);

		switch (cur_logo_pos) {
		case 0: // bottom right
			if (touchX >= (cur_logo_float_view_width - logo_width)
					&& touchY >= (cur_logo_float_view_height - logo_height)) {
				result = true;
			}
			break;
		case 1:
			if (touchX <= logo_width
					&& touchY >= (cur_logo_float_view_height - logo_height)) {
				result = true;
			}
			break;
		case 2:
			if (touchX >= (cur_logo_float_view_width - logo_width)
					&& touchY <= logo_height) {
				result = true;
			}
			break;
		case 3:
			if (touchX <= logo_width && touchY <= logo_height) {
				result = true;
			}
			break;
		}

		return result;
	}

	private void updateViewPosition() {

		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);

		wm.updateViewLayout(view_floatview, wmParams);
	}

	private void updateViewPositionEnd() {
		if (FVIsClick) {
			FVIsClick = false;
			return;
		}
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);

		wm.updateViewLayout(view_floatview, wmParams);
		determineLogoPosition();
	}

	private boolean checkIsInGT() {
		if (GTApp.isInGT()){
			view_floatview.setVisibility(View.GONE);
			return true;
		} else {
			view_floatview.setVisibility(View.VISIBLE);
			return false;
		}
	}

	private void checkIsVisiable() {
		int id = GTServiceController.INSTANCE.getCurAviableService();
		switch (id) {
		case 0:
			view_floatview.setVisibility(View.GONE);
			break;
		case 1:
			view_floatview.setVisibility(View.VISIBLE);
			if (GTServiceController.INSTANCE.show_alert) {
				img_left_top_logo.setBackgroundResource(R.drawable.logo1);
				img_right_top_logo.setBackgroundResource(R.drawable.logo1);
				img_left_bottom_logo.setBackgroundResource(R.drawable.logo1);
				img_right_bottom_logo.setBackgroundResource(R.drawable.logo1);

			} else {
				img_left_top_logo
						.setBackgroundResource(R.drawable.gt_entrlogo2);
				img_right_top_logo
						.setBackgroundResource(R.drawable.gt_entrlogo2);
				img_left_bottom_logo
						.setBackgroundResource(R.drawable.gt_entrlogo2);
				img_right_bottom_logo
						.setBackgroundResource(R.drawable.gt_entrlogo2);
			}
			break;
		}

	}

	TimerTask taskTimer = new TimerTask() {
		public void run() {
			long curTime = System.currentTimeMillis();
			double tempTime = (curTime - baseTime) / (1000 + 0.0);
			BigDecimal b = new BigDecimal(tempTime);
			showTime = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	};

	Handler logo_pos_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGO_BOTTOM_RIGHT:
				img_left_top_logo.setVisibility(View.GONE);
				img_right_top_logo.setVisibility(View.GONE);
				img_left_bottom_logo.setVisibility(View.GONE);
				img_right_bottom_logo.setVisibility(View.VISIBLE);
				cur_logo_pos = 0;
				break;
			case LOGO_BOTTOM_LEFT:
				img_left_top_logo.setVisibility(View.GONE);
				img_right_top_logo.setVisibility(View.GONE);
				img_left_bottom_logo.setVisibility(View.VISIBLE);
				img_right_bottom_logo.setVisibility(View.GONE);
				cur_logo_pos = 1;
				break;
			case LOGO_TOP_RIGHT:
				img_left_top_logo.setVisibility(View.GONE);
				img_right_top_logo.setVisibility(View.VISIBLE);
				img_left_bottom_logo.setVisibility(View.GONE);
				img_right_bottom_logo.setVisibility(View.GONE);
				cur_logo_pos = 2;
				break;
			case LOGO_TOP_LEFT:
				img_left_top_logo.setVisibility(View.VISIBLE);
				img_right_top_logo.setVisibility(View.GONE);
				img_left_bottom_logo.setVisibility(View.GONE);
				img_right_bottom_logo.setVisibility(View.GONE);
				cur_logo_pos = 3;
				break;
			}
		}
	};

	private void determineLogoPosition() {
		int top_left_x = wmParams.x;
		int top_left_y = wmParams.y;

		int top_right_x = wmParams.x + view_floatview.getMeasuredWidth();
		int top_right_y = wmParams.y;

		int bottom_left_x = wmParams.x;
		int bottom_left_y = wmParams.y + view_floatview.getMeasuredHeight();

		int bottom_right_x = wmParams.x + view_floatview.getMeasuredWidth();
		int bottom_right_y = wmParams.y + view_floatview.getMeasuredHeight();

		int dev_width = DeviceUtils.getDevWidth();
		int dev_height = DeviceUtils.getDevHeight();

		int logo_height = (int) (25 * dev_density);

		if (top_left_x < 0 && top_left_y < 0) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 0;
			msg.sendToTarget();
		} else if (top_right_x > dev_width && top_left_y < 0) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 1;
			msg.sendToTarget();
		} else if (bottom_left_x < 0 && bottom_left_y > dev_height) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 2;
			msg.sendToTarget();
		} else if (bottom_right_x > dev_width && bottom_right_y > dev_height) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 3;
			msg.sendToTarget();
		} else if (bottom_right_x < dev_width && bottom_left_x >= 0
				&& bottom_left_y + logo_height > dev_height) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 2;
			msg.sendToTarget();
		} else if (bottom_right_x > dev_width && bottom_right_y < dev_height
				&& top_right_y > 0) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 1;
			msg.sendToTarget();
		} else if (top_left_x > 0 && top_right_x < dev_width && top_right_y < 0) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 0;
			msg.sendToTarget();
		} else if (top_left_x < 0 && top_left_y > 0
				&& bottom_left_y < dev_height) {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 0;
			msg.sendToTarget();
		} else {
			Message msg = logo_pos_handler.obtainMessage();
			msg.what = 0;
			msg.sendToTarget();
		}
	}

	public void showImg() {
		view_floatview.setVisibility(View.VISIBLE);
	}

	public static void setReDirectXY(int x, int y) {
		redirect_x = x;
		redirect_y = y;
	}

	private void redirectFloatViewPos(int x, int y) {
		redirectLogoPos(x, y);
		wm.updateViewLayout(view_floatview, wmParams);
		view_floatview.setVisibility(View.VISIBLE);
	}

	private void redirectLogoPos(int x, int y) {
		int dev_width = DeviceUtils.getDevWidth();
		int logo_height = (int) (25 * dev_density);
		if (x > dev_width / 2) { // logo 在右边
			if (y < view_floatview.getMeasuredHeight()) { // 右上角
				wmParams.x = dev_width - view_floatview.getMeasuredWidth();
				wmParams.y = y - logo_height;
				Message msg = logo_pos_handler.obtainMessage();
				msg.what = 2;
				msg.sendToTarget();
			} else { // 右边其他位置
				wmParams.x = dev_width - view_floatview.getMeasuredWidth();
				wmParams.y = y - view_floatview.getMeasuredHeight();
				Message msg = logo_pos_handler.obtainMessage();
				msg.what = 0;
				msg.sendToTarget();
			}
		} else { // logo 在左边
			if (y < view_floatview.getMeasuredHeight()) { // 左上角
				wmParams.x = 0;
				wmParams.y = y - logo_height;
				Message msg = logo_pos_handler.obtainMessage();
				msg.what = 3;
				msg.sendToTarget();
			} else { // 左下角
				wmParams.x = 0;
				wmParams.y = y - view_floatview.getMeasuredHeight();
				Message msg = logo_pos_handler.obtainMessage();
				msg.what = 1;
				msg.sendToTarget();
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onDestroy() {
		refresh_fv_flag = false;
		refresh_timer_flag = false;
		super.onDestroy();
	}
}
