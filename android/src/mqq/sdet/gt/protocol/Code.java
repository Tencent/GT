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
package mqq.sdet.gt.protocol;

import android.util.SparseArray;

public class Code {

	public static final int OK = 0;
	public static final int SERVICE_ERROR = 100;
	public static final int NULL_PARAM = 101;
	public static final int NULL_RESULT = 102;

	public static final int UPLOAD_FILE_NUM_OVER = 1001;
	public static final int UPLOAD_FILE_SIZE_OVER = 1002;
	public static final int UPLOAD_PRODUCT_MAX_SIZE = 1003;
	public static final int UPLOAD_FILE_NUM_NOT_SAMEAS_ATTR_NUM = 1004;
	public static final int UPLOAD_TIMES_ON_PRODUCT_OVER = 1105;
	public static final int UPLOAD_FILE_NO_NEW_FILE = 1106;
	public static final int UPLOAD_FILE_EMPTY_PRODUCT_LIST = 1107;

	public static final int NOT_VALID_LETTER = 1201;

	public static final int NET_ERROR = 9997;
	public static final int SQL_ERROR = 9998;
	public static final int UNKNOW_ERROR = 9999;

	
	private static final SparseArray<String> map = new SparseArray<String>();

	static
	{
		map.put(OK, ErrorMsg.OK);
		map.put(SERVICE_ERROR, ErrorMsg.SERVICE_ERROR);
		map.put(NULL_PARAM, ErrorMsg.NULL_PARAM);
		map.put(NULL_RESULT, ErrorMsg.NULL_RESULT);
		
		map.put(UPLOAD_FILE_NUM_OVER, ErrorMsg.UPLOAD_FILE_NUM_OVER);
		map.put(UPLOAD_FILE_SIZE_OVER, ErrorMsg.UPLOAD_FILE_SIZE_OVER);
		map.put(UPLOAD_PRODUCT_MAX_SIZE, ErrorMsg.UPLOAD_PRODUCT_MAX_SIZE);
		map.put(UPLOAD_FILE_NUM_NOT_SAMEAS_ATTR_NUM, ErrorMsg.UPLOAD_FILE_NUM_NOT_SAMEAS_ATTR_NUM);
		map.put(UPLOAD_TIMES_ON_PRODUCT_OVER, ErrorMsg.UPLOAD_TIMES_ON_PRODUCT_OVER);
		map.put(UPLOAD_FILE_NO_NEW_FILE, ErrorMsg.UPLOAD_FILE_NO_NEW_FILE);
		map.put(UPLOAD_FILE_EMPTY_PRODUCT_LIST, ErrorMsg.UPLOAD_FILE_EMPTY_PRODUCT_LIST);
		

		map.put(NOT_VALID_LETTER, ErrorMsg.NOT_VALID_LETTER);
		
		map.put(NET_ERROR, ErrorMsg.NET_ERROR);
		map.put(SQL_ERROR, ErrorMsg.SQL_ERROR);
		map.put(UNKNOW_ERROR, ErrorMsg.UNKNOW_ERROR);
	}

	public static String getErrorMsg(int code)
	{
		String ret = map.get(code);
		if (ret == null)
		{
			ret = map.get(UNKNOW_ERROR);
		}
		return ret;
	}
}
