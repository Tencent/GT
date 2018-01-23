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
package com.tencent.wstt.gt.plugin.gps;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.log.LogUtils;
import com.tencent.wstt.gt.plugin.BaseService;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.GTUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class GTGPSRecordEngine extends BaseService {
//	private static final String TAG = "GTGPSRecordEngine";
	
	private static GTGPSRecordEngine INSTANCE;

	private LocationManager lm;
	private LocationListener locationListener;
	private List<GPSRecordListener> listeners;
	private boolean isRecord;

	private List<String> gpsList;
	private String recordFile;
	private long timeStamp = 0;
	private WakeLock mWakeLock;
	
	public static GTGPSRecordEngine getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new GTGPSRecordEngine();
		}
		return INSTANCE;
	}

	private GTGPSRecordEngine()
	{
		gpsList = new ArrayList<String>();
		listeners = new ArrayList<GPSRecordListener>();
	}

	public synchronized void addListener(GPSRecordListener listener)
	{
		listeners.add(listener);
	}

	public synchronized void removeListener(GPSRecordListener listener)
	{
		listeners.remove(listener);
	}

	synchronized public boolean isRecord()
	{
		return isRecord;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	@Override
	public IBinder onBind() {
		return null;
	}

	@Override
	public void onCreate(Context context) {

		Toast.makeText(GTApp.getContext(), "start record..",
				Toast.LENGTH_SHORT).show();

		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locationListener);

		recordFile = GTUtils.getSaveDate() + ".gps";

		isRecord = true;

		for (GPSRecordListener listener : listeners)
		{
			listener.onRecordStart();
		}

		PowerManager powerManager = (PowerManager)
				context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"location_in_bg");
		// 防止休眠锁
		mWakeLock.acquire();
	}

	public void onDestroy() {
		// 防止休眠锁未释放
		if (mWakeLock.isHeld()) {
			mWakeLock.release();
		}
		super.onDestroy();
		lm.removeUpdates(locationListener);
		saveGPS(recordFile);

		for (GPSRecordListener listener : listeners)
		{
			listener.onRecordStop();
		}
		isRecord = false;
	}

	private DecimalFormat df = new DecimalFormat("0.000");

	public String locToString(Location loc) {
		StringBuffer sb = new StringBuffer();
		
		long currtime = System.currentTimeMillis();
		String timestr = GTUtils.getGpsSaveTime(currtime);

		sb.append(loc.getLongitude());
		sb.append(",");
		sb.append(loc.getLatitude());
		sb.append(",");
		sb.append(loc.getAccuracy());
		sb.append(",");
		sb.append(loc.getBearing());
		sb.append(",");
		sb.append(loc.getSpeed());
		sb.append(",");
		sb.append(timestr);
		sb.append(",");
		sb.append(df.format((double) currtime / 1000.0));
		// sb.append(df.format(System.currentTimeMillis()/1000.0));
		// sb.append(df.format(loc.getTime()/1000.0));
		sb.append(",");
		sb.append(loc.getAltitude());
		if (timeStamp != 0) {
			if ((System.currentTimeMillis() - timeStamp) < 60000) {
				Toast.makeText(GTApp.getContext(), "record tag success",
						Toast.LENGTH_SHORT).show();
				sb.append(",");
				sb.append("tag");
			}
			timeStamp = 0;
		}
		return sb.toString();
	}

	private void saveGPS(String path) {
		try {
			File f = null;
			if (FileUtil.isPathStringValid(path)) {
				String validPath = FileUtil.convertValidFilePath(path,
						LogUtils.LOG_POSFIX);
				if (FileUtil.isPath(validPath)) {
					f = new File(validPath);
					f.mkdirs();
				} else {
					f = new File(Env.ROOT_GPS_FOLDER, validPath);
				}
			}

			LogUtils.writeFilterLog(gpsList, f, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			gpsList.add(locToString(loc));
			Log.d("loc", locToString(loc));
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}
}
