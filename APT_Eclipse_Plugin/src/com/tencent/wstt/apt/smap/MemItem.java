/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.smap;

import java.util.Arrays;

public class MemItem {
	public static final int NUM = 3;
	public static final int PSS = 0;
	public static final int SD = 1;
	public static final int PD = 2;
	
	public int[]vals = new int[NUM];
	
	public MemItem()
	{
		Arrays.fill(vals, 0);
	}
	
	public void setVal(int pss, int sd, int pd)
	{
		vals[PSS] = pss;
		vals[SD] = sd;
		vals[PD] = pd;
	}
}
