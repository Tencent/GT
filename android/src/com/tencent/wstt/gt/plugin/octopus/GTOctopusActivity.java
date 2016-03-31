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

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.plugin.PluginManager;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;
import com.tencent.wstt.gt.utils.StringUtil;
import com.tencent.wstt.gt.utils.ToastUtil;
import com.tencent.wstt.gt.utils.WtloginUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import mqq.sdet.gt.protocol.Code;
import mqq.sdet.gt.protocol.ErrorMsg;
import oicq.wlogin_sdk.request.Ticket;
import oicq.wlogin_sdk.request.WUserSigInfo;
import oicq.wlogin_sdk.request.WtloginHelper;
import oicq.wlogin_sdk.request.WtloginHelper.SigType;
import oicq.wlogin_sdk.request.WtloginListener;
import oicq.wlogin_sdk.tools.ErrMsg;
import oicq.wlogin_sdk.tools.util;

public class GTOctopusActivity extends GTBaseActivity
		implements OnClickListener, OctopusPluginListener {
	/*
	 * 转菊花Dialog
	 */
	private ProgressDialog proDialog;

	private Spinner spProductName;

	private static ArrayList<Pair<String, String>> productPairs = new ArrayList<Pair<String, String>>();
	private ArrayAdapter<Pair<String, String>> productAdapter;
	private String strNewProjName;
	private String strIntent;
	private Bundle newProjBundle = new Bundle();
	private Boolean isNewProj = false;
	
	static final String PRODUCT_ID = "upload_product_id";
	static final String PRODUCT_NAME = "upload_product_name";
	static final String PATH1 = "upload_product_version";
	static final String PATH2 = "upload_feature";
	static final String PATH3 = "upload_path3";

	static final String UIN = "qq_uin";
	static final String S_KEY = "qq_sk";
	static final String P_S_KEY = "qq_psk";
	static final String LS_KEY = "qq_lsk";

	static final String SRC = "srcFolder";
	static final String FILE_ARRAY = "file_array";

	/*
     * 各种请求的回调在这里实现，安全起见，GT不独立完成登录事务，只实现快速登录成功的回调
     * 需要用户在手Q中处理好登录再使用GT
     */
	WtloginListener wtloginListener = new WtloginListener() {
		@Override
		public void OnGetStWithPasswd(String userAccount, long dwSrcAppid,
				int dwMainSigMap, long dwSubDstAppid, String userPasswd,
				WUserSigInfo userSigInfo, int ret, ErrMsg errMsg) {
			switch (ret) {
			case util.S_GET_IMAGE: {

			}
				break;

			case util.S_GET_SMS: {

			}
				break;

			case util.S_SUCCESS: {
				loginSuccess(userAccount);
			}
				break;
		
			case util.S_BABYLH_EXPIRED:
			case util.S_LH_EXPIRED: {

			}
				break;

			default:
				WtloginUtil.setUin(null);
				// 取消菊花
				dismissProDialog();
				ToastUtil.ShowLongToast(GTOctopusActivity.this, errMsg.getTitle() + "，" + errMsg.getMessage());
				finish();
			}
		}
	};

	// 显示菊花
	private void showProDialog(String title, String message)
	{
		proDialog = ProgressDialog.show(this, title, message, true, true);
	}

	// 取消菊花
	private void dismissProDialog()
	{
		if (null != proDialog)
		{
			proDialog.dismiss();
			proDialog = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent newProjIntent = this.getIntent();
		
		if(newProjIntent != null && newProjIntent.getAction()=="New_Proj"){
		isNewProj = true;
		newProjBundle=newProjIntent.getExtras();  
		strNewProjName = newProjBundle.getString("name");
		strIntent = newProjBundle.getString("intent");
		}
		// 屏蔽进入界面默认唤出键盘在第一个EditText上
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.pi_octopus);

		WtloginUtil.getHelper().SetListener(wtloginListener);
		
		TextView back = (TextView)findViewById(R.id.back_gt);
		back.setOnClickListener(this);
		Button uploadBtn = (Button)findViewById(R.id.upload_btn);
		uploadBtn.setOnClickListener(this);

		spProductName = (Spinner)findViewById(R.id.sp_product_name);

		if (productPairs != null)
		{
			productPairs.clear();
		}
		productAdapter = new ArrayAdapter<Pair<String, String>>(
				this, android.R.layout.simple_spinner_item, productPairs);
		productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spProductName.setAdapter(productAdapter);

		GTOctopusEngine.getInstance().addListener(this);

		/*
         * @important! 快速登录入口
         * 完成wtlogin的接入之后，请到qqlogin.oa.com 申请快速登录权限
         * TODO 判断是否登录过，如未登录过，则进入快速登录；如登录过，则进入上传页
         */
		String lsk = WtloginUtil.getLsKey(WtloginUtil.getUin());
		if (lsk == null || lsk.isEmpty())
		{
			Intent intent = WtloginUtil.getIntent();

			boolean canQlogin = (intent!=null);
			if (!canQlogin) {
				ToastUtil.ShowLongToast(this, R.string.qq_need_install);
				finish();
				return;
			}

			try {
				GTOctopusActivity.this.startActivityForResult(intent, WtloginUtil.REQ_QLOGIN);
			} catch (Exception e) {
				ToastUtil.ShowLongToast(this, R.string.qq_quicklogin_error);
			}
			return;
		}

		// 转菊花，等待产品列表获取结果
		showProDialog(getString(R.string.qq_quicklogin_trying),
				getString(R.string.qq_quicklogin_trying_content));
		loginSuccess(WtloginUtil.getUin());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		GTOctopusEngine.getInstance().removeListener(this);
		WtloginUtil.getHelper().SetListener(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upload_btn:
			if ((null != WtloginUtil.getUin())&&(isNewProj == false))
			{
				String sk = WtloginUtil.getSKey(WtloginUtil.getUin());
				String psk = WtloginUtil.getPsKey(WtloginUtil.getUin());
				String lsk = WtloginUtil.getLsKey(WtloginUtil.getUin());
				dataPersisted();
				showUploadDialog(sk, psk, lsk);
			}else if(isNewProj==true){
				String sk = WtloginUtil.getSKey(WtloginUtil.getUin());
				String psk = WtloginUtil.getPsKey(WtloginUtil.getUin());
				String lsk = WtloginUtil.getLsKey(WtloginUtil.getUin());
				File file = new File(strNewProjName);
				comfirmUpdateSize(file,sk,psk,lsk);
			}
			else
			{
				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.qq_need_login);
			}
			break;
		case R.id.back_gt:
			finish();
			break;
		}
	}

	private void dataPersisted()
	{
//		GTPref.getGTPref().edit().putString(PRODUCT_NAME, productName = spProductName.getText().toString().trim()).commit();
//		GTPref.getGTPref().edit().putString(PRODUCT_VERSION, productVersion = etProductVersion.getText().toString().trim()).commit();
//		GTPref.getGTPref().edit().putString(FEATURE, feature = etFeature.getText().toString().trim()).commit();
	}

	private void showUploadDialog(final String skey, final String pskey, final String lskey)
	{
		if (! GTUtils.isSDCardExist()) {
			return;
		}

		final List<File> folders = new ArrayList<File>(
				SavedGWDataHelper.getGWFolders());

		if (folders.isEmpty()) {
			Toast.makeText(this, R.string.no_saved_logs, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		ArrayAdapter<File> dropdownAdapter = new GWFolderAdapter(this,
				folders, SavedGWDataHelper.getGWDirectory() , -1, false);

		Builder builder = new Builder(this);

		builder.setTitle(R.string.pi_octopus_upload_title)
			.setCancelable(true)
			.setSingleChoiceItems(dropdownAdapter,
					-1,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							
							File folder = folders.get(which);
							if (folder.getParentFile().getParentFile().equals(
									SavedGWDataHelper.getGWDirectory()))
							{
								return; // 点击上层目录行，啥也不做
							}
							dialog.dismiss();
							
							// 弹出新的对话框，提示用户本次上传的内容大小
							comfirmUpdateSize(folder, skey, pskey, lskey);
						}
					});

		builder.show();
	}

	private void comfirmUpdateSize(final File folder,
			final String skey, final String pskey, final String lskey)
	{
		// 从folder解析出三级目录
		String[] paths = folder.getPath().split(FileUtil.separator);
		if (paths == null || paths.length <= 3)
		{
			// TODO 非预期目录有问题，无法处理的error
			return;
		}
		final String path1 = paths[paths.length - 3];
		final String path2 = paths[paths.length - 2];
		final String path3 = paths[paths.length - 1];

		Object oProductPair = spProductName.getSelectedItem();
		if (oProductPair == null)
		{
			ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_upload_not_select);
			return;
		}
		if (!(oProductPair instanceof Pair))
		{
			ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_upload_not_select);
			return;
		}
		final Pair<String, String> productPair = (Pair) oProductPair;

		// 转菊花，等待产品列表获取结果
		showProDialog(getString(R.string.pi_octopus_calc_size),
				getString(R.string.pi_octopus_calc_size_content));
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				long size = 0;
				File[] csvFiles = folder.listFiles(FileUtil.CSV_FILTER);
				// 网络操作需要在独立线程完成
				PreUploadEntry preUploadEntry = HttpAssist.preUpload(csvFiles, productPair.first,
						path1, path2, path3 ,WtloginUtil.getUin() , skey, pskey, lskey);
				if (preUploadEntry == null)
				{
					ToastUtil.ShowLongToast(GTApp.getContext(),ErrorMsg.NET_ERROR);
					return;
				}

				final List<String> chosedFilePathList = new ArrayList<String>();
				for (File f : preUploadEntry.choicedCsvFileList)
				{
					size += f.length();
					chosedFilePathList.add(f.getPath());
				}
				final long sizeKB = size / 1024 + 1;

				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						dismissProDialog();

						AlertDialog.Builder builder = new Builder(GTOctopusActivity.this);
						builder.setMessage(getString(R.string.pi_octopus_upload_confirm_content) + sizeKB + "KB");
						builder.setTitle(getString(R.string.pi_octopus_upload_confirm_title));
						builder.setPositiveButton(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.setNegativeButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										dialog.dismiss();
										Object oProductPair = spProductName.getSelectedItem();
										if (oProductPair == null)
										{
											ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_upload_not_select);
											return;
										}
										if (!(oProductPair instanceof Pair))
										{
											ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_upload_not_select);
											return;
										}
										Pair<String, String> productPair = (Pair) oProductPair;

										// 从folder解析出三级目录
										String[] paths = folder.getPath().split(FileUtil.separator);
										if (paths == null || paths.length <= 3)
										{
											// TODO 报个error
											return;
										}
										// 分析文件，准备上传，用Service处理
										Intent intent = new Intent();
										intent.putExtra(SRC, folder.getPath());
										intent.putExtra(FILE_ARRAY, chosedFilePathList.toArray(new String[]{}));
										
										intent.putExtra(PRODUCT_ID, productPair.first);
										intent.putExtra(PRODUCT_NAME, productPair.second);
										intent.putExtra(PATH1, paths[paths.length - 3]);
										intent.putExtra(PATH2, paths[paths.length - 2]);
										intent.putExtra(PATH3, paths[paths.length - 1]);
										intent.putExtra(UIN, WtloginUtil.getUin());
										intent.putExtra(S_KEY, skey);
										intent.putExtra(P_S_KEY, pskey);
										intent.putExtra(LS_KEY, lskey);
										
										PluginManager.getInstance().getPluginControler(
												).startService(GTOctopusEngine.getInstance(), intent);
									}
								});
						builder.setCancelable(false);
						builder.show();
					}});
				
				
			}},"choicedCsvFilesThread").start();
	}
	
	@Override
	public void onStartUpload(final String folderName) {
		// 转菊花 需要runOnUiThread
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showProDialog("upload..", "upload  "+ folderName +"..wait...");
			}});
	}

	@Override
	public void onUploadSucess() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 取消菊花
				dismissProDialog();
				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_upload_sucess);
			}
		});
	}

	@Override
	public void onUploadFail(final String errorStr) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 取消菊花
				dismissProDialog();
				ToastUtil.ShowLongToast(GTApp.getContext(), errorStr);
			}
		});
	}

	/* 进行验证码验证，或者进行快速登录的回调，这两个是进行快速登录可能遇到的情况 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String userAccount = "";
		ErrMsg errMsg = null;
		WUserSigInfo userSigInfo = null;
		switch (requestCode) {
		case WtloginUtil.REQ_VCODE: {
			if (data == null)
				break;
			Bundle bundle = data.getExtras();
			if (bundle == null)
				break;
			userAccount = bundle.getString("ACCOUNT");
			errMsg = (ErrMsg) bundle.getParcelable("ERRMSG");
			userSigInfo = (WUserSigInfo) bundle.getParcelable("USERSIG");

			if (resultCode == util.S_SUCCESS) {
				// 转菊花，等待产品列表获取结果
				showProDialog(getString(R.string.pi_octopus_pull_products),
						getString(R.string.pi_octopus_pull_products_content));
				loginSuccess(userAccount);
			} else if (resultCode == util.S_LH_EXPIRED || resultCode == util.S_BABYLH_EXPIRED) {
				Ticket ticket = WtloginHelper.GetUserSigInfoTicket(userSigInfo, SigType.WLOGIN_LHSIG);
				finish();
				ToastUtil.ShowLongToast(GTApp.getContext(), "lhsig=" + util.buf_to_string(ticket._sig) + " errMsg=" + errMsg);
			} else {
				finish();
				ToastUtil.ShowLongToast(GTApp.getContext(), "errMsg=" + errMsg);
			}
		}
			break;
		case WtloginUtil.REQ_QLOGIN: // 快速登录返回
			try {
				if (data == null) { // 这种情况下用户多半是直接按了返回按钮，没有进行快速登录；快速登录失败可提醒用户输入密码登录
					break;
				}
				
				WUserSigInfo sigInfo = WtloginUtil.getSigInfo(data);
				if (sigInfo == null) {
					ToastUtil.ShowLongToast(GTApp.getContext(),
							R.string.pi_octopus_login_user_quickerror);
					break;
				}
				
				WtloginUtil.setUin(sigInfo.uin);

				// 转菊花，等待产品列表获取结果
				showProDialog(getString(R.string.pi_octopus_pull_products),
						getString(R.string.pi_octopus_pull_products_content));

				// 快速登录只是从手Q换取了A1票据，A1则相当于用户密码，在此仍需要再发起一次A1换票的流程，才能拿到目标票据
				WtloginUtil.getStWithPasswd(sigInfo);
				// 换票据成功后会触发wtloginListener的监听
			} catch (Exception e) {
				util.printException(e);
			}
			break;
		default:
			break;
		}
	}

	public void loginSuccess(String userAccount) {
		// 用uin和lskey拉回产品ID和产品列表,并更新产品下拉列表
		new Thread()
		{
			@Override
			public void run()
			{
				final String lsk = WtloginUtil.getLsKey(WtloginUtil.getUin());
				final int retCode = HttpAssist.prepareProductPairs(
						WtloginUtil.getUin(), lsk, productPairs);

				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						// 取消菊花
						dismissProDialog();

						switch(retCode)
						{
						case Code.OK:
							ArrayAdapter<Pair<String, String>> productAdapter = new ArrayAdapter<Pair<String, String>>(
									GTOctopusActivity.this, android.R.layout.simple_spinner_item, productPairs);
							productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spProductName.setAdapter(productAdapter);
							break;
						case Code.UPLOAD_FILE_EMPTY_PRODUCT_LIST:
							// 跳到提示页或弹出提示框，提示用户去bugly注册产品
							comfirmToProductRegistPage(lsk);
							break;
						default:
							finish();
							ToastUtil.ShowLongToast(
									GTApp.getContext(), Code.getErrorMsg(retCode));
							break;
					}
				}});
			}
		}.start();
	}

	
	/*
	 * 确认是否去Bugly页面注册产品
	 */
	private void comfirmToProductRegistPage(final String lskey)
	{
		View rl_save = LayoutInflater.from(this).inflate(
				R.layout.pi_octopus_dailog_regist_product, null, false);
		final EditText et_project_editor = (EditText)rl_save.findViewById(R.id.project_editor);

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getString(R.string.pi_octopus_upload_regist_product_title))
			.setView(rl_save)
			.setPositiveButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
						GTOctopusActivity.this.finish();
					}
				})
			.setNegativeButton(getString(R.string.pi_octopus_upload_regist_product_OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						if(strIntent == "newproj"){
							dialog.dismiss();
							isNewProj = true;
							showProDialog(getString(R.string.pi_octopus_reg_bugly),
									getString(R.string.pi_octopus_reg_bugly_content));
							// 申请appId后将appId到UI上，结束菊花
							waitForApplyAppId(strNewProjName);
						}else{
							// 应对文本进行合法性校验
							String appName = et_project_editor.getText().toString().trim();
							if (! StringUtil.isLetter(appName) || appName.isEmpty())
							{
								ToastUtil.ShowLongToast(GTOctopusActivity.this, getString(R.string.save_folder_valid));
								GTOctopusActivity.this.finish();
								return;
							}
							dialog.dismiss();

							// 弹菊花等待拉取appId的网络操作完成
							showProDialog(getString(R.string.pi_octopus_reg_bugly),
									getString(R.string.pi_octopus_reg_bugly_content));
	
							// 申请appId后将appId到UI上，结束菊花
							waitForApplyAppId(appName);
						}
					}
				})
			.setCancelable(false)
			.show();
	}

	/*
	 * 申请appId后将唯一更新appId到UI上，结束菊花
	 * @param 要申请的app名字
	 */
	private void waitForApplyAppId(final String name)
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				String appId = null;
				try {
					appId = HttpAssist.registProduct(
							WtloginUtil.getUin(), WtloginUtil.getLsKey(WtloginUtil.getUin()), name);
				} catch (Exception e) {
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							ToastUtil.ShowLongToast(GTOctopusActivity.this, R.string.pi_octopus_reg_bugly_error);
							// 取消菊花
							dismissProDialog();
						}});
					return;
				}
				final String appIdFinal = appId;
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						if (appIdFinal == null)
						{
							dismissProDialog();
							ToastUtil.ShowLongToast(GTOctopusActivity.this, R.string.pi_octopus_reg_bugly_error);
							return;
						}

						// 正常获取appId的情况，更新UI
						productPairs.clear();
						productPairs.add(new ProductPair<String, String>(appIdFinal, name));
						ArrayAdapter<Pair<String, String>> productAdapter = new ArrayAdapter<Pair<String, String>>(
								GTOctopusActivity.this, android.R.layout.simple_spinner_item, productPairs);
						productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spProductName.setAdapter(productAdapter);
	
						// 取消菊花
						dismissProDialog();
					}});
			}}, "ApplyAppIdThread").start();
	}
}
