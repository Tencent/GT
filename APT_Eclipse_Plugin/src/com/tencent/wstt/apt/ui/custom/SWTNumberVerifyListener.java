/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.ui.custom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class SWTNumberVerifyListener implements VerifyListener {

	@Override
	public void verifyText(VerifyEvent e) {
		Pattern pattern = Pattern.compile("[0-9]\\d*");
		Matcher matcher = pattern.matcher(e.text);
		if (matcher.matches()) 
		{
			e.doit = true;
		} 
		else if (e.text.length() > 0) 
		{
			e.doit = false;
		} 
		else 
		{
			e.doit = true;
		}
	}
}
