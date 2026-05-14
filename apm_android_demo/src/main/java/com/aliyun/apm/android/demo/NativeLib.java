package com.aliyun.apm.android.demo;

public class NativeLib {
    static {
        System.loadLibrary("nativecrash");
    }

    public native void mockSigSegv();
    public native void mockSigAbrt();
    public native void mockSigBus();
    public native void mockSigIll();
}
