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
package com.tencent.wstt.gt.utils;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import com.tencent.wstt.gt.GTApp;

/**
 * bitmap工具类
 * 	createPurgeableBitmap方法会消耗java内存，但可以通过options参数设置屏幕自适应参数等
 *  createOriginalBitmap方法不使用options参数从而不会消耗java内存，但照片不会自动缩放
 */
public class BitmapUtil {

	public static Bitmap createPurgeableBitmap(InputStream is) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(is, null, getPurgeableBitmapOptions());
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return bm;
	}

	public static Bitmap createPurgeableBitmap(Resources res, int resId) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeResource(res, resId, getPurgeableBitmapOptions());
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return bm;
	}

	public static Bitmap createPurgeableBitmap(String resPath) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeFile(resPath, getPurgeableBitmapOptions());
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return bm;
	}

	public static Bitmap createBitmap(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap b = null;
		try {
			b = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return b;
	}
	
	public static Bitmap createPurgeableBitmap(byte[] data, int offset, int length) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeByteArray(data, offset, length, getPurgeableBitmapOptions());
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return bm;
	}

	private static BitmapFactory.Options getPurgeableBitmapOptions() {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Config.RGB_565;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inTargetDensity = GTApp.getContext().getResources().getDisplayMetrics().densityDpi;
		return opts;
	}
	
	public static Bitmap createOriginalBitmap(InputStream is) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(is, null, null);
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return bm;
	}
	
	public static Bitmap createOriginalBitmap(String filePath) {
		Bitmap b = null;
		try {
			b = BitmapFactory.decodeFile(filePath, null);
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return b;
	}

	public static Bitmap createJustBoundsBitmap(Resources res, int resId) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeResource(res, resId, getJustBoundsOptions());
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return bm;
	}

	private static BitmapFactory.Options getJustBoundsOptions() {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		return opts;
	}

}
