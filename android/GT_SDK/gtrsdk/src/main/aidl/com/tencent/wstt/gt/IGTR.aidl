package com.tencent.wstt.gt;

import com.tencent.wstt.gt.IRemoteClient;
import com.tencent.wstt.gt.GTRParam;

//GTR的通信接口，负责GTRService与GTR交换数据
interface IGTR {

    //GTRDK从GTRService拉取数据：
    GTRParam pullInPara();

    //GTRDK向GTRService推送数据：
    void pushData(in String packageName, long startTestTime, int pid, String data);

    /**
     * 将当前客户端注册到GT服务，GT目前支持1个被测应用的统计，后续被测应用在
     * 尝试注册失败后将暂停连接GT。等待下一次连接的机会。
     */
    int register(IRemoteClient client, String cookie);

    void startGTRAnalysis(String packageName, int pid);

    void stopGTRAnalysis();
}
