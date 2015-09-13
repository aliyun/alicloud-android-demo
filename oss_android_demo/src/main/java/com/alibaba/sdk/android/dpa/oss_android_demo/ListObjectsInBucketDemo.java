package com.alibaba.sdk.android.dpa.oss_android_demo;

import android.util.Log;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.model.ListObjectOption;
import com.alibaba.sdk.android.oss.model.ListObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectResult.ObjectInfo;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;


/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Apr 2, 2015
 *
 */
public class ListObjectsInBucketDemo {
    private static String TAG = "ListObjectsInBucketDemo";

    private OSSService ossService;
    private OSSBucket bucket;

    public void show() {
        ossService = OSSAndroidDemo.ossService;

        bucket = ossService.getOssBucket(OSSAndroidDemo.bucketName);

        listObjectsInBucketByDefaultSetting();
        listWithSpecifyDelimiter();
        listWithSpecifyMarker();
        listWithSpecifyPrefix();
        listWithSpecifyPrefixAndMaxKeys();
    }

    // 默认设置下罗列bucket内object
    public void listObjectsInBucketByDefaultSetting() {
        ListObjectOption opt = new ListObjectOption();
        ListObjectResult result;
        try {
            result = bucket.listObjectsInBucket(opt);
            Log.d(TAG, "[listObjectInBucketByDefaultSetting] - size: " + result.getObjectInfoList().size());
            Log.d(TAG, "[listObjectInBucketByDefaultSetting] - nextMarker: " + result.getNextMarker());
            Log.d(TAG, "[listObjectInBucketByDefaultSetting] - IsTruncated: " + result.isTruncated());
            for (ObjectInfo ele : result.getObjectInfoList()) {
                Log.d(TAG, "[listObjectInBucketByDefaultSetting] - " + ele.getObjectKey());
                Log.d(TAG, "[listObjectInBucketByDefaultSetting] - " + ele.getLastModified());
                Log.d(TAG, "[listObjectInBucketByDefaultSetting] - " + String.valueOf(ele.getSize()));
                Log.d(TAG, "[listObjectInBucketByDefaultSetting] - " + ele.getEtag());
            }
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 指定从某个文件开始罗列默认个数object
    public void listWithSpecifyMarker() {
        ListObjectOption opt = new ListObjectOption();
        opt.setMarker("prefix3");
        ListObjectResult result;
        try {
            result = bucket.listObjectsInBucket(opt);
            for (ObjectInfo ele : result.getObjectInfoList()) {
            }
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 罗列指定前缀object
    public void listWithSpecifyPrefix() {
        ListObjectOption opt = new ListObjectOption();
        opt.setPrefix("prefix");;
        ListObjectResult result;
        try {
            result = bucket.listObjectsInBucket(opt);
            for (ObjectInfo ele : result.getObjectInfoList()) {
            }
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 罗列指定个数和前缀的object
    public void listWithSpecifyPrefixAndMaxKeys() {
        ListObjectOption opt = new ListObjectOption();
        opt.setMaxKeys(10);
        opt.setPrefix("prefix");
        ListObjectResult result;
        try {
            result = bucket.listObjectsInBucket(opt);
            for (ObjectInfo ele : result.getObjectInfoList()) {
            }
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 指定分隔符罗列object
    public void listWithSpecifyDelimiter() {
        ListObjectOption opt = new ListObjectOption();
        opt.setDelimiter("/");
        opt.setPrefix("prefixdir/");
        opt.setMaxKeys(500);
        ListObjectResult result;
        try {
            result = bucket.listObjectsInBucket(opt);
            Log.d(TAG, "[listWithSpecifyDelimiter] - size: " + result.getObjectInfoList().size());
            Log.d(TAG, "[listWithSpecifyDelimiter] - nextMarker: " + result.getNextMarker());
            Log.d(TAG, "[listWithSpecifyDelimiter] - IsTruncated: " + result.isTruncated());
            for (ObjectInfo ele : result.getObjectInfoList()) {
                Log.d(TAG, "[listWithSpecifyDelimiter] - info: " + ele.getObjectKey() + " " + ele.getLastModified());
            }
            for (ObjectInfo ele : result.getObjectInfoList()) {
            }
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }
}
