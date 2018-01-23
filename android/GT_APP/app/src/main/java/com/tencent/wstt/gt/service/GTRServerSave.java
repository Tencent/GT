package com.tencent.wstt.gt.service;

import com.tencent.wstt.gt.GTConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 数据磁盘写入器
 * 保存目录：/sdcard/pdm/data/包名_开始时间/进程ID.txt
 * Created by p_hongjcong on 2017/7/12.
 */

public class GTRServerSave {



    //bufferedWriter缓存列表，防止一直做文件打开操作
    private static HashMap<String,BufferedWriter> bufferedWriters = new HashMap<>();


    /**
     * 写入数据
     * @param packageName
     * @param startTestTime
     * @param pid
     * @param data
     * @throws Exception
     */
    public static void saveData(String packageName,long startTestTime,int pid,String data) throws Exception {
        if (packageName==null){
            throw new Exception("packageName is null");
        }
        BufferedWriter bufferedWriter = initBufferedWriter(packageName,startTestTime,pid);
        try {
            bufferedWriter.write("\n"+data);
            bufferedWriter.flush();
        }catch (Exception e){
            //如果写失败，可能是由于文件被删除导致的，我们重新初始化bufferedWriter
            addNewBufferedWriter(packageName,startTestTime,pid);
            bufferedWriter = initBufferedWriter(packageName,startTestTime,pid);
            bufferedWriter.write("\n"+data);
            bufferedWriter.flush();
        }
    }


    /**
     * 初始化BufferedWriter
     * @param packageName
     * @param startTestTime
     * @param pid
     * @return
     * @throws IOException
     */
    public static BufferedWriter initBufferedWriter(String packageName, long startTestTime, int pid) throws IOException {
        String key = packageName+startTestTime+pid;
        BufferedWriter bufferedWriter = bufferedWriters.get(key);
        if (bufferedWriter==null){
            addNewBufferedWriter(packageName,startTestTime,pid);
        }
        bufferedWriter = bufferedWriters.get(key);
        return bufferedWriter;
    }


    public static void addNewBufferedWriter(String packageName, long startTestTime, int pid) throws IOException {
        String key = packageName+startTestTime+pid;
        //创建文件
        File dataFile = new File(getSaveFilePath( packageName, startTestTime, pid));
        dataFile.getParentFile().mkdirs();
        dataFile.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile, true),"utf-8"));
        bufferedWriters.put(key,bufferedWriter);
    }


    public static String getSaveFilePath(String packageName, long startTestTime, int pid){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
        Date curDate = new Date(startTestTime);//获取当前时间
        String dateStr = dateFormat.format(curDate);
        String dataFilePath = GTConfig.gtrDirPath+packageName+"_"+dateStr+"/"+pid+".txt";
        return dataFilePath;
    }



}
