package com.alibaba.sdk.android.oss.sample;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class ListObjectsSamples {

    private OSS oss;
    private String testBucket;

    public ListObjectsSamples(OSS client, String testBucket) {
        this.oss = client;
        this.testBucket = testBucket;
    }

    // 异步罗列Bucket中文件
    public void AyncListObjects() {
        // 创建罗列请求
        ListObjectsRequest listObjects = new ListObjectsRequest(testBucket);
        // 设置成功、失败回调，发送异步罗列请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                            + result.getObjectSummaries().get(i).getETag() + " "
                            + result.getObjectSummaries().get(i).getLastModified());
                }
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }

    // 同步罗列指定prefix/delimiter文件
    public void listObjectsWithPrefix() {
        ListObjectsRequest listObjects = new ListObjectsRequest(testBucket);
        // 设定前缀
        listObjects.setPrefix("folder");
        listObjects.setDelimiter("/");

        try {
            // 发送同步罗列请求，等待结果返回
            ListObjectsResult result = oss.listObjects(listObjects);
            for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                Log.d("listObjectsWithPrefix", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                        + result.getObjectSummaries().get(i).getETag() + " "
                        + result.getObjectSummaries().get(i).getLastModified());
            }

            for (int i = 0; i < result.getCommonPrefixes().size(); i++) {
                Log.d("listObjectsWithPrefix", "prefixes: " + result.getCommonPrefixes().get(i));
            }
        }
        catch (ClientException clientException) {
            clientException.printStackTrace();
        }
        catch (ServiceException serviceException) {
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
        }
    }

    // 异步下载指定前缀文件
    public void asyncListObjectsWithPrefix() {
        ListObjectsRequest listObjects = new ListObjectsRequest(testBucket);
        // 设定前缀
        listObjects.setPrefix("file");

        // 设置成功、失败回调，发送异步罗列请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                            + result.getObjectSummaries().get(i).getETag() + " "
                            + result.getObjectSummaries().get(i).getLastModified());
                }
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }
}
