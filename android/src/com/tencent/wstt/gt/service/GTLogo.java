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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTMainActivity;
import com.tencent.wstt.gt.activity.SplashActivity;
import com.tencent.wstt.gt.api.utils.DeviceUtils;

public class GTLogo extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	public static View entrance_view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	public static ImageView entrance_img;
	private float moveX;
	private float moveY;
	private float ProX;
	private float ProY;
	private float move_oldX = -1000;
	private float move_oldY = -1000;
	// 处理logo的短按、长按事件用
	private boolean keyUpDown = false;
	private int timer = 0;
	private boolean move_event = false;
	// logo靠边的动画处理
	private int logo_width = 0;
	private int dev_width = 0;
	private int half_logo_width = 0;
	private int cur_X = 0;
	private int mAnimDistance = 20;
	private final int MSG_ANIMATION_ToLeft = 1;
	private final int MSG_ANIMATION_ToRight = 2;
	private final int MSG_ANIMATION_FINISH = 3;
	// 从悬浮窗切到logo后logo的重定位处理
	private static int redirect_x = -5000; // 这里设这个初始值，是因为屏幕上不可能取道这个值
	private static int redirect_y = -5000;

	public static boolean refresh_logo_flag = false;
	public static boolean fromFloatview_pos = false;
	public static boolean gtLogoRunned = false;
	public static boolean gtlogo_run_flag = true;

	private final int ID_FLOATVIEW = 1;

	public void onCreate() {

		super.onCreate();

		gtLogoRunned = true;

		entrance_view = LayoutInflater.from(this).inflate(R.layout.gt_entrance,
				null);
		entrance_img = (ImageView) entrance_view
				.findViewById(R.id.entrance_img);
		Drawable drawable = getResources().getDrawable(R.drawable.logo3);
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		entrance_img.setImageBitmap(bitmap);
		entrance_img.setBackgroundColor(0);
		entrance_img.setVisibility(View.VISIBLE);

		createView();

		// 新启一个线程处理数据，如果有UI变化，通过线程去让handler更新UI
		refresh_logo_flag = true;
		Thread thread = new Thread(new MyRunnable());
		thread.start();
	}

	public class MyRunnable implements Runnable {
		public void run() {
			while (refresh_logo_flag) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = logo_handler.obtainMessage();
				msg.what = 0;
				msg.sendToTarget();

				if (GTServiceController.INSTANCE
						.getServiceControllerSwitchState()
						&& ID_FLOATVIEW == GTServiceController.INSTANCE
								.getCurAviableService()) {
					Message myMsg = logo_handler.obtainMessage();
					myMsg.what = 1;
					myMsg.sendToTarget();
					GTServiceController.INSTANCE
							.setServiceControllerSwitchStateClose();
				}
			}
		}
	}

	public Handler logo_handler = new Handler() {
		public void handleMessage(Message msg) {

			if (!checkIsInGT()) {
				if (msg.what == 0) {
					if (-5000 != redirect_x) {
						redirectLogoPos(redirect_x, redirect_y);
						redirect_x = -5000;
					}
				}
				if (msg.what == 1) {
					entrance_view.setVisibility(View.GONE);

					int[] loc = new int[2];
					entrance_view.getLocationOnScreen(loc);
					int abs_x = loc[0];
					int abs_y = loc[1];
					int logo_width = entrance_view.getMeasuredWidth();
					int logo_height = entrance_view.getMeasuredHeight();
					int statusBar_height = DeviceUtils
							.getStatusBarHeight(getApplicationContext());
					int view_bottom_right_x = abs_x + logo_width;
					int view_bottom_right_y = (abs_y - statusBar_height)
							+ logo_height;
					int view_bottom_left_x = abs_x;
					int view_bottom_left_y = (abs_y - statusBar_height)
							+ logo_height;
					int floatview_pos_x = 0;
					int floatview_pos_y = 0;
					if (abs_x > dev_width / 2) { // right
						floatview_pos_x = view_bottom_right_x;
						floatview_pos_y = view_bottom_right_y;
					} else { // left
						floatview_pos_x = view_bottom_left_x;
						floatview_pos_y = view_bottom_left_y;
					}

					GTFloatView.setReDirectXY(floatview_pos_x, floatview_pos_y);

				}
				checkIsVisiable();
			}

			super.handleMessage(msg);
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

	private static boolean LogoIsClick = false;

	private void viewIsOnClick() {
		timer = 0;
		LogoIsClick = true;
		if (!GTMainActivity.dlgIsShow) {
			Intent intent = new Intent(GTLogo.this, SplashActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	private void viewIsOnLongClick() {
		timer = 0;
		GTServiceController.INSTANCE.setCurAviableService(ID_FLOATVIEW);
		Message msg = logo_handler.obtainMessage();
		msg.what = 1;
		msg.sendToTarget();

		if (!GTFloatView.floatViewRunned) {
			Intent intent = new Intent(GTLogo.this, GTFloatView.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(intent);
		}
	}

	private void createView() {
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
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
			wm.addView(entrance_view, wmParams);
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
		

		entrance_img.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				x = event.getRawX();
				y = event.getRawY()
						- DeviceUtils
								.getStatusBarHeight(getApplicationContext());

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					move_event = false;
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					ProX = event.getRawX();
					ProY = event.getRawY();

					clickHandler.sendEmptyMessage(0);

					break;
				case MotionEvent.ACTION_MOVE:
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
					updateViewPositionEnd();
					move_oldX = -1000;
					move_oldY = -1000;
					mTouchStartX = mTouchStartY = 0;

					clickHandler.sendEmptyMessage(1);
					break;
				}

				return true;
			}
		});
	}

	private void updateViewPosition() {

		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);

		wm.updateViewLayout(entrance_view, wmParams);
	}

	private void updateViewPositionEnd() {
		if (LogoIsClick) {
			LogoIsClick = false;
			return;
		}
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		cur_X = wmParams.x;

		wm.updateViewLayout(entrance_view, wmParams);

		processLogoPosition();
	}

	private void processLogoPosition() {
		logo_width = entrance_view.getMeasuredWidth();
		dev_width = DeviceUtils.getDevWidth();
		half_logo_width = logo_width / 2;
		if ((cur_X + half_logo_width) < dev_width / 2) {
			mAnimHandler.sendEmptyMessage(MSG_ANIMATION_ToLeft);
		} else {
			mAnimHandler.sendEmptyMessage(MSG_ANIMATION_ToRight);
		}
	}

	private Handler mAnimHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ANIMATION_ToLeft:
				doAnimationToLeft();
				break;
			case MSG_ANIMATION_ToRight:
				doAnimationToRight();
				break;
			case MSG_ANIMATION_FINISH:
				break;
			}
		};
	};

	private void doAnimationToLeft() {
		if (cur_X > 0) {
			cur_X -= mAnimDistance;
			wmParams.x = cur_X;
			wm.updateViewLayout(entrance_view, wmParams);
			mAnimHandler.sendEmptyMessage(MSG_ANIMATION_ToLeft);
		}
	}

	private void doAnimationToRight() {
		if (cur_X < dev_width) {
			cur_X += mAnimDistance;
			wmParams.x = cur_X;
			wm.updateViewLayout(entrance_view, wmParams);
			mAnimHandler.sendEmptyMessage(MSG_ANIMATION_ToRight);
		}
	}

	private boolean checkIsInGT() {
		if (GTApp.isInGT()){
			entrance_view.setVisibility(View.GONE);
			return true;
		} else {
			entrance_view.setVisibility(View.VISIBLE);
			return false;
		}
	}

	void checkIsVisiable() {
		int id = GTServiceController.INSTANCE.getCurAviableService();
		switch (id) {
		case 0:
			entrance_view.setVisibility(View.VISIBLE);
			if (GTServiceController.INSTANCE.show_alert) {
				Drawable drawable = getResources()
						.getDrawable(R.drawable.logo1);
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				entrance_img.setImageBitmap(bitmap);
				entrance_img.setBackgroundColor(0);
				entrance_img.setVisibility(View.VISIBLE);
			} else {
				Drawable drawable = getResources().getDrawable(
						R.drawable.gt_entrlogo);
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				entrance_img.setImageBitmap(bitmap);
				entrance_img.setBackgroundColor(0);
				entrance_img.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			entrance_view.setVisibility(View.GONE);
			break;
		}
	}

	public static void setReDirectXY(int x, int y) {
		redirect_x = x;
		redirect_y = y;
	}

	private void redirectLogoPos(int x, int y) {
		wmParams.x = x;
		wmParams.y = y;
		cur_X = wmParams.x;

		wm.updateViewLayout(entrance_view, wmParams);
		entrance_view.setVisibility(View.VISIBLE);

		processLogoPosition();
	}

	@Override
	public void onDestroy() {
		refresh_logo_flag = false;
		try
		{
			/*
			 * 有的Android6会报permission denied for this window type问题
			 * https://github.com/intercom/intercom-android/issues/116
			 * 在这种系统上直接屏蔽悬浮窗
			 */
			wm.removeView(entrance_view);
		}
		catch (Exception e)
		{
			
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
