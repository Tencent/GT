package com.gtr.test;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by p_hongjcong on 2017/7/20.
 */

public class TestFileOperation {

    public  void  test(){
        File file = new File("/sdcard/dd.txt");
        if (!file.exists()){
            file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DDDDDDDDDDDDDDD\n");
            bufferedWriter.write(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
