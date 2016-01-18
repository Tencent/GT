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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.dao.GTPref;
import com.tencent.wstt.gt.utils.StringUtil;
import com.tencent.wstt.gt.views.CustomerConfirmDialog;
import com.tencent.wstt.gt.views.CustomerConfirmDialog.IDialogListener;

/**
 * 法律声明。
 */
public class Eula implements OnCancelListener {
	/**
	 * 法律条款文件名
	 */
	private static final String ASSET_EULA = "EULA";

	/**
	 * 弱引用SplashActivity
	 */
	private WeakReference<SplashActivity> mWelcomeAcrivityRef;

	public Eula(SplashActivity activity) {
		mWelcomeAcrivityRef = new WeakReference<SplashActivity>(activity);
	}

	/**
	 * 创建法律条款显示Dialog
	 */
	public Dialog buildEulaDialog(String eulaStr) {
		SplashActivity activity = mWelcomeAcrivityRef.get();
		if (activity == null || activity.isFinishing()) {
			return null;
		}
		
		String eula = StringUtil.isEmptyOrWhitespaceOnly(eulaStr)
				? Eula.readEula().toString():eulaStr;

		final CustomerConfirmDialog dialog = new CustomerConfirmDialog(activity,
				R.layout.legalterm_body);
		dialog.setTitle(R.string.eula_title);
		dialog.setCancelable(true);
		dialog.setPositiveButton(R.string.eula_accept);
		dialog.setNegativeButton(R.string.eula_refuse);
		dialog.setOnCancelListener(this);
		dialog.setViewVisibility(R.id.title, View.GONE);
		dialog.hideNegativeButton();
		dialog.showAgreementCheckBox();
		dialog.setViewContent(R.id.info, Html.fromHtml(eula));

		dialog.setListener(new IDialogListener() {

			@Override
			public void onSure() {
				if (dialog.isCheckBoxAgreementChecked())
				{
					accept();
				}
				else
				{
					refuse();
				}
			}

			@Override
			public void onCancel() {
				refuse();
			}
		});
		return dialog;
	}

	public void onCancel(DialogInterface dialog) {
		refuse();
	}

	/**
	 * 是否确认过了法律条款了。
	 * 
	 * @return <code>true</code>如果已接受，否则为<code>false</code>
	 */
	public static boolean isAccepted() {
		return GTPref.getGTPref().getBoolean(GTPref.PREFERENCE_EULA_ACCEPTED,
				false);
	}

	/**
	 * 接受。
	 */
	private void accept() {
		SplashActivity activity = mWelcomeAcrivityRef.get();
		if (activity == null || activity.isFinishing()) {
			return;
		}
		activity.eluaStr = null; // 用完及时置null
		GTPref.getGTPref().edit()
				.putBoolean(GTPref.PREFERENCE_EULA_ACCEPTED, true).commit();
		Intent intent = new Intent(activity, GTMainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 拒绝。
	 */
	private void refuse() {
		GTApp.exitGT();
	}

	/**
	 * 从文件中读取法律条款。
	 */
	public static CharSequence readEula() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(GTApp.getContext()
					.getAssets().open(ASSET_EULA)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null)
				buffer.append(line).append('\n');
			return buffer;
		} catch (IOException e) {
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 从网络读取法律条款。
	 */
	public static CharSequence readEula(URL url) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null)
				buffer.append(line).append('\n');
			return buffer;
		} catch (IOException e) {
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
