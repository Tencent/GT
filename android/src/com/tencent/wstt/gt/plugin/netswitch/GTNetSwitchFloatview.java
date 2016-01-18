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
package com.tencent.wstt.gt.plugin.netswitch;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.api.utils.SignalUtils;
import com.tencent.wstt.gt.plugin.BaseService;

public class GTNetSwitchFloatview extends BaseService {

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private Context mContext = null;
	public static View net_switch_view;
	private ImageView iv;
	private int delaytime = 1000;

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

	private static GTNetSwitchFloatview INSTANCE;

	public static GTNetSwitchFloatview getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTNetSwitchFloatview();
		}
		return INSTANCE;
	}

	@Override
	public void onCreate(Context context) {
		super.onCreate(context);

		mContext = context;
		net_switch_view = LayoutInflater.from(context).inflate(
				R.layout.gt_float_netswitch, null);
		iv = (ImageView) net_switch_view.findViewById(R.id.img2);
		iv.setVisibility(View.GONE);

		createView();

		Thread thread_floatview = new Thread(new floatviewRunnable());
		thread_floatview.start();

		Thread thread_net_state = new Thread(new showNetStateRunnable());
		thread_net_state.start();
		netswitch();
		initNetSwitch();
	}

	private Handler floatview_Handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				wm.updateViewLayout(net_switch_view, wmParams);
				break;
			}

		};
	};

	public class floatviewRunnable implements Runnable {

		@Override
		public void run() {
			while (floatview_flag) {
				floatview_Handler.sendEmptyMessage(0);
				try {
					Thread.sleep(delaytime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class showNetStateRunnable implements Runnable {
		@Override
		public void run() {
			while (net_state_flag) {
				showNetState();
				try {
					Thread.sleep(delaytime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private TextView tv_netState;
	private int wifi_strength;
	private String mobile_type;
	private int mobile_dbm;
	private String mobile_state;

	private void showNetState() {
		tv_netState = (TextView) net_switch_view.findViewById(R.id.show_info);

		WifiManager wm = (WifiManager) GTApp.getContext().getSystemService(
				Context.WIFI_SERVICE);
		String wifi_state = "";
		switch (wm.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			wifi_state = "disabled";
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			wifi_state = "disabling";
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			wifi_state = "enabled";
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			wifi_state = "enabling";
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			wifi_state = "unknown";
			break;
		}

		wifi_strength = SignalUtils.getWifiStrength();

		mobile_type = SignalUtils.getNetType();

		mobile_state = "";
		if (DeviceUtils.getMobileDataStatus("getMobileDataEnabled")) {
			mobile_state = "enabled";
		} else {
			mobile_state = "disabled";
		}

		mobile_dbm = SignalUtils.getDBM();

		tv_net_switch_drawableHandler.sendEmptyMessage(4);

	}

	private TextView tv_wifi;
	private TextView tv_mobile;

	private void netswitch() {
		tv_wifi = (TextView) net_switch_view.findViewById(R.id.tv_wifi);
		tv_wifi.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_net_switch_clickHandler.sendEmptyMessage(0);
					break;
				}
				return false;
			}
		});

		tv_mobile = (TextView) net_switch_view.findViewById(R.id.tv_mobile);
		tv_mobile.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tv_net_switch_clickHandler.sendEmptyMessage(1);
					break;
				}
				return false;
			}
		});
	}

	private void initNetSwitch() {
		WifiManager wm = (WifiManager) GTApp.getContext().getSystemService(
				Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			tv_net_switch_drawableHandler.sendEmptyMessage(1);
		} else {
			tv_net_switch_drawableHandler.sendEmptyMessage(0);
		}

		if (DeviceUtils.getMobileDataStatus("getMobileDataEnabled")) {
			tv_net_switch_drawableHandler.sendEmptyMessage(3);
		} else {
			tv_net_switch_drawableHandler.sendEmptyMessage(2);
		}
	}

	private boolean up_event = false;
	private boolean tv_wifi_down = false;
	private int tv_wifi_timer = 0;

	private boolean tv_mobile_down = false;
	private int tv_mobile_timer = 0;

	private Handler tv_net_switch_clickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				tv_wifi_down = true;
				tv_wifi_MoveClickListener();
				break;
			case 1:
				tv_mobile_down = true;
				tv_mobile_MoveClickListener();
				break;
			}

		};
	};

	private Handler tv_net_switch_drawableHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				tv_wifi.setBackgroundColor(Color.RED);
				tv_wifi.setText("wifi:OFF");
				break;
			case 1:
				tv_wifi.setBackgroundColor(Color.GREEN);
				tv_wifi.setText("wifi:ON");
				break;
			case 2:
				tv_mobile.setBackgroundColor(Color.RED);
				tv_mobile.setText("mobile:OFF");
				break;
			case 3:
				tv_mobile.setBackgroundColor(Color.GREEN);
				tv_mobile.setText("mobile:ON");
				break;
			case 4:
				tv_netState.setText("wifi:" + wifi_strength + "|" + mobile_type
						+ ":" + mobile_dbm + "\n" + "DataNet:" + mobile_state);
				break;
			}

		};
	};

	private void tv_wifi_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_wifi_down) {
					try {
						sleep(200);
						tv_wifi_timer++;
						if (up_event) {
							tv_wifi_down = false;
							if (tv_wifi_timer <= 2) {
								tv_wifi_onClick();
							}
							tv_wifi_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_wifi_onClick() {
		WifiManager wm = (WifiManager) GTApp.getContext().getSystemService(
				Context.WIFI_SERVICE);
		if (WifiManager.WIFI_STATE_ENABLED == wm.getWifiState()) {
			wm.setWifiEnabled(false);
			tv_net_switch_drawableHandler.sendEmptyMessage(0);
		} else {
			wm.setWifiEnabled(true);
			tv_net_switch_drawableHandler.sendEmptyMessage(1);
		}
	}

	private void tv_mobile_MoveClickListener() {
		new Thread() {
			public void run() {
				while (tv_mobile_down) {
					try {
						sleep(200);
						tv_mobile_timer++;
						if (up_event) {
							tv_mobile_down = false;
							if (tv_mobile_timer <= 2) {
								tv_mobile_onClick();
							}
							tv_mobile_timer = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void tv_mobile_onClick() {
		if (DeviceUtils.getMobileDataStatus("getMobileDataEnabled")) {
			DeviceUtils.setMobileDataStatus(false);
			tv_net_switch_drawableHandler.sendEmptyMessage(2);
		} else {
			DeviceUtils.setMobileDataStatus(true);
			tv_net_switch_drawableHandler.sendEmptyMessage(3);
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

		wm.addView(net_switch_view, wmParams);

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
					up_event = false;
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					up_event = true;
					updateViewPosition();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(net_switch_view, wmParams);
	}

	public void showImg() {
		if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5
				&& !iv.isShown()) {
			iv.setVisibility(View.VISIBLE);
		} else if (iv.isShown()) {
			iv.setVisibility(View.GONE);
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
	public void onDestroy() {
		super.onDestroy();

		floatview_flag = false;
		net_state_flag = false;
		wm.removeView(net_switch_view);

		INSTANCE = null;
	}

	@Override
	public IBinder onBind() {
		return null;
	}

}
