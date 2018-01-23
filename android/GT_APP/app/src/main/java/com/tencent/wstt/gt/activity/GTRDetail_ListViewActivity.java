package com.tencent.wstt.gt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.dao.DetailListData;
import com.tencent.wstt.gt.analysis4.GTRAnalysis;
import com.tencent.wstt.gt.analysis4.GTRAnalysisCallback;
import com.tencent.wstt.gt.analysis4.GTRAnalysisResult;

import java.util.ArrayList;

public class GTRDetail_ListViewActivity extends Activity {





    //标题栏：
    ImageButton backImageView;
    TextView titleTextView;
    ImageButton deleteImageView;
    //List：
    LinearLayout listLayout;
    ListView listView;
    MyAdapter myAdapter;
    //结论表：
    TextView resultTextView;
    //类型：
    public static final int TYPE_Activity = 0;
    public static final int TYPE_Fragment = 1;
    public static final int TYPE_ViewBuild = 2;
    public static final int TYPE_ViewDraw = 3;
    public static final int TYPE_IO = 4;
    public static final int TYPE_GC = 5;
    int type = 0;


    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉页面标题栏
        setContentView(R.layout.gtr_activity_detail_list_view);


        //获取类型：
        Intent intent = this.getIntent();
        type = intent.getIntExtra("type",TYPE_Activity);

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
        //列表：
        listLayout = (LinearLayout) findViewById(R.id.linear_list);
        myAdapter = new MyAdapter(this);
        listView = (ListView) findViewById(R.id.detail_list);
        listView.setAdapter(myAdapter);
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
        public void refreshPageLoadInfo(GTRAnalysisResult gtrAnalysisResult) {
            initView();
        }

        @Override
        public void refreshFragmentInfo(GTRAnalysisResult gtrAnalysisResult) {
            initView();
        }

        @Override
        public void refreshViewBuildInfo(GTRAnalysisResult gtrAnalysisResult) {
            initView();
        }

        @Override
        public void refreshViewDrawInfo(GTRAnalysisResult gtrAnalysisResult) {
            initView();
        }

        @Override
        public void refreshIOInfo(GTRAnalysisResult gtrAnalysisResult) {
            initView();
        }
    };





    void initView(){
        switch (type) {
            case TYPE_Activity:
                initActivityView();
                break;
            case TYPE_Fragment:
                initFragmentView();
                break;
            case TYPE_ViewBuild:
                initViewBuildView();
                break;
            case TYPE_ViewDraw:
                initViewDrawView();
                break;
            case TYPE_IO:
                initIOView();
                break;
            case TYPE_GC:
                initGCView();
                break;
        }
    }

    void initActivityView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("Activity测速");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新列表数据：
                myAdapter.setDatas(gtrAnalysisResult.allActivityListData);
                myAdapter.notifyDataSetChanged();
                //刷新结果数据：
                String text1 = "总页面数：" + gtrAnalysisResult.pageNum;
                String text2 = "超时(>500ms)页面数：" + gtrAnalysisResult.overPageNum;
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2)
                        .toString()
                );
            }
        });
    }

    void initFragmentView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("Fragment测速");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新列表数据：
                myAdapter.setDatas(gtrAnalysisResult.allFragmentListData);
                myAdapter.notifyDataSetChanged();
                //刷新结果数据：
                String text1 = "总Fragment数：" + gtrAnalysisResult.fragmentNum;
                String text2 = "超时(>300ms)Fragment数：" + gtrAnalysisResult.overFragmentNum;
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2)
                        .toString()
                );
            }
        });
    }
    void initViewBuildView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("View构建检测");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新列表数据：
                myAdapter.setDatas(gtrAnalysisResult.allViewBuildListData);
                myAdapter.notifyDataSetChanged();
                //刷新结果数据：
                String text1 = "总共View构建数：" + gtrAnalysisResult.viewBuildNum;
                String text2 = "超时(>10ms)View构建数：" + gtrAnalysisResult.overViewBuildNum;
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2)
                        .toString()
                );
            }
        });
    }

    void initViewDrawView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("View绘制检测");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新列表数据：
                myAdapter.setDatas(gtrAnalysisResult.allViewDrawListData);
                myAdapter.notifyDataSetChanged();
                //刷新结果数据：
                String text1 = "总共View绘制数：" + gtrAnalysisResult.viewDrawNum;
                String text2 = "超时(>5ms)View绘制数：" + gtrAnalysisResult.overViewDrawNum;
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2)
                        .toString()
                );
            }
        });
    }

    void initIOView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("IO检测");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新列表数据：
                myAdapter.setDatas(gtrAnalysisResult.allDBIOListData);
                myAdapter.notifyDataSetChanged();
                //刷新结果数据：
                String text1 = "总共数据库IO次数：" + gtrAnalysisResult.dbIONum;
                String text2 = "主线程数据库IO次数：" + gtrAnalysisResult.mainThreadDBIONum;
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2)
                        .toString()
                );
            }
        });
    }
    void initGCView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("GC检测");
                GTRAnalysisResult gtrAnalysisResult = GTRAnalysis.getGtrAnalysisResult();
                if (gtrAnalysisResult==null){
                    return;
                }
                //刷新列表数据：
                myAdapter.setDatas(gtrAnalysisResult.allGCListData);
                myAdapter.notifyDataSetChanged();
                //刷新结果数据：
                String text1 = "总共GC次数：" + gtrAnalysisResult.gcNum;
                String text2 = "显示GC次数：" + gtrAnalysisResult.explicitGCNum;
                resultTextView.setText(new StringBuilder()
                        .append(text1).append("\n")
                        .append(text2)
                        .toString()
                );
            }
        });
    }

    public static class MyAdapter extends BaseAdapter{

        LayoutInflater inflater;
        ArrayList<DetailListData> datas;


        public MyAdapter(Activity activity){
            inflater = activity.getLayoutInflater();
        }

        public void setDatas(ArrayList<DetailListData> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            if (datas==null){
                return 0;
            }else {
                return datas.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (datas==null){
                return null;
            }else {
                return datas.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = inflater.inflate(R.layout.gtr_item_detail_list,null);
            TextView textView = (TextView) listItem.findViewById(R.id.text_item);
            if (datas!=null && datas.size()>position){
                textView.setText(datas.get(position).string);
                switch (datas.get(position).type){
                    case DetailListData.Error:
                        textView.setTextColor(Color.RED);
                        break;
                    case DetailListData.Warning:
                        textView.setTextColor(Color.YELLOW);
                        break;
                    case DetailListData.Normal:
                        textView.setTextColor(Color.WHITE);
                        break;
                }
            }
            return listItem;
        }
    }







}
