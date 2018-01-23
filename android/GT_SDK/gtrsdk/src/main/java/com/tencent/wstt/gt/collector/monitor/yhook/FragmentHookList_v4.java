package com.tencent.wstt.gt.collector.monitor.yhook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.GTRLog;
import com.tencent.wstt.gt.client.GTRClient;

import com.kunpeng.pit.HookAnnotation;

/**
 * HOOK列表
 *
 * Created by elvis on 2017/2/20.
 */
public class FragmentHookList_v4 {
    private static final String TAG = "HookList_Fragment_V4";

    /** Fragment 生命周期: **/
    @HookAnnotation(className = "android.support.v4.app.Fragment",
            methodName = "onAttach",
            methodSig = "(Landroid/content/Context;)V")
    public static void onAttach(Object thiz, Context context) {
        GTRLog.e(TAG,"onAttach");
        long start = System.currentTimeMillis();
        onAttach_backup(thiz, context);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.onAttach")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void onAttach_backup(Object thiz, Context context) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void onAttach_tmp(Object thiz, Context context) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performCreate",
            methodSig = "(Landroid/os/Bundle;)V")
    public static void performCreate(Object thiz, Bundle savedInstanceState) {
        GTRLog.e(TAG,"performCreate");
        long start = System.currentTimeMillis();
        performCreate_backup(thiz, savedInstanceState);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }
        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performCreate")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performCreate_backup(Object thiz, Bundle savedInstanceState) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performCreate_tmp(Object thiz, Bundle savedInstanceState) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performCreateView",
            methodSig = "(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;")
    public static View performCreateView(Object thiz, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GTRLog.e(TAG,"performCreateView");
        long start = System.currentTimeMillis();
        View resultView =performCreateView_backup(thiz, inflater, container, savedInstanceState);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performCreateView")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
        return resultView;
    }

    public static View  performCreateView_backup(Object thiz, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    public static View performCreateView_tmp(Object thiz, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
        return null;
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performActivityCreated",
            methodSig = "(Landroid/os/Bundle;)V")
    public static void performActivityCreated(Object thiz, Bundle savedInstanceState) {
        GTRLog.e(TAG,"performActivityCreated");
        long start = System.currentTimeMillis();
        performActivityCreated_backup(thiz, savedInstanceState);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performActivityCreated")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performActivityCreated_backup(Object thiz, Bundle savedInstanceState) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performActivityCreated_tmp(Object thiz, Bundle savedInstanceState) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performStart",
            methodSig = "()V")
    public static void performStart(Object thiz) {
        GTRLog.e(TAG,"performStart");
        long start = System.currentTimeMillis();
        performStart_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performStart")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performStart_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performStart_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performResume",
            methodSig = "()V")
    public static void performResume(Object thiz) {
        GTRLog.e(TAG,"performResume");
        long start = System.currentTimeMillis();
        performResume_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performResume")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performResume_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performResume_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performPause",
            methodSig = "()V")
    public static void performPause(Object thiz) {
        GTRLog.e(TAG,"performPause");
        long start = System.currentTimeMillis();
        performPause_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performPause")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performPause_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performPause_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performStop",
            methodSig = "()V")
    public static void performStop(Object thiz) {
        GTRLog.e(TAG,"performStop");
        long start = System.currentTimeMillis();
        performStop_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performStop")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performStop_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performStop_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performDestroyView",
            methodSig = "()V")
    public static void performDestroyView(Object thiz) {
        GTRLog.e(TAG,"performDestroyView");
        long start = System.currentTimeMillis();
        performDestroyView_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performDestroyView")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performDestroyView_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performDestroyView_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performDestroy",
            methodSig = "()V")
    public static void performDestroy(Object thiz) {
        GTRLog.e(TAG,"performDestroy");
        long start = System.currentTimeMillis();
        performDestroy_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment){
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performDestroy")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performDestroy_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performDestroy_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "performDetach",
            methodSig = "()V")
    public static void performDetach(Object thiz) {
        GTRLog.e(TAG,"performDetach");
        long start = System.currentTimeMillis();
        performDetach_backup(thiz);
        long end = System.currentTimeMillis();

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.performDetach")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(start)
                .append(GTConfig.separator).append(end)
                .toString());
    }

    public static void  performDetach_backup(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void performDetach_tmp(Object thiz) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "onHiddenChanged",
            methodSig = "(Z)V")
    public static  void onHiddenChanged(Object thiz, boolean hidden) {
        GTRLog.e(TAG,"onHiddenChanged:" + hidden);
        long time = System.currentTimeMillis();
        onHiddenChanged_backup(thiz, hidden);

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.onHiddenChanged")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(time)
                .append(GTConfig.separator).append(hidden)
                .toString());
    }

    public static void  onHiddenChanged_backup(Object thiz, boolean hidden) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void onHiddenChanged_tmp(Object thiz, boolean hidden) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(
            className = "android.support.v4.app.Fragment",
            methodName = "setUserVisibleHint",
            methodSig = "(Z)V")
    public static  void setUserVisibleHint(Object thiz, boolean isVisibleToUser) {
        GTRLog.e(TAG,"setUserVisibleHint:" + isVisibleToUser);
        long time = System.currentTimeMillis();
        setUserVisibleHint_backup(thiz, isVisibleToUser);

        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Object fragment = thiz;

        if (fragment instanceof android.support.v4.app.Fragment) {
            fragmentClassName = ((android.support.v4.app.Fragment)fragment).getClass().getName();
            fragmentHashCode = "" + thiz.hashCode();
            Activity activity = ((android.support.v4.app.Fragment)fragment).getActivity();
            if (activity != null) {
                activityClassName = activity.getClass().getName();
                activityHashCode = "" + activity.hashCode();
            }
        }

        GTRClient.pushData(new StringBuilder()
                .append("FragmentV4.setUserVisibleHint")
                .append(GTConfig.separator).append(activityClassName)
                .append(GTConfig.separator).append(activityHashCode)
                .append(GTConfig.separator).append(fragmentClassName)
                .append(GTConfig.separator).append(fragmentHashCode)
                .append(GTConfig.separator).append(time)
                .append(GTConfig.separator).append(isVisibleToUser)
                .toString());
    }

    public static void  setUserVisibleHint_backup(Object thiz, boolean isVisibleToUser) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }

    public static void setUserVisibleHint_tmp(Object thiz, boolean isVisibleToUser) {
        GTRLog.d(TAG, "这个日志出现了就爆炸了");
    }
}
