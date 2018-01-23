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
package com.tencent.wstt.gt.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;

/**
 * Toast显示帮助类，不需要在UI线程调用也不会死
 */
public abstract class ToastUtil {

	private static void _showToast(final Context context, final int textid,
			final String text, final int delay, final boolean allowToastQueue) {
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				synchronized (ToastUtil.class) {
					Toast toast = null;
					if (textid == -1) {
						toast = Toast.makeText(context, text, delay);
					} else {
						toast = Toast.makeText(context, textid, delay);
					}
					toast.show();
				}
			}
		});
	}

	private static void _showToast(final Context context, final int textid,
			final String text, final int delay, final String gravity,
			final boolean allowToastQueue) {
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				synchronized (ToastUtil.class) {
					Toast toast = null;
					if (textid == -1) {
						toast = Toast.makeText(context, text, delay);
					} else {
						toast = Toast.makeText(context, textid, delay);
					}
					if (gravity.equals("center")) {
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}else{
						toast.show();
					}
				}
			}
		});
	}

	private static void _showToast(final Context context, final int textid,
			final String text, final int delay, final boolean allowToastQueue,
			final int gravity, final int xOffset, final int yOffset) {
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				synchronized (ToastUtil.class) {
					Toast toast = null;
					if (textid == -1) {
						toast = Toast.makeText(context, text, delay);
					} else {
						toast = Toast.makeText(context, textid, delay);
					}
					toast.setGravity(gravity, xOffset, yOffset);
					toast.show();
				}
			}
		});
	}

	public static void ShowShortToast(final Context context, final int textid) {
		_showToast(context, textid, null, Toast.LENGTH_SHORT, false);
	}

	public static void ShowShortToast(final Context context, final String text) {
		_showToast(context, -1, text, Toast.LENGTH_SHORT, false);
	}

	public static void ShowLongToast(final Context context, final int textid) {
		_showToast(context, textid, null, Toast.LENGTH_LONG, false);

	}

	public static void ShowLongToast(final Context context, final String text) {
		_showToast(context, -1, text, Toast.LENGTH_LONG, false);
	}

	public static void ShowLongToast(final Context context, final String text,
			final int gravity, final int xOffset, final int yOffset) {
		_showToast(context, -1, text, Toast.LENGTH_LONG, false, gravity,
				xOffset, yOffset);
	}

	public static void ShowLongToast(final Context context, final String text,
			String gravity) {
		if (gravity.equals("center")) {
			_showToast(context, -1, text, Toast.LENGTH_LONG, "center", false);
		} else {
			_showToast(context, -1, text, Toast.LENGTH_LONG, "bottom", false);
		}
	}

	/**
	 * 
	 * @param context
	 * @param textid
	 * @param allowToastQueue
	 *            是否允许Toast等待显示, 如果不允许, 3秒内的第二条Toast将不被显示
	 */
	public static void ShowShortToast(final Context context, final int textid,
			boolean allowToastQueue) {
		_showToast(context, textid, null, Toast.LENGTH_SHORT, allowToastQueue);
	}

	/**
	 * 
	 * @param context
	 * @param text
	 * @param allowToastQueue
	 *            是否允许Toast等待显示, 如果不允许, 3秒内的第二条Toast将不被显示
	 */
	public static void ShowShortToast(final Context context, final String text,
			boolean allowToastQueue) {
		_showToast(context, -1, text, Toast.LENGTH_SHORT, allowToastQueue);
	}

	/**
	 * 
	 * @param context
	 * @param textid
	 * @param allowToastQueue
	 *            是否允许Toast等待显示, 如果不允许, 3秒内的第二条Toast将不被显示
	 */
	public static void ShowLongToast(final Context context, final int textid,
			boolean allowToastQueue) {
		_showToast(context, textid, null, Toast.LENGTH_LONG, allowToastQueue);

	}

	/**
	 * 
	 * @param context
	 * @param text
	 * @param allowToastQueue
	 *            是否允许Toast等待显示, 如果不允许, 3秒内的第二条Toast将不被显示
	 */
	public static void ShowLongToast(final Context context, final String text,
			boolean allowToastQueue) {
		_showToast(context, -1, text, Toast.LENGTH_LONG, allowToastQueue);
	}

	/**
	 * 
	 * @param window
	 *            通常由Activity的getWindow()方法获取
	 * @param message
	 *            消息内容
	 * @param color
	 *            RGB颜色，如Color.argb(0xff, 0xcb, 0x74, 0x18)
	 */
	public static void openToastWithColor(Window window, String message,
			int color) {
		SpannableString msg = new SpannableString(message);
		int msg_len = msg.length();
		msg.setSpan(new ForegroundColorSpan(color), 31, msg_len - 9,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		LayoutInflater inflater = LayoutInflater.from(GTApp.getContext());
		View view = inflater.inflate(R.layout.gt_toast,
				(ViewGroup) window.findViewById(R.id.toast_layout));
		TextView textView = (TextView) view.findViewById(R.id.toast_text);

		textView.setText(msg);

		Toast toast = Toast
				.makeText(GTApp.getContext(), msg, Toast.LENGTH_LONG);
		Drawable drawable = toast.getView().getBackground();
		view.setBackgroundDrawable(drawable);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
