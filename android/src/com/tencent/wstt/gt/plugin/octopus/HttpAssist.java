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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;
import com.tencent.wstt.gt.utils.RootUtil;
import com.tencent.wstt.gt.utils.StringUtil;
import com.tencent.wstt.gt.utils.ToastUtil;

import android.util.Pair;
import mqq.sdet.gt.protocol.Attr;
import mqq.sdet.gt.protocol.Code;
import mqq.sdet.gt.protocol.ErrorMsg;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class HttpAssist {
	private static final int TIME_OUT = 10000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码

	// 注册产品，直接返回产品id
	public static String registProduct(String uin, String p_lskey, String name) throws Exception
	{
		if (uin == null || p_lskey == null || name == null)
		{
			throw new Exception("null参数!");
		}

		String encodeName = URLEncoder.encode(name, CHARSET);
		String RequestURL = "http://gt.qq.com/GTAppServer/user/regproduct" + "?name=" + encodeName;
		String CONTENT_TYPE = "application/json"; // 内容类型 text/plain
		BufferedReader in = null;
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
//				conn.setDoOutput(true); // 允许输出流
//				conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("GET"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//				conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);
//				conn.setRequestProperty("Cookie", "uin=o0" + uin + ";"
			conn.setRequestProperty("Cookie", " p_luin=o0" + uin + ";" + " p_lskey=" + p_lskey + ";");

			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int resCode = conn.getResponseCode();
			if (resCode != 200)
			{
				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_product_network_error);
				throw new Exception("未知网络错误!");
			}
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = null;
			StringBuilder sb = new StringBuilder();
			
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}

			JSONObject retj = new JSONObject(sb.toString());
			String ret = retj.getString("appId");
			if (ret == null || ret.isEmpty())
			{
				throw new Exception("注册产品失败!");
			}
			return ret;
		}
		finally
		{
			FileUtil.closeReader(in);
		}
	}

	public static int prepareProductPairs(String uin, String p_lskey, List<Pair<String, String>> pairs)
	{
		String RequestURL = "http://gt.qq.com/GTAppServer/user/products";
		String CONTENT_TYPE = "application/json"; // 内容类型 text/plain
		BufferedReader in = null;
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
//			conn.setDoOutput(true); // 允许输出流
//			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("GET"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);
//			conn.setRequestProperty("Cookie", "uin=o0" + uin + ";"
			conn.setRequestProperty("Cookie", " p_luin=o0" + uin + ";" + " p_lskey=" + p_lskey + ";");
			
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int resCode = conn.getResponseCode();
			if (resCode != 200)
			{
//				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_product_network_error);
				return resCode + Code.SERVICE_ERROR;
			}
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = null;
			StringBuilder sb = new StringBuilder();
			
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}

			pairs.clear();

			JSONObject retj = new JSONObject(sb.toString());
			JSONArray appIds = retj.getJSONArray("appIds");
			JSONArray appNames = retj.getJSONArray("appNames");
			for (int i = 0; i < appIds.length(); i++)
			{
				Pair<String, String> pair = new ProductPair<String, String>(appIds.getString(i), appNames.getString(i));
				pairs.add(pair);
			}

			if (pairs.isEmpty())
			{
//				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_product_zero_product);
				return Code.UPLOAD_FILE_EMPTY_PRODUCT_LIST;
			}
			else
			{
//				ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_login_user_success);
				return Code.OK;
			}
		}
		catch (Exception e)
		{
//			ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_product_get_error);
			return Code.NET_ERROR;
		}
		finally
		{
			FileUtil.closeReader(in);
		}
	}

	public static int uploadFile(File[]  descfiles, File[] csvFiles,
			String productId, String product,
			String path1, String path2, String path3,
			String uin, String sk, String psk, String lsk) {
		return doUpload(descfiles, csvFiles, productId, product, path1, path2, path3 ,uin , sk, psk, lsk);
	}

	/*
	 * 正式上传前的预处理
	 * @return 返回的是云端没有本地有的文件列表，这些文件是可以上传的
	 */
	public static PreUploadEntry preUpload(File[] csvFiles, String productId
			, String path1, String path2, String path3
			, String uin, String sk, String psk, String lsk)
	{
		StringBuilder requestUrlBuilder = new StringBuilder();
		try {
			requestUrlBuilder.append("http://gt.qq.com/GTAppServer/user/preupload?product=");
			requestUrlBuilder.append(productId);
			requestUrlBuilder.append("&path1=");
			requestUrlBuilder.append(URLEncoder.encode(path1, CHARSET));
			requestUrlBuilder.append("&path2=");
			requestUrlBuilder.append(URLEncoder.encode(path2, CHARSET));
			requestUrlBuilder.append("&path3=");
			requestUrlBuilder.append(URLEncoder.encode(path3, CHARSET));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}

		String CONTENT_TYPE = "application/json"; // 内容类型 text/plain
		BufferedReader in = null;
		try {
			URL url = new URL(requestUrlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
//			conn.setDoOutput(true); // 允许输出流
//			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("GET"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);
//			conn.setRequestProperty("Cookie", "uin=o0" + uin + ";"
			conn.setRequestProperty("Cookie", " p_luin=o0" + uin + ";" + " p_lskey=" + lsk + ";");
			
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int resCode = conn.getResponseCode();
			if (resCode != 200)
			{
				ToastUtil.ShowLongToast(GTApp.getContext(), ErrorMsg.UNKNOW_ERROR);
				return null;
			}
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = null;
			StringBuilder sb = new StringBuilder();
			
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			
			PreUploadEntry result = new PreUploadEntry();
			JSONObject retj = new JSONObject(sb.toString());
			JSONArray hasExistFileList = null;

			int freeSize = retj.getInt("freeSize"); // 产品剩余容量
			result.freeSize = freeSize;

			List<File> listFile = new ArrayList(Arrays.asList(csvFiles));
			if (retj.has("retList"))
			{
				hasExistFileList = retj.getJSONArray("retList");
			}
			else
			{
				result.choicedCsvFileList = listFile;
				return result;
			}

			// 遍历云端返回的目录下已存在的文件名，如果listFile
			for (int i = 0; i < hasExistFileList.length(); i++)
			{
				String filename = hasExistFileList.getString(i);
				for (int j = listFile.size() - 1; j >= 0; j--)
				for (File file : listFile)
				{
					if (file.getName().equals(filename))
					{
						listFile.remove(j);
						break;
					}
				}
			}
			result.choicedCsvFileList.addAll(listFile);

			return result;
		}
		catch (Exception e)
		{
			ToastUtil.ShowLongToast(GTApp.getContext(), R.string.pi_octopus_product_get_error);
			return null;
		}
		finally
		{
			FileUtil.closeReader(in);
		}
	}

	private static int doUpload(File[]  descfiles, File[] csvFiles
			, String productId, String product
			, String path1, String path2, String path3
			, String uin, String sk, String psk, String lsk)
	{
		if (csvFiles == null || csvFiles.length == 0)
		{
			return Code.UPLOAD_FILE_NO_NEW_FILE;
		}

		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型 text/plain
														// multipart/form-data
//		String RequestURL = "http://localhost:8080/GTAppServer/gt/upload";
		String RequestURL = "http://gt.qq.com/GTAppServer/user/upload";
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			conn.setRequestProperty("Cookie"," uin=o0" + uin + ";" + " p_luin=o0" + uin + ";" + " p_lskey=" + lsk + ";");
			
			OutputStream outputSteam = conn.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputSteam);
			StringBuffer sb = new StringBuffer();

			// productId
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"productId\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(productId);
			sb.append(LINE_END);

			// product
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"product\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(product);
			sb.append(LINE_END);

			// path1
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"path1\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(path1);
			sb.append(LINE_END);

			// path2
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"path2\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(path2);
			sb.append(LINE_END);

			// path3
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"path3\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(path3);
			sb.append(LINE_END);

			// date
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"date\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(GTUtils.getSaveDateMs());
			sb.append(LINE_END);

			// uin
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"uin\"");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(uin);
			sb.append(LINE_END);

			// 基本信息解析，处理成Json
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"attrs\"");
			sb.append(LINE_END);
			sb.append(LINE_END);

			JSONObject json_data = new JSONObject();
			JSONObject env_attr = new JSONObject();
			List json_list = new ArrayList();

			// 1.1 读手机信息
			try {
				env_attr.put("android_ver", android.os.Build.VERSION.SDK_INT);
				env_attr.put("phone_model", DeviceUtils.getDevModel());
				env_attr.put("rom", android.os.Build.DISPLAY);
				env_attr.put("root", RootUtil.rootJustNow);
				json_data.put("env_attr", env_attr);
			} catch (JSONException e1) {
				// do nothing, continue
			}

			for (File file : csvFiles)
			{
				if (file != null) {
					

					// Json信息体
					/*
					 * 到这都是CSV文件了:Ps0_com.tencent.wstt.gtdemo_20150710072353.csv
					 * 
					 * key,Ps0:com.tencent.wstt.gtdemo
					 * alias,PSS0
					 * unit,KB
					 * begin date,2015/7/10
					 * end date,2015/7/10
					 * count,76
					 * 
					 * total,dalvik,native
					 * min,18863,1027,1887
					 * max,18863,1027,1887
					 * avg,18863,1027,1887
					 * 
					 * 22:27.0,18863,1027,1887
					 * ....
					 * 
					 * 解析数据，分析各类型数据，返回不同的JSON串
					 * 1.解析文件名，判定性能数据关注点类型。普通型，累积型（不需展示平均值），SM型（比普通型多出卡顿区间、流畅区间、分数项），其他型（类似SM型，用户自定义，暂无）
					 * 2.解析文件，判断数据变换类型
					 *     a.是否需要单位变换。如单位字段是*1000 mA或/1000 mA这样的，说明数据要乘以1000或除以1000后才匹配空格后的单位（默认性能指标暂无）
					 *     b.是否数据变化更新（默认间隔更新）。先标准判断（有状态行updateMode,1），再默认型判断（流量、电量、温度）
					 *     c.默认比较模式（读取状态行compareMode,0），但主要靠用户UI选择
					 *     d.关注点类型（靠文件名解析不了的，读状态行concernMode,0,1,2,3）
					 *     e.是否数据带单位，即数据行不是纯数字，如CPU，也支持单位变换的单位(在属性段以min,max上的单位为准)
					 *     f.是否多曲线
					 * 9.根据综合类型，确定展示内容，展示曲线抽稀方案，以后迁移数据库方便
					 */
					// 1
					JSONObject attrMap = new JSONObject();
					JSONObject file_attr = new JSONObject();
					JSONObject base_attr = new JSONObject();
					JSONObject statistics_attr = new JSONObject();
					PerfDataType type = new PerfDataType();
					String name = file.getName();
					type.parseDefaultConcernMode(name);

					// 2
					BufferedReader br = null;
					try {
						br = new BufferedReader(new FileReader(file));
						String buffer = null;
						boolean ret = true;
						// 读状态段，以读到空行为标准
						while((buffer = br.readLine()) != null && !buffer.trim().equals("")){
							ret = visitStateLine(buffer, type, base_attr);
							if (!ret) break;
						}
						if (!ret) break;
						file_attr.put("basic_attr", base_attr);

						// 读属性段，以读到空行为标准
						while((buffer = br.readLine()) != null && !buffer.trim().equals("")){
							ret = visitAttrLine(buffer, type, statistics_attr);
							if (!ret) break;
						}
						if (!ret) break;
						file_attr.put("statistics_attr", statistics_attr);
						attrMap.put("file_attr", file_attr);
						attrMap.put("file_name", name);
						json_list.add(attrMap);
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally
					{
						FileUtil.closeReader(br);
					}
				}
			}
			try {
				json_data.put("attr_list", json_list);
				String json = json_data.toString();
//				GTLog.logD("GT_Test", json);

				// 进行UTF-8编码，方便中文
				String encodeJson = URLEncoder.encode(json, CHARSET);
				sb.append(encodeJson);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			for (File file : descfiles)
			{
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				StringBuffer sbFileHeader = new StringBuffer();
				sbFileHeader.append(PREFIX);
				sbFileHeader.append(BOUNDARY);
				sbFileHeader.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sbFileHeader.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\""
						+ LINE_END);
				sbFileHeader.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sbFileHeader.append(LINE_END);
				dos.write(sbFileHeader.toString().getBytes());
				writeFile(file, dos);
				dos.write(LINE_END.getBytes());
			}

			for (File file : csvFiles) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				StringBuffer sbFileHeader = new StringBuffer();
				sbFileHeader.append(PREFIX);
				sbFileHeader.append(BOUNDARY);
				sbFileHeader.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sbFileHeader.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\""
						+ LINE_END);
				sbFileHeader.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sbFileHeader.append(LINE_END);
				dos.write(sbFileHeader.toString().getBytes());
				writeFile(file, dos);
				dos.write(LINE_END.getBytes());
			}
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			if (res == HttpURLConnection.HTTP_OK) {
				String contentType = conn.getHeaderField("Content-Type");
				String charset = null;
				for (String param : contentType.replace(" ", "").split(";")) {
					if (param.startsWith("charset=")) {
						charset = param.split("=", 2)[1];
						break;
					}
				}
				if (charset == null) charset = CHARSET;

				InputStream in = conn.getInputStream();
				BufferedReader reader = null;
				StringBuilder retData = new StringBuilder();
				try {
					reader = new BufferedReader(new InputStreamReader(in, charset));
					for (String line; (line = reader.readLine()) != null;) {
						retData.append(line);
					}
					JSONObject retj = new JSONObject(retData.toString());
					int retcode = retj.getInt(Attr.CODE);
					return retcode;
				} 
				catch (Exception e)
				{
					return Code.SERVICE_ERROR;
				}
				finally
				{
					FileUtil.closeReader(reader);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Code.UNKNOW_ERROR;
	}
	
	private static void writeFile(File file, DataOutputStream dos)
	{
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = is.read(bytes)) != -1) {
				dos.write(bytes, 0, len);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileUtil.closeInputStream(is);
		}
	}

	private static boolean visitStateLine(String line, PerfDataType type, JSONObject map4Json) throws JSONException
	{
		line = line.trim();
		String[] seqs = line.split(",");
		if (seqs.length < 2) return true; // 元素值是空字符串的行，如大部分unit,直接跳过即可
		if (seqs[0].equals("key")
				|| seqs[0].equals("alias")
				|| seqs[0].equals("begin date")
				|| seqs[0].equals("end date")
				|| seqs[0].equals("count"))
		{
			map4Json.put(seqs[0], seqs[1]);
		}
		if (seqs[0].equals("key"))
		{
			type.key = seqs[1];
		}
		else if (seqs[0].equals("alias"))
		{
			type.alias = seqs[1];
		}

		// 文件中该属性一定要在unit属性前面行，才会生效
		else if (seqs[0].equals("need unit change"))
		{
			type.needUnitChange = Integer.parseInt(seqs[0]) == 0 ? false : true;
		}
		else if (seqs[0].equals("unit"))
		{
			if (seqs.length > 1)
			{
				// 判断单位变换，先去掉单位两边的括号
				String realUnit = seqs[1].substring(1, seqs[1].length() - 1);
				if (type.needUnitChange
						&& realUnit.startsWith("*") || realUnit.startsWith(FileUtil.separator))
				{
					String[] temp = realUnit.split(" ");
					String unitAfterChanged = temp[1];
					map4Json.put(seqs[0], unitAfterChanged);
					if (temp[0].startsWith("*"))
					{
						type.unitChangeType = 0;
						type.unitChangeCarry = Integer.parseInt(temp[0].substring(1));
					}
					else if (temp[0].startsWith(FileUtil.separator))
					{
						type.unitChangeType = 1;
						type.unitChangeCarry = Integer.parseInt(temp[0].substring(1));
					}
				}
				else
				{
					map4Json.put(seqs[0], realUnit);
				}
				type.unitLengthBeforeChange = realUnit.length();
			}
		}
		else if (seqs[0].equals("updateMode"))
		{
			type.updateMode = Integer.parseInt(seqs[0]);
			
		}
		else if (seqs[0].equals("compareMode"))
		{
			type.compareMode = Integer.parseInt(seqs[0]);
		}
		else if (seqs[0].equals("concernMode"))
		{
			type.concernMode = Integer.parseInt(seqs[0]);
		}
		else if (seqs[0].equals("multiMode"))
		{
			type.multiMode = Integer.parseInt(seqs[0]);
		}
		return true;
	}

	private static boolean visitAttrLine(String line, PerfDataType type, JSONObject map4Json) throws JSONException
	{
		line = line.trim();
		String[] seqs = line.split(",");
		if (seqs.length < 2) return false;

		// 累加型不需要均值
		if (type.concernMode == 1 && seqs[0].equals("avg"))
		{
			return true;
		}

		// 如果是Multi型，第一行第一列是空白串，如,total,dalvik,native
		// type.isMulti < 2代表复合Multi型预留，暂不处理
		if (type.multiMode < 2 && seqs[0].equals(""))
		{
			type.multiMode = 1;

			JSONArray attrList = new JSONArray();
			for (int i = 1; i < seqs.length; i++)
			{
				type.multiNameList.add(seqs[i]);
				attrList.put(seqs[i]);
			}
			map4Json.put("data_title", attrList);
		}
		else if (seqs.length == 2) // 段数为2，说明是一维数据，也加上默认的data_title
		{
			JSONArray attrList = new JSONArray();
			attrList.put(type.alias);
			map4Json.put("data_title", attrList);
		}
		
		if (seqs[0].equals("min")
				|| seqs[0].equals("max")
				|| seqs[0].equals("avg"))
		{
			JSONArray attrList = new JSONArray();
			if (type.multiMode == 0)
			{
				String data = seqs[1];
				if (! StringUtil.isNumeric(seqs[1]))
				{
					if (type.unitLengthBeforeChange <= 0)
					{
						return false;
					}
					data = data.substring(0, data.length() - type.unitLengthBeforeChange);
					type.isDataWithUnit = true;
				}
				attrList.put(data);
				map4Json.put(seqs[0], attrList);
			}
			else if (type.multiMode == 1)
			{
				for (int i = 1; i < seqs.length; i++)
				{
					String data = seqs[i];
					if (! StringUtil.isNumeric(seqs[i]))
					{
						if (type.unitLengthBeforeChange <= 0)
						{
							return false;
						}
						data = data.substring(0, data.length() - type.unitLengthBeforeChange);
						type.isDataWithUnit = true;
					}
					attrList.put(data);
				}
				map4Json.put(seqs[0], attrList);
			}
		}
		else
		{
			JSONArray attrList = new JSONArray();
			if (type.multiMode == 0)
			{
				String data = seqs[1];
				if (! StringUtil.isNumeric(seqs[1]))
				{
					if (type.unitLengthBeforeChange <= 0)
					{
						// 未知的不可解属性，直接跳过这行
						return true;
					}
					data = data.substring(0, data.length() - type.unitLengthBeforeChange);
				}
				attrList.put(data);
				map4Json.put(seqs[0], attrList);
			}
			else if (type.multiMode == 1)
			{
				for (int i = 1; i < seqs.length; i++)
				{
					String data = seqs[i];
					if (! StringUtil.isNumeric(seqs[i]))
					{
						if (type.unitLengthBeforeChange <= 0)
						{
							// 未知的不可解属性，直接跳过这行
							return true;
						}
						data = data.substring(0, data.length() - type.unitLengthBeforeChange);
						type.isDataWithUnit = true;
					}
					attrList.put(data);
				}
				map4Json.put(seqs[0], attrList);
			}
		}
		return true;
	}

	static class PerfDataType
	{
		int concernMode = 0; // 关注模式，0代表普通型，1代表累加型，2代表SM型，3之后待分配
		int compareMode = 0; // 默认比较模式，待定义
		int updateMode = 0; // 数据更新模式，0代表间隔更新，1代表变化更新
		boolean needUnitChange = false; // 是否需要单位变换
		int unitChangeType = 0; // 单位变换类型，0代表*，1代表/，2代表<<，3代表>>
		int unitChangeCarry = 1; // 单位变换量
		int unitLengthBeforeChange = 0; // 变换前的单位长度
		boolean isDataWithUnit = false; // 是否数据带单位
		int multiMode = 0; // 是否多曲线，0代表单曲线，1代表同y轴多x轴曲线，2代表多y轴多x轴曲线(预留)
		List<String> multiNameList = new ArrayList<String>();
		String key = "";
		String alias = "";

		void parseDefaultConcernMode(String name)
		{
			// 关注模式
			if (name.startsWith("SM:"))
			{
				this.concernMode = 2;
			}
			else if (name.startsWith("Pnet:")
					|| name.equals("NET")
					|| name.startsWith("Pjif"))
			{
				this.concernMode = 1;
			}
			this.concernMode = 0;

			// 数据更新模式
			if (name.startsWith("Pnet:")
					|| name.equals("NET")
					|| name.equals("Power")
					|| name.equals("Temperature"))
			{
				this.updateMode = 1;
			}
		}
	}
}
