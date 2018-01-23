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
package com.tencent.wstt.gt.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wstt.gt.GTApp;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.ProcessUtils;
import com.tencent.wstt.gt.manager.AUTManager;
import com.tencent.wstt.gt.manager.ClientFactory;
import com.tencent.wstt.gt.manager.ClientManager;
import com.tencent.wstt.gt.manager.OpUIManager;
import com.tencent.wstt.gt.manager.SingleInstanceClientFactory;
import com.tencent.wstt.gt.plugin.PluginManager;
import com.tencent.wstt.gt.proInfo.floatView.OnDataChangedListener;
import com.tencent.wstt.gt.proInfo.floatView.GTBaseDataFloatview;
import com.tencent.wstt.gt.proInfo.floatView.GTMemHelperFloatview;
import com.tencent.wstt.gt.utils.CommonString;
import com.tencent.wstt.gt.utils.DLog;
import com.tencent.wstt.gt.utils.FileUtils;
import com.tencent.wstt.gt.utils.ToastUtil;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisCallback;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;

import java.io.File;

public class GTAUTFragment1 extends Fragment implements OnClickListener {
    private TextView testedAppDesc;
    private TextView tv_select;
    private static TextView tv_selectedApp;
    private static TextView tv_AppName;
    private static TextView tv_PkName;
    private TextView selectApp;
    private TextView selectAppIcon;
    private TextView tv_Float_On;
    private TextView tv_Float_Off;
    private RelativeLayout floatSwitch;
    private TextView cleanBtn;
    private TextView startTv;
    private TextView stopTv;
    private TextView pushDataTv;
    private TextView appPidTv;
    private RelativeLayout cpuRelativeLayout;
    private TextView itemCpuValueTv;
    private RelativeLayout memoryRelativeLayout;
    private TextView itemMemValueTv;
    private RelativeLayout flowRelativeLayout;
    private TextView itemFlowValueTv;
    private RelativeLayout smRelativeLayout;
    private TextView itemSmValueTv;
    private RelativeLayout activityRelativeLayout;
    private TextView itemCheckactValueTv;
    private RelativeLayout fragmentRelativeLayout;
    private TextView itemCheckfragValueTv;
    private RelativeLayout viewBuildRelativeLayout;
    private TextView itemViewconValueTv;
    private RelativeLayout viewDrawRelativeLayout;
    private TextView itemViewdrawValueTv;
    private RelativeLayout ioRelativeLayout;
    private TextView itemIoValueTv;
    private RelativeLayout gcRelativeLayout;
    private TextView itemGCValueTv;

    private static String pkn_old = null; // 保留上次选中的AUT的包名名称
    private int selectDrawable;
    private int defaultDrawable;
    private static boolean isFloat = true;
    private static String[] cb_alias = {CommonString.pcpu_alias, CommonString.pjif_alias, CommonString.pnet_alias,
            CommonString.pm_pss_alias, CommonString.pm_pd_alias};
    private static String[] cb_key = {CommonString.pcpu_key, CommonString.pjif_key, CommonString.pnet_key,
            CommonString.pm_pss_key, CommonString.pm_pd_key};
    private Thread thread;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    //// TODO: 2017/8/25
    private static OnDataChangedListener changedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View autLayout = inflater.inflate(R.layout.fragment_aut,
                container, false);
        return initView(autLayout);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_select.setOnClickListener(select);

        cpuRelativeLayout.setOnClickListener(this);
        memoryRelativeLayout.setOnClickListener(this);
        flowRelativeLayout.setOnClickListener(this);
        smRelativeLayout.setOnClickListener(this);
        activityRelativeLayout.setOnClickListener(this);
        fragmentRelativeLayout.setOnClickListener(this);
        viewBuildRelativeLayout.setOnClickListener(this);
        viewDrawRelativeLayout.setOnClickListener(this);
        ioRelativeLayout.setOnClickListener(this);
        gcRelativeLayout.setOnClickListener(this);

        startTv.setOnClickListener(this);
        stopTv.setOnClickListener(this);
        cleanBtn.setOnClickListener(this);
        pushDataTv.setOnClickListener(this);

        selectDrawable = R.drawable.swbtn_selected;
        defaultDrawable = R.drawable.swbtn_default;
        if (isFloat) {
            tv_Float_On.setText("");
            tv_Float_On.setBackgroundResource(selectDrawable);
            tv_Float_Off.setText("OFF");
            tv_Float_Off.setBackgroundResource(defaultDrawable);
        } else {
            tv_Float_On.setText("ON");
            tv_Float_On.setBackgroundResource(selectDrawable);
            tv_Float_Off.setText("");
            tv_Float_Off.setBackgroundResource(defaultDrawable);
        }

        floatSwitch.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!tv_PkName.getText().toString().equals("n/a")) {
                    if (isFloat) {
                        isFloat = false;
                        tv_Float_On.setText("ON");
                        tv_Float_On.setBackgroundResource(defaultDrawable);
                        tv_Float_Off.setText("");
                        tv_Float_Off.setBackgroundResource(selectDrawable);

                        // if(!tv_PkName.getText().toString().equals("n/a")){
                        Intent intent = new Intent(GTApp.getContext(),
                                GTMemHelperFloatview.class);
                        intent.putExtra("pName", tv_PkName.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PluginManager
                                .getInstance()
                                .getPluginControler()
                                .startService(
                                        GTBaseDataFloatview.getInstance(),
                                        intent);
                        // }
                    } else {
                        isFloat = true;
                        tv_Float_On.setText("");
                        tv_Float_On.setBackgroundResource(selectDrawable);
                        tv_Float_Off.setText("OFF");
                        tv_Float_Off.setBackgroundResource(defaultDrawable);

                        PluginManager
                                .getInstance()
                                .getPluginControler()
                                .stopService(
                                        GTBaseDataFloatview.getInstance());
                    }
                }
                v.performClick();
                return false;
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean newHiddenState) {
        if (!newHiddenState) {
            doResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        doResume();
    }

    public static void resetAppInfo() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (AUTManager.appic != null && tv_selectedApp != null) {
                    tv_selectedApp.setBackgroundDrawable(AUTManager.appic);
                }

                if (tv_PkName != null) {
                    tv_PkName.setText(AUTManager.pkn);
                }

                if (tv_PkName != null) {
                    tv_AppName.setText(AUTManager.apn);
                }

                if (tv_PkName != null) {
                    tv_PkName.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void doResume() {
        if (AUTManager.appic != null) {
            syncProcessRunPkgState();
            resetAppInfo();
        }

		/*
         * 判断每次选择的应用是否为同样的，若不一样清空进程采集设置
		 */
        if (pkn_old != null && !pkn_old.equals(AUTManager.pkn)) {
            AUTManager.proNameIdMap.clear();
            AUTManager.proNameList.clear();
            AUTManager.proPidList.clear();
            // 清除旧的AUT_CLIENT
            ClientManager.getInstance().removeClient(ClientManager.AUT_CLIENT);
            // 创建新的AUT_CLIENT
            ClientFactory cf = new SingleInstanceClientFactory();
            cf.orderClient(
                    ClientManager.AUT_CLIENT, ClientManager.AUT_CLIENT.hashCode(), null, null);
        }

        // 保证GT读取到最新的应用状态
        AUTManager.findProcess();
        pkn_old = AUTManager.pkn;

        addDataChangeCallBack();
        thread = new Thread(new Runnable() {
            public void run() {
                AUTManager.appstatus = "running";
                checkRegist();
            }
        });
        thread.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.setGroupVisible(0, true); // 屏蔽设置主菜单
    }

    private View initView(View view) {
        testedAppDesc = (TextView) view.findViewById(R.id.tested_app_desc);
        tv_select = (TextView) view.findViewById(R.id.selected_app_bg);
        tv_selectedApp = (TextView) view.findViewById(R.id.app_pic);
        tv_AppName = (TextView) view.findViewById(R.id.selected_apn);
        tv_PkName = (TextView) view.findViewById(R.id.select_tested_pkn);
        selectApp = (TextView) view.findViewById(R.id.select_app);
        selectAppIcon = (TextView) view.findViewById(R.id.select_app_icon);
        tv_Float_On = (TextView) view.findViewById(R.id.tv_float_on);
        tv_Float_Off = (TextView) view.findViewById(R.id.tv_float_off);
        floatSwitch = (RelativeLayout) view.findViewById(R.id.basedata_switch);
        cleanBtn = (TextView) view.findViewById(R.id.button_clean);
        startTv = (TextView) view.findViewById(R.id.button_start);
        stopTv = (TextView) view.findViewById(R.id.button_stop);
        pushDataTv = (TextView) view.findViewById(R.id.button_pulldata);
        appPidTv = (TextView) view.findViewById(R.id.app_pid_tv);
        cpuRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_cpu);
        itemCpuValueTv = (TextView) view.findViewById(R.id.item_cpu_value_tv);
        memoryRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_memory);
        itemMemValueTv = (TextView) view.findViewById(R.id.item_mem_value_tv);
        flowRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_flow);
        itemFlowValueTv = (TextView) view.findViewById(R.id.item_flow_value_tv);
        smRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_sm);
        itemSmValueTv = (TextView) view.findViewById(R.id.item_sm_value_tv);
        activityRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_activity);
        itemCheckactValueTv = (TextView) view.findViewById(R.id.item_checkact_value_tv);
        fragmentRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_fragment);
        itemCheckfragValueTv = (TextView) view.findViewById(R.id.item_checkfrag_value_tv);
        viewBuildRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_viewbuild);
        itemViewconValueTv = (TextView) view.findViewById(R.id.item_viewcon_value_tv);
        viewDrawRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_viewdraw);
        itemViewdrawValueTv = (TextView) view.findViewById(R.id.item_viewdraw_value_tv);
        ioRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_io);
        itemIoValueTv = (TextView) view.findViewById(R.id.item_io_value_tv);
        gcRelativeLayout = (RelativeLayout) view.findViewById(R.id.item_gc);
        itemGCValueTv = (TextView) view.findViewById(R.id.item_gc_value_tv);
        return view;
    }

    private OnClickListener select = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!OpUIManager.gw_running) {
                if (pkn_old != null) {

                    Builder builder = new Builder(
                            getActivity());
                    builder.setMessage(getString(R.string.AUT_page_tip1));
                    builder.setTitle(getString(R.string.AUT_page_tip_title));
                    builder.setPositiveButton(R.string.cancel,
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
                                    // UI需要清理dataSet
                                    //暂停GTR收集
                                    //TODO
//                                    GTRAnalysisAndroid.clear();
                                    //GTRAnalysis.clear();
                                    GTRAnalysis.clear();
                                    Intent intent = new Intent(
                                            getActivity(),
                                            GTShowPackageActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    builder.setCancelable(false);
                    builder.show();
                    // }
                } else {
                    Intent intent = new Intent(getActivity(),
                            GTShowPackageActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // finish();
                }
            } else {
                ToastUtil.ShowLongToast(getActivity(),
                        getString(R.string.AUT_page_tip2), "center");
            }
        }
    };


    private GTRAnalysisCallback analysisCallback = null;


    private void addDataChangeCallBack() {
        analysisCallback = new GTRAnalysisCallback() {

            @Override
            public void refreshPid(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshPid(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetData();
                    }
                });
            }

            @Override
            public void refreshNormalInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshNormalInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemCpuValueTv.setText(GTRAnalysis.getGtrAnalysisResult().nowCPU + "%");
                        itemMemValueTv.setText(GTRAnalysis.getGtrAnalysisResult().nowMemory + "MB");
                        itemFlowValueTv.setText( Formatter.formatFileSize(getActivity(), GTRAnalysis.getGtrAnalysisResult().nowFlow));
                    }
                });
            }

            @Override
            public void refreshBlockInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshBlockInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemSmValueTv.setText("低流畅区间数:" + GTRAnalysis.getGtrAnalysisResult().lowSMNum + "    大卡顿数:" + GTRAnalysis.getGtrAnalysisResult().bigBlockNum);
                    }
                });
            }

            @Override
            public void refreshPageLoadInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshPageLoadInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemCheckactValueTv.setText("Activity总数:" + GTRAnalysis.getGtrAnalysisResult().pageNum + "     >500ms:" + GTRAnalysis.getGtrAnalysisResult().overPageNum);
                    }
                });
            }

            @Override
            public void refreshFragmentInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshFragmentInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemCheckfragValueTv.setText("fragment总数:" + GTRAnalysis.getGtrAnalysisResult().fragmentNum + "     超时:" + GTRAnalysis.getGtrAnalysisResult().overFragmentNum);
                    }
                });
            }

            @Override
            public void refreshViewBuildInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshViewBuildInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemViewconValueTv.setText("构建超时view总数:" + GTRAnalysis.getGtrAnalysisResult().viewBuildNum+ "     超时:"+ GTRAnalysis.getGtrAnalysisResult().overViewBuildNum);
                    }
                });
            }

            @Override
            public void refreshViewDrawInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshViewDrawInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemViewdrawValueTv.setText("绘制超时view总数:" + GTRAnalysis.getGtrAnalysisResult().viewDrawNum + "     超时:" +GTRAnalysis.getGtrAnalysisResult().overViewDrawNum );
                    }
                });
            }

            @Override
            public void refreshIOInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshIOInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemIoValueTv.setText("次数:" + GTRAnalysis.getGtrAnalysisResult().dbIONum + "     主线程:" + GTRAnalysis.getGtrAnalysisResult().mainThreadDBIONum);
                    }
                });
            }

            @Override
            public void refreshGCInfo(GTRAnalysisResult gtrAnalysisResult) {
                super.refreshGCInfo(gtrAnalysisResult);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemGCValueTv.setText("次数:" + GTRAnalysis.getGtrAnalysisResult().gcNum + "     显示:" +GTRAnalysis.getGtrAnalysisResult().explicitGCNum);
                    }
                });
            }
        };
        GTRAnalysis.addCallBack(analysisCallback);
    }

    private void resetData() {
        appPidTv.setText("PId:" + GTRAnalysis.getGtrAnalysisResult().pId);
        itemCpuValueTv.setText(GTRAnalysis.getGtrAnalysisResult().nowCPU + "%");
        itemMemValueTv.setText(GTRAnalysis.getGtrAnalysisResult().nowMemory + "MB");
        itemFlowValueTv.setText( Formatter.formatFileSize(getActivity(), GTRAnalysis.getGtrAnalysisResult().nowFlow));
        itemCheckactValueTv.setText("Activity总数:" + GTRAnalysis.getGtrAnalysisResult().pageNum + "     >500ms:" + GTRAnalysis.getGtrAnalysisResult().overPageNum);
        itemCheckfragValueTv.setText("fragment总数:" + GTRAnalysis.getGtrAnalysisResult().fragmentNum + ">超时:" + GTRAnalysis.getGtrAnalysisResult().overFragmentNum);
        itemViewconValueTv.setText("构建超时view总数:" + GTRAnalysis.getGtrAnalysisResult().overViewBuildNum);
        itemViewdrawValueTv.setText("绘制超时view总数:" + GTRAnalysis.getGtrAnalysisResult().overViewDrawNum);
        itemIoValueTv.setText("次数:" + GTRAnalysis.getGtrAnalysisResult().mainThreadDBIONum);
        itemSmValueTv.setText("低流畅区间数:" + GTRAnalysis.getGtrAnalysisResult().lowSMNum + "    大卡顿数:" + GTRAnalysis.getGtrAnalysisResult().bigBlockNum);
    }

    private static void checkRegist() {
        int count = 0;
        AUTManager.pIds = null;
        while (AUTManager.pIds == null) {
            AUTManager.findProcess();
            count++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count > 20) {
                AUTManager.appstatus = GTApp.getContext().getString(R.string.AUT_app_lanuch);
                break;
            }
        }
    }

    private void syncProcessRunPkgState() {
        if (AUTManager.pkn != null) {
            if (ProcessUtils.hasProcessRunPkg(AUTManager.pkn.toString())) {
                AUTManager.appstatus = "running";
            } else {
                // 如果直接调Activity的getString，有时会因Fragment GTAUTFragment{41afc180} not attached to Activity而crash
                // http://bugly.qq.com/detail?app=900010910&pid=1&ii=116#stack
                AUTManager.appstatus = GTApp.getContext().getString(R.string.AUT_app_lanuch);
            }
        }
    }

    @Override
    public void onDestroyView() {
        GTRAnalysis.removeCallBack(analysisCallback);
        mHandler.removeCallbacksAndMessages(null);
        DLog.e("handler--onDestroy");
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_stop:
                new AlertDialog.Builder(getActivity())
                        .setMessage("停止测试，将杀死被测应用")
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GTRAnalysis.stop(true);
                                ToastUtil.ShowShortToast(getActivity(), "停止采集");
                            }
                        })
                        .show();
                break;
            case R.id.button_pulldata:
                File[] files = null;
                try {
                    files = FileUtils.checkFileIsExit();
                } catch (Exception e) {
                    if (e.getMessage().equals("File not exist")) {
                        Toast.makeText(getActivity(), "Directory does not exist", Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
                if (files!=null &&files.length > 0) {
                    // TODO 2017/11/25  增加导出到微信功能
                    new AlertDialog.Builder(getActivity())
                            .setMessage("请选择导出方式")
                            .setNegativeButton("本地", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), ShowFileListActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("pullType", "local");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            })
                            .setPositiveButton("微信", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), ShowFileListActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("pullType", "wx");
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            })
                    .show();

                } else {
                    ToastUtil.ShowLongToast(getActivity(),
                            "无测试数据", "center");
                }

                break;

            case R.id.button_start:
                try {
                    // 如果当前正在统计别的应用，停掉
                    GTRAnalysis.stop(true);

                    // 启动GTR数据统计，打开应用
                    GTRAnalysis.start(getContext(), AUTManager.pkn);
                    addDataChangeCallBack();
                    thread = new Thread(new Runnable() {
                        public void run() {
                            AUTManager.appstatus = "running";
                            checkRegist();
                        }
                    });
                    thread.start();
                } catch (Exception e) {
                    Log.w("GTSettingActivity", "can not start App:" + AUTManager.pkn);
                    ToastUtil.ShowLongToast(getActivity(), getString(R.string.AUT_page_tip3) + AUTManager.pkn);
                }
                break;
            case R.id.button_clean:
                Builder builder = new Builder(
                        getActivity());
                builder.setMessage(getString(R.string.AUT_page_tip1));
                builder.setTitle(getString(R.string.AUT_page_tip_title));
                builder.setPositiveButton(R.string.cancel,
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
                                // UI需要清理dataSet
                                GTRAnalysis.clear();
                                ToastUtil.ShowLongToast(getActivity(), "数据已清除", "center");
                                resetData();
                                dialog.dismiss();

                            }
                        });
                builder.setCancelable(false);
                builder.show();
                break;
            case R.id.item_cpu:
                Intent intent_cpu = new Intent(getActivity(), GTRDetail_LineChartActivity.class);
                intent_cpu.putExtra("type", GTRDetail_LineChartActivity.TYPE_CPU);
                startActivity(intent_cpu);
                break;
            case R.id.item_memory:
                Intent intent_mem = new Intent(getActivity(), GTRDetail_LineChartActivity.class);
                intent_mem.putExtra("type", GTRDetail_LineChartActivity.TYPE_Memory);
                startActivity(intent_mem);
                break;
            case R.id.item_flow:
                Intent intent_flow = new Intent(getActivity(), GTRDetail_LineChartActivity.class);
                intent_flow.putExtra("type", GTRDetail_LineChartActivity.TYPE_Flow);
                startActivity(intent_flow);
                break;
            case R.id.item_sm:
                Intent intent_sm = new Intent(getActivity(), GTRDetail_LineChartActivity.class);
                intent_sm.putExtra("type", GTRDetail_LineChartActivity.TYPE_SM);
                startActivity(intent_sm);
                break;
            case R.id.item_activity:
                Intent intent_activity = new Intent(getActivity(), GTRDetail_ListViewActivity.class);
                intent_activity.putExtra("type", GTRDetail_ListViewActivity.TYPE_Activity);
                startActivity(intent_activity);
                break;
            case R.id.item_fragment:
                Intent intent_fragment = new Intent(getActivity(), GTRDetail_ListViewActivity.class);
                intent_fragment.putExtra("type", GTRDetail_ListViewActivity.TYPE_Fragment);
                startActivity(intent_fragment);
                break;
            case R.id.item_viewbuild:
                Intent intent_viewbuild = new Intent(getActivity(), GTRDetail_ListViewActivity.class);
                intent_viewbuild.putExtra("type", GTRDetail_ListViewActivity.TYPE_ViewBuild);
                startActivity(intent_viewbuild);
                break;
            case R.id.item_viewdraw:
                Intent intent_viewdraw = new Intent(getActivity(), GTRDetail_ListViewActivity.class);
                intent_viewdraw.putExtra("type", GTRDetail_ListViewActivity.TYPE_ViewDraw);
                startActivity(intent_viewdraw);
                break;
            case R.id.item_io:
                Intent intent_io = new Intent(getActivity(), GTRDetail_ListViewActivity.class);
                intent_io.putExtra("type", GTRDetail_ListViewActivity.TYPE_IO);
                startActivity(intent_io);
                break;
            case R.id.item_gc:
                Intent intent_gc = new Intent(getActivity(), GTRDetail_ListViewActivity.class);
                intent_gc.putExtra("type", GTRDetail_ListViewActivity.TYPE_GC);
                startActivity(intent_gc);
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void addDataChangedListener(OnDataChangedListener listener){
        changedListener=listener;
    }
}
