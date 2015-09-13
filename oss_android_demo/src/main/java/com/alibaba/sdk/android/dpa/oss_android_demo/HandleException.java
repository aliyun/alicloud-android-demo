package com.alibaba.sdk.android.dpa.oss_android_demo;

import org.w3c.dom.Document;

import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.OSSException.ExceptionType;
import com.alibaba.sdk.android.oss.model.OSSResponseInfo;


/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Apr 2, 2015
 *
 */
public class HandleException {

    public static void handleException(OSSException ossException) {
        ossException.printStackTrace();

        if (ossException.getExceptionType() == ExceptionType.LOCAL_EXCEPTION) {

            String objectKey = ossException.getObjectKey(); // 获取该任务对应的ObjectKey
            String mesString = ossException.getMessage(); // 异常信息
            Exception localException = ossException.getException(); // 取得原始的异常

        } else if (ossException.getExceptionType() == ExceptionType.OSS_EXCEPTION) {

            String objectKey = ossException.getObjectKey(); // 获取该任务对应的ObjectKey
            OSSResponseInfo resp = ossException.getOssRespInfo(); // 获取根据OSS响应的内容构造的数据结构
            int statusCode = resp.getStatusCode(); // OSS响应的http状态码
            Document dom = resp.getResponseInfoDom(); // 根据OSS响应内容解析得到的文档结构，您可以通过它获取更详细的信息
            String errorCode = resp.getCode(); // OSS反馈的错误码
            String requestId = resp.getRequestId(); // 该次任务的请求ID
            String hostId = resp.getHostId(); // 该次任务请求的主机
            String message = resp.getMessage(); // OSS反馈的错误信息
        }
    }
}
