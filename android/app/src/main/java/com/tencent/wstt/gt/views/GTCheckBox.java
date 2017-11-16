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
/**
 * 重新实现了自己的CheckBox，可以文字排左，方框排右了。
 * 其实就是重写了CompoundButton的onDraw()方法。
 */
package com.tencent.wstt.gt.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.DeviceUtils;

public class GTCheckBox extends CompoundButton {
	private Drawable mButtonDrawable;
	private Paint p = new Paint();
	private Bitmap bmpSlider = null;

	public GTCheckBox(Context context) {
		this(context, null);
	}

	public GTCheckBox(Context context, AttributeSet attrs) {
		this(context, attrs, 0x0101006c);
	}

	public GTCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 目前该方法有个未解决的问题：由于首行的super.onDraw(canvas);调用，导致buttonDrawable会画两次。
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final Drawable buttonDrawable = mButtonDrawable;
		if (buttonDrawable != null) {
			final int verticalGravity = getGravity()
					& Gravity.VERTICAL_GRAVITY_MASK;
			final int height = buttonDrawable.getIntrinsicHeight();

			int y = 0;

			switch (verticalGravity) {
			case Gravity.BOTTOM:
				y = getHeight() - height;
				break;
			case Gravity.CENTER_VERTICAL:
				y = (getHeight() - height) / 2;
				break;
			}

			buttonDrawable.setBounds(
					getWidth() - buttonDrawable.getIntrinsicWidth(), y,
					getWidth(), y + height);
			buttonDrawable.draw(canvas);

			if (bmpSlider == null) {
				// bmpSlider = BitmapFactory.decodeResource(getResources(),
				// R.drawable.switch_slider);
				bmpSlider = BitmapFactory.decodeResource(getResources(),
						R.drawable.btn_slip_1);
			}

			int width = DeviceUtils.getDevWidth();
			int textSize = 20;
			if (width >= 720) {
				textSize = 28;
			} else if (width <= 480) {
				textSize = 15;
			}

			if (isChecked()) {
				p.setColor(0xFFFFFFFF);
				float mDensity = getResources().getDisplayMetrics().density;
				p.setTextSize(textSize);
				p.setAntiAlias(true);
				p.setTextAlign(Align.CENTER);
				// p.setAlpha(50);
				// p.setShadowLayer(1, 1, 1, 0xFFFFFFFF);
				canvas.drawText("On",
						getWidth() - buttonDrawable.getIntrinsicWidth() * 2 / 3
								- 2 * mDensity, y + height / 2 + 4 * mDensity,
						p);
				canvas.drawBitmap(bmpSlider, getWidth() - bmpSlider.getWidth(),
						y, null);
			} else {
				p.setColor(0xFF798089);
				float mDensity = getResources().getDisplayMetrics().density;
				p.setTextSize(textSize);
				p.setAntiAlias(true);
				// p.setAlpha(50);
				// p.setShadowLayer(1, 1, 1, 0xFF798089);
				p.setTextAlign(Align.CENTER);
				canvas.drawText("Off",
						getWidth() - buttonDrawable.getIntrinsicWidth() / 3 + 2
								* mDensity, y + height / 2 + 4 * mDensity, p);
				canvas.drawBitmap(bmpSlider,
						getWidth() - buttonDrawable.getIntrinsicWidth(), y,
						null);
			}
		}
	}

	/**
	 * 设置按钮的背景
	 */
	@Override
	public void setButtonDrawable(Drawable drawable) {
		if (drawable != null) {
			if (mButtonDrawable != null) {
				mButtonDrawable.setCallback(null);
				unscheduleDrawable(mButtonDrawable);
			}
			drawable.setCallback(this);
			drawable.setState(getDrawableState());
			drawable.setVisible(getVisibility() == VISIBLE, false);
			mButtonDrawable = drawable;
			mButtonDrawable.setState(null);
			setMinHeight(mButtonDrawable.getIntrinsicHeight());
		}
		refreshDrawableState();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (mButtonDrawable != null) {
			int[] myDrawableState = getDrawableState();

			// Set the state of the Drawable
			mButtonDrawable.setState(myDrawableState);

			invalidate();
		}
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return super.verifyDrawable(who) || who == mButtonDrawable;
	}

	@Override
	public int getCompoundPaddingLeft() {
		return 0;
	}

	public void setButtonGray(boolean isEnabled) {
		if (isEnabled) {
			mButtonDrawable.setAlpha(255);
		} else {
			mButtonDrawable.setAlpha(128);
		}
	}
}
