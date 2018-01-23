package com.tencent.wstt.gt.datatool;

import com.tencent.wstt.gt.datatool.obj.AppInfo;
import com.tencent.wstt.gt.datatool.obj.DeviceInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class GTRDataReader {

    public static final String separator = "_&&GTR&_";
    public static final String separatorFile = "_&&GTRFile&_";
    public static final String deviceFileName = "device.txt";
    public static final String appFileName = "app.txt";


    private String dataDirPath = null;
    //Device信息和APP信息
    private DeviceInfo deviceInfo = null;
    private AppInfo appInfo = null;
    //数据文件的读取流（多个文件）和当前行的数据
    HashMap<BufferedReader, String[]> fileBufferedReaders = new HashMap<>();

    /**
     * 构造函数，配置数据目录，并初始化文件读取流
     *
     * @param dataDirPath
     */
    public GTRDataReader(String dataDirPath) throws Exception {
        this.dataDirPath = dataDirPath;
        if (dataDirPath == null) {
            throw new Exception("error : dataDirPath is null");
        }
        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            throw new Exception("the dataDir is not exists");
        }
//		// 读取Device信息和APP信息文件
//		File deviceFile = new File(dataDirPath,deviceFileName);
//		if (!deviceFile.exists()) {
//			throw new Exception("the device.txt file is not exists");
//		}else{
//			deviceInfo = readDeviceFile(deviceFile);
//		}
//		File appFile = new File(dataDirPath,appFileName);
//		if (!appFile.exists()) {
//			throw new Exception("the app.txt file is not exists");
//		}else{
//			appInfo = readAppFile(appFile);
//		}
        // 初始化数据文件读取流
        File[] files = dataDir.listFiles();
        for (File file : files) {
            String[] nameSplit = file.getName().split("_|\\.");
            int pid = -1;
            try {
                if (nameSplit != null && nameSplit.length > 1) {
                    pid = Integer.parseInt(nameSplit[0]);
                }
            } catch (Exception e) {
            }
            if (!file.getName().equals(deviceFileName)                    //数据文件不包括deviceFile
                    && !file.getName().equals(appFileName)                //数据文件不包括appFile
                    && pid != -1                                            //数据文件必须以进程号开头
                    && nameSplit[nameSplit.length - 1].equals("txt")) {    //数据文件是txt格式
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                String data = bufferedReader.readLine();
                if (data != null) {
                    fileBufferedReaders.put(bufferedReader, data.split(separator));
                }
            }
        }

    }


    /**
     * 读取device信息
     *
     * @param deviceFile
     * @return
     * @throws Exception
     */
    private DeviceInfo readDeviceFile(File deviceFile) throws Exception {

        //TODO 如果数据异常可以直接抛出

        return null;
    }

    /**
     * 读取APP信息
     *
     * @param appFile
     * @return
     * @throws Exception
     */
    private AppInfo readAppFile(File appFile) throws Exception {

        //TODO 如果数据异常可以直接抛出

        return null;
    }


    /**
     * 取下一条数据（按每行的ID顺序）
     *
     * @return
     */
    public String[] readData() {
        //取到当前数据ID最小的那个bufferReader
        long minDataID = -1;
        String[] minData = null;
        BufferedReader minDataReader = null;
        for (BufferedReader bufferedReader : fileBufferedReaders.keySet()) {
            long dataId = Long.parseLong(fileBufferedReaders.get(bufferedReader)[0]);
            if (minDataID == -1 || dataId < minDataID) {
                minDataReader = bufferedReader;
                minData = fileBufferedReaders.get(bufferedReader);
            }
        }
        //将bufferReader读取下一条数据
        if (minDataReader != null) {
            String newData = null;
            try {
                newData = minDataReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (newData != null) {
                fileBufferedReaders.put(minDataReader, newData.split(separator));
            } else {
                fileBufferedReaders.remove(minDataReader);
            }
        }
        return minData;
    }

}
