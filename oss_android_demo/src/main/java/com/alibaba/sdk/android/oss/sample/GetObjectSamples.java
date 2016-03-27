package com.alibaba.sdk.android.oss.sample;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.Range;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class GetObjectSamples {

    private OSS oss;
    private String testBucket;
    private String testObject;

    public GetObjectSamples(OSS client, String testBucket, String testObject) {
        this.oss = client;
        this.testBucket = testBucket;
        this.testObject = testObject;
    }

    public void getObjectSample() {

        // 构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(testBucket, testObject);

        try {
            // 同步执行下载请求，返回结果
            GetObjectResult getResult = oss.getObject(get);

            Log.d("Content-Length", "" + getResult.getContentLength());

            // 获取文件输入流
            InputStream inputStream = getResult.getObjectContent();

            byte[] buffer = new byte[2048];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                // 处理下载的数据，比如图片展示或者写入文件等
                Log.d("asyncGetObjectSample", "read length: " + len);
            }
            Log.d("asyncGetObjectSample", "download success.");

            // 下载后可以查看文件元信息
            ObjectMetadata metadata = getResult.getMetadata();
            Log.d("ContentType", metadata.getContentType());


        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void asyncGetObjectSample() {

        GetObjectRequest get = new GetObjectRequest(testBucket, testObject);

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();

                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.d("asyncGetObjectSample", "read length: " + len);
                    }
                    Log.d("asyncGetObjectSample", "download success.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }

    public void asyncGetObjectRangeSample() {

        GetObjectRequest get = new GetObjectRequest(testBucket, testObject);

        // 设置范围
        get.setRange(new Range(0, 99)); // 下载0到99共100个字节，文件范围从0开始计算

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();

                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.d("asyncGetObjectSample", "read length: " + len);
                    }
                    Log.d("asyncGetObjectSample", "download success.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    }
}
