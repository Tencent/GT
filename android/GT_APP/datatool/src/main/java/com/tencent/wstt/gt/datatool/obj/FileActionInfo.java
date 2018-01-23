package com.tencent.wstt.gt.datatool.obj;

import java.util.ArrayList;

/**
 * Created by p_hongjcong on 2017/8/3.
 */

public class FileActionInfo {


    public int fd;//文件ID
    public String filePath;//文件路径
    public String fileName;//文件名

    public long startTime = 0;
    public long endTime = 0;
    public long readNum = 0;
    public long readSize = 0;
    public long writeNum = 0;
    public long writeSize = 0;
    public boolean isMainThread = false;
    public boolean isMutilThread = false;

    public ArrayList<DiskIOInfo> diskIOInfos = new ArrayList<>();


    public FileActionInfo() {
    }

    public void addDiskIOInfo(DiskIOInfo diskIOInfo, int mainThreadID) {
        diskIOInfos.add(diskIOInfo);
        startTime = -1;
        endTime = -1;
        readNum = 0;
        readSize = 0;
        writeNum = 0;
        writeSize = 0;
        isMainThread = false;
        isMutilThread = false;

        int threadId = -1;

        for (DiskIOInfo temp : diskIOInfos) {
            fd = diskIOInfo.fd;
            filePath = diskIOInfo.filePath;
            fileName = diskIOInfo.fileName;
            if (startTime == -1 || temp.actionStart <= startTime) {
                startTime = temp.actionStart;
            }
            if (endTime == -1 || temp.actionEnd >= endTime) {
                endTime = temp.actionEnd;
            }
            if (temp.actionName.equals(DiskIOInfo.READ)) {
                readNum = readNum + 1;
                readSize = readSize + temp.actionSize;
            }
            if (temp.actionName.equals(DiskIOInfo.WRITE)) {
                writeNum = writeNum + 1;
                writeSize = writeSize + temp.actionSize;
            }
            if (temp.threadID == mainThreadID) {
                isMainThread = true;
            }
            if (threadId == -1) {
                threadId = temp.threadID;
            }
            if (temp.threadID != threadId) {
                isMutilThread = true;
            }
        }


    }


}
