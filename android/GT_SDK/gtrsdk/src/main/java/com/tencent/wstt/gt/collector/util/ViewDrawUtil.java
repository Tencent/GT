package com.tencent.wstt.gt.collector.util;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.client.GTRClient;

/**
 * Created by p_hongjcong on 2017/7/14.
 */
public class ViewDrawUtil {
    /**
     *  通过Hook ViewGroup的dispatchDraw  以弹栈的形式来记录当前的绘制元素和绘制深度
     */

    private static String drawClassName = "";
    private static String objectHashCode = "";
    private static long drawBegin = 0;
    private static long drawEnd = 0;
    private static int drawDeep = 0;
    private static StringBuilder drawPath = null;

    // dispatchDraw的栈深，每当栈弹光，说明一次绘制完成
    private static int stackSize = 0;

    public static synchronized void onViewGroup_dispatchDraw_before(String drawClassName, String objectHashCode) {
        //绘制开始--创建对象
        if (stackSize == 0) {
            ViewDrawUtil.drawClassName = drawClassName;
            ViewDrawUtil.objectHashCode = objectHashCode;
            drawBegin = System.currentTimeMillis();
            drawDeep = 0;
            drawPath = new StringBuilder();
        }
        drawPath.append(stackSize).append(",").append(drawClassName).append(";");
        stackSize++;
        if (stackSize > drawDeep) {
            drawDeep = stackSize;
        }
    }

    public static synchronized void onViewGroup_dispatchDraw_after() {
        stackSize--;

        // 绘制结束--保存对象
        if (stackSize == 0) {
            drawEnd = System.currentTimeMillis();
            GTRClient.pushData(new StringBuilder()
                    .append("ViewGroup.dispatchDraw")
                    .append(GTConfig.separator).append(drawClassName)
                    .append(GTConfig.separator).append(objectHashCode)
                    .append(GTConfig.separator).append(drawBegin)
                    .append(GTConfig.separator).append(drawEnd)
                    .append(GTConfig.separator).append(drawDeep)
                    .append(GTConfig.separator).append(drawPath.toString())
                    .toString());
        }
    }
}
