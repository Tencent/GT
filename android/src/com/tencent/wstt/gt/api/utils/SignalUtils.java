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

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.tencent.wstt.gt.GTApp;

/**
 * 信号工具类。
 */
public class SignalUtils {
	
	private static GTSignalListener gTSignalListener = null;
	
	/*
	 * 该方法只有在GTApp初始化时调用，非用户接口
	 */
	public static void init() {
		if (null == gTSignalListener)
		{
			gTSignalListener = new GTSignalListener(GTApp.getContext());
		}
	}

	/**
	 * 获取cellid
	 * 
	 * @param context
	 * 			当前进程的上下文环境
	 * @return cellid
	 */
	public static int getCellId() {
		return gTSignalListener.getCellId(GTApp.getContext());
	}
	
	/**
	 * 获取运营商信息
	 * @return 运营商信息
	 */
	public static String getOperator() {
		return gTSignalListener.getOperator();
	}
	
	/**
	 * 获取网络类型。
	 * NETWORK_TYPE_CDMA 网络类型为CDMA。
	 * NETWORK_TYPE_EDGE 网络类型为EDGE。
	 * NETWORK_TYPE_EVDO_0 网络类型为EVDO0。
	 * NETWORK_TYPE_EVDO_A 网络类型为EVDOA。
	 * NETWORK_TYPE_GPRS 网络类型为GPRS。
	 * NETWORK_TYPE_HSDPA 网络类型为HSDPA。
	 * NETWORK_TYPE_HSPA 网络类型为HSPA。
	 * NETWORK_TYPE_HSUPA 网络类型为HSUPA。
	 * NETWORK_TYPE_UMTS 网络类型为UMTS。
	 * 
	 * 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
	 * 
	 * @return 网络类型
	 */
	public static String getNetType() {
		return gTSignalListener.getNetType();
	}
	
	/**
	 * 获取wifi强度
	 * @return wifi强度
	 */
	public static int getWifiStrength() {
		return gTSignalListener.getWifiStrength();
	}
	
	/**
	 * 获取信号值
	 * @return 信号值
	 */
	public static int getDBM() {
		return gTSignalListener.getDBM();
	}
	
	/**
	 * 获取移动终端的类型
	 * @return 移动终端的类型
	 */
	public static String getPhoneType() {
		return gTSignalListener.getPhoneType();
	}
	
	private static class GTSignalListener extends PhoneStateListener {
		private TelephonyManager teleManager;
		private WifiManager wifiManager;
		private int dbm = 0; // DBM值
		private int wifi = 0; // WIFI信号强度
		private String phoneType = ""; // 手机制式
		
		private boolean isWifiRssiAvailable = true; // wifi信号检查是否可用

		public GTSignalListener(Context context) {
			teleManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			teleManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}

		/**
		 * 获取cellid
		 * 
		 * @param context
		 * 			当前进程的上下文环境
		 * @return cellid
		 */
		public int getCellId(Context context) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			GsmCellLocation mGsmCellLocation = (GsmCellLocation) tm
					.getCellLocation();
			int cid = mGsmCellLocation.getCid();
			return cid;
		}

		/**
		 * 获取运营商信息
		 * @return 运营商信息
		 */
		public String getOperator() {
			String operatorInfo = teleManager.getSimOperator();
			String operator = "";
			if (operatorInfo != null) {
				if (operatorInfo.equals("46000") || operatorInfo.equals("46002")
						|| operatorInfo.equals("46007")) {
					operator = "中国移动";
				} else if (operatorInfo.equals("46001")) {
					operator = "中国联通";
				} else if (operatorInfo.equals("46003")) {
					operator = "中国电信";
				}
			}
			return operator;
		}

		/**
		 * 获取网络类型 NETWORK_TYPE_CDMA 网络类型为CDMA NETWORK_TYPE_EDGE 网络类型为EDGE
		 * NETWORK_TYPE_EVDO_0 网络类型为EVDO0 NETWORK_TYPE_EVDO_A 网络类型为EVDOA
		 * NETWORK_TYPE_GPRS 网络类型为GPRS NETWORK_TYPE_HSDPA 网络类型为HSDPA
		 * NETWORK_TYPE_HSPA 网络类型为HSPA NETWORK_TYPE_HSUPA 网络类型为HSUPA
		 * NETWORK_TYPE_UMTS 网络类型为UMTS
		 * 
		 * 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
		 * 
		 * @return 网络类型
		 */
		public String getNetType() {
			int type = teleManager.getNetworkType();
			String netType = "";

			switch (type) {
			case TelephonyManager.NETWORK_TYPE_UMTS:
				netType = "UMTS(3G)";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				netType = "HSDPA(3G)";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				netType = "GPRS(2G)";
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				netType = "EDGE(2G)";
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
				netType = "CDMA(2G)";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				netType = "EVDO0(3G)";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				netType = "EVDOA(3G)";
				break;
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				netType = "未知";
				break;
			default:
				netType = "其它" + teleManager.getNetworkType();
			}
			return netType;
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {

			super.onSignalStrengthsChanged(signalStrength);

			/**
			 * 返回移动终端的类型
			 * 
			 * PHONE_TYPE_CDMA 手机制式为CDMA，电信 PHONE_TYPE_GSM 手机制式为GSM，移动和联通
			 * PHONE_TYPE_NONE 手机制式未知
			 */
			;
			switch (teleManager.getPhoneType()) {
			case TelephonyManager.PHONE_TYPE_CDMA:
				// RSSI
				dbm = signalStrength.getCdmaDbm() * 2 - 113;
				phoneType = "CDMA";
				break;
			case TelephonyManager.PHONE_TYPE_GSM:
				// cinr：Carrier to Interference plus Noise Ratio（载波与干扰和噪声比）
				dbm = signalStrength.getGsmSignalStrength() * 2 - 113;
				phoneType = "GSM";
				break;
			default:
				dbm = signalStrength.getEvdoDbm() * 2 - 113;
				phoneType = "EVDO";
				break;
			}

			// 获取wifi信号强度
			if (isWifiRssiAvailable)
			{
				try
				{
					wifi = wifiManager.getConnectionInfo().getRssi();
				}
				catch (Exception e)
				{
					isWifiRssiAvailable = false;
					Log.w(this.getClass().getSimpleName(), "get Wi-Fi rssi unavailable.");
				}
				
			}
			
			
		}

		/**
		 * 获取wifi强度
		 * @return wifi强度
		 */
		public int getWifiStrength() {
			return wifi;
		}

		/**
		 * 获取信号值
		 * @return 信号值
		 */
		public int getDBM() {
			return dbm;
		}

		/**
		 * 获取移动终端的类型
		 * @return 移动终端的类型
		 */
		public String getPhoneType() {
			return phoneType;
		}
	}
}
