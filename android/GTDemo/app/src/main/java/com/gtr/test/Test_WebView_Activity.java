package com.gtr.test;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

import com.tencent.wstt.gtr.sdkdemo.R;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.net.ssl.HttpsURLConnection;

public class Test_WebView_Activity extends Activity {



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webview);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Log.e("vhread","onCreate");
        HandlerThread handlerThread = new HandlerThread("sdasdad");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int a = 6;
                Log.e("vhread","sdsdsdsdsd"+a);

            }
        });


        Thread thread = new Thread("DDDDDD"){
            @Override
            public void run() {
                int a = 6;
                Log.e("vhread","DDDDDD"+a);
                super.run();
            }
        };
        thread.start();



        //WebView 测试
        WebView webView = (WebView) findViewById(R.id.wwww);
        webView.loadUrl("http://www.cnblogs.com/ITtangtang/p/3920916.html");

        //URLConnection 测试
        new Thread(){
            @Override
            public void run() {
                URLStreamHandler urlStreamHandler = new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL u) throws IOException {
                        return null;
                    }
                };
                try {
                    URL url = new URL("https://www.cnblogs.com/ITtangtang/p/3920916.html");
                    URLConnection rulConnection = url.openConnection();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) rulConnection;
                    httpsURLConnection.connect();
                    OutputStream outStrm = httpsURLConnection.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(outStrm);
                    oos.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Test_WebView_Activity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
