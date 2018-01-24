package com.tencent.wstt.gt.analysis4;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.datatool.GTRAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/**
 * Created by p_guilfu on 2017/12/11.
 */

public class GTRDataToJsManager {
    private static final int BUFFER_SIZE = 1 << 20;

    public static final String resultDataFilePath = Env.GTR_DATAJS_PATH_NAME;
    public static final String getResultDataFilePath = Env.GTR_WX_DATAJS_PATH_NAME;
    private static GTRAnalysis gtrAnalysis = null;
    private static boolean isWX = false;

    public static Boolean toAnalysis(String dataDirPath, boolean b) throws Exception {
        isWX = b;
        long startTime0 = System.currentTimeMillis();
        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            throw new Exception("dataDirPath is not exists :" + dataDirPath);
        }

        File[] dataFiles = dataDir.listFiles();
        for (File temp : dataFiles) {
            if (temp.getName().endsWith(".txt")) {
                // 解析数据
                gtrAnalysis = getGTRAnalysis(temp.getAbsolutePath());
                long startTime = System.currentTimeMillis();
                Log.i("adam", " 序列化数据时间 =" + (startTime - startTime0) + "ms");
            }
        }

        long startTime1 = System.currentTimeMillis();

//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("\n\n\nvar appInfo = " + JSON.toJSONString(gtrAnalysis.getAppInfo()) + ";\n")
//                .append("var deviceInfo = " + JSON.toJSONString(gtrAnalysis.getDeviceInfo()) + ";\n")
//                .append("var frames = " + JSON.toJSONString(gtrAnalysis.getFrames()) + ";\n")
//                .append("var normalInfos = " + JSON.toJSONString(gtrAnalysis.getNormalInfos()) + ";\n")
//                .append("var gtrThreadInfos = " + JSON.toJSONString(gtrAnalysis.getGtrThreadInfos()) + ";\n")
//                .append("var frontBackStates = " + JSON.toJSONString(gtrAnalysis.getFrontBackStates()) + ";\n")
//                .append("var frontBackInfo = " + JSON.toJSONString(gtrAnalysis.getFrontBackInfo()) + ";\n")
//                .append("var lowSMInfos = " + JSON.toJSONString(gtrAnalysis.getLowSMInfos()) + ";\n")
//                .append("var allBlockInfos = " + JSON.toJSONString(gtrAnalysis.getAllBlockInfos()) + ";\n")
//                .append("var bigBlockIDs = " + JSON.toJSONString(gtrAnalysis.getBigBlockIDs()) + ";\n")

//                .append("var pageLoadInfos = " + JSON.toJSONString(gtrAnalysis.getPageLoadInfos()) + ";\n")
//                .append("var overActivityInfos = " + JSON.toJSONString(gtrAnalysis.getOverActivityInfos()) + ";\n")
//                .append("var overViewDraws = " + JSON.toJSONString(gtrAnalysis.getOverViewDraws()) + ";\n")
//                .append("var operationInfos = " + JSON.toJSONString(gtrAnalysis.getOperationInfos()) + ";\n")
//                .append("var viewBuildInfos = " + JSON.toJSONString(gtrAnalysis.getViewBuildInfos()) + ";\n")
//                .append("var overViewBuilds = " + JSON.toJSONString(gtrAnalysis.getOverViewBuilds()) + ";\n")
//                .append("var fragmentInfos = " + JSON.toJSONString(gtrAnalysis.getFragmentInfos()) + ";\n")
//                .append("var overFragments = " + JSON.toJSONString(gtrAnalysis.getOverFragments()) + ";\n")
//                .append("var allGCInfos = " + JSON.toJSONString(gtrAnalysis.getAllGCInfos()) + ";\n")
//                .append("var explicitGCs = " + JSON.toJSONString(gtrAnalysis.getExplicitGCs()) + ";\n")

//                .append("var diskIOInfos = " + JSON.toJSONString(gtrAnalysis.getDiskIOInfos()) + ";\n")
//                .append("var fileActionInfos = " + JSON.toJSONString(gtrAnalysis.getFileActionInfos()) + ";\n")
//                .append("var fileActionInfosInMainThread = " + JSON.toJSONString(gtrAnalysis.getFileActionInfosInMainThread()) + ";\n")
//                .append("var dbActionInfos = " + JSON.toJSONString(gtrAnalysis.getDbActionInfos()) + ";\n")
//                .append("var dbActionInfosInMainThread = " + JSON.toJSONString(gtrAnalysis.getDbActionInfosInMainThread()) + ";\n")
//                .append("var logInfos = " + JSON.toJSONString(gtrAnalysis.getLogInfos()) + ";\n")
//                .append("var flagInfo = " + JSON.toJSONString(gtrAnalysis.getFlagList()) + ";\n\n\n\n")
//                .append("//基础性能\nvar tableBaseData_base= frontBackInfo;\n//卡顿检测\nvar tableBaseData_lowSM = lowSMInfos;\nvar tableBaseData_bigBlock = bigBlockIDs;\n" +
//                        "//页面测速\nvar tableBaseData_overActivity = overActivityInfos;\nvar tableBaseData_allPage = pageLoadInfos;\n" +
//                        "//Fragment测速\nvar tableBaseData_overFragment = overFragments;\nvar tableBaseData_allFragment = fragmentInfos;\n" +
//                        "//布局检测\nvar tableBaseData_overViewBuild = overViewBuilds;\nvar tableBaseData_overViewDraw = overViewDraws;\n" +
//                        "//GC检测\nvar tableBaseData_explicitGC = explicitGCs;\n" +
//                        "//IO检测\nvar tableBaseData_fileActionInMainThread = fileActionInfosInMainThread;\nvar tableBaseData_dbActionInMainThread = dbActionInfosInMainThread;\nvar tableBaseData_db = dbActionInfos;\n" +
//                        "//关键日志\nvar tableBaseData_logcat = logInfos;\n");
//        appendDataJs(stringBuilder.toString());

        toDataJs();

        long startTime2 = System.currentTimeMillis();
        Log.i("adam", "写入数据时间 =" + (startTime2 - startTime1) + "ms");

        return true;
    }

    public static GTRAnalysis getGTRAnalysis(String dataFilePath) throws Exception {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            throw new Exception("dataFilePath is not exists:" + dataFilePath);
        } else {
            GTRAnalysis gtrAnalysis = new GTRAnalysis();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

            for (String dataLine = bufferedReader.readLine(); dataLine != null; dataLine = bufferedReader.readLine()) {
                try {
                    if (dataLine.length() > 0) {
                        gtrAnalysis.distribute(dataLine.split(GTConfig.separator));
                    }
                } catch (Exception e) {
                    System.out.println("ErrorData:" + dataLine);
                    e.printStackTrace();
                }
            }

            return gtrAnalysis;
        }
    }

    private static void appendDataJs(String data) {
        BufferedWriter bufferedWriter = null;

        try {
            File resultDataFile = new File(resultDataFilePath);
            if (isWX) {
                resultDataFile = new File(getResultDataFilePath);
            }

            if (resultDataFile.exists()) {
                resultDataFile.delete();
            }
            resultDataFile.getParentFile().mkdirs();
            resultDataFile.createNewFile();

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultDataFile, true), "utf-8"));
            bufferedWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void appendJSList(BufferedWriter writer, String varName, ArrayList list) throws IOException {
        appendVariableName(writer, varName);
        appendList(writer, list);
        writer.write(";\n");
    }

    private static void appendList(BufferedWriter writer, ArrayList list) throws IOException {
        writer.write("[");

        if (!list.isEmpty()) {
            writer.write(JSON.toJSONString(list.get(0)));

            for (int i = 1; i < list.size(); i++) {
                writer.write(",");
                writer.write(JSON.toJSONString(list.get(i)));
            }

            list.clear();
        }

        writer.write("]");
    }

    private static void appendJSObject(BufferedWriter writer, String varName, Object obj) throws IOException {
        appendVariableName(writer, varName);
        writer.write(JSON.toJSONString(obj));
        writer.write(";\n");
    }

    private static void appendRawString(BufferedWriter writer, String data) throws IOException {
        writer.write(data);
        writer.write(";\n");
    }

    private static void appendVariableName(BufferedWriter writer, String varName) throws IOException {
        writer.write("var ");
        writer.write(varName);
        writer.write("=");
    }

    private static void toDataJs(File des) {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(des), BUFFER_SIZE);
            writer.write("\n\n\n");

            appendJSObject(writer, "appInfo", gtrAnalysis.getAppInfo());
            appendJSObject(writer, "deviceInfo", gtrAnalysis.getDeviceInfo());
            appendJSList(writer, "frames", gtrAnalysis.getFrames());
            appendJSList(writer, "normalInfos", gtrAnalysis.getNormalInfos());
            appendJSList(writer, "gtrThreadInfos", gtrAnalysis.getGtrThreadInfos());

            appendJSList(writer, "frontBackStates", gtrAnalysis.getFrontBackStates());
            appendJSObject(writer, "frontBackInfo", gtrAnalysis.getFrontBackInfo());
            appendJSList(writer, "lowSMInfos", gtrAnalysis.getLowSMInfos());
            appendJSList(writer, "allBlockInfos", gtrAnalysis.getAllBlockInfos());
            appendJSList(writer, "bigBlockIDs", gtrAnalysis.getBigBlockIDs());

            appendJSList(writer, "pageLoadInfos", gtrAnalysis.getPageLoadInfos());
            appendJSList(writer, "overActivityInfos", gtrAnalysis.getOverActivityInfos());
            appendJSList(writer, "overViewDraws", gtrAnalysis.getOverViewDraws());
            appendJSList(writer, "operationInfos", gtrAnalysis.getOperationInfos());
            appendJSList(writer, "viewBuildInfos", gtrAnalysis.getViewBuildInfos());

            appendJSList(writer, "overViewBuilds", gtrAnalysis.getOverViewBuilds());
            appendJSList(writer, "fragmentInfos", gtrAnalysis.getFragmentInfos());
            appendJSList(writer, "overFragments", gtrAnalysis.getOverFragments());
            appendJSList(writer, "allGCInfos", gtrAnalysis.getAllGCInfos());
            appendJSList(writer, "explicitGCs", gtrAnalysis.getExplicitGCs());

            appendJSList(writer, "diskIOInfos", gtrAnalysis.getDiskIOInfos());
            appendJSList(writer, "fileActionInfos", gtrAnalysis.getFileActionInfos());
            appendJSList(writer, "fileActionInfosInMainThread", gtrAnalysis.getFileActionInfosInMainThread());
            appendJSList(writer, "dbActionInfos", gtrAnalysis.getDbActionInfos());

            appendJSList(writer, "dbActionInfosInMainThread", gtrAnalysis.getDbActionInfosInMainThread());
            appendJSList(writer, "logInfos", gtrAnalysis.getLogInfos());
            appendJSList(writer, "flagInfo", gtrAnalysis.getFlagList());

            writer.write("\n\n\n");

            String data = "//基础性能\nvar tableBaseData_base= frontBackInfo;\n" +
                    "//卡顿检测\nvar tableBaseData_lowSM = lowSMInfos;\n" +
                    "var tableBaseData_bigBlock = bigBlockIDs;\n" +
                    "//页面测速\nvar tableBaseData_overActivity = overActivityInfos;\nvar tableBaseData_allPage = pageLoadInfos;\n" +
                    "//Fragment测速\nvar tableBaseData_overFragment = overFragments;\nvar tableBaseData_allFragment = fragmentInfos;\n" +
                    "//布局检测\nvar tableBaseData_overViewBuild = overViewBuilds;\nvar tableBaseData_overViewDraw = overViewDraws;\n" +
                    "//GC检测\nvar tableBaseData_explicitGC = explicitGCs;\n" +
                    "//IO检测\nvar tableBaseData_fileActionInMainThread = fileActionInfosInMainThread;\n" +
                    "var tableBaseData_dbActionInMainThread = dbActionInfosInMainThread;\nvar tableBaseData_db = dbActionInfos;\n" +
                    "//关键日志\nvar tableBaseData_logcat = logInfos;\n";
            appendRawString(writer, data);

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer == null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void toDataJs() {
        File des = isWX ? new File(getResultDataFilePath)
                : new File(resultDataFilePath);
        if (toCreateFileDir(des)) {
            toDataJs(des);
        } else {
            System.out.println("ErrorData: 文件创建失败，请开启系统读写权限后重试");
        }
    }
    
    private static boolean toCreateFileDir(File des) {
        try {
            if (des.exists()) {
                des.delete();
            }
            des.getParentFile().mkdirs();
            des.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
