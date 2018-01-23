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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用Client的调用入口
 */
public class ClientManager {
	// 全局单例Client创建时的类型区分
	public static final String AUT_CLIENT = "_AUT__";
	public static final String DEFAULT_CLIENT = "_DEFAULT__";
	public static final String GLOBAL_CLIENT = "_GLOBAL__";
	public static final String EMPTY_CLIENT = "_EMPTY__";
	public static ClientManager INSTANCE = new ClientManager();
	
	// Client的字典容器，以client名称为key的字典
	private Map<String, Client> clientMapS = new HashMap<String, Client>();
	// Client的字典容器，以client名称的hash值或UID为key的字典
	private Map<Integer, Client> clientMapI = new HashMap<Integer, Client>();
	// 通过Key查intKey的字典
	private Map<String, Integer> keyMap = new HashMap<String, Integer>();

	// 三个常用的Client，便于UI区分使用
	Client autClient;
	Client defaultClient;
	Client globalClient;
	Client emptyClient;
	
	private ClientManager()
	{
		emptyClient = EmptyClient.getInstance();
	}
	
	public static ClientManager getInstance()
	{
		return INSTANCE;
	}
	
	public Client getClient(String key)
	{
		return clientMapS.get(key);
	}

	public Client getClient(int intKey)
	{
		return clientMapI.get(intKey);
	}

	public int getClientKey(String key)
	{
		return keyMap.get(key);
	}

	public Client getAUTClient()
	{
		return autClient;
	}
	
	public void setAUTClient(Client client)
	{
		this.autClient = client;
	}
	
	public Client getDefaultClient()
	{
		return defaultClient;
	}
	
	public void setDefaultClient(Client client)
	{
		this.defaultClient = client;
	}
	
	public Client getGlobalClient()
	{
		return globalClient;
	}
	
	public void setGlobalClient(Client client)
	{
		this.globalClient = client;
	}
	
	public Client getEmptyClient()
	{
		return emptyClient;
	}
	
	synchronized public void addClient(String key, int intKey, Client client)
	{
		if (! clientMapS.containsKey(key))
		{
			clientMapS.put(key, client);
			clientMapI.put(intKey, client);
			keyMap.put(key, intKey);
		}
	}
	
	synchronized public void removeClient(String key)
	{
		clientMapS.remove(key);
		if (keyMap.get(key) == null)
		{
			return;
		}
		Client client = clientMapI.remove(keyMap.get(key));
		client.clear();
	} 
	
	synchronized public Collection<Client> getAllClient()
	{
		return clientMapS.values();
	}
}
