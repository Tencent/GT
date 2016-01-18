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

import com.tencent.wstt.gt.R;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 自定义view的确认对话框（显示服务条款）
 */
public class CustomerConfirmDialog extends CustomerDialog {
	private IDialogListener listener;

	private View mContentView;

	/*
	 * 自定义layout的ID
	 */
	private int mLayoutId;

	public CustomerConfirmDialog(Context context, int layoutId) {
		super(context);

		mLayoutId = layoutId;

		// 初始化按钮监听
		initButtonListener();
	}

	/**
	 * 初始化按钮监听
	 */
	private void initButtonListener() {
		Button positiveButton = getPositiveButton();
		positiveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener != null) {
					listener.onSure();
				}
			}
		});

		Button negativeButton = getNegativeButton();
		negativeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (listener != null) {
					listener.onCancel();
				}
			}
		});
	}

	/**
	 * 设置监听
	 * 
	 * @param l
	 */
	public void setListener(IDialogListener l) {
		this.listener = l;
	}

	/**
	 * 设置控件可见
	 * 
	 * @param message
	 */
	public void setViewVisibility(int viewId, int visibility) {
		if (mContentView == null) {
			return;
		}

		mContentView.findViewById(viewId).setVisibility(visibility);
	}

	/**
	 * 设置内容文字
	 * 
	 * @param message
	 */
	public void setViewContent(int viewId, Spanned content) {
		if (mContentView == null) {
			return;
		}

		((TextView) mContentView.findViewById(viewId)).setText(content);
	}

	/**
	 * 设置确认按钮名称
	 * 
	 * @param text
	 */
	public void setPositiveButton(String text) {
		Button positiveButton = getPositiveButton();
		positiveButton.setText(text);
	}

	/**
	 * 设置确认按钮名称
	 * 
	 * @param textResId
	 */
	public void setPositiveButton(int textResId) {
		setPositiveButton(getString(textResId));
	}

	/**
	 * 设置取消按钮名称， 同时设置 取消监听 可以为空
	 * 
	 * @param text
	 */
	public void setNegativeButton(String text) {
		Button negativeButton = getNegativeButton();
		negativeButton.setText(text);
	}

	/**
	 * 设置取消按钮名称， 同时设置 取消监听 可以为空
	 * 
	 * @param textResId
	 */
	public void setNegativeButton(int textResId) {
		String text = getString(textResId);
		setNegativeButton(text);
	}

	@Override
	protected View initContentView() {
		if (mLayoutId == 0) {
			mLayoutId = R.layout.legalterm_body;
		}

		mContentView = LayoutInflater.from(context).inflate(mLayoutId, null);
		return mContentView;
	}

	/**
	 * dialog 操作监听
	 */
	public static interface IDialogListener {
		/**
		 * 确定回调
		 */
		void onSure();

		/**
		 * 取消回调
		 */
		void onCancel();
	}
}
