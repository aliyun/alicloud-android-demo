package com.alibaba.sophix.demo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by wuer on 16/10/18.
 */
public class DexFixDemo {
    private static final String TAG = "BaseBug";

    @TestFieldAnnotation(fieldVer = 1)
    private int temp;

    @TestMethodAnnotation(methodVer = 11)
    public static void test_annotation() {
        try {
            Method method = DexFixDemo.class.getDeclaredMethod("test_annotation");
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Log.d("Sophix", "test_annotation: " + annotation.toString());
            }

            TestFieldAnnotation testFieldAnn = DexFixDemo.class.getDeclaredField("temp").getAnnotation(TestFieldAnnotation.class);
            Log.d("Sophix", "TestFieldAnnotation fieldVer: " + testFieldAnn.fieldVer());
        } catch (Exception e) {
            Log.e("Sophix", "test error", e);
        }
    }

    public static void test_normal(Context context) {
        Toast.makeText(context, "old apk from java...", Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, "new apk from java...", Toast.LENGTH_SHORT).show();
    }


    public static void test_addField() {
        A obj = new A();
        StringBuilder stringBuilder = new StringBuilder("test_addField")
                .append(" a:").append(obj.a)
                //.append(" b:").append(obj.b)
                .append(" c:").append(obj.c);
        Log.d("Sophix", stringBuilder.toString());
    }

    public static void test_addMethod() {
        A aObj = new A();
        aObj.a_t2();
    }
}

class A {
    int a = 0;
    int b = 1;
    int c = 2;

    void a_t1() {
        Log.d("Sophix", "A a_t1");
    }
    void a_t2() {
        Log.d("Sophix", "A a_t2");
    }
}





