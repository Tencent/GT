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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tencent.wstt.gt.R;

public abstract class CustomerDialog extends Dialog implements OnCancelListener {
	/**
	 * 上下文
	 */
	protected Context context;

	/**
	 * dialog的contentView
	 */
	private View contentView;

	/**
	 * dialog的内容view
	 */
	protected View centerView;

	/**
	 * dialog的titleView
	 */
	protected View titleView;

	/**
	 * 按钮点击事件监听的代理
	 */
	private ButtonClickDelegate mButtonClickDelegate;

	private CheckBox mCheckBoxAgreement;

	/**
	 * 底部的区域
	 */
	private View mBottomArea;

	/**
	 * 创建二次确认dialog
	 */
	public CustomerDialog(Context context) {
		this(context, R.style.Dialog);
	}

	public CustomerDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initCustomerView();
		setOnCancelListener(this);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		Button negativeButton = getNegativeButton();
		if (negativeButton != null) {
			negativeButton.performClick();
		}
	}

	/**
	 * 设置按钮响应监听的代理
	 * 
	 * @param delegate
	 */
	public void setButtonClickDelegate(ButtonClickDelegate delegate) {
		mButtonClickDelegate = delegate;
	}

	/**
	 * 初始化自定义的View对象
	 */
	private void initCustomerView() {
		contentView = LayoutInflater.from(context).inflate(R.layout.dialog,
				null);
		titleView = contentView.findViewById(R.id.dialog_title_text);

		centerView = initContentView();
		((ViewGroup) contentView.findViewById(R.id.content_view))
				.addView(centerView);

		// 初始化按钮监听
		initButtonListener();

		mBottomArea = contentView.findViewById(R.id.bottom_area);
		mCheckBoxAgreement = (CheckBox) mBottomArea
				.findViewById(R.id.cb_agreement);

		setContentView(contentView);
	}

	/**
	 * 初始化按钮监听
	 */
	private void initButtonListener() {
		Button positiveButton = (Button) contentView
				.findViewById(R.id.button_positive);
		positiveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performButtonOnClick(v);
			}
		});

		Button negativeButton = (Button) contentView
				.findViewById(R.id.button_negative);
		negativeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performButtonOnClick(v);
			}
		});
	}

	/**
	 * 处理按钮被点击
	 * 
	 * @param v
	 */
	private void performButtonOnClick(View v) {
		// 如果有代理 则让代理去处理
		// 如果没有代理 默认行为为dismiss
		if (mButtonClickDelegate != null) {
			mButtonClickDelegate.onClick(v);
		} else {
			dismiss();
		}
	}

	/**
	 * 得到确认button
	 * 
	 * @return
	 */
	public Button getPositiveButton() {
		return (Button) contentView.findViewById(R.id.button_positive);
	}

	/**
	 * 得到否定的button
	 * 
	 * @return
	 */
	public Button getNegativeButton() {
		return (Button) contentView.findViewById(R.id.button_negative);
	}

	/**
	 * 初始化contentView视图 由子类根据自己不同的风格去实现
	 */
	protected abstract View initContentView();

	/**
	 * 设置title的文本
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		TextView titleTextView = (TextView) titleView
				.findViewById(R.id.dialog_title_text);
		titleTextView.setText(title);
	}

	/**
	 * 设置title的文本
	 * 
	 * @param titleResId
	 */
	public void setTitle(int titleResId) {
		String title = getString(titleResId);
		setTitle(title);
	}

	/**
	 * 根据资源id获得String
	 * 
	 * @param resId
	 * @return
	 */
	protected String getString(int resId) {
		return context.getString(resId);
	}

	/**
	 * 隐藏title
	 */
	public void hideTitleView() {
		if (null != titleView) {
			titleView.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏底部区域
	 */
	public void hideBottomArea() {
		if (null != mBottomArea) {
			mBottomArea.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏否定按钮
	 */
	public void hideNegativeButton() {
		if (null != mBottomArea) {
			Button btn = (Button) mBottomArea
					.findViewById(R.id.button_negative);
			if (null != btn) {
				btn.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 显示协议CheckBox，默认是隐藏的
	 */
	public void showAgreementCheckBox() {
		if (null != mBottomArea) {
			View v = mBottomArea.findViewById(R.id.agreement_area);
			if (null != v) {
				v.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 协议CheckBox的状态
	 */
	public boolean isCheckBoxAgreementChecked() {
		return mCheckBoxAgreement == null ? false : mCheckBoxAgreement
				.isChecked();
	}

	/**
	 * 按钮点击相应的代理接口
	 */
	public static interface ButtonClickDelegate {
		public void onClick(View v);
	}

}
