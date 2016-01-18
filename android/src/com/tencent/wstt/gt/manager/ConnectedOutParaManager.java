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

import com.tencent.wstt.gt.OutPara;

public class ConnectedOutParaManager extends IOutParaManager {

	public ConnectedOutParaManager(Client client) {
		super(client);
	}

	@Override
	public void register(OutPara para) {
		/*
		 * 只有当悬浮框出参为空时，才会更新悬浮框，这样有已关注参数时，新来的AC参数不会打扰用户
		 * 此时后来的参数应该主动设置为非AC状态，否则在将AC参数都拖下去后，后来的AC参数会立即
		 * 增补到悬浮框上，而参数列表不同步，会比较怪异
		 */
		if (null != para && null != para.getKey() && !contains(para.getKey()))
		{
			para.setClient(client.getKey());
			outParaMap.put(para.getKey(), para);

			// 悬浮窗需要立即反应，所以如果是AC参数立即更新UI列表
			OpUIManager.addItemToAC(para);
		}
	}

}
