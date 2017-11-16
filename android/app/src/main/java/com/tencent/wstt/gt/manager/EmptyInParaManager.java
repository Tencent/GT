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

import java.util.Collections;
import java.util.List;

import com.tencent.wstt.gt.InPara;

public class EmptyInParaManager implements IInParaManager {
	
	private static EmptyInParaManager INSTANCE = new EmptyInParaManager();
	
	private EmptyInParaManager()
	{
		
	}
	
	public static EmptyInParaManager getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void register(String paraName, String alias, String defaultValue,
			String... optionalValues) {

	}

	@Override
	public void register(InPara para) {

	}

	@Override
	public void removeOutPara(String paraName) {

	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public List<InPara> getAll() {
		return Collections.emptyList();
	}

	@Override
	public InPara getInPara(int positon) {
		return null;
	}

	@Override
	public InPara getInPara(String paraName) {
		return null;
	}

	@Override
	public String getInPara(String paraName, String origVal) {
		return null;
	}

	@Override
	public boolean getInPara(String paraName, boolean origVal) {
		return false;
	}

	@Override
	public int getInPara(String paraName, int origVal) {
		return -1;
	}

	@Override
	public float getInPara(String paraName, float origVal) {
		return -1;
	}

	@Override
	public double getInPara(String paraName, double origVal) {
		return -1;
	}

	@Override
	public short getInPara(String paraName, short origVal) {
		return -1;
	}

	@Override
	public byte getInPara(String paraName, byte origVal) {
		return -1;
	}

	@Override
	public long getInPara(String paraName, long origVal) {
		return -1;
	}

}
