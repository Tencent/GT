/**
 * Tencent is pleased to support the open source community by making APT available.
 * Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */


package com.tencent.wstt.apt.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
* @Description 用于项APTConsole控制台中输出当前的APT运行信息 
* @date 2013年11月10日 下午5:51:39 
*
 */
public class APTConsoleFactory implements IConsoleFactory {
	private static APTConsoleFactory instance = null;
	private MessageConsoleStream mcs = null;

	private static final String APT_CONSOLE_NAME = "APTConsole";

	private APTConsoleFactory() {
	}

	public static APTConsoleFactory getInstance() {
		if (instance == null) {
			instance = new APTConsoleFactory();
		}
		return instance;
	}

	@Override
	public void openConsole() {
		if (mcs == null) {
			mcs = getMessageConsoleStream();
		}
	}

	public void APTPrint(String name) {
		if (mcs != null) {
			mcs.getConsole().activate();
			mcs.println(name);
		} else {
			mcs = getMessageConsoleStream();
			mcs.getConsole().activate();
			mcs.println(name);
		}
	}

	private MessageConsoleStream getMessageConsoleStream() {
		MessageConsoleStream mcsObj;
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();

		MessageConsole aptConsole = new MessageConsole(APT_CONSOLE_NAME, null);
		manager.addConsoles(new IConsole[] { aptConsole });
		manager.showConsoleView(aptConsole);
		mcsObj = aptConsole.newMessageStream();
		mcsObj.setActivateOnWrite(true);

		return mcsObj;
	}

}
