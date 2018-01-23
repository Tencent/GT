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

/**
 * TODO Default客户端出参的注册，应做好单位和性能初始化及多曲线等
 */
public class DefaultOutParaManager extends IOutParaManager {

	public DefaultOutParaManager(Client client) {
		super(client);
	}

	@Override
	public void register(OutPara para) {
		if (null != para && null != para.getKey() && !contains(para.getKey()))
		{
			para.setClient(client.getKey());
			if (para.getKey().equals("CPU")) {
				para.setDisplayProperty(OutPara.DISPLAY_NORMAL);
			}
			else {
				para.setDisplayProperty(OutPara.DISPLAY_DISABLE);
			}
			outParaMap.put(para.getKey(), para);
		}
	}

}
