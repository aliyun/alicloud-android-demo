package com.alibaba.sophix.demo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wuer on 2017/1/10.
 */

public class SOFixDemo {
    static {
        System.loadLibrary("jnitest");
    }

    public static native void print();

    public static native String test1(String value);

    public static native void test2();

    public static void test(Context context) {
        String result = test1("apk from native...");
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
