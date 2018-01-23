package com.kunpeng.pit;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuruikai756 on 28/03/2017.
 */

public class HookMain {
    private static final String TAG = "YHOOK";
    private static List<Class<?>> hookInfoClasses = new LinkedList<>();
    public static HashMap<String, Method> targetMethods = new HashMap<>();
    public static HashMap<String, Long> targetCopyMethods = new HashMap<>();

    static {
        System.loadLibrary("yhook");
        init(android.os.Build.VERSION.SDK_INT);
    }

    public static void doHookDefault(Class hookClazz) {
        if (hookClazz == null) {
            throw new IllegalArgumentException("Class cannot be null!");
        }
        Method[] methods = hookClazz.getDeclaredMethods();
        for (Method method : methods) {
            HookAnnotation annotation = method.getAnnotation(HookAnnotation.class);
            if (annotation != null && isSDKMatach(annotation)) {
                String className = annotation.className();
                String methodName = annotation.methodName();
                String methodSig = annotation.methodSig();
                String backupMethodName = methodName + "_backup";
                Method backup = null;
                for (Method method1 : methods) {
                    if (method1.getName().equals(backupMethodName)) {
                        backup = method1;
                        break;
                    }
                }
                Method tmp = null;
                String tmpMethodName = methodName + "_tmp";
                for (Method method1 : methods) {
                    if (method1.getName().equals(tmpMethodName)) {
                        tmp = method1;
                        break;
                    }
                }
                try {
                    Class<?> clazz = Class.forName(className);
                    findAndBackupAndHook(clazz, methodName, methodSig, method, backup,tmp);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    static void findAndBackupAndHook(Class targetClass, String methodName, String methodSig,
                                            Method hook, Method backup,Method tmp) {
        try {
            int hookParamCount = hook.getParameterTypes().length;
            int targetParamCount = getParamCountFromSignature(methodSig);
            Log.d(TAG, "target method param count is " + targetParamCount);
            boolean isStatic = (hookParamCount == targetParamCount);
            // virtual method has 'thiz' object as the first parameter
//            Method tmp = generateTempMethod(backup);
            findAndBackupAndHook(targetClass, methodName, methodSig, isStatic, hook, backup, tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int getParamCountFromSignature(String signature) throws Exception {
        int index;
        int count = 0;
        int seg;
        try { // Read all declarations between for `(' and `)'
            if (signature.charAt(0) != '(') {
                throw new Exception("Invalid method signature: " + signature);
            }
            index = 1; // current string position
            while (signature.charAt(index) != ')') {
                seg = parseSignature(signature.substring(index));
                index += seg;
                count++;
            }

        } catch (final StringIndexOutOfBoundsException e) { // Should never occur
            throw new Exception("Invalid method signature: " + signature, e);
        }
        return count;
    }

    static int parseSignature(String signature) throws Exception {
        int count = 0;
        switch (signature.charAt(0)) {
            case 'B': // byte
            case 'C': // char
            case 'D': // double
            case 'F': // float
            case 'I': // int
            case 'J': // long
            case 'S': // short
            case 'Z': // boolean
            case 'V': // void
                count++;
                break;
            case 'L': // class
                count++; // char L
                while (signature.charAt(count) != ';') {
                    count++;
                }
                count++; // char ;
                break;
            case '[': // array
                count++; // char [
                count += parseSignature(signature.substring(count));
                break;
            default:
                throw new Exception("Invalid type: " + signature);
        }
        return count;
    }

    /**
     * 判断当前系统版本是否与hook方法匹配
     *
     * @param annotation
     * @return
     */
    static boolean isSDKMatach(HookAnnotation annotation) {
        int sdkVersion = annotation.sdkVersion();
        if (sdkVersion > -1) {
            byte type = annotation.sdkType();
            int current = Build.VERSION.SDK_INT;
            switch (type) {
                case HookAnnotation.MATACH_EQUAL:
                    return current == sdkVersion;
                case HookAnnotation.MATACH_LESS:
                    return current < sdkVersion;
                case HookAnnotation.MATACH_GREATER:
                    return current > sdkVersion;
                default:
                    return false;
            }
        }
        return true;
    }

     /**
     * AccessibleObject权限绕过
     * Build.VERSION.SDK_INT == 24
     * @param obj
     */
    static void setAccessible(AccessibleObject obj){
        try {
            Field override = obj.getClass().getSuperclass().getSuperclass().getDeclaredField("override");
            override.setAccessible(true);
            override.set(obj, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static native void findAndBackupAndHook(Class targetClass, String methodName, String methodSig,
                                                    boolean isStatic,
                                                    Method hook, Method backup, Method temp);

    private static native void init(int SDK_version);
}
