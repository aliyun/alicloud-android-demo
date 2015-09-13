package com.alibaba.sdk.android.dpa.oss_android_demo;

import android.util.Log;

import com.alibaba.sdk.android.dpa.util.ToolKit;
import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.UploadPartInfo;
import com.alibaba.sdk.android.oss.model.UploadPartResult;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSMultipart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: zhouzhuo<yecan.xyc@alibaba-inc.com>
 * Jul 31, 2015
 *
 */
public class MultipartUploadDemo {

    private static String TAG = "MultipartUploadDemo";

    private OSSService ossService;
    private OSSBucket bucket;
    private String src_file_dir;

    public void show() {
        ossService = OSSAndroidDemo.ossService;
        src_file_dir = OSSAndroidDemo.srcFileDir;

        bucket = ossService.getOssBucket(OSSAndroidDemo.bucketName);

        try {
            multipartUploadInAnObject();
            multipartUploadInMultiObjects();
        } catch (OSSException e) {
            HandleException.handleException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 分块上传，各阶段都持有同一个OSSMultipart对象进行上传
    public void multipartUploadInAnObject() throws OSSException, IOException {
        String objectkey = "bigFile.dat";
        OSSMultipart multipart = ossService.getOssMultipart(bucket, objectkey);
        multipart.setContentType("application/octet-stream");

        multipart.initiateMultiPartUpload();

        File file = new File(src_file_dir + "file1m");

        InputStream inputStream = new FileInputStream(file);
        multipart.setUploadpart(1, inputStream, 128 * 1024);
        multipart.uploadPart();

        inputStream = new FileInputStream(file);
        byte[] data = ToolKit.readFullyToByteArray(inputStream);
        multipart.setUploadpart(2, data);
        multipart.uploadPart();

        List<UploadPartInfo> info = multipart.listParts();
        for (int i = 0; i < info.size(); i++) {
            Log.d(TAG, "[multipartUploadInAnObject] - " + info.get(i).getPartNumber() + " " + info.get(i).geteTag());
        }

        multipart.completeMultipartUpload();
    }

    // 分块上传，不同阶段使用不同OSSMultipart对象上传，需要把前面得到的信息设置到新的OSSMultipart对象中
    public void multipartUploadInMultiObjects() throws OSSException, IOException {
        String objectkey = "bigFile.dat";
        OSSMultipart multipart1 = ossService.getOssMultipart(bucket, objectkey);
        multipart1.setContentType("application/octet-stream");

        List<UploadPartResult> list = new ArrayList<UploadPartResult>();
        String uploadId = multipart1.initiateMultiPartUpload();

        OSSMultipart multipart2 = ossService.getOssMultipart(bucket, objectkey);
        multipart2.designateUploadId(uploadId);

        File file = new File(src_file_dir + "file1m");
        InputStream inputStream = new FileInputStream(file);
        multipart2.setUploadpart(1, inputStream, 128 * 1024);
        UploadPartResult result1 = multipart2.uploadPart();
        list.add(result1);

        OSSMultipart multipart3 = ossService.getOssMultipart(bucket, objectkey);
        multipart3.designateUploadId(uploadId);

        inputStream = new FileInputStream(file);
        byte[] data = ToolKit.readFullyToByteArray(inputStream);
        multipart3.setUploadpart(2, data);
        UploadPartResult result2 = multipart3.uploadPart();
        list.add(result2);

        OSSMultipart multipart4 = ossService.getOssMultipart(bucket, objectkey);
        multipart4.designateUploadId(uploadId);
        List<UploadPartInfo> info = multipart4.listParts();
        for (int i = 0; i < info.size(); i++) {
            Log.d(TAG, "[multipartUploadInAnObject] - " + info.get(i).getPartNumber() + " " + info.get(i).geteTag());
        }

        OSSMultipart multipart5 = ossService.getOssMultipart(bucket, objectkey);
        multipart5.designateUploadId(uploadId);
        multipart5.designatePartList(list);
        multipart5.completeMultipartUpload();
    }
}
