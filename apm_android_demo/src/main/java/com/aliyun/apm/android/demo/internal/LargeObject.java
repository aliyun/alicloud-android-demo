package com.aliyun.apm.android.demo.internal;

public class LargeObject {
    private long[] bigData;

    public LargeObject() {
        int bytes = 1024 * 1024 * 2;
        bigData = new long[bytes / 8];
    }
}
