package com.gtr.test;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission;

/**
 * Created by Elvis on 2016/11/23.
 */

public class PermissionTool {


    /**
     * 普通权限--只需要在Manifest.xml中申请就可以
     */
    private static final String[] normalPermissions = {
            permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            permission.ACCESS_NETWORK_STATE,
            permission.ACCESS_NOTIFICATION_POLICY,//SDK>=11才可以使用
            permission.ACCESS_WIFI_STATE,
            permission.BLUETOOTH,
            permission.BLUETOOTH_ADMIN,
            permission.BROADCAST_STICKY,
            permission.CHANGE_NETWORK_STATE,
            permission.CHANGE_WIFI_MULTICAST_STATE,
            permission.CHANGE_WIFI_STATE,
            permission.DISABLE_KEYGUARD,
            permission.EXPAND_STATUS_BAR,
            permission.GET_PACKAGE_SIZE,
            permission.INSTALL_SHORTCUT,//SDK>=19才可以使用
            permission.INTERNET,
            permission.KILL_BACKGROUND_PROCESSES,
            permission.MODIFY_AUDIO_SETTINGS,
            permission.NFC,
            permission.READ_SYNC_SETTINGS,
            permission.READ_SYNC_STATS,
            permission.RECEIVE_BOOT_COMPLETED,
            permission.REORDER_TASKS,
            permission.REQUEST_INSTALL_PACKAGES,//SDK>=23才可以使用
            permission.SET_ALARM,
            permission.SET_TIME_ZONE,
            permission.SET_WALLPAPER,
            permission.SET_WALLPAPER_HINTS,
            permission.TRANSMIT_IR,//SDK>=19才可以使用
            permission.UNINSTALL_SHORTCUT,//SDK>=19才可以使用
            permission.USE_FINGERPRINT,//SDK>=23才可以使用
            permission.VIBRATE,
            permission.WAKE_LOCK,
            permission.WRITE_SYNC_SETTINGS,
    };


    /**
     * 危险权限--必要权限--开启应用必须获取的
     */
    private static final String[] dangerousPermissions = {
            //存储卡
            permission.READ_EXTERNAL_STORAGE,//SDK>=16才可以使用
            permission.WRITE_EXTERNAL_STORAGE,
            //位置
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION,
            //传感器
            permission.BODY_SENSORS,//SDK>=20才可以使用
            //日历
            permission.READ_CALENDAR,
            permission.WRITE_CALENDAR,
            //person_3
            permission.CAMERA,
            //联系人
            permission.READ_CONTACTS,
            permission.WRITE_CONTACTS,
            permission.GET_ACCOUNTS,
            //麦克风
            permission.RECORD_AUDIO,
            //手机
            permission.READ_PHONE_STATE,
            permission.CALL_PHONE,
            permission.READ_CALL_LOG,//SDK>=16才可以使用
            permission.WRITE_CALL_LOG,//SDK>=16才可以使用
            permission.ADD_VOICEMAIL,//SDK>=14才可以使用
            permission.USE_SIP,
            permission.PROCESS_OUTGOING_CALLS,
            //短信
            permission.SEND_SMS,
            permission.RECEIVE_SMS,
            permission.READ_SMS,
            permission.RECEIVE_WAP_PUSH,
            permission.RECEIVE_MMS,
    };


    //权限申请标记
    public static final int APPLY_PERMISSIONS = 154;

    /**
     * 申请权限列表
     *
     * @return
     */
    public static boolean applyPermissions(String[] permissions, Activity activity) {
        //1.筛选未申请的权限：
        ArrayList<String> unApplyList = new ArrayList<String>();
        for (int i = 0; permissions!=null&&i < permissions.length; i++) {
            //筛选未拥有&&满足API要求的权限
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {//未拥有的权限
                if (permissions[i].equals(permission.READ_EXTERNAL_STORAGE) && getSDKVersionNumber() < 16) {//读取文件权限要求16以上
                } else if (permissions[i].equals(permission.BODY_SENSORS) && getSDKVersionNumber() < 20) {//获取体征数据要求20以上
                } else if (permissions[i].equals(permission.READ_CALL_LOG) && getSDKVersionNumber() < 16) {//读取手机通讯录要求16以上
                } else if (permissions[i].equals(permission.WRITE_CALL_LOG) && getSDKVersionNumber() < 16) {//写入手机通讯录要求16以上
                } else if (permissions[i].equals(permission.ADD_VOICEMAIL) && getSDKVersionNumber() < 14) {//添加语音信箱要求14以上
                } else {
                    unApplyList.add(permissions[i]);
                }
            }
        }
        //2.如果所有权限都拥有了，返回true，否则申请所有未申请的权限
        if (unApplyList.size() == 0) {
            return true;
        } else {
            if (getSDKVersionNumber() < 23) {
                Toast.makeText(activity, "权限不足，请到设置页面给予应用相应的权限！", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            } else {
                String[] unApplyArray = new String[unApplyList.size()];
                for (int i = 0; i < unApplyArray.length; i++) {
                    unApplyArray[i] = unApplyList.get(i);
                }
                ActivityCompat.requestPermissions(activity, unApplyArray, APPLY_PERMISSIONS);
                return false;
            }
        }
    }


    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

}