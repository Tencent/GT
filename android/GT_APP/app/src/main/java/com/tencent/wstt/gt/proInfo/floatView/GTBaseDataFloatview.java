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
package com.tencent.wstt.gt.proInfo.floatView;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.activity.GTAUTFragment;
import com.tencent.wstt.gt.activity.GTAUTFragment1;
import com.tencent.wstt.gt.api.utils.DeviceUtils;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.plugin.BaseService;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisCallback;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;

import java.util.ArrayList;

public class GTBaseDataFloatview extends BaseService implements OnTouchListener, OnDataChangedListener {
    private final String SimpleName = this.getClass().getSimpleName();

    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private Context mContext = null;
    public static View net_switch_view;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private float StartX;
    private float StartY;

    public boolean floatview_flag = true;
    public boolean net_state_flag = true;

    private final int SCREEN_HORIZONTALLY = 0;
    private final int SCREEN_VERTICAL = 1;

    private String pName = "";

    private TextView tv_switch_total;
    private TextView tv_switch_over;
    private TextView tv_cpu;

    private TextView tv_lost10;
    private TextView tv_lost20;
    private TextView tv_sm;
    private TextView tv_float_sm;

    private TextView tv_float_net;
    private SMLightView smLightView;

    private Button flag_btn;

    private boolean isMoved; // 一次touch操作中是否有移动过

    public static ArrayList<MemInfo> memInfoList = new ArrayList<MemInfo>();

    private static GTBaseDataFloatview INSTANCE;

    private Handler mHandler = new Handler();

    public static GTBaseDataFloatview getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new GTBaseDataFloatview();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);

        mContext = context;
        net_switch_view = LayoutInflater.from(context).inflate(
                R.layout.gt_float_basedata, null);

        createView();
        netSwitch();

        if (AUTManager.pkn != null) {
            GTAUTFragment1.addDataChangedListener(this);
            GTAUTFragment.addDataChangedListener(this);
            addDataChangeCallBack();
        }

        resetData();
    }

    private void netSwitch() {
        tv_switch_total = (TextView) net_switch_view.findViewById(R.id.tv_db_page_total);

        tv_switch_over = (TextView) net_switch_view.findViewById(R.id.tv_db_page_over);
        tv_cpu = (TextView) net_switch_view.findViewById(R.id.tv_db_background_cpu);

        tv_lost10 = (TextView) net_switch_view.findViewById(R.id.tv_lost_10);
        tv_lost20 = (TextView) net_switch_view.findViewById(R.id.tv_lost_20);
        tv_sm = (TextView) net_switch_view.findViewById(R.id.tv_basedata_sm);
        tv_float_sm = (TextView) net_switch_view.findViewById(R.id.tv_float_sm);
        smLightView = (SMLightView) net_switch_view.findViewById(R.id.sm_lightview);

        tv_float_net = (TextView) net_switch_view.findViewById(R.id.tv_floatview_net);

        tv_switch_total.setOnTouchListener(this);
        tv_switch_over.setOnTouchListener(this);
        tv_cpu.setOnTouchListener(this);

        flag_btn = (Button)net_switch_view.findViewById(R.id.add_flag_btn);
        flag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GTRAnalysis.packageName != null) {
                    long time = System.currentTimeMillis();

                    // id and time fields are set to -1 for flags
                    String data = "-1^-1^" + "GTRFlagRecord" + "^" + time;

                    Intent intent = new Intent();
                    intent.setAction("com.tencent.wstt.gt.baseCommand.addFlag");
                    intent.putExtra("data", data);
                    intent.putExtra("flagTime", time);
                    mContext.sendBroadcast(intent);
                }
            }
        });
    }


    private void createView() {
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;
        try {
            wm.addView(net_switch_view, wmParams);
        } catch (Exception e) {
            /*
             * 有的Android6会报permission denied for this window type问题
			 * https://github.com/intercom/intercom-android/issues/116
			 * 在这种系统上直接屏蔽悬浮窗
			 */
            stopSelf();
            return;
        }

        net_switch_view.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                x = event.getRawX();
                if (SCREEN_VERTICAL == checkScreenOrientation()) {
                    y = event.getRawY()
                            - DeviceUtils.getStatusBarHeight(mContext);
                } else {
                    y = event.getRawY();
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        StartX = x;
                        StartY = y;
                        // 获取相对View的坐标，即以此View左上角为原点
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        isMoved = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        if (!isMoved) {
                            isMoved = false;
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        if (Math.abs(x - StartX) > 10 || Math.abs(y - StartY) > 10) {
            wmParams.x = (int) (x - mTouchStartX);
            wmParams.y = (int) (y - mTouchStartY);
            isMoved = true;
            wm.updateViewLayout(net_switch_view, wmParams);
        }
    }

    private int checkScreenOrientation() {
        int dev_width = DeviceUtils.getDevWidth();
        int dev_height = DeviceUtils.getDevHeight();
        int orientation = SCREEN_VERTICAL; // 0:竖屏 1:横屏
        if (dev_width > dev_height) {
            orientation = SCREEN_HORIZONTALLY;
        }
        return orientation;
    }

    @Override
    public void onStart(Intent intent) {
        pName = intent.getStringExtra("pName");
        super.onStart(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        floatview_flag = false;
        net_state_flag = false;

        try {
            wm.removeView(net_switch_view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        INSTANCE = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public IBinder onBind() {
        return null;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private void addDataChangeCallBack() {
        GTRAnalysis.addCallBack(new GTRAnalysisCallback() {
            @Override
            public void refreshNormalInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshNormalInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (GTRAnalysis.getGtrAnalysisResult().backCpuTotal != 0) {
                            tv_cpu.setText("后台CPU: " + toRealTime((GTRAnalysis.getGtrAnalysisResult().backTime * GTRAnalysis.getGtrAnalysisResult().backCpuApp) / GTRAnalysis.getGtrAnalysisResult().backCpuTotal) + "/" + toRealTime(GTRAnalysis.getGtrAnalysisResult().backTime));
                        }
                        tv_lost10.setText("cpu:" + GTRAnalysis.getGtrAnalysisResult().nowCPU + "%");
                        tv_float_net.setText("Net:" + Formatter.formatFileSize(mContext, GTRAnalysis.getGtrAnalysisResult().nowFlow));
                        tv_lost20.setText("内存:" + GTRAnalysis.getGtrAnalysisResult().nowMemory + "MB");
                    }
                });
            }

            @Override
            public void refreshSMInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshSMInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_sm.setText("丢帧(nowSM:" + GTRAnalysis.getGtrAnalysisResult().nowSM + ")");
                        if (GTRAnalysis.getGtrAnalysisResult().nowSM >= 50) {
                            smLightView.drawGreen();
                        } else if (40 <= GTRAnalysis.getGtrAnalysisResult().nowSM && GTRAnalysis.getGtrAnalysisResult().nowSM < 50) {
                            smLightView.drawYellow();
                        } else if (GTRAnalysis.getGtrAnalysisResult().nowSM < 40) {
                            smLightView.drawRed();
                        }
                    }
                });
            }

            @Override
            public void refreshPageLoadInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshPageLoadInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_switch_total.setText("total:" + GTRAnalysis.getGtrAnalysisResult().pageNum);
                        tv_switch_over.setText(">500ms:" + GTRAnalysis.getGtrAnalysisResult().overPageNum);
                    }
                });
            }

            @Override
            public void refreshBlockInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshBlockInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_float_sm.setText("低流畅值次数:" + GTRAnalysis.getGtrAnalysisResult().lowSMNum);
                    }
                });
            }
        });
    }

    @Override
    public void onDataChanged() {
        resetData();
    }

    private void resetData() {
        tv_switch_total.setText("total:" + 0);
        tv_switch_over.setText(">500ms:" + 0);
        tv_cpu.setText("后台CPU: 0/0");
        tv_lost10.setText("cpu:0%");

        tv_lost20.setText("内存:0");
        tv_sm.setText("丢帧(nowSM:0)");
        tv_float_net.setText("Net:0Kb");

        String smStr = "低流畅值次数:0";
        tv_float_sm.setText(smStr);
        smLightView.drawGreen();
    }

    public String toRealTime(long time) {
        String realTime = "";
        if (time >= 86400000) {
            long day = time / 86400000;
            realTime = realTime + day + "天";
            time = time - day * 86400000;
        }
        if (time >= 3600000) {
            long hour = time / 3600000;
            realTime = realTime + hour + "时";
            ;
            time = time - hour * 3600000;
        }
        if (time >= 60000) {
            long min = time / 60000;
            realTime = realTime + min + "分";
            time = time - min * 60000;
        }
        double sec = (double) time / 1000;
        realTime = realTime + sec + "秒";
        return realTime;
    }
}
