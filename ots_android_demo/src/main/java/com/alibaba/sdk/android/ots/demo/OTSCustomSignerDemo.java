package com.alibaba.sdk.android.ots.demo;

import com.aliyun.openservices.ots.ClientException;
import com.aliyun.openservices.ots.OTSClient;
import com.aliyun.openservices.ots.OTSErrorCode;
import com.aliyun.openservices.ots.ServiceException;
import com.aliyun.openservices.ots.auth.OTSBasicSigner;
import com.aliyun.openservices.ots.model.CapacityUnit;
import com.aliyun.openservices.ots.model.CreateTableRequest;
import com.aliyun.openservices.ots.model.DeleteTableRequest;
import com.aliyun.openservices.ots.model.PrimaryKeyType;
import com.aliyun.openservices.ots.model.TableMeta;
import com.aliyun.openservices.ots.utils.BinaryUtil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTSCustomSignerDemo {
    private static final String TABLE_NAME = "SampleTable";
    private static final String COLUMN_GID_NAME = "gid";
    private static final String COLUMN_UID_NAME = "uid";

    public static void run(final String endPoint, final String accessKeyId,
                           final String accessKeySecret, final String instanceName) {

        OTSClient client = new OTSClient(endPoint, new OTSBasicSigner() {
            private static final String ALGORITHM = "HmacSHA1";
            private static final String ENCODING = "UTF-8";

            @Override
            public String getAccessKeyId() {
                return accessKeyId;
            }

            @Override
            public String sign(String data) {
                try {
                    Mac mac = Mac.getInstance(ALGORITHM);
                    mac.init(new SecretKeySpec(accessKeySecret.getBytes(ENCODING), ALGORITHM));
                    byte[] signData = mac.doFinal(data.getBytes(ENCODING));
                    return BinaryUtil.toBase64String(signData);
                }
                catch(UnsupportedEncodingException ex){
                    throw new RuntimeException("Unsupported encoding: " + ENCODING);
                }
                catch(NoSuchAlgorithmException ex){
                    throw new RuntimeException("Unsupported algorithm: " + ALGORITHM);
                }
                catch(InvalidKeyException ex){
                    throw new RuntimeException();
                }
            }
        }, instanceName);

        try {
            // 创建表
            createTable(client, TABLE_NAME);

        } catch (ServiceException e) {
            System.err.println("操作失败，详情：" + e.getMessage());
            // 可以根据错误代码做出处理， OTS的ErrorCode定义在OTSErrorCode中。
            if (OTSErrorCode.QUOTA_EXHAUSTED.equals(e.getErrorCode())) {
                System.err.println("超出存储配额。");
            }
            // Request ID可以用于有问题时联系客服诊断异常。
            System.err.println("Request ID:" + e.getRequestId());
        } catch (ClientException e) {
            // 可能是网络不好或者是返回结果有问题
            System.err.println("请求失败，详情：" + e.getMessage());
        } finally {
            // 不留垃圾。
            try {
                deleteTable(client, TABLE_NAME);
            } catch (ServiceException e) {
                System.err.println("删除表格失败，原因：" + e.getMessage());
                e.printStackTrace();
            } catch (ClientException e) {
                System.err.println("删除表格请求失败，原因：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void createTable(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        TableMeta tableMeta = new TableMeta(tableName);
        tableMeta.addPrimaryKeyColumn(COLUMN_GID_NAME, PrimaryKeyType.INTEGER);
        tableMeta.addPrimaryKeyColumn(COLUMN_UID_NAME, PrimaryKeyType.INTEGER);
        // 将该表的读写CU都设置为100
        CapacityUnit capacityUnit = new CapacityUnit(100, 100);

        CreateTableRequest request = new CreateTableRequest();
        request.setTableMeta(tableMeta);
        request.setReservedThroughput(capacityUnit);
        client.createTable(request);

        System.out.println("表已创建");
    }

    private static void deleteTable(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        DeleteTableRequest request = new DeleteTableRequest();
        request.setTableName(tableName);
        client.deleteTable(request);

        System.out.println("表已删除");
    }
}
