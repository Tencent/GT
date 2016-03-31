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
package com.tencent.wstt.gt.plugin.octopus;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class GWFolderAdapter extends ArrayAdapter<File> {
	private List<File> objects;
	private File root; // /GT/GW
	private int checked;
	private boolean multiMode;
	private boolean[] checkedItems;
	private int resId;

	public GWFolderAdapter(Context context, List<File> objects, File root, int checked, boolean multiMode) {
		super(context, -1, objects);
		this.objects = objects;
		this.root = root;
		this.checked = checked;
		this.multiMode = multiMode;
		if (multiMode) {
			checkedItems = new boolean[objects.size()];
		}
		resId = multiMode? R.layout.pi_checkbox_dropdown_filename : R.layout.pi_spinner_dropdown_filename;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		Context context = parent.getContext();
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resId, parent, false);
		}
		
		CheckedTextView text1 = (CheckedTextView) view.findViewById(android.R.id.text1);
		TextView text2 = (TextView) view.findViewById(android.R.id.text2);
		
		File folder = objects.get(position);
		if (folder.getParentFile().getParentFile().equals(this.root))
		{
			text1.setText(folder.getParentFile().getName() + FileUtil.separator + folder.getName());
			text2.setVisibility(View.GONE);
			view.setBackgroundColor(Color.BLUE);
			view.setTouchDelegate(null);
		}
		else
		{
			text1.setText("  " + folder.getName());
			text2.setVisibility(View.VISIBLE);
			view.setBackgroundColor(Color.GRAY);
		}

		if (multiMode) {
			text1.setChecked(checkedItems[position]);
		} else {
			text1.setChecked(checked == position);
		}
		
		Date lastModified = null;
		if (folder.exists()) {
			lastModified = new Date(folder.lastModified());
		} else {
			lastModified = new Date();
		}
		text2.setText("  " + GTUtils.getGpsSaveTime(lastModified));
		
		return view;
	}
}
