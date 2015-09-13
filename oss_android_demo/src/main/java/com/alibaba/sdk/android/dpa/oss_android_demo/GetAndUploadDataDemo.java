package com.alibaba.sdk.android.dpa.oss_android_demo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.DeleteCallback;
import com.alibaba.sdk.android.oss.callback.GetBytesCallback;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.Range;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSData;


/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Apr 2, 2015
 *
 */
public class GetAndUploadDataDemo {
    private static String TAG = "GetAndUploadDataDemo";

    private OSSService ossService;
    private OSSBucket bucket;

    public void show() {

        ossService = OSSAndroidDemo.ossService;

        bucket = ossService.getOssBucket(OSSAndroidDemo.bucketName); // 替换为你的bucketName

        // 可选地进行访问权限或者CDN加速域名的设置
        // bucket.setBucketACL(AccessControlList.PUBLIC_READ);

        // 设置CDN加速域名时， 权限至少是公共读
        // bucket.setCdnAccelerateHostId("<cname.to.cdn.domain.com>");

        // 可选地进行Bucket cname的设置
        // bucket.setBucketHostId("<cname.to.bucket>");

        syncUpload();
        delay();
        asyncUpload();
        delay();
        syncGetData();
        delay();
        asyncGetData();
        delay();
        getDataWithRange();
        delay();
        getDataRangeWithRightOpen();
        delay();
        syncDelete();
        delay();
        uploadWithMeta();
        delay();
        asyncDelete();
    }

    public void delay() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 同步上传数据
    public void syncUpload() {
        byte[] dataToUpload = "Piece of data of length 26".getBytes();
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        data.setData(dataToUpload, "text/plain");
        data.addXOSSMetaHeader("x-oss-meta-name1", "value1");

        // 直接从数据流上传
        // InputStream is = new ByteArrayInputStream(dataToUpload);
        // data.setInputstream(is, dataToUpload.length, "text/txt");

        try {
            data.upload();
            Log.d(TAG, "upload finish!");
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 异步上传数据
    public void asyncUpload() {
        byte[] dataToUpload = "Piece of data of length 26".getBytes();
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        data.setData(dataToUpload, "text/plain");

        // 直接从数据流上传
        // InputStream is = new ByteArrayInputStream(dataToUpload);
        // data.setInputstream(is, dataToUpload.length, "text/txt");

        data.uploadInBackground(new SaveCallback() {

            @Override
            public void onSuccess(String objectKey) {
                Log.d(TAG, "[onSuccess] - " + objectKey + " upload success!");
            }

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.d(TAG, "[onProgress] - current upload " + objectKey + " bytes: " + byteCount + " in total: " + totalSize);
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "[onFailure] - upload " + objectKey + " failed!\n" + ossException.toString());
            }
        });
    }

    // 上传数据的同时附加自定义meta数据
    public void uploadWithMeta() {
        byte[] dataToUpload = "Piece of data of length 26".getBytes();
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        data.setData(dataToUpload, "text/plain");

        // 直接从数据流上传
        // InputStream is = new ByteArrayInputStream(dataToUpload);
        // data.setInputstream(is, dataToUpload.length, "text/txt");


        data.addXOSSMetaHeader("x-oss-meta-key1", "value1"); // 只对上传操作有效，且必须在上传前调用才生效
        data.addXOSSMetaHeader("x-oss-meta-key2", "value2"); // 自定义的meta属性必须以"x-oss-meta-"为前缀
        data.addXOSSMetaHeader("x-oss-meta-key3", "value3"); // 不支持同名的meta属性键值对

        data.uploadInBackground(new SaveCallback() {

            @Override
            public void onSuccess(String objectKey) {
                Log.d(TAG, "[onSuccess] - " + objectKey + " upload success!");
            }

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.d(TAG, "[onProgress] - current upload " + objectKey + " bytes: " + byteCount + " in total: " + totalSize);
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "[onFailure] - upload " + objectKey + " failed!\n" + ossException.toString());
            }
        });
    }

    // 同步获取数据
    public void syncGetData() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        try {
            byte[] bData = data.get();
            Log.v(TAG, "finish getting data! length: " + bData.length);
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 同步获取指定范围的数据
    public void getDataWithRange() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        data.setRange(0, 9);
        try {
            byte[] bData = data.get();
            Log.d(TAG, "finish getting data! length: " + bData.length);
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 同步获取指定起点为范围的数据
    public void getDataRangeWithRightOpen() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        data.setRange(10, Range.INFINITE);
        try {
            byte[] bData = data.get();
            Log.d(TAG, "finish getting data! length: " + bData.length);
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 异步获取数据
    public void asyncGetData() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        // 同样也可以设置范围
        // data.setRange(10, Range.INFINITE);
        data.getInBackground(new GetBytesCallback() {

            @Override
            public void onSuccess(String objectKey, byte[] data) {
                Log.d(TAG, "[onSuccess] - " + objectKey + " length: " + data.length);
            }

            @Override
            public void onProgress(String objectKey, int byteCount, int totalSize) {
                Log.d(TAG, "[onProgress] - current download: " + objectKey + " bytes:" + byteCount + " in total:" + totalSize);
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "[onFailure] - download " + objectKey + " failed!\n" + ossException.toString());
            }
        });
    }

    // 获取数据后，可以直接获取该数据的meta信息
    public void getMetaAfterGetData() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        try {
            byte[] bData = data.get();
            Log.d(TAG, "[syncGetData] - length: " + bData.length);
            List<BasicNameValuePair> metaList = data.getMeta();
            for (BasicNameValuePair pair : metaList) {
                Log.d(TAG, "[getMetaAfterGetData] - " + pair.getName() + " : " + pair.getValue());
            }
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 同步删除数据
    public void syncDelete() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        try {
            data.delete();
            Log.d(TAG, "delete finish!");
        } catch (OSSException e) {
            HandleException.handleException(e);
        }
    }

    // 异步删除数据
    public void asyncDelete() {
        OSSData data = ossService.getOssData(bucket, "upload.txt");
        data.deleteInBackground(new DeleteCallback() {

            @Override
            public void onSuccess(String objectKey) {
                Log.d(TAG, "[onSuccess] - delete " + objectKey + " success!");
            }

            @Override
            public void onFailure(String objectKey, OSSException ossException) {
                Log.e(TAG, "[onFailure] - delete " + objectKey + " failed!\n" + ossException.toString());
                HandleException.handleException(ossException);
            }
        });
    }
}
