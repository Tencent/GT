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
package com.tencent.wstt.gt.plugin.smtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tencent.wstt.gt.log.logcat.LogLine;
import com.tencent.wstt.gt.log.logcat.RuntimeHelper;
import com.tencent.wstt.gt.log.logcat.VersionHelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SMLogService extends IntentService {

    private static final String TAG = SMLogService.class.getSimpleName();

    Process dumpLogcatProcess = null;
    BufferedReader reader = null;

    private boolean killed;
    private final Object lock = new Object();

    public SMLogService() {
        super("SMLogService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killProcess();
    }

    protected void onHandleIntent(Intent intent) {
        try {

            String str = intent.getStringExtra("pid");
            int pid = Integer.parseInt(str);

            List<String> args = new ArrayList<String>(Arrays.asList("logcat", "-v", "time", "Choreographer:I", "*:S"));

            dumpLogcatProcess = RuntimeHelper.exec(args);
            reader = new BufferedReader(new InputStreamReader(dumpLogcatProcess.getInputStream()), 8192);

            String line;

            while ((line = reader.readLine()) != null && !killed) {

                // filter "The application may be doing too much work on its main thread."
                if (!line.contains("uch work on its main t")) {
                    continue;
                }
                int pID = LogLine.newLogLine(line, false).getProcessId();
                if (pID != pid){
                    continue;
                }

                line = line.substring(50, line.length() - 71);
                Integer value = Integer.parseInt(line.trim());

                SMServiceHelper.getInstance().dataQueue.offer(value);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString() + "unexpected exception");
        } finally {
            killProcess();
        }
    }

    private void killProcess() {
        if (!killed) {
            synchronized (lock) {
                if (!killed && reader != null) {
                    Log.d(TAG, "CatlogService ended");
                    if (dumpLogcatProcess != null) {
                        RuntimeHelper.destroy(dumpLogcatProcess);
                    }
                    // post-jellybean, we just kill the process, so there's no need
                    // to close the bufferedReader.  Anyway, it just hangs.
                    if (VersionHelper.getVersionSdkIntCompat() < VersionHelper.VERSION_JELLYBEAN
                            && reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    killed = true;
                }
            }
        }
    }
}