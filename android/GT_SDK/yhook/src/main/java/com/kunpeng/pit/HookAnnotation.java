package com.kunpeng.pit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yuchaofei on 16/3/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HookAnnotation {

    public final static byte MATACH_EQUAL = 0x01;
    public final static byte MATACH_LESS = 0x02;
    public final static byte MATACH_GREATER = 0x04;

    int sdkVersion() default -1;

    byte sdkType() default MATACH_EQUAL;//配合sdkVersion使用

    String className();

    String methodName();

    String methodSig();
}
