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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;

public class GTAboutActivity extends GTBaseActivity implements View.OnClickListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gt_aboutactivity);

		TextView tv_about_back = (TextView) findViewById(R.id.about_back);
		tv_about_back.setOnClickListener(this);

		TextView tv_homepage = (TextView) findViewById(R.id.homepage);
		tv_homepage.setOnClickListener(this);

		TextView tv_about = (TextView) findViewById(R.id.about_gt);
		tv_about.setText("GT " + GTConfig.VERSION + "(Android)");

		TextView tv_terms = (TextView) findViewById(R.id.policy);
		tv_terms.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.about_back:
			finish();
			break;
		case R.id.homepage:
			Intent intentHp = new Intent(GTAboutActivity.this,
					ShowhtmlActivity.class);
			intentHp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentHp.putExtra("uri", Env.GT_HOMEPAGE);
			startActivity(intentHp);
			break;
		case R.id.policy:
			Intent intentPolicy = new Intent(GTAboutActivity.this,
					ShowhtmlActivity.class);
			intentPolicy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentPolicy.putExtra("uri", Env.GT_POLICY);
			startActivity(intentPolicy);
			break;
		default:
		}
	}
}
