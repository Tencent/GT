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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 当TextView显示的SpannableString恰好要被换行符截断的时候，会报异常（目前只在4.1和4.1.1上出现）
 * 重写TextView 在onMeasure()中捕获异常
 */
public class PatchedTextView extends TextView {
	public PatchedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public PatchedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public PatchedTextView(Context context) {
		super(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}catch (ArrayIndexOutOfBoundsException e){
			setText(getText().toString());
			super.onMeasure(widthMeasureSpec, heightMeasureSpec); 
		}
	}	
	@Override
	public void setGravity(int gravity){
		try{
			super.setGravity(gravity);
		}catch (ArrayIndexOutOfBoundsException e){
			setText(getText().toString());
			super.setGravity(gravity); 
		}
	}
	@Override
	public void setText(CharSequence text, BufferType type) {
		try{
			super.setText(text, type);
		}catch (ArrayIndexOutOfBoundsException e){
			setText(text.toString());
		}
	}
}
