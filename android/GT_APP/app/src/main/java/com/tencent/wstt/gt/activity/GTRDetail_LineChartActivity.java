package com.tencent.wstt.gt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.views.ScrollLineChartView;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisCallback;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;

public class GTRDetail_LineChartActivity extends Activity {



    //标题栏：
    ImageButton backImageView;
    TextView titleTextView;
    ImageButton deleteImageView;
    //图：
    LinearLayout chartLinearLayout;
    ScrollLineChartView scrollLineChartView;
    //结论表：
    TextView resultTextView;
    //类型：
    public static final int TYPE_CPU = 0;
    public static final int TYPE_Memory = 1;
    public static final int TYPE_Flow = 2;
    public static final int TYPE_SM = 3;
    int type = 0;


    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉页面标题栏
        setContentView(R.layout.gtr_activity_detail_line_chart);

        //获取类型：
        Intent intent = this.getIntent();
        type = intent.getIntExtra("type",TYPE_CPU);

        //标题栏：
        backImageView = (ImageButton) findViewById(R.id.image_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        titleTextView = (TextView) findViewById(R.id.text_title);
        deleteImageView = (ImageButton) findViewById(R.id.image_delete);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GTRAnalysis.clear();
            }
        });
        //图：
        chartLinearLayout = (LinearLayout) findViewById(R.id.linear_chart);
        scrollLineChartView = new ScrollLineChartView(this);
        scrollLineChartView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //scrollLineChartView.setY_interval(5.0);
        chartLinearLayout.addView(scrollLineChartView);
        //总结数据：
        resultTextView = (TextView) findViewById(R.id.text_result);
        //初始化视图：
        initView();


    }

    @Override
    protected void onStart() {
        super.onStart();
        GTRAnalysis.addCallBack(gtrAnalysisCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GTRAnalysis.removeCallBack(gtrAnalysisCallback);
    }


    GTRAnalysisCallback gtrAnalysisCallback = new GTRAnalysisCallback(){
        @Override
        public void refreshNormalInfo(final GTRAnalysisResult gtrAnalysisResult) {
            super.refreshNormalInfo(gtrAnalysisResult);
            initView();
        }
        @Override
        public void refreshSMInfo(final GTRAnalysisResult gtrAnalysisResult) {
            super.refreshSMInfo(gtrAnalysisResult);
            initView();
        }
    };


    void initView(){
        switch (type) {
            case TYPE_CPU:
                initCPUView();
                break;
            case TYPE_Memory:
                initMemoryView();
                break;
            case TYPE_Flow:
                initFlowView();
                break;
            case TYPE_SM:
                initSMView();
                break;
        }
    }

    private void initCPUView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("CPU");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新折线图数据：
                scrollLineChartView.setX_name("时间(s)");
                scrollLineChartView.setY_name("CPU(%)");
                scrollLineChartView.setY_minValue(0.0);
                scrollLineChartView.setY_maxValue(100.0);
                scrollLineChartView.setX_capacity(20);//显示最近20*3秒的数据
                scrollLineChartView.setDatas(gtrAnalysisResult.allCPUChartDatas);
                //刷新结果数据：
                String text1 = "当前CPU：" + gtrAnalysisResult.nowCPU+"%";
                String text2 = "前台时，平均CPU：" + (gtrAnalysisResult.frontCpuTotal==0?0:gtrAnalysisResult.frontCpuApp / gtrAnalysisResult.frontCpuTotal)+"%";
                String text3 = "前台时，最高CPU：" + gtrAnalysisResult.frontCpuMax+"%";
                String text4 = "后台时，平均CPU：" + (gtrAnalysisResult.backCpuTotal==0?0:gtrAnalysisResult.backCpuApp / gtrAnalysisResult.backCpuTotal)+"%";
                String text5 = "后台时，最高CPU：" + gtrAnalysisResult.backCpuMax+"%";
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2).append("\n")
                        .append(text3).append("\n")
                        .append(text4).append("\n")
                        .append(text5)
                        .toString()
                );
            }
        });
    }

    private void initMemoryView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("Memory");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新折线图数据：
                scrollLineChartView.setX_name("时间(s)");
                scrollLineChartView.setY_name("内存(MB)");
                scrollLineChartView.setY_minValue(0.0);
                scrollLineChartView.setX_capacity(20);//显示最近20*3秒的数据
                scrollLineChartView.setDatas(gtrAnalysisResult.allMemoryChartDatas);
                //刷新结果数据：
                String text1 = "当前Memory：" + gtrAnalysisResult.nowMemory+"MB";
                String text2 = "前台时，平均Memory：" + (gtrAnalysisResult.frontMemoryAverage_Num==0?0:gtrAnalysisResult.frontMemoryAverage_Sum/gtrAnalysisResult.frontMemoryAverage_Num)+"MB";
                String text3 = "前台时，最高Memory：" + gtrAnalysisResult.frontMemoryMax+"MB";
                String text4 = "后台时，平均Memory：" + (gtrAnalysisResult.backMemoryAverage_Num==0?0:gtrAnalysisResult.backMemoryAverage_Sum/gtrAnalysisResult.backMemoryAverage_Num)+"MB";
                String text5 = "后台时，最高Memory：" + gtrAnalysisResult.backMemoryMax+"MB";
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2).append("\n")
                        .append(text3).append("\n")
                        .append(text4).append("\n")
                        .append(text5)
                        .toString()
                );
            }
        });
    }

    private void initFlowView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("流量");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新折线图数据：
                scrollLineChartView.setX_name("时间(s)");
                scrollLineChartView.setY_name("流量(KB/s)");
                scrollLineChartView.setY_minValue(0.0);
                scrollLineChartView.setY_maxValue(10.0);
                scrollLineChartView.setX_capacity(20);//显示最近20*3秒的数据
                scrollLineChartView.setDatas(gtrAnalysisResult.allFlowChartDatas);
                //刷新结果数据：
                String text1 = "当前Flow：" + gtrAnalysisResult.nowFlow/1024+"KB";
                String text2 = "前台时，总上行Flow：" + gtrAnalysisResult.frontFlowUpload/1024+"KB";
                String text3 = "前台时，总下行Flow：" + gtrAnalysisResult.frontFlowDownload/1024+"KB";
                String text4 = "后台时，总上行Flow：" + gtrAnalysisResult.backFlowUpload/1024+"KB";
                String text5 = "后台时，总下行Flow：" + gtrAnalysisResult.backFlowDownload/1024+"KB";
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2).append("\n")
                        .append(text3).append("\n")
                        .append(text4).append("\n")
                        .append(text5)
                        .toString()
                );
            }
        });
    }

    private void initSMView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("流畅值");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新折线图数据：
                scrollLineChartView.setX_name("时间(s)");
                scrollLineChartView.setY_name("SM(帧/s)");
                scrollLineChartView.setY_minValue(0.0);
                scrollLineChartView.setY_maxValue(60.0);
                scrollLineChartView.setX_capacity(60);//显示最近60*01秒的数据
                scrollLineChartView.setDatas(gtrAnalysisResult.allSMChartDatas);
                //刷新结果数据：
                String text1 = "当前SM：" + gtrAnalysisResult.nowSM;
                resultTextView.setText(new StringBuilder()
                        .append(text1)
                        .toString()
                );
            }
        });
    }











}
