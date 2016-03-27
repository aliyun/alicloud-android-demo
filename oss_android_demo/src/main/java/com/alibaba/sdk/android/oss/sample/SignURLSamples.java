package com.alibaba.sdk.android.oss.sample;


import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ClientException;
import java.io.IOException;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class SignURLSamples {

    private OSS oss;
    private String testBucket;
    private String testObject;

    public SignURLSamples(OSS client, String testBucket, String testObject, String uploadFilePath) {
        this.oss = client;
        this.testBucket = testBucket;
        this.testObject = testObject;
    }

    // 如果Bucket是私有的，需要签出有签名的URL，并指定过期时间
    public void presignConstrainedURL() {
        try {
            // 获取签名url，过期时间为5分钟
            String url = oss.presignConstrainedObjectURL(testBucket, testObject, 5 * 60);
            Log.d("signContrainedURL", "get url: " + url);
            // 访问该url
            Request request = new Request.Builder().url(url).build();
            Response resp = new OkHttpClient().newCall(request).execute();
            if (resp.code() == 200) {
                Log.d("signContrainedURL", "object size: " + resp.body().contentLength());
            } else {
                Log.e("signContrainedURL", "get object failed, error code: " + resp.code()
                        + "error message: " + resp.message());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
    }


    // 如果Bucket是公共读的，那么可以签出public的URL，不需要设置过期时间
    public void presignPublicURL() {
        try {
            // 获取签名url，没有过期时间
            String url = oss.presignPublicObjectURL(testBucket, testObject);
            Log.d("signPublicURL", "get url: " + url);
            // 访问该url
            Request request = new Request.Builder().url(url).build();
            Response resp = new OkHttpClient().newCall(request).execute();
            if (resp.code() == 200) {
                Log.d("signPublicURL", "get object size: " + resp.body().contentLength());
            } else {
                Log.e("signPublicURL", "get object failed, error code: " + resp.code()
                        + "error message: " + resp.message());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
