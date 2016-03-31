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

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;

public class ErrorMsg {
	public static final String OK = "OK";
	
	public static final String SERVICE_ERROR = GTApp.getContext().getString(R.string.pi_octopus_upload_err_service_error);
	public static final String NULL_PARAM = GTApp.getContext().getString(R.string.pi_octopus_upload_err_null_param);
	public static final String NULL_RESULT = GTApp.getContext().getString(R.string.pi_octopus_upload_null_result);

	public static final String UPLOAD_FILE_NUM_OVER = GTApp.getContext().getString(R.string.pi_octopus_upload_file_num_over);
	public static final String UPLOAD_FILE_SIZE_OVER = GTApp.getContext().getString(R.string.pi_octopus_upload_file_size_over);
	public static final String UPLOAD_PRODUCT_MAX_SIZE = GTApp.getContext().getString(R.string.pi_octopus_upload_product_max_size);
	public static final String UPLOAD_FILE_NUM_NOT_SAMEAS_ATTR_NUM = GTApp.getContext().getString(R.string.pi_octopus_upload_file_num_not_sameas_attr_num);
	public static final String UPLOAD_TIMES_ON_PRODUCT_OVER = GTApp.getContext().getString(R.string.pi_octopus_upload_times_on_product_over);
	public static final String UPLOAD_FILE_NO_NEW_FILE = GTApp.getContext().getString(R.string.pi_octopus_upload_file_no_new_file);
	public static final String UPLOAD_FILE_EMPTY_PRODUCT_LIST = GTApp.getContext().getString(R.string.pi_octopus_product_zero_product);

	public static final String NOT_VALID_LETTER = GTApp.getContext().getString(R.string.pi_octopus_upload_not_valid_letter);

	public static final String NET_ERROR = GTApp.getContext().getString(R.string.pi_octopus_upload_network_error);
	public static final String SQL_ERROR = GTApp.getContext().getString(R.string.pi_octopus_upload_sql_error);
	public static final String UNKNOW_ERROR = GTApp.getContext().getString(R.string.pi_octopus_upload_unknow_error);
}
