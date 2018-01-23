package com.kunpeng.pit;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;


/**
 * HOOK列表
 * <p>
 * Created by elvis on 2017/2/20.
 * 这个仅仅 是用作例子滴
 */

public class FragmentHookList_v4 {

    private static final String TAG = "HookList_Fragment_V4";


    /**
     * Fragment 生命周期:
     **/
    @HookAnnotation(className = "android.support.v4.app.Fragment",
            methodName = "onAttach",
            methodSig = "(Landroid/content/Context;)V")
    public static void onAttach(Object thiz, Context context) {
        Log.d(TAG, "onAttach");
        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.onAttach", this, context);
//        HookMain.UpdateTargetCopy("android.support.v4.app.Fragment","onAttach","(Landroid/content/Context;)V");
        onAttach_backup(thiz, context);
        long end = System.currentTimeMillis();
        String activityClassName = "";
        String activityHashCode = "";
        String fragmentClassName = "";
        String fragmentHashCode = "";
        Log.d(TAG, "onAttach_backup  finsih 1");
        new Fragment();
        Log.d(TAG, "onAttach_backup  finsih 2");
        fragmentClassName = ((android.support.v4.app.Fragment) thiz).getClass().getName();
        Log.d(TAG, "onAttach_backup  finsih 2");
        fragmentHashCode = "" + thiz.hashCode();
        Log.d(TAG, "onAttach_backup  finsih 3");
        Activity activity = ((android.support.v4.app.Fragment) thiz).getActivity();
        if (activity != null) {
            activityClassName = activity.getClass().getName();
            activityHashCode = "" + activity.hashCode();
        }
        Log.d(TAG, "onAttach_backup all finsih ");
//        GTRClient.saveData(
//                new StringBuilder().append("FragmentV4.onAttach")
//                        .append(GTRConfig.separator).append(activityClassName)
//                        .append(GTRConfig.separator).append(activityHashCode)
//                        .append(GTRConfig.separator).append(fragmentClassName)
//                        .append(GTRConfig.separator).append(fragmentHashCode)
//                        .append(GTRConfig.separator).append(start)
//                        .append(GTRConfig.separator).append(end)
//                        .toString());
    }

    public static void onAttach_backup(Object thiz, Context context) {
        Log.d(TAG, "这个日志出现了就爆炸了");
    }
    public static void onAttach_tmp(Object thiz, Context context) {
        Log.d(TAG, "这个日志出现了就爆炸了");
    }

    @HookAnnotation(className = "com.gtr.test.ClassWithVirtualMethod",
            methodName = "tac",
            methodSig = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
    public static String tac(Object thiz, String a, String b, String c, String d) {
//        Log.d("YAHFA", "in ClassWithVirtualMethod.tac(): " + a + ", " + b + ", " + c + ", " + d);
//        HookMain.getInstance().UpdateTargetCopy("com.gtr.test.ClassWithVirtualMethod",
//                "tac",
//                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
        Log.d("YAHFA", "in ClassWithVirtualMethod.tac(): " + a + ", " + b + ", " + c + ", " + d);
        return tac_backup(thiz, a, b, c, d);
    }

    public static String tac_backup(Object thiz, String a, String b, String c, String d) {
        Log.d(TAG, "tac_backup这个日志出现了就爆炸了");
        return null;
    }
    public static String tac_tmp(Object thiz, String a, String b, String c, String d) {
        Log.d(TAG, "tac_backup这个日志出现了就爆炸了");
        return null;
    }
    /**
     * View绘制相关：
     **/
//    @HookAnnotation(className = "android.view.ViewGroup", methodName = "dispatchDraw", methodSig = "(Landroid/graphics/Canvas;)V")
//    public static void dispatchDraw(Object thiz, Canvas canvas) {
////        android.view.ViewGroup
////        Log.e(TAG,"ViewGroup.dispatchDraw");
//        Object view = thiz;
//        String drawClassName = view.getClass().getName();//当前绘制类名（包含包名）
//        Log.e(TAG, "ViewGroup.dispatchDraw ，call Calss = " + drawClassName);
//        String objectHashCode = "" + view.hashCode();//当前绘制的对象名（包含包名和对象地址）
//        dispatchDraw_backup(thiz, canvas);
////        ViewDrawUtil.onViewGroup_dispatchDraw_before(drawClassName,objectHashCode);
////        KHookManager.getInstance().callOriginalMethod("android.view.ViewGroup.dispatchDraw", this, canvas);
////        ViewDrawUtil.onViewGroup_dispatchDraw_after();
//    }
//
//    public static void dispatchDraw_backup(Object thiz, Canvas canvas) {
//        Log.e(TAG, "ViewGroup.dispatchDraw 爆炸");
//    }

//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performCreate(Bundle savedInstanceState) {
//        Log.e("ElvisElvis","performCreate");
//        Log.e(TAG,"performCreate");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performCreate", this, savedInstanceState);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performCreate")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    View performCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//        Log.e(TAG,"performCreateView");
//        long start = System.currentTimeMillis();
//        View resultView = KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performCreateView", this, inflater, container, savedInstanceState);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performCreateView")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//        return resultView;
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performActivityCreated(Bundle savedInstanceState) {
//        Log.e(TAG,"performActivityCreated");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performActivityCreated", this, savedInstanceState);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performActivityCreated")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performStart() {
//        Log.e(TAG,"performStart");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performStart", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performStart")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performResume() {
//        Log.e(TAG,"performResume");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performResume", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performResume")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performPause() {
//        Log.e(TAG,"performPause");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performPause", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performPause")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performStop() {
//        Log.e(TAG,"performStop");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performStop", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performStop")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performDestroyView() {
//        Log.e(TAG,"performDestroyView");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performDestroyView", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performDestroyView")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performDestroy() {
//        Log.e(TAG,"performDestroy");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performDestroy", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performDestroy")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    void performDetach() {
//        Log.e(TAG,"performDetach");
//        long start = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.performDetach", this);
//        long end = System.currentTimeMillis();
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.performDetach")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(start)
////                        .append(GTRConfig.separator).append(end)
////                        .toString());
//    }
//
//
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    public void onHiddenChanged(boolean hidden) {
//        Log.e(TAG,"onHiddenChanged:"+hidden);
//        long time = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.onHiddenChanged", this,hidden);
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.onHiddenChanged")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(time)
////                        .append(GTRConfig.separator).append(hidden)
////                        .toString());
//    }
//
//    @HookAnnotation(className = "android.support.v4.app.Fragment")
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        Log.e(TAG,"setUserVisibleHint:"+isVisibleToUser);
//        long time = System.currentTimeMillis();
//        KHookManager.getInstance().callOriginalMethod("android.support.v4.app.Fragment.setUserVisibleHint", this,isVisibleToUser);
//        String activityClassName = "";
//        String activityHashCode = "";
//        String fragmentClassName = "";
//        String fragmentHashCode = "";
//        Object fragment = this;
//        if (fragment instanceof android.support.v4.app.Fragment){
//            fragmentClassName =  ((android.support.v4.app.Fragment)fragment).getClass().getName();
//            fragmentHashCode = ""+this.hashCode();
//            Activity activity = ((android.support.v4.app.Fragment) fragment).getActivity();
//            if (activity!=null){
//                activityClassName  = activity.getClass().getName();
//                activityHashCode = ""+activity.hashCode();
//            }
//        }
////        GTRClient.saveData(
////                new StringBuilder().append("FragmentV4.setUserVisibleHint")
////                        .append(GTRConfig.separator).append(activityClassName)
////                        .append(GTRConfig.separator).append(activityHashCode)
////                        .append(GTRConfig.separator).append(fragmentClassName)
////                        .append(GTRConfig.separator).append(fragmentHashCode)
////                        .append(GTRConfig.separator).append(time)
////                        .append(GTRConfig.separator).append(isVisibleToUser)
////                        .toString());
//    }
}
