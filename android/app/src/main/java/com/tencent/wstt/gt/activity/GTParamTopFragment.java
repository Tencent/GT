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
package com.tencent.wstt.gt.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.tencent.wstt.gt.R;

public class GTParamTopFragment extends Fragment implements OnClickListener {
	// 编辑按钮
	private Button btnEdit;
	
	// 页面碎片对象
	private GTParamInFragment inFragment;
	private GTParamOutFragment outFragment;
	private GTParamInEditFragment inEditFragment;
	private GTParamOutEditFragment outEditFragment;

	// Tab布局
	private Button btnIn;
	private Button btnOut;
	
	// 对Fragment进行管理
	private FragmentManager fragmentManager;

	// 记录当前是否是入参状态，默认是false，即出参状态
	private boolean isInState;

	// 记录当前是否是编辑状态，默认是false，即展示状态
	private boolean isEdit;

	// 记录当前是否可见
	private boolean isShow;

	private int curTabId;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putInt("curTabId", curTabId);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootLayout = inflater.inflate(R.layout.gt_param_top,
				container, false);
		// 初始化布局元素
		initViews(rootLayout);
		fragmentManager = getChildFragmentManager();
		// 第一次启动时选中第1个tab
		if (savedInstanceState != null)
		{
			setFragSelection(savedInstanceState.getInt("curTabId"));
		}
		else
		{
			setFragSelection(1);
		}

		return rootLayout;
	}
	
	private void initViews(View rootLayout)
	{
		btnEdit = (Button) rootLayout.findViewById(R.id.btn_switch_item);
		btnEdit.setOnClickListener(this);
		
		btnIn = (Button) rootLayout.findViewById(R.id.btn_input);
		btnOut = (Button) rootLayout.findViewById(R.id.btn_output);

		btnIn.setOnClickListener(this);
		btnOut.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_input:
			if (isInState) return;
			isInState = true;
			break;
		case R.id.btn_output:
			if (!isInState) return;
			isInState = false;
			break;
		case R.id.btn_switch_item:
			isEdit = !isEdit;
			if (isEdit)
			{
				btnEdit.setBackgroundResource(R.drawable.edit_selected);
				btnEdit.setTextColor(getResources().getColor(R.color.swbtn_select_textcolor));
				btnEdit.setText(getString(R.string.para_done));
			}
			else
			{
				btnEdit.setBackgroundResource(R.drawable.swbtn_default);
				btnEdit.setTextColor(getResources().getColor(R.color.swbtn_default_textcolor));
				btnEdit.setText(getString(R.string.para_edit));
			}
			break;
		default:
			break;
		}
		
		if (isInState && !isEdit) // 入参展示
		{
			curTabId = 0;
			setFragSelection(0);
		}
		else if (!isInState && !isEdit) // 出参展示
		{
			curTabId = 1;
			setFragSelection(1);
		}
		else if (isInState && isEdit) // 入参编辑
		{
			curTabId = 2;
			setFragSelection(2);
		}
		else if (!isInState && isEdit) // 出参编辑
		{
			curTabId = 3;
			setFragSelection(3);
		}
	}

	/**
	 * 根据传入的index参数来设置选中的Fragment页。
	 * 
	 * @param index
	 *            每个Fragment页对应的下标。
	 */
	private void setFragSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		
		switch (index) {
		case 0:
			// 入参
			btnIn.setBackgroundResource(R.drawable.swbtn_selected);
			btnIn.setTextColor(Color.WHITE);
			if (inFragment == null) {
				inFragment = new GTParamInFragment();
				transaction.add(R.id.content, inFragment);
			} else {
				transaction.show(inFragment);
			}
			break;
		case 1:
			// 出参
			btnOut.setBackgroundResource(R.drawable.swbtn_selected);
			btnOut.setTextColor(Color.WHITE);
			if (outFragment == null) {
				outFragment = new GTParamOutFragment();
				transaction.add(R.id.content, outFragment);
			} else {
				transaction.show(outFragment);
			}
			break;
		case 2:
			// 入参编辑
			btnIn.setBackgroundResource(R.drawable.swbtn_selected);
			btnIn.setTextColor(Color.WHITE);
			if (inEditFragment == null) {
				inEditFragment = new GTParamInEditFragment();
				transaction.add(R.id.content, inEditFragment);
			} else {
				transaction.show(inEditFragment);
			}
			break;
		case 3:
			// 出参编辑
			btnOut.setBackgroundResource(R.drawable.swbtn_selected);
			btnOut.setTextColor(Color.WHITE);
			if (outEditFragment == null) {
				outEditFragment = new GTParamOutEditFragment();
				transaction.add(R.id.content, outEditFragment);
			} else {
				transaction.show(outEditFragment);
			}
			break;
		default:
			break;
		}
		/*
		 * 直接使用commit()可能会出错：
		 * IllegalStateException: Can not perform this action after onSaveInstanceState：\
		 * 
		 * @see http://developer.android.com/reference/android/app/FragmentTransaction.html#commitAllowingStateLoss()
		 * 
		 * 大致意思是说我使用的 commit方法是在Activity的onSaveInstanceState()之后调用的，这样会出错，因为onSaveInstanceState
		 * 方法是在该Activity即将被销毁前调用，来保存Activity数据的，如果在保存玩状态后再给它添加Fragment就会出错。解决办法就
		 * 是把commit（）方法替换成 commitAllowingStateLoss()就行了，其效果是一样的。
		 */
		// transaction.commit(); 
		transaction.commitAllowingStateLoss();
	}
	
	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		btnIn.setBackgroundResource(R.drawable.swbtn_default);
		btnIn.setTextColor(getResources().getColor(R.color.tab_default_textcolor));
		btnOut.setBackgroundResource(R.drawable.swbtn_default);
		btnOut.setTextColor(getResources().getColor(R.color.tab_default_textcolor));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (inFragment != null) {
			transaction.hide(inFragment);
		}
		if (outFragment != null) {
			transaction.hide(outFragment);
		}
		if (inEditFragment != null) {
			transaction.hide(inEditFragment);
		}
		if (outEditFragment != null) {
			transaction.hide(outEditFragment);
		}
	}

	public void onShow(boolean show)
	{
		if (isShow == show) // 一直就在参数页，不走此逻辑
		{
			return;
		}

		if (isInState && !isEdit) // 入参展示
		{
			inFragment.onShow(show);
		}
		else if (!isInState && !isEdit) // 出参展示
		{
			outFragment.onShow(show);
		}
		else if (isInState && isEdit) // 入参编辑
		{
			inEditFragment.onShow(show);
		}
		else if (!isInState && isEdit) // 出参编辑
		{
			outEditFragment.onShow(show);
		}
		
		this.isShow = show;
	}
}
