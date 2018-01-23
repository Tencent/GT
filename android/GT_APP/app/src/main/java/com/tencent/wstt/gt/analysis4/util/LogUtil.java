package com.tencent.wstt.gt.analysis4.util;


import com.tencent.wstt.gt.analysis4.obj.LogInfo;

/**
 * Created by p_hongjcong on 2017/8/14.
 */

public class LogUtil {
    public static final String[] GRADEKEYS = new String[] {
            "A ", "A/", "E ", "E/", "W ", "W/",
            "I ", "I/", "D ", "D/", "V ", "V/"
    };

    public static final String[] GTRTAGS = new String[] {
            "KHOOK", "Kat", "Elvis", "elvis", "Matt", "matt",
            "GTR", "GTR_DATA_TAG", "crash lib", "crash path",
            "zzzzzdddd", "addCallback", "_PDM_"
    };

    public static LogInfo onCollectLog(String log, long time) {
        //grade:
        int minGradeLocal = log.length();
        for (int i = 0; i < GRADEKEYS.length; i++) {
            if (log.indexOf(GRADEKEYS[i]) != -1 && log.indexOf(GRADEKEYS[i]) < minGradeLocal) {
                minGradeLocal = log.indexOf(GRADEKEYS[i]);
            }
        }

        if (minGradeLocal == log.length()) {
            //没有匹配到grade信息，则不是log格式信息
            return null;
        }

        String grade = log.substring(minGradeLocal, minGradeLocal+1);
        //tag:
        String logWithoutGrade = log.substring(minGradeLocal + 2, log.length());
        int tagEndLocal = logWithoutGrade.indexOf(":");
        if (tagEndLocal == -1) {
            //没有找到":"，则不是log格式信息
            return null;
        }
        String tag = logWithoutGrade.substring(0, tagEndLocal);
        //logContent
        String logContent = logWithoutGrade.substring(tagEndLocal + 1,logWithoutGrade.length());
        //isGTR
        boolean isGTR = false;
        for (String s: GTRTAGS) {
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

        return logInfo;
    }
}
