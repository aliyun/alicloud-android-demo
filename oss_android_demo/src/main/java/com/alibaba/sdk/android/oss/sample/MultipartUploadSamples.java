package com.alibaba.sdk.android.oss.sample;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.PartETag;
import com.alibaba.sdk.android.oss.model.UploadPartRequest;
import com.alibaba.sdk.android.oss.model.UploadPartResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhuo on 12/4/15.
 */
public class MultipartUploadSamples {
    private OSS oss;
    private String testBucket;
    private String testObject;
    private String uploadFilePath;

    public MultipartUploadSamples(OSS client, String testBucket, String testObject, String uploadFilePath) {
        this.oss = client;
        this.testBucket = testBucket;
        this.testObject = testObject;
        this.uploadFilePath = uploadFilePath;
    }

    public void multipartUpload() throws ClientException, ServiceException, IOException {

        String uploadId;

        InitiateMultipartUploadRequest init = new InitiateMultipartUploadRequest(testBucket, testObject);
        InitiateMultipartUploadResult initResult = oss.initMultipartUpload(init);

        uploadId = initResult.getUploadId();

        long partSize = 128 * 1024;

        int currentIndex = 1;

        File uploadFile = new File(uploadFilePath);
        InputStream input = new FileInputStream(uploadFile);
        long fileLength = uploadFile.length();

        long uploadedLength = 0;
        List<PartETag> partETags = new ArrayList<PartETag>();
        while (uploadedLength < fileLength) {
            int partLength = (int)Math.min(partSize, fileLength - uploadedLength);
            byte[] partData = IOUtils.readStreamAsBytesArray(input, partLength);

            UploadPartRequest uploadPart = new UploadPartRequest(testBucket, testObject, uploadId, currentIndex);
            uploadPart.setPartContent(partData);
            UploadPartResult uploadPartResult = oss.uploadPart(uploadPart);
            partETags.add(new PartETag(currentIndex, uploadPartResult.getETag()));

            uploadedLength += partLength;
            currentIndex++;
        }

        CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(testBucket, testObject, uploadId, partETags);
        CompleteMultipartUploadResult completeResult = oss.completeMultipartUpload(complete);

        Log.d("multipartUpload", "multipart upload success! Location: " + completeResult.getLocation());
    }
}
