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
package com.tencent.wstt.gt.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.utils.CommonString;
import com.tencent.wstt.gt.utils.FileUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

public class NetUtils {
	public static Map<String, NetUtils> netInfoMap = new HashMap<String, NetUtils>();
	private static final int TYPE_WIFI = 0;
	private static final int TYPE_3G = 1;
	private static final int TYPE_GPRS = 2;
	private static final float B2K = 1024.00f;

	// 采集应用流量的方案
	private static final int TYPE_CASE1 = 1;
	private static final int TYPE_CASE2 = 2;
	private static final int TYPE_CASE3 = 3;
	private static int netCase = TYPE_CASE1;

	/**
	 * 获取整体的网络接收流量，包括wifi和Mobile
	 * 
	 * @return 总字节数
	 */
	public static long getNetRxTotalBytes() {
		long total = TrafficStats.getTotalRxBytes();
		return total;
	}

	/**
	 * 获取整体的网络输出流量，包括wifi和Mobile
	 * 
	 * @return 总字节数
	 */
	public static long getNetTxTotalBytes() {
		long total = TrafficStats.getTotalTxBytes();
		return total;
	}

	public static long getNetTxMobileBytes() {
		long total = TrafficStats.getMobileTxBytes();
		return total;
	}

	public static long getNetRxMobileBytes() {
		long total = TrafficStats.getMobileRxBytes();
		return total;
	}

	public static long getNetTxWifiBytes() {
		long total = getNetTxTotalBytes() - getNetTxMobileBytes();
		return total;
	}

	public static long getNetRxWifiBytes() {
		long total = getNetRxTotalBytes() - getNetRxMobileBytes();
		return total;
	}

	/**
	 * 获取整体的网络接收流量，包括wifi和Mobile
	 * 
	 * @return 总数据包数
	 */
	public static long getNetRxTotalPackets() {
		long total = TrafficStats.getTotalRxPackets();
		return total;
	}

	/**
	 * 获取整体的网络输出流量，包括wifi和Mobile
	 * 
	 * @return 总数据包数
	 */
	public static long getNetTxTotalPackets() {
		long total = TrafficStats.getTotalRxPackets();
		return total;
	}

	/**
	 * 根据进程id获取网络发送流量
	 * 
	 * @return 字节数
	 */
	public static long getOutOctets(String pName) {
		int uid = ProcessUtils.getProcessUID(pName);
		String netPath = "/proc/uid_stat/" + uid + "/tcp_snd";

		switch (netCase)
		{
		case TYPE_CASE1:
			File f = new File(netPath);
			if (!f.exists()) {
				// 转方案2
				netCase = TYPE_CASE2;
			}
			else
			{
				String ret = "0";
				try {
					FileReader fr = new FileReader(netPath);
					BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
					ret = localBufferedReader.readLine();
					FileUtil.closeReader(localBufferedReader);
					return Long.parseLong(ret);
				} catch (Exception e) {
					netCase = TYPE_CASE2;
				}

				// 最后一个尝试
				if ((ret == null || ret.equals("0"))
						&& (TrafficStats.getUidTxBytes(uid) > 0 || TrafficStats.getUidRxBytes(uid) > 0))
				{
					netCase = TYPE_CASE2;
				}
			}

			// 如果方案1判断不支持，不需要break直接跳方案2
//			break;

		case TYPE_CASE2:
			long s = TrafficStats.getUidTxBytes(uid);
			if (s >= 0)
			{
				return s;
			}
			netCase = TYPE_CASE3;
			
		case TYPE_CASE3:
		default:
			break;
		}
		return 0;
	}

	/**
	 * 根据进程id获取网络接收流量
	 * 
	 * @return 字节数
	 */
	public static long getInOctets(String pName) {
		int uid = ProcessUtils.getProcessUID(pName);
		String netPath = "/proc/uid_stat/" + uid + "/tcp_rcv";

		switch (netCase)
		{
		case TYPE_CASE1:
			File f = new File(netPath);
			if (!f.exists()) {
				// 转方案2
				netCase = TYPE_CASE2;
			}
			else
			{
				String ret = "0";
				try {
					FileReader fr = new FileReader(netPath);
					BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
					ret = localBufferedReader.readLine();
					FileUtil.closeReader(localBufferedReader);
					return Long.parseLong(ret);
				} catch (Exception e) {
					netCase = TYPE_CASE2;
				}

				// 最后一个尝试
				if ((ret == null || ret.equals("0"))
						&& (TrafficStats.getUidTxBytes(uid) > 0 || TrafficStats.getUidRxBytes(uid) > 0))
				{
					netCase = TYPE_CASE2;
				}
			}

			// 如果方案1判断不支持，不需要break直接跳方案2
//			break;

		case TYPE_CASE2:
			long r = TrafficStats.getUidRxBytes(uid);
			if (r >= 0)
			{
				return r;
			}
			netCase = TYPE_CASE3;
			
		case TYPE_CASE3:
		default:
			break;
		}
		return 0;
	}

	/**
	 * 根据UID获取网络接收流量
	 * 
	 * @param uid
	 * @return
	 */
	public static long getUidRBytes(int uid) {
		long rBytes = TrafficStats.getUidRxBytes(uid);
		return rBytes;
	}

	/**
	 * 根据UID获取网络传送流量
	 * 
	 * @param uid
	 * @return
	 */
	public static long getUidTBytes(int uid) {
		long tBytes = TrafficStats.getUidTxBytes(uid);
		return tBytes;
	}

	/**
	 * 获取网络连接类型
	 * 
	 * @return -1表示没有网络
	 */
	public static final int getNetWorkType() {
		Context c = GTApp.getContext();
		ConnectivityManager conn = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conn == null) {
			return -1;
		}
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return -1;
		}
		int type = info.getType();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return TYPE_WIFI;
		} else {
			TelephonyManager tm = (TelephonyManager) c
					.getSystemService(Context.TELEPHONY_SERVICE);
			switch (tm.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return TYPE_GPRS;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return TYPE_GPRS;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return TYPE_GPRS;
			default:
				return TYPE_3G;
			}
		}
	}

	private static long t_base_wifi = 0;
	private static long t_base_3G = 0;
	private static long t_base_2G = 0;
	private static long r_base_wifi = 0;
	private static long r_base_3G = 0;
	private static long r_base_2G = 0;

	private static double t_add_wifi = 0;
	private static double t_add_3G = 0;
	private static double t_add_2G = 0;
	private static double r_add_wifi = 0;
	private static double r_add_3G = 0;
	private static double r_add_2G = 0;

	public static double getT_add_wifi() {
		return t_add_wifi;
	}

	public static double getT_add_3G() {
		return t_add_3G;
	}

	public static double getT_add_2G() {
		return t_add_2G;
	}

	public static double getR_add_wifi() {
		return r_add_wifi;
	}

	public static double getR_add_3G() {
		return r_add_3G;
	}

	public static double getR_add_2G() {
		return r_add_2G;
	}

	public static void initNetValue() {
		t_base_wifi = getNetTxWifiBytes();
		t_base_3G = t_base_2G = getNetTxMobileBytes();
		r_base_wifi = getNetRxWifiBytes();
		r_base_3G = r_base_2G = getNetRxMobileBytes();

		t_add_wifi = 0;
		t_add_3G = 0;
		t_add_2G = 0;
		r_add_wifi = 0;
		r_add_3G = 0;
		r_add_2G = 0;
	}

	private static long t_cur_wifi = 0;
	private static long t_cur_3G = 0;
	private static long t_cur_2G = 0;
	private static long r_cur_wifi = 0;
	private static long r_cur_3G = 0;
	private static long r_cur_2G = 0;

	public static String getNetValue() {
		StringBuffer sb = new StringBuffer();
		int cur_net_type = getNetWorkType();

		java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
		switch (cur_net_type) {
		case 0:
			t_cur_wifi = getNetTxWifiBytes();
			r_cur_wifi = getNetRxWifiBytes();
			t_add_wifi = (t_cur_wifi - t_base_wifi) / B2K;
			r_add_wifi = (r_cur_wifi - r_base_wifi) / B2K;

			sb.append("wifi:t");
			sb.append(df.format(t_add_wifi));
			sb.append("KB|r");
			sb.append(df.format(r_add_wifi));
			sb.append("KB  3G:t");
			sb.append(df.format(t_add_3G));
			sb.append("KB|r");
			sb.append(df.format(r_add_3G));
			sb.append("KB  2G:t");
			sb.append(df.format(t_add_2G));
			sb.append("KB|r");
			sb.append(df.format(r_add_2G));
			sb.append("KB");
			break;
		case 1:
			t_cur_3G = getNetTxMobileBytes();
			r_cur_3G = getNetRxMobileBytes();
			t_add_3G = (t_cur_3G - t_base_3G) / B2K;
			r_add_3G = (r_cur_3G - r_base_3G) / B2K;

			sb.append("3G:t");
			sb.append(df.format(t_add_3G));
			sb.append("KB|r");
			sb.append(df.format(r_add_3G));
			sb.append("KB  wifi:t");
			sb.append(df.format(t_add_wifi));
			sb.append("KB|r");
			sb.append(df.format(r_add_wifi));
			sb.append("KB  2G:t");
			sb.append(df.format(t_add_2G));
			sb.append("KB|r");
			sb.append(df.format(r_add_2G));
			sb.append("KB");
			break;
		case 2:
			t_cur_2G = getNetTxMobileBytes();
			r_cur_2G = getNetRxMobileBytes();
			t_add_2G = (t_cur_2G - t_base_2G) / B2K;
			r_add_2G = (r_cur_2G - r_base_2G) / B2K;

			sb.append("2G:t");
			sb.append(df.format(t_add_2G));
			sb.append("KB|r");
			sb.append(df.format(r_add_2G));
			sb.append("KB  wifi:t");
			sb.append(df.format(t_add_wifi));
			sb.append("KB|r");
			sb.append(df.format(r_add_wifi));
			sb.append("KB  3G:t");
			sb.append(df.format(t_add_3G));
			sb.append("KB|r");
			sb.append(df.format(r_add_3G));
			sb.append("KB");
			break;
		}

		return sb.toString();
	}

	private long p_t_base = 0;
	private long p_r_base = 0;

	private double p_t_add = 0;
	private double p_r_add = 0;

	public double getP_t_add() {
		return p_t_add;
	}

	public double getP_r_add() {
		return p_r_add;
	}

	public NetUtils(String pName) {
		initProcessNetValue(pName);
	}

	public void initProcessNetValue(String pName) {
		
		p_t_base = getOutOctets(pName);
		p_r_base = getInOctets(pName);

		p_t_add = 0;
		p_r_add = 0;
	}

	private long p_t_cur = 0;
	private long p_r_cur = 0;

	public String getProcessNetValue(String pName) {
		StringBuffer sb = new StringBuffer();

		java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
		p_t_cur = getOutOctets(pName);
		p_r_cur = getInOctets(pName);
		p_t_add = (p_t_cur - p_t_base) / B2K;
		p_r_add = (p_r_cur - p_r_base) / B2K;

		sb.append("t");
		sb.append(df.format(p_t_add));
		sb.append("KB|r");
		sb.append(df.format(p_r_add));
		sb.append("KB");

		return sb.toString();
	}
	
	public static void clearNetValue(String key)
	{
		if (key == null) return;
		
		String pkgName = "";
		NetUtils netUtils = null;
		if (key.equals(CommonString.NET_key))
		{
			NetUtils.initNetValue();
		}
		else if (key.startsWith(CommonString.PNET_KEY_PRE))
		{
			pkgName = key.substring(CommonString.PNET_KEY_PRE.length());
			netUtils = NetUtils.netInfoMap.get(pkgName);
			if (null != netUtils)
			{
				netUtils.initProcessNetValue(pkgName);
			}
		}
	}
	
	public static boolean isWifiActive() {
		Context c = GTApp.getContext();
		ConnectivityManager manager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (manager != null) {
			info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if ("WIFI".equals(info[i].getTypeName())
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
