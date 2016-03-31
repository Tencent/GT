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
package com.tencent.wstt.gt.plugin.octopus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.gt.plugin.BaseService;
import com.tencent.wstt.gt.plugin.PluginManager;
import com.tencent.wstt.gt.plugin.PluginTaskExecutor;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import mqq.sdet.gt.protocol.Code;
import mqq.sdet.gt.protocol.ErrorMsg;
import mqq.sdet.gt.protocol.Restrict;
import mqq.sdet.gt.protocol.SimpleValidBean;

/**
 * 处理八爪鱼事务的核心类，扩展自BaseService是因为上传过程需要后台服务处理
 *
 */
public class GTOctopusEngine extends BaseService implements PluginTaskExecutor {
	// 唯一实例
	private static GTOctopusEngine INSTANCE;

	// 观察者们，包括UI和自动化模块
	private List<OctopusPluginListener> listeners;

	private GTOctopusEngine()
	{
		listeners = new ArrayList<OctopusPluginListener>();
	}

	public static GTOctopusEngine getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTOctopusEngine();
		}
		return INSTANCE;
	}

	public synchronized void addListener(OctopusPluginListener listener)
	{
		if (! listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	public synchronized void removeListener(OctopusPluginListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void execute(Bundle bundle) {
		String cmd = bundle.getString("cmd");
		if (cmd != null && cmd.equals("start")) {
			String folderPath = bundle.getString(GTOctopusActivity.SRC);

			String productId = bundle.getString(GTOctopusActivity.PRODUCT_ID);
			String productName = bundle.getString(GTOctopusActivity.PRODUCT_NAME);
			String path1 = bundle.getString(GTOctopusActivity.PATH1);
			String path2 = bundle.getString(GTOctopusActivity.PATH2);
			String path3 = bundle.getString(GTOctopusActivity.PATH3);

			if (folderPath != null
					&& productName != null && path1 != null)
			{
				// 分析文件，准备上传，用Service处理
				Intent intent = new Intent();
				intent.putExtra(GTOctopusActivity.SRC, folderPath);
				intent.putExtra(GTOctopusActivity.PRODUCT_ID, productId);
				intent.putExtra(GTOctopusActivity.PRODUCT_NAME, productName);
				intent.putExtra(GTOctopusActivity.PATH1, path1);
				intent.putExtra(GTOctopusActivity.PATH2, path2);
				intent.putExtra(GTOctopusActivity.PATH3, path3);

				PluginManager.getInstance().getPluginControler().startService(this, intent);
			}
		}
	}

	@Override
	public void onCreate(Context context) {
		super.onCreate(context);
	}

	@Override
	public void onStart(Intent intent) {
		super.onStart(intent);
		if (null == intent)
		{
			return;
		}

		final String srcFolder = intent.getStringExtra(GTOctopusActivity.SRC);
		final String[] files = intent.getStringArrayExtra(GTOctopusActivity.FILE_ARRAY);

		final String productId = intent.getStringExtra(GTOctopusActivity.PRODUCT_ID);
		final String productName = intent.getStringExtra(GTOctopusActivity.PRODUCT_NAME);
		final String path1 = intent.getStringExtra(GTOctopusActivity.PATH1);
		final String path2 = intent.getStringExtra(GTOctopusActivity.PATH2);
		final String path3 = intent.getStringExtra(GTOctopusActivity.PATH3);
		final String uin = intent.getStringExtra(GTOctopusActivity.UIN);
		final String sk = intent.getStringExtra(GTOctopusActivity.S_KEY);
		final String psk = intent.getStringExtra(GTOctopusActivity.P_S_KEY);
		final String lsk = intent.getStringExtra(GTOctopusActivity.LS_KEY);
		
		if (null == srcFolder || null == uin || null == sk || null == psk || null == lsk)
		{
			return;
		}

		new Thread(new Runnable(){

			@Override
			public void run() {
				upload(srcFolder, files, productId, productName, path1, path2, path3, uin, sk, psk, lsk);
			}}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		INSTANCE = null;
	}

	@Override
	public IBinder onBind() {
		return null;
	}

	private void upload(String folderName
			, String[] filePaths
			, String productId
			, String productName
			, String path1
			, String path2
			, String path3
			, String uin
			, String sk
			, String psk
			, String lsk)
	{
		File folder = new File(folderName);
		if (folder.isDirectory() &&
				folder.listFiles() != null && folder.listFiles().length > 0)
		{
			for (OctopusPluginListener listener : listeners)
			{
				listener.onStartUpload(folderName);
			}
			
			File[] csvfiles = getFilesFromPaths(filePaths);
			File[] descfiles = folder.listFiles(FileUtil.DESC_FILTER);

			if (csvfiles.length > 0)
			{
				// 校验基本合法性
				SimpleValidBean valid = valid(descfiles, csvfiles, productId, productName, path1, path2, path3, uin);
				if (valid.getRetCode() != Code.OK)
				{
					for (OctopusPluginListener listener : listeners)
					{
						listener.onUploadFail(valid.getErrorMsg());
					}
					return;
				}
				
				int ret = HttpAssist.uploadFile(descfiles, csvfiles, productId, productName
						, path1, path2, path3, uin, sk, psk, lsk);
				if (ret == Code.OK)
				{
					for (OctopusPluginListener listener : listeners)
					{
						listener.onUploadSucess();
					}
				}
				else
				{
					// 走到这都是上传失败了
					for (OctopusPluginListener listener : listeners)
					{
						listener.onUploadFail(Code.getErrorMsg(ret));
					}
				}
			}
			else
			{
				for (OctopusPluginListener listener : listeners)
				{
					listener.onUploadFail((Code.getErrorMsg(Code.UPLOAD_FILE_NO_NEW_FILE)));
				}
			}
		}
	}

	private static File[] getFilesFromPaths(String[] paths)
	{
		List<File> fileList = new ArrayList<File>();
		for (String path : paths)
		{
			File f = new File(path);
			if (f.exists())
			{
				fileList.add(f);
			}
		}
		return fileList.toArray(new File[]{});
	}

	private static SimpleValidBean valid(File[] descFiles, File[] csvFiles, String... strs) {
		// TODO 1.描绘文件应该分开校验 2.应该依据描述文件或请求去重，描绘文件里不写更新列表，重组上传的文件
		SimpleValidBean ret = new SimpleValidBean();
		ret.setRetCode(Code.OK);

		for (String s : strs)
		{
			if (s == null)
			{
				ret.setRetCode(Code.NULL_PARAM);
				ret.setErrorMsg(ErrorMsg.NULL_PARAM);
				return ret;
			}
		}
		if (csvFiles == null)
		{
			ret.setRetCode(Code.NOT_VALID_LETTER);
			ret.setErrorMsg(ErrorMsg.NOT_VALID_LETTER);
			return ret;
		}
		for (String s : strs)
		{
			if (! StringUtil.isLetter(s))
			{
				ret.setRetCode(Code.NULL_PARAM);
				ret.setErrorMsg(ErrorMsg.NULL_PARAM);
				return ret;
			}
		}
		if (csvFiles.length > Restrict.MAX_UPLOAD_FILE_NUM)
		{
			ret.setRetCode(Code.UPLOAD_FILE_NUM_OVER);
			ret.setErrorMsg(ErrorMsg.UPLOAD_FILE_NUM_OVER);
			return ret;
		}
		for (File f : csvFiles)
		{
			if (f.length() > Restrict.UPLOAD_SINGLE_FILE_MAX_SIZE)
			{
				ret.setRetCode(Code.UPLOAD_FILE_SIZE_OVER);
				ret.setErrorMsg(ErrorMsg.UPLOAD_FILE_SIZE_OVER);
				return ret;
			}
		}
		
		return ret;
	}
}
