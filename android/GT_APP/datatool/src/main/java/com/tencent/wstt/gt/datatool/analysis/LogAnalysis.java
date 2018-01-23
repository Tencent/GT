package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.obj.LogInfo;
import com.tencent.wstt.gt.datatool.GTRAnalysis;

import java.util.ArrayList;

public class LogAnalysis {


    ArrayList<LogInfo> logInfos;


    IOAnalysis ioAnalysis;
    GCAnalysis gcAnalysis;

    public LogAnalysis(GTRAnalysis gtrAnalysis, ArrayList<LogInfo> logInfos, IOAnalysis ioAnalysis, GCAnalysis gcAnalysis) {
        this.logInfos = logInfos;
        this.ioAnalysis = ioAnalysis;
        this.gcAnalysis = gcAnalysis;
    }

    String[] GRADEKEYS = new String[]{
            "A ", "A/", "E ", "E/", "W ", "W/",
            "I ", "I/", "D ", "D/", "V ", "V/"
    };
    String[] GTRTAGS = new String[]{
            "KHOOK", "Kat", "Elvis", "elvis", "Matt", "matt", "GTR", "GTR_DATA_TAG"
            , "crash lib", "crash path", "zzzzzdddd", "addCallback", "_PDM_"
    };

    public void onCollectLog(String log, long time) {
        //grade:
        int minGradeLocal = log.length();
        for (int i = 0; i < GRADEKEYS.length; i++) {
            if (log.indexOf(GRADEKEYS[i]) != -1 && log.indexOf(GRADEKEYS[i]) < minGradeLocal) {

                minGradeLocal = log.indexOf(GRADEKEYS[i]);
            }
        }
        if (minGradeLocal == log.length()) {
            return;
        }
        String grade = log.substring(minGradeLocal, minGradeLocal + 1);
        //tag:
        String logWithoutGrade = log.substring(minGradeLocal + 2, log.length());
        int tagEndLocal = logWithoutGrade.indexOf(":");
        if (tagEndLocal == -1) {
            return;
        }
        String tag = logWithoutGrade.substring(0, tagEndLocal);
        //logContent
        String logContent = logWithoutGrade.substring(tagEndLocal + 1, logWithoutGrade.length());
        //isGTR
        boolean isGTR = false;
        for (String s : GTRTAGS) {
            if (tag.indexOf(s) != -1) {
                isGTR = true;
            }
        }

        LogInfo logInfo = new LogInfo();
        logInfo.time = time;
        logInfo.grade = grade;
        logInfo.tag = tag;
        logInfo.logContent = logContent;
        logInfo.isGTR = isGTR;
        logInfos.add(logInfo);


        checkIOLog(logInfo);
        checkGCLog(logInfo);

    }


    /**
     * 检测是否为IO模块的数据，送至IOAnalysis处理
     *
     * @param logInfo
     */
    private void checkIOLog(LogInfo logInfo) {
        if (logInfo.tag.indexOf("GTR_DATA_TAG") != -1) {
            String[] data = logInfo.logContent.split(GTRAnalysis.separatorFile);
            int tid;
            int fd;
            int size;
            long timeStart;
            long timeEnd;
            String path;
            if (data[0].indexOf("file_open") != -1) {
                tid = Integer.parseInt(data[1]);
                fd = Integer.parseInt(data[2]);
                path = data[3];
                timeStart = Long.parseLong(data[4]);
                timeEnd = Long.parseLong(data[5]);
                ioAnalysis.onFileOpen(tid, fd, path, timeStart, timeEnd);
            } else if (data[0].indexOf("file_write") != -1 || data[0].indexOf("file_pwrite64") != -1) {
                tid = Integer.parseInt(data[1]);
                fd = Integer.parseInt(data[2]);
                size = Integer.parseInt(data[3]);
                timeStart = Long.parseLong(data[4]);
                timeEnd = Long.parseLong(data[5]);
                ioAnalysis.onFileRead(tid, fd, size, timeStart, timeEnd);
            } else if (data[0].indexOf("file_read") != -1 || data[0].indexOf("file_pread64") != -1) {
                tid = Integer.parseInt(data[1]);
                fd = Integer.parseInt(data[2]);
                size = Integer.parseInt(data[3]);
                timeStart = Long.parseLong(data[4]);
                timeEnd = Long.parseLong(data[5]);
                ioAnalysis.onFileWrite(tid, fd, size, timeStart, timeEnd);
            }
        }
    }

    /**
     * 检测是否为GC模块的数据，送至IOAnalysis处理
     *
     * @param logInfo
     */
    private void checkGCLog(LogInfo logInfo) {
        if ((logInfo.tag.contains("dalvikvm") || logInfo.tag.contains("art"))
                && logInfo.logContent.contains("GC")
                && logInfo.logContent.contains("freed")
                && logInfo.logContent.contains("paused")) {
            gcAnalysis.onCollectGC(logInfo.tag, logInfo.logContent, logInfo.time);
        }
    }


}
