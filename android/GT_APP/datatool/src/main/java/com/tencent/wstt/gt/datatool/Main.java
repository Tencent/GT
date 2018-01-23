package com.tencent.wstt.gt.datatool;

import com.alibaba.fastjson.JSON;
import com.tencent.wstt.gt.datatool.util.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Main {


    //获取数据目录：
    //private static File nowDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    private static File nowDir = new File("E:\\4.Projects\\AndroidStudio\\GTR\\gtrAnalysisHtml\\gtr");

    public static final String dataDirPath = new File(nowDir, "data").getAbsolutePath();
    public static final String demoDirPath = new File(nowDir, "demo").getAbsolutePath();
    public static final String resultDirPath = new File(nowDir, "result").getAbsolutePath();
    public static final String resultDataFilePath = resultDirPath + "/data/data.js";


    public static final ArrayList<GTRAnalysis> dataAnalysises = new ArrayList<>();

    public static void main(String[] args) throws Exception {


        //GTRAnalysis：读取数据文件，装载数据对象
        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            throw new Exception("dataDirPath is not exists :" + dataDirPath);
        }
        File[] dataFiles = dataDir.listFiles();
        for (File temp : dataFiles) {
            if (temp.getName().endsWith(".txt")) {
                GTRAnalysis gtrAnalysis = getGTRAnalysis(temp.getAbsolutePath());
                dataAnalysises.add(gtrAnalysis);
            }
        }

        //GTRDataHandler：将对象数据转化为HTML数据
        GTRAnalysis gtrAnalysis = dataAnalysises.get(0);//TODO 暂时只分析第一个文件
        FileUtil.copyDirectory(demoDirPath, resultDirPath, true);//拷贝HTML库文件


        appendHtmlData("");
        appendHtmlData("");
        appendHtmlData("");
        appendHtmlData("var appInfo = " + JSON.toJSONString(gtrAnalysis.getAppInfo()) + ";");
        appendHtmlData("var deviceInfo = " + JSON.toJSONString(gtrAnalysis.getDeviceInfo()) + ";");
        appendHtmlData("var frames = " + JSON.toJSONString(gtrAnalysis.getFrames()) + ";");
        appendHtmlData("var normalInfos = " + JSON.toJSONString(gtrAnalysis.getNormalInfos()) + ";");
        appendHtmlData("var gtrThreadInfos = " + JSON.toJSONString(gtrAnalysis.getGtrThreadInfos()) + ";");
        appendHtmlData("var frontBackStates = " + JSON.toJSONString(gtrAnalysis.getFrontBackStates()) + ";");
        appendHtmlData("var frontBackInfo = " + JSON.toJSONString(gtrAnalysis.getFrontBackInfo()) + ";");
        appendHtmlData("var lowSMInfos = " + JSON.toJSONString(gtrAnalysis.getLowSMInfos()) + ";");
        appendHtmlData("var allBlockInfos = " + JSON.toJSONString(gtrAnalysis.getAllBlockInfos()) + ";");
        appendHtmlData("var bigBlockIDs = " + JSON.toJSONString(gtrAnalysis.getBigBlockIDs()) + ";");
        appendHtmlData("var pageLoadInfos = " + JSON.toJSONString(gtrAnalysis.getPageLoadInfos()) + ";");
        appendHtmlData("var overActivityInfos = " + JSON.toJSONString(gtrAnalysis.getOverActivityInfos()) + ";");
        appendHtmlData("var overViewDraws = " + JSON.toJSONString(gtrAnalysis.getOverViewDraws()) + ";");
        appendHtmlData("var operationInfos = " + JSON.toJSONString(gtrAnalysis.getOperationInfos()) + ";");
        appendHtmlData("var viewBuildInfos = " + JSON.toJSONString(gtrAnalysis.getViewBuildInfos()) + ";");
        appendHtmlData("var overViewBuilds = " + JSON.toJSONString(gtrAnalysis.getOverViewBuilds()) + ";");
        appendHtmlData("var fragmentInfos = " + JSON.toJSONString(gtrAnalysis.getFragmentInfos()) + ";");
        appendHtmlData("var overFragments = " + JSON.toJSONString(gtrAnalysis.getOverFragments()) + ";");
        appendHtmlData("var allGCInfos = " + JSON.toJSONString(gtrAnalysis.getAllGCInfos()) + ";");
        appendHtmlData("var explicitGCs = " + JSON.toJSONString(gtrAnalysis.getExplicitGCs()) + ";");
        appendHtmlData("var diskIOInfos = " + JSON.toJSONString(gtrAnalysis.getDiskIOInfos()) + ";");
        appendHtmlData("var fileActionInfos = " + JSON.toJSONString(gtrAnalysis.getFileActionInfos()) + ";");
        appendHtmlData("var fileActionInfosInMainThread = " + JSON.toJSONString(gtrAnalysis.getFileActionInfosInMainThread()) + ";");
        appendHtmlData("var dbActionInfos = " + JSON.toJSONString(gtrAnalysis.getDbActionInfos()) + ";");
        appendHtmlData("var dbActionInfosInMainThread = " + JSON.toJSONString(gtrAnalysis.getDbActionInfosInMainThread()) + ";");
        appendHtmlData("var logInfos = " + JSON.toJSONString(gtrAnalysis.getLogInfos()) + ";");

        appendHtmlData("");
        appendHtmlData("");
        appendHtmlData("");


        appendHtmlData("//基础性能");
        appendHtmlData("var tableBaseData_base= frontBackInfo;");
        appendHtmlData("//卡顿检测");
        appendHtmlData("var tableBaseData_lowSM = lowSMInfos;");
        appendHtmlData("var tableBaseData_bigBlock = bigBlockIDs;");
        appendHtmlData("//页面测速");
        appendHtmlData("var tableBaseData_overActivity = overActivityInfos;");
        appendHtmlData("var tableBaseData_allPage = pageLoadInfos;");
        appendHtmlData("//Fragment测速");
        appendHtmlData("var tableBaseData_overFragment = overFragments;");
        appendHtmlData("var tableBaseData_allFragment = fragmentInfos;");
        appendHtmlData("//布局检测");
        appendHtmlData("var tableBaseData_overViewBuild = overViewBuilds;");
        appendHtmlData("var tableBaseData_overViewDraw = overViewDraws;");
        appendHtmlData("//GC检测");
        appendHtmlData("var tableBaseData_explicitGC = explicitGCs;");
        appendHtmlData("//IO检测");
        appendHtmlData("var tableBaseData_fileActionInMainThread = fileActionInfosInMainThread;");
        appendHtmlData("var tableBaseData_dbActionInMainThread = dbActionInfosInMainThread;");
        appendHtmlData("var tableBaseData_db = dbActionInfos;");
        appendHtmlData("//关键日志");
        appendHtmlData("var tableBaseData_logcat = logInfos;");


//        //基础信息：
//        appendHtmlData("var normalInfoArray = " + JSON.toJSONString(gtrAnalysis.getNormalInfos()) + ";");
//        appendHtmlData("var operationInfoArray = " + JSON.toJSONString(gtrAnalysis.getOperationInfos()) + ";");
//        appendHtmlData("var viewBuildInfoArray = " + JSON.toJSONString(gtrAnalysis.getViewBuildInfos()) + ";");
//        appendHtmlData("var screenStateArray = " + JSON.toJSONString(gtrAnalysis.getScreenStates()) + ";");
//        appendHtmlData("var frontBackStateArray = " + JSON.toJSONString(gtrAnalysis.getFrontBackStates()) + ";");

//        appendHtmlData("var baseSummaryChart_data = " + GTRDataHandler.get_baseSummaryChart_data(gtrAnalysis) + ";");
//        appendHtmlData("var blockLowSMTable_data = " + GTRDataHandler.get_blockLowSMTable_data(gtrAnalysis) + ";");
//        appendHtmlData("var blockBigTable_data = " + GTRDataHandler.get_blockBigTable_data(gtrAnalysis) + ";");
//        appendHtmlData("var pageLoadOverTable_data = " + GTRDataHandler.get_pageLoadOverTable_data(gtrAnalysis) + ";");
//        appendHtmlData("var pageLoadAllTable_data = " + GTRDataHandler.get_pageLoadAllTable_data(gtrAnalysis) + ";");
//        appendHtmlData("var ioAllFileTable_data = " + GTRDataHandler.get_ioAllFileTable_data(diskIOInfos, appInfo) + ";");


        System.out.println("数据报告已生成：" + resultDirPath);


    }

    public static final String separator = "_&&GTR&_";
    public static final String separatorFile = "_&&GTRFile&_";
    public static final String gtClearDataFlag = "--gtClearData---";

    static GTRAnalysis getGTRAnalysis(String dataFilePath) throws Exception {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            throw new Exception("dataFilePath is not exists:" + dataFilePath);
        }
        GTRAnalysis gtrAnalysis = new GTRAnalysis();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String dataLine = bufferedReader.readLine();
        while (dataLine != null) {
            try {
                if (dataLine.length() > 0) {
                    if (dataLine.startsWith(gtClearDataFlag)) {
                        gtrAnalysis.clear();
                    } else {
                        gtrAnalysis.distribute(dataLine.split(separator));
                    }
                }
            } catch (Exception e) {
                System.out.println("ErrorData:" + dataLine);
                e.printStackTrace();
            }

            dataLine = bufferedReader.readLine();
        }
        return gtrAnalysis;
    }


    /**
     * 以追加的形式写入HTML数据
     */
    static BufferedWriter bufferedWriter;

    static void appendHtmlData(String data) throws IOException {
        if (bufferedWriter == null) {
            File resultDataFile = new File(resultDataFilePath);
            if (resultDataFile.exists()) {
                resultDataFile.delete();
            }
            resultDataFile.getParentFile().mkdirs();
            resultDataFile.createNewFile();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultDataFile, true), "utf-8"));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(data).append("\n");
        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.flush();
    }


}
