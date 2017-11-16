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
package com.tencent.wstt.gt.manager;

import android.widget.CheckBox;
import android.widget.TextView;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;

public class ParamConst {

	public static final String PROMPT_TITLE = GTApp.getContext().getString(R.string.para_air_title);
	public static final String PROMPT_INIT_TITLE = GTApp.getContext().getString(R.string.para_air_title)
	+ "\n                                  empty";
	public static final String DIVID_TITLE = GTApp.getContext().getString(R.string.para_default_title);
	public static final String PROMPT_DISABLE_TITLE = GTApp.getContext().getString(R.string.para_disable_title);

	public static class ViewHolderPrompt {
		public TextView tv_draglist_title;
	}
	
	public static class ViewHolderDivide {
		public TextView tv_draglist_title;
		public TextView tv_listrow_top_border;
	}

	public static class ViewHolderDisable {
		public TextView tv_draglist_title;
		public TextView tv_listrow_top_border;

	}

	public static class ViewHolderDrag {
		public TextView tv_key;
		public TextView tv_alias;
		public TextView tv_value;
		public TextView tv_listview_bottom_border;
	}

	public static class ViewHolderDrag_nopic {
		public TextView tv_key;
		public TextView tv_alias;
		public TextView tv_value;
		public TextView tv_listview_bottom_border;
		public TextView tv_his_data;
		public TextView tv_listrowbg;
		public CheckBox cb;
		public boolean checked = false;
		public boolean alert = false;

		public boolean isAlert() {
			return alert;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}
	}
}
