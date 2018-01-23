package com.tencent.wstt.gt.datatool.analysis;

import com.tencent.wstt.gt.datatool.GTRAnalysis;
import com.tencent.wstt.gt.datatool.obj.DeviceInfo;

/**
 * Created by p_hongjcong on 2017/8/1.
 */

public class DeviceAnalysis {


    DeviceInfo deviceInfo;

    public DeviceAnalysis(GTRAnalysis gtrAnalysis, DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void onCollectDeviceInfo(String vendor, String model, String sdkName, int sdkInt) {

        deviceInfo.vendor = vendor;
        deviceInfo.model = model;
        deviceInfo.sdkName = sdkName;
        deviceInfo.sdkInt = sdkInt;
    }

}
