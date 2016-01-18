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
package test.tencent.wstt.gt.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tencent.client.R;
import com.tencent.wstt.gt.client.AbsGTParaLoader;
import com.tencent.wstt.gt.client.GT;
import com.tencent.wstt.gt.client.GTAutoTesterForApp;
import com.tencent.wstt.gt.client.InParaManager;
import com.tencent.wstt.gt.client.OutParaManager;

public class ClientMain extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btn_open = (Button) findViewById(R.id.open);
		btn_open.setOnClickListener(open);
		Button btn_close = (Button) findViewById(R.id.close);
		btn_close.setOnClickListener(close);

		Button btn_debug = (Button) findViewById(R.id.debug_log);
		btn_debug.setOnClickListener(debug);

		Button btn_debugclose = (Button) findViewById(R.id.debug_close);
		btn_debugclose.setOnClickListener(debugClose);
	}

	private OnClickListener open = new OnClickListener() {
		public void onClick(View v) {

			GT.connect(getApplicationContext(), new AbsGTParaLoader() {

				@Override
				public void loadInParas(InParaManager inPara) {
					// 定义入参：变量名、入参值及备选值

				}

				@Override
				public void loadOutParas(OutParaManager outPara) {
					// 定义出参：变量名、缩写名

				}
			});
			
		}
	};

	private OnClickListener close = new OnClickListener() {
		public void onClick(View v) {
			GT.disconnect(getApplicationContext());
			finish();
			System.exit(0);
		}
	};

	private OnClickListener debug = new OnClickListener() {
		public void onClick(View v) {
			GTAutoTesterForApp.startTest("com.tencent.wstt.gt");
			GTAutoTesterForApp.startSample("cpu");
			GTAutoTesterForApp.startSample("pss");
			GTAutoTesterForApp.startSample("net");
		}
	};

	private OnClickListener debugClose = new OnClickListener() {
		public void onClick(View v) {
			GTAutoTesterForApp.endTestAndClear("testxxx");
		}
	};

}