package com.gtr.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wstt.gt.client.AbsGTParaLoader;
import com.tencent.wstt.gt.client.GT;
import com.tencent.wstt.gt.client.InParaManager;
import com.tencent.wstt.gt.client.OutParaManager;
import com.tencent.wstt.gtr.sdkdemo.R;

/**
 * Created by p_guilfu on 2018/1/15.
 */

public class Test_Input_Output_Activity extends Activity implements UserStrings {

    private String explanation_connect = "点击下方\"Connect GT\"连接到GT控制台";

    private String explanation_intodemo = "0、点击下方\"Into Demo\"进入demo" + "\n" +
            "1、demo演示了从网络下载10张图片并显示到界面上的简单功能" + "\n" +
            "2、连接方式、线程数到底该设置多少？可以通过“输入参数”实时调整尝试" + "\n" +
            "3、设置的值是否更合理？可以通过“输出参数”在悬浮窗中实时观察结果" + "\n" +
            "4、设置的值对性能消耗如何？可以通过GT控制台的Profiler界面实时展示性能统计数据（线程中、线程间耗时）";

    private String explanation_disconnect = "点击下方\"Disconnect GT\"断开与GT控制台的连接（会清空在控制台中的输入输出参数）";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_input_output);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv_connect = (TextView)findViewById(R.id.tv_connect);
        tv_connect.setText(explanation_connect);
        Button btn_connect = (Button)findViewById(R.id.connect);
        btn_connect.setOnClickListener(connect);

        TextView tv_intodemo = (TextView)findViewById(R.id.tv_intodemo);
        tv_intodemo.setText(explanation_intodemo);
        Button btn_intodemo = (Button)findViewById(R.id.intodemo);
        btn_intodemo.setOnClickListener(intodemo);

        TextView tv_disconnect = (TextView)findViewById(R.id.tv_disconnect);
        tv_disconnect.setText(explanation_disconnect);
        Button btn_disconnect = (Button)findViewById(R.id.disconnect);
        btn_disconnect.setOnClickListener(disconnect);
    }

    /*
     * connect按钮按下后，会和GT控制台服务进行连接；
     * 如果GT控制台应用尚未启动，则会在连接过程中被启动；
     * 如果GT控制台已启动，并且没有与其他被测应用程序连接，则会成功与本应用连接上。
     */
    OnClickListener connect = new OnClickListener() {
        @Override
        public void onClick(View v) {
			/*
			 *  GT usage
			 * 与GT控制台连接，同时注册输入输出参数
			 */
            GT.connect(getApplicationContext(), new AbsGTParaLoader() {

                @Override
                public void loadInParas(InParaManager inPara) {
					/*
					 * 注册输入参数，将在GT控制台上按顺序显示
					 */
                    inPara.register(并发线程数, "TN", "1", "2", "3");
                    inPara.register(KeepAlive, "KA", "false", "true");
                    inPara.register(读超时, "超时", "5000", "10000","1000");
                    inPara.register(连接超时, "连超时", "5000", "10000","1000");

                    // 定义默认显示在GT悬浮窗的3个输入参数
                    inPara.defaultInParasInAC(并发线程数, KeepAlive, 读超时);

                    // 设置默认无效的一个入参（GT1.1支持）
                    inPara.defaultInParasInDisableArea(连接超时);
                }

                @Override
                public void loadOutParas(OutParaManager outPara) {
					/*
					 * 注册输出参数，将在GT控制台上按顺序显示
					 */
                    outPara.register(下载耗时, "耗时", false, "ms");
                    outPara.register(实际带宽, "带宽", false, "KB/s");
                    outPara.register(singlePicSpeed, "SSPD", false, "KB/s");
                    outPara.register(NumberOfDownloadedPics, "NDP");

                    // 定义默认显示在GT悬浮窗的3个输出参数
                    outPara.defaultOutParasInAC(下载耗时, 实际带宽, singlePicSpeed);
                }
            });

            // 默认在GT一连接后就展示悬浮窗（GT1.1支持）
            GT.setFloatViewFront(true);

            // 默认打开性能统计开关（GT1.1支持）
            GT.setProfilerEnable(true);

            Toast.makeText(Test_Input_Output_Activity.this, getString(R.string.connect_gt), Toast.LENGTH_SHORT).show();
        }
    };

    /*
     * intodemo按钮按下后，会进入本demo的正式功能页面。
     */
    OnClickListener intodemo = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Test_Input_Output_Activity.this, GTDemoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    /*
     * disconnect按钮按下后，会立即与GT控制台断开连接，同时关闭悬浮窗。
     */
    OnClickListener disconnect = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // GT usage
            GT.setFloatViewFront(false);
            GT.disconnect(getApplicationContext());

            Toast.makeText(Test_Input_Output_Activity.this, getString(R.string.disconnect_gt), Toast.LENGTH_SHORT).show();
//            Test_Input_Output_Activity.this.finish();
//            System.exit(0);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Test_Input_Output_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}