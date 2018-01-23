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

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtil {
    public static boolean isEmptyOrWhitespaceOnly(String str) {
    	if (TextUtils.isEmpty(str)) {
    		return true;
    	}
    	for (int i = 0; i < str.length(); i++) {
    		if (!Character.isWhitespace(str.charAt(i))) {
    			return false;
    		}
    	}
    	return true;
    }

	public static String assertNotNullString(String str) {
		return str == null ? "" : str;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[(pos + 1)]));
		}
		return result;
	}

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);

		for (int i = 0; i < bArray.length; i++) {
			String sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase(Locale.US));
		}
		return sb.toString();
	}

	public static byte[] getBytes(String data) {
		try {
			return data.getBytes("iso-8859-1");
		} catch (UnsupportedEncodingException e) {
		}
		return new byte[0];
	}

	/**
	 * 判断是否是数字，支持负数
	 * @param s
	 * @return
	 */
	public static boolean isNumeric(String s) {
		if (s == null || s.length() == 0)
		{
			return false;
		}
		int numStartPos = 0;
		if (s.charAt(0) == '-')
		{
			numStartPos = 1;
		}
		
		for (int i = s.length(); --i >= numStartPos;) {
			int chr = s.charAt(i);
			if ((chr < 48 || chr > 57) && chr != '.')
				return false;
		}
		return true;
	}

	/**
	 * 判断是否有特殊字符
	 * @param s
	 * @return 如没有特殊字符，返回true
	 */
	public static boolean isLetter(String s) {
		Pattern p = Pattern.compile("[^?!@#$%\\^&*(),<>;:]+");
		Matcher m = p.matcher(s);
		return m.matches();
	}
}