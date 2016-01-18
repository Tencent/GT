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
package com.tencent.wstt.gt.client.internal.connect;

import com.tencent.wstt.gt.data.control.DataCacheController;

public class DisconnectingState extends AbsConnState {

private DataCacheController dataCacheController;
	
	public DisconnectingState(DataCacheController dataCacheController)
	{
		this.dataCacheController = dataCacheController;
	}

	@Override
	public void init(IConnState lastState) {
		// 这时不作为了
		super.init(lastState);
	}
	
	@Override
	public void finish() {

		/*
		 * 根据下个状态决定是否大清理，有可能是Connected/NotConnected/NotInstalled
		 * 但这样太复杂，还是放在上个状态的finish自行清理了
		 * 
		 * 唯一需要防住的是connecting直接到disconnecting的情况，就是下面这行
		 * 因为后面的状态是NotConnected/NotInstalled时也会清理，只是防守后个状态是Connected
		 */
		dataCacheController.dispose();
	}
}
