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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class ShowhtmlActivity extends GTBaseActivity
{
	WebView webview;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_showhtml);
		Intent intent = getIntent();
		String action = intent.getStringExtra("uri");
		String uri = Env.GT_HOMEPAGE;
		if (action != null && ! action.equals(""))
		{
			uri = action;
		}

		String cookies = intent.getStringExtra("cookies");

		webview = (WebView) findViewById(R.id.showhtml);
		if (Build.VERSION.SDK_INT >= 11)
		{
			// WebView安全性修改
			try {
				Method m = webview.getClass().getMethod("removeJavascriptInterface", String.class);
				m.invoke(webview, "searchBoxJavaBridge_");
				m.invoke(webview, "accessibility");
				m.invoke(webview, "accessibilityTraversal");
			} catch (NoSuchMethodException e) {
				// 说明是Andorid2.3以下，do nothing
			} catch (IllegalArgumentException e) {

			} catch (IllegalAccessException e) {

			} catch (InvocationTargetException e) {
				
			}
		}

		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.getSettings().setAppCacheEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setDatabaseEnabled(true);

		if (cookies != null)
		{
			CookieSyncManager.createInstance(this);
			CookieManager cookieManager = CookieManager.getInstance();
//			String oldCookie = cookieManager.getCookie(uri);
//			ToastUtil.ShowLongToast(this, oldCookie);
			cookieManager.setAcceptCookie(true);
			cookieManager.removeSessionCookie();// 移除
			
			String[] cookieArray = cookies.split(";");
			for (String cookie : cookieArray)
			{
				if (!cookie.trim().isEmpty())
				{
					cookieManager.setCookie(uri, cookie);//cookies是在HttpClient中获得的cookie
				}
			}
			CookieSyncManager.getInstance().sync();
		}

		try {
			webview.loadUrl(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}