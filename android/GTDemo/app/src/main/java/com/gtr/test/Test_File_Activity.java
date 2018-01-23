package com.gtr.test;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.wstt.gtr.sdkdemo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test_File_Activity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_file);

        write();
    }




    void write(){
        File file = new File("sdcard", "iohook.txt");
        try {
            file.createNewFile(); // 创建文件
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 向文件写入内容(输出流)
        String str = "亲爱的小南瓜！";
        byte bt[] = new byte[1024];
        bt = str.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(file);
            try {
                in.write(bt, 0, bt.length);
                in.close();
                // boolean success=true;
                // System.out.println("写入文件成功");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
