/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.util;


import java.net.NetworkInterface;
import java.util.Enumeration;


import com.tencent.wstt.apt.Activator;


/**
* @Description 获取当前PC的MAC地址之类的环境信息 
* @date 2013年11月10日 下午6:20:26 
*
 */
public class GetEnviromentInfoUtil {

	private static final String MAC_ADR_NEED_TO_REMOVE = "00000000000000e0";
	/**
	 * 获取当前插件版本
	* @Title: getPluginVersion  
	* @Description:   
	* @return 
	* String 
	* @throws
	 */
	public static String getPluginVersion()
	{
		return Activator.getDefault().getBundle().getVersion().toString();
	}

	/**
	 * 获取当前登录的用户名
	* @Title: getUserName  
	* @Description:   
	* @return 
	* String 
	* @throws
	 */
	public static String getUserName()
	{
		return System.getProperty("user.name");
	}
	
	
	/**
	 * 获取当前用户所使用的系统名称
	* @Title: getOSName  
	* @Description:   
	* @return 
	* String 
	* @throws
	 */
	public static String getOSName()
	{
		return System.getProperty("os.name");
	}
	
	
	/**
	 * 获取MAC地址，如果有多个网卡，返回多个网卡MAC地址的连接
	* @Title: getMac  
	* @Description:   
	* @return 
	* String 
	* @throws
	 */
	public static String getMac() {
		StringBuilder builder = new StringBuilder();
		try {
			Enumeration<NetworkInterface> el = NetworkInterface
					.getNetworkInterfaces();
			while (el.hasMoreElements()) {
				byte[] mac = el.nextElement().getHardwareAddress();
				StringBuilder subBuilder = new StringBuilder();
				if (mac == null)
					continue;
				for (byte b : mac) {
					subBuilder.append(hexByte(b));
				}
				String subMacAdr = subBuilder.toString();
				if(subMacAdr.equalsIgnoreCase(MAC_ADR_NEED_TO_REMOVE))
				{
					continue;
				}
				builder.append(subMacAdr);
			}
			
			if(builder.length() <= 0)
			{
				return "-1";
			}else if(builder.length()>=128)
			{
				return builder.toString().substring(0, 128);
			}
			else
			{
				System.out.println(builder.toString());
				return builder.toString();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return "-1";
		}
	}
	
	
	private static String hexByte(byte b)
	{
		return String.format("%02x", b);
	}
}
