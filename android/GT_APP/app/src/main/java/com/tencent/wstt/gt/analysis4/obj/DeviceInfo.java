package com.tencent.wstt.gt.analysis4.obj;

import java.util.HashMap;

public class DeviceInfo {
	

    public String vendor;
    public String model;
    
    public String sdkName;
    public int sdkInt;
    
    
    //蓝牙版本：bluetoothVersion
    //内存大小：ramSize
    //传感器列表：sensors(格式：{typeID,sensorName;typeID,sensorName...})
    //GPU版本：gpuVersion
	//GPU厂商：gpuVendor
	//GPU提供商：gpuRender
    //...
    public HashMap<String, String> hardwareInfos = new HashMap<>();


    
    
    
    

}
