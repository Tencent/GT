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
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.utils.FileUtil;
import com.tencent.wstt.gt.utils.StringUtil;
import com.tencent.wstt.gt.utils.ToastUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.SparseArray;

/**
 * 进程信息工具类。
 */
public class ProcessUtils {

	private static IProcess processUtil;

	/**
	 * 根据手机系统的版本号，适配ProcessUtils的实现，全局调用一次即可。
	 * Android4.x及以下系统，用老的方式效率更高。
	 * Android5.x及以上系统，用shell命令方式。
	 */
	synchronized public static void init()
	{
		if (Env.API < 21)
		{
			processUtil = new Process4x();
		}
		else if (Env.API < 23) // 5.x+
		{
			processUtil = new Process5x();
		}
		else // 6.x+
		{
			processUtil = new Process6x();
		}
	}

	public static String getPackageByUid(int uid)
	{
		return processUtil.getPackageByUid(uid);
	}

	/*
	 * 在选择被测应用后应该更新，包括广播和sdk自动化中，注意广播和sdk自动化中执行su操作有风险
	 */
	public static boolean initUidPkgCache()
	{
		return processUtil.initUidPkgCache();
	}

	/**
	 * 是否至少有一个进程在运行指定包名的应用程序
	 * 
	 * @param pkgName
	 *            指定的包名
	 * @return 是否至少有一个进程在运行指定包名的应用程序
	 */
	public static boolean hasProcessRunPkg(String pkgName) {
		return processUtil.hasProcessRunPkg(pkgName);
	}

	/**
	 * 根据进程名，获取进程UID，反查UID，性能需要高
	 *
	 *            当前进程的上下文环境
	 * @param pName
	 *            进程名
	 * @return 进程UID
	 */
	public static int getProcessUID(String pName) {
		return processUtil.getProcessUID(pName);
	}

	/**
	 * 根据进程名，获取进程PID
	 *
	 *            当前进程的上下文环境
	 * @param pName
	 *            进程名
	 * @return 进程PID
	 */
	public static int getProcessPID(String pName) {
		return processUtil.getProcessPID(pName);
	}

	/**
	 * 判断进程是否在运行。
	 * 
	 * @param sPid
	 *            进程号
	 * @return true 正在运行；false 停止运行
	 */
	public static boolean isProcessAlive(String sPid) {
		return processUtil.isProcessAlive(sPid);
	}

	public static List<ProcessInfo> getAllRunningAppProcessInfo() {
		return processUtil.getAllRunningAppProcessInfo();
	}

	public static void killprocess(String proc, int cmd) {
		processUtil.killprocess(proc, cmd);
	}

	private static void killprocessNormal(String proc, int killMethod) {
		try {
			ArrayList<String> pid_list = new ArrayList<String>();

			ProcessBuilder execBuilder = null;

			execBuilder = new ProcessBuilder("sh", "-c", "ps |grep " + proc);

			execBuilder.redirectErrorStream(true);

			Process exec = null;
			exec = execBuilder.start();
			InputStream is = exec.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line = "";
			while ((line = reader.readLine()) != null) {
				String regEx = "\\s[0-9][0-9]*\\s";
				Pattern pat = Pattern.compile(regEx);
				Matcher mat = pat.matcher(line);
				if (mat.find()) {
					String temp = mat.group();
					temp = temp.replaceAll("\\s", "");
					pid_list.add(temp);
				}
			}

			for (int i = 0; i < pid_list.size(); i++) {
				execBuilder = new ProcessBuilder("su", "-c", "kill", "-" + killMethod, pid_list.get(i));
				exec = null;
				exec = execBuilder.start();

				execBuilder = new ProcessBuilder("su", "-c", "kill" + " -" + killMethod + " " + pid_list.get(i));
				exec = null;
				exec = execBuilder.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ProcessInfo
	{
		public String name; // 进程名
		public int pid;  // PID
		public int ppid; // 父PID
		public int uid; //  UID
		
		public ProcessInfo(int pid, String name, int ppid, int uid)
		{
			this.pid = pid;
			this.name = name;
			this.ppid = ppid;
			this.uid = uid;
		}

		@Override
		public int hashCode()
		{
			int result = 17;
			if (name != null)
				result = 37 * result + name.hashCode();
			result = 37 * result + (int) (pid ^ (pid >>> 32));
			result = 37 * result + (int) (ppid ^ (ppid >>> 32));
			result = 37 * result + (int) (uid ^ (uid >>> 32));
			return result;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o instanceof ProcessInfo)
			{
				ProcessInfo another = (ProcessInfo)o;
				if (this.pid == another.pid 
						&& this.ppid == another.ppid
						&& this.name != null
						&& another.name != null
						&& this.name.equals(another.name))
				{
					return true;
				}
			}
			return false;
		}
	}

	static interface IProcess {
		List<ProcessInfo> getAllRunningAppProcessInfo();
		String getPackageByUid(int uid);
		int getProcessPID(String pName);
		int getProcessUID(String pName);
		boolean hasProcessRunPkg(String pkgName);
		boolean isProcessAlive(String sPid);
		void killprocess(String proc, int cmd);
		boolean initUidPkgCache();
	}
	
	static class Process5x implements IProcess
	{
		private boolean isRootcheckedResult = true;

		private Map<String, ProcessInfo> procInfoCache =
				new HashMap<String, ProcessInfo>();
		// uid和package的对应
		private SparseArray<String> uidPkgCache = null;

		private SparseArray<String> getUidPkgCache()
		{
			return uidPkgCache;
		}

		@Override
		public List<ProcessInfo> getAllRunningAppProcessInfo() {
			List<ProcessInfo> appProcessList = new ArrayList<ProcessInfo>();
			
			// 先取Android进程的父进程zygote的进程号，64位app对应的是zygote64
			int zygotePid = -1;
			int zygotePid64 = -1;

			/*
			 * 如果使用了较新版本的root权限控制工具，ps命令会看不到zygote进程，这时需要用su命令
			 */
			boolean needRoot = true;

			// 先尝试sh命令
			BufferedReader readerZ = null;
			try {
				ProcessBuilder execBuilderZ = null;
				execBuilderZ = new ProcessBuilder("sh", "-c", "ps |grep zygote");
				execBuilderZ.redirectErrorStream(true);
				Process execZ = execBuilderZ.start();
				InputStream isZ = execZ.getInputStream();
				readerZ = new BufferedReader(
						new InputStreamReader(isZ));
				String lineZ = "";

				while ((lineZ = readerZ.readLine()) != null) {
					needRoot = false;
					String[] arrayZ = lineZ.trim().split("\\s+");
					if (arrayZ.length >= 9) {
						if (arrayZ[8].equals("zygote"))
						{
							zygotePid = Integer.parseInt(arrayZ[1]);
						}
						else if (arrayZ[8].equals("zygote64"))
						{
							zygotePid64 = Integer.parseInt(arrayZ[1]);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			} finally {
				FileUtil.closeReader(readerZ);
			}

			/*
			 * sh若搞不定，改用su权限执行ps命令
			 */
			if (needRoot)
			{
				try {
					ProcessBuilder execBuilderZ = null;
					execBuilderZ = new ProcessBuilder("su", "-c", "ps |grep zygote");
					execBuilderZ.redirectErrorStream(true);
					Process execZ = execBuilderZ.start();
					InputStream isZ = execZ.getInputStream();
					readerZ = new BufferedReader(
							new InputStreamReader(isZ));
					String lineZ = "";

					while ((lineZ = readerZ.readLine()) != null) {
						String[] arrayZ = lineZ.trim().split("\\s+");
						if (arrayZ.length >= 9) {
							if (arrayZ[8].equals("zygote"))
							{
								zygotePid = Integer.parseInt(arrayZ[1]);
							}
							else if (arrayZ[8].equals("zygote64"))
							{
								zygotePid64 = Integer.parseInt(arrayZ[1]);
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					FileUtil.closeReader(readerZ);
				}
			}

			if (zygotePid < 0)
			{
				return appProcessList;
			}

			// 正式取可用的Android进程，前面不管是sh还是su搞定的，这里都用sh即可。
			BufferedReader reader = null;
			try {
				ProcessBuilder execBuilder = null;
				execBuilder = new ProcessBuilder("sh", "-c", "ps |grep u0_a");
				execBuilder.redirectErrorStream(true);
				Process exec = null;
				exec = execBuilder.start();
				InputStream is = exec.getInputStream();
				reader = new BufferedReader(
						new InputStreamReader(is));

				String line = "";
				while ((line = reader.readLine()) != null) {
					String[] array = line.trim().split("\\s+");
					if (array.length >= 9) {
						int uid = Integer.parseInt(array[0].substring(4)) + 10000;
						int pid = Integer.parseInt(array[1]);
						int ppid = Integer.parseInt(array[2]);
						// 过滤掉系统子进程，只留下父进程是zygote的进程
						if (ppid == zygotePid || ppid == zygotePid64)
						{
							ProcessInfo pi = new ProcessInfo(pid, array[8], ppid, uid);
							appProcessList.add(pi);
							procInfoCache.put(array[8], pi);
						}
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				FileUtil.closeReader(reader);
			}

			return appProcessList;
		}

		@Override
		public String getPackageByUid(int uid) {
			if (uidPkgCache == null)
			{
				initUidPkgCache();
			}
			return uidPkgCache.get(uid);
		}

		@Override
		public int getProcessPID(String pName) {
			int pId = -1;
			List<ProcessInfo> appProcessInfos = getAllRunningAppProcessInfo();
			for (ProcessInfo info : appProcessInfos) {
				if (info.name.equals(pName)) {
					pId = info.pid;
					break;
				}
			}
			return pId;
		}

		@Override
		public int getProcessUID(String pName) {
			int uId = 0;
			if (procInfoCache.isEmpty())
			{
				List<ProcessInfo> appProcessInfos = getAllRunningAppProcessInfo();

				for (ProcessInfo pi : appProcessInfos) {
					if (pi.name.equals(pName)) {
						
						uId = pi.uid;
						break;
					}
				}
			}
			else // uid和pName都不会变，所以从缓存查
			{
				ProcessInfo pi = procInfoCache.get(pName);
				/*
				 * 但是有初始记录的进程并不是主进程的情况，比如：
				 * 微信往往是com.tencent.mm:push进程活着，而com.tencent.mm进程初始是不在的
				 * 此时需要找与com.tencent.mm相似的com.tencent.mm:push进程信息作为替代
				 */
				if (pi == null)
				{
					for (ProcessInfo tpi : procInfoCache.values())
					{
						if (tpi.name.startsWith(pName))
						{
							pi = tpi;
							procInfoCache.put(pName, pi);
							break;
						}
					}
				}
				uId = pi == null ? -1 : pi.uid;
			}
			return uId;
		}

		@Override
		public boolean hasProcessRunPkg(String pkgName) {
			if (pkgName == null) return false;
			int uid = -1;

			int len = getUidPkgCache().size();
			// 如果是没有root过的手机，uidPkgCache是空的，采用替代方案，但对于进程命名中不包括包名的没有办法
			if (len != 0)
			{
				for (int i = 0; i < len; i++)
				{
					if (pkgName.equals(getUidPkgCache().valueAt(i)))
					{
						uid = getUidPkgCache().keyAt(i);
						break;
					}
				}

				if (uid == -1) return false;
				List<ProcessInfo> appProcessInfos = getAllRunningAppProcessInfo();
				for (ProcessInfo info : appProcessInfos) {
					if (info.uid == uid)
					{
						return true;
					}
				}
				return false;
			}
			else // 替代方案
			{
				List<ProcessInfo> appProcessInfos = getAllRunningAppProcessInfo();
				for (ProcessInfo info : appProcessInfos) {
					if (info.name.contains(pkgName))
					{
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public boolean isProcessAlive(String sPid) {
			boolean isAlive = false;
			if (sPid != null) {
				// 采用进入目录的方式判断会比较快
				BufferedReader reader = null;
				try {
					ProcessBuilder execBuilder = null;
					execBuilder = new ProcessBuilder("sh", "-c", "cd proc/" + sPid);
					execBuilder.redirectErrorStream(true);
					Process exec = null;
					exec = execBuilder.start();
					InputStream is = exec.getInputStream();
					reader = new BufferedReader(
							new InputStreamReader(is));

					String line = reader.readLine();
					if (line == null) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally
				{
					FileUtil.closeReader(reader);
				}
			}

			return isAlive;
		}

		@Override
		public void killprocess(String proc, int cmd) {
			killprocessNormal(proc, cmd);
		}

		@Override
		public boolean initUidPkgCache() {
			if (!isRootcheckedResult)
			{
				return false;
			}
			
			uidPkgCache = new SparseArray<String>();
			String pkgListPath = "/data/system/packages.list";
			try {
				CMDExecute.doCmd("chmod 777 " + pkgListPath);
			} catch (Exception e) {
				ToastUtil.ShowShortToast(GTApp.getContext(), "root needed!");
				isRootcheckedResult = false;
				return false;
			}
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(pkgListPath));
				String temp;
				while((temp = br.readLine()) != null){
					String[] tempArray = temp.trim().split("\\s+");
					if(tempArray.length > 2){
						if(StringUtil.isNumeric(tempArray[1])){
							uidPkgCache.put(Integer.parseInt(tempArray[1]), tempArray[0]);
						}
					}
				}
				if (uidPkgCache.size() <= 0)
				{
					isRootcheckedResult = false;
					return false;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				isRootcheckedResult = false;
				return false;
			}
			finally
			{
				FileUtil.closeReader(br);
			}
			isRootcheckedResult = true;
			return true;
		}
	}

	static class Process4x  implements IProcess
	{
		// uid和package的对应
		private SparseArray<String> uidPkgCache = null;

		private SparseArray<String> getUidPkgCache()
		{
			return uidPkgCache;
		}

		@Override
		public List<ProcessInfo> getAllRunningAppProcessInfo() {
			ActivityManager am = (ActivityManager) GTApp.getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> appProcessList = am
					.getRunningAppProcesses();
			List<ProcessInfo> ret = new ArrayList<ProcessInfo>();
			for (ActivityManager.RunningAppProcessInfo info : appProcessList)
			{
				// pid目前不需要，默认赋值为-1
				ProcessInfo processInfo = new ProcessInfo(info.pid, info.processName, -1, info.uid);
				ret.add(processInfo);
			}
			
			return ret;
		}

		@Override
		public String getPackageByUid(int uid) {
			// Android4.x不需要此方法
			throw new UnsupportedOperationException();
		}

		@Override
		public int getProcessPID(String pName) {
			int pId = -1;
			ActivityManager am = (ActivityManager) GTApp.getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> appProcessList = am
					.getRunningAppProcesses();
			int pLength = appProcessList.size();
			for (int i = 0; i < pLength; i++) {
				if (appProcessList.get(i).processName.equals(pName)) {
					pId = appProcessList.get(i).pid;
					break;
				}
			}
			return pId;
		}

		@Override
		public int getProcessUID(String pName) {
			int uId = 0;
			ActivityManager am = (ActivityManager) GTApp.getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> appProcessList = am
					.getRunningAppProcesses();
			int pLength = appProcessList.size();
			for (int i = 0; i < pLength; i++) {
				if (appProcessList.get(i).processName.equals(pName)) {
					uId = appProcessList.get(i).uid;
					break;
				}
			}
			return uId;
		}

		@Override
		public boolean hasProcessRunPkg(String pkgName) {
			if (pkgName == null) return false;
			int uid = -1;
			int len = getUidPkgCache().size();
			for (int i = 0; i < len; i++)
			{
				if (pkgName.equals(getUidPkgCache().valueAt(i)))
				{
					uid = getUidPkgCache().keyAt(i);
					break;
				}
			}

			List<ProcessInfo> appProcessInfos = getAllRunningAppProcessInfo();
			for (ProcessInfo info : appProcessInfos) {
				if (info.uid == uid)
				{
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean isProcessAlive(String sPid) {
			boolean isAlive = false;
			if (sPid != null && GTApp.getContext() != null) {
				int pid = -1;
				try
				{
					pid = Integer.parseInt(sPid);
				}
				catch (Exception e)
				{
					return false;
				}

				ActivityManager am = (ActivityManager) GTApp.getContext()
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<ActivityManager.RunningAppProcessInfo> appProcessList = am
						.getRunningAppProcesses();
				for (RunningAppProcessInfo info : appProcessList) {
					if (info.pid == pid) {
						isAlive = true;
						break;
					}
				}
			}

			return isAlive;
		}

		@Override
		public void killprocess(String proc, int cmd) {
			killprocessNormal(proc, cmd);
		}

		@Override
		public boolean initUidPkgCache() {
			// do nothing for 4.x
			uidPkgCache = new SparseArray<String>();
			ActivityManager am = (ActivityManager) GTApp.getContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> appProcessList = am
					.getRunningAppProcesses();
			for (RunningAppProcessInfo info : appProcessList) {
				String[] pkgList = info.pkgList;
				for (String pkg : pkgList)
				{
					uidPkgCache.put(info.uid, pkg);
				}
			}
			return true;
		}
	}

	/**
	 * 6.x系统中直接用ps命令取的即是可操作的进程
	 */
	static class Process6x extends Process5x
	{
		// 有的6.x系统，在ps命令的策略上和5.x还是一样的，此时应该用5x的策略
		static boolean isLike5x = false;

		@Override
		public List<ProcessInfo> getAllRunningAppProcessInfo() {
			if (isLike5x)
			{
				return super.getAllRunningAppProcessInfo();
			}
			List<ProcessInfo> appProcessList = new ArrayList<ProcessInfo>();

			// 正式取可用的Android进程
			BufferedReader reader = null;
			try {
				ProcessBuilder execBuilder = null;
				execBuilder = new ProcessBuilder("sh", "-c", "ps");
				execBuilder.redirectErrorStream(true);
				Process exec = null;
				exec = execBuilder.start();
				InputStream is = exec.getInputStream();
				reader = new BufferedReader(
						new InputStreamReader(is));

				String line = "";
				while ((line = reader.readLine()) != null) {
					String[] array = line.trim().split("\\s+");
					if (array.length >= 9) {
						// 先屏蔽常用的shell命令进程
						if (array[8].equals("su") || array[8].equals("sh")
								|| array[8].equals("sush") || array[8].equals("ps"))
						{
							continue;
						}
						int uid = -1;
						try
						{
							uid = Integer.parseInt(array[0].substring(4)) + 10000;
						}
						catch (Exception e)
						{
							// 如果异常了，说明取到了系统进程信息，则判定为该6.x系统的策略和5.x一致，应尝试5.x的策略
							isLike5x = true;
							FileUtil.closeReader(reader); // 提前关闭流
							return super.getAllRunningAppProcessInfo();
						}

						int pid = Integer.parseInt(array[1]);
						int ppid = Integer.parseInt(array[2]);

						ProcessInfo pi = new ProcessInfo(pid, array[8], ppid, uid);
						appProcessList.add(pi);
						super.procInfoCache.put(array[8], pi);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				FileUtil.closeReader(reader);
			}

			return appProcessList;
		}
	}
}
