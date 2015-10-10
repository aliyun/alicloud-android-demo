package com.alibaba.sdk.android.ots.demo;

import com.aliyun.openservices.ots.ClientException;
import com.aliyun.openservices.ots.OTSClient;
import com.aliyun.openservices.ots.OTSErrorCode;
import com.aliyun.openservices.ots.ServiceException;
import com.aliyun.openservices.ots.model.CapacityUnit;
import com.aliyun.openservices.ots.model.CreateTableRequest;
import com.aliyun.openservices.ots.model.DeleteTableRequest;
import com.aliyun.openservices.ots.model.DescribeTableRequest;
import com.aliyun.openservices.ots.model.DescribeTableResult;
import com.aliyun.openservices.ots.model.ListTableResult;
import com.aliyun.openservices.ots.model.PrimaryKeyType;
import com.aliyun.openservices.ots.model.ReservedThroughputChange;
import com.aliyun.openservices.ots.model.ReservedThroughputDetails;
import com.aliyun.openservices.ots.model.TableMeta;
import com.aliyun.openservices.ots.model.UpdateTableRequest;
import com.aliyun.openservices.ots.model.UpdateTableResult;

/**
 * Created by pengqun on 15/7/24.
 */
public class OTSTableOperationDemo {
    private static final String TABLE_NAME = "SampleTable";
    private static final String COLUMN_GID_NAME = "gid";
    private static final String COLUMN_UID_NAME = "uid";

    public static void run(final String endPoint, final String accessKeyId,
                           final String accessKeySecret, final String instanceName) {

        OTSClient client = new OTSClient(endPoint, accessKeyId, accessKeySecret, instanceName);

        try {
            // 创建表
            createTable(client, TABLE_NAME);

            // 查看表的属性
            describeTable(client, TABLE_NAME);

            // 修改表的CapacityUnit设置。
            // 注意：OTS对上调CapacityUnit的次数没有限制，但是对下调CapacityUnit的次数有限制。
            // 修改CU的频率有限制，两次UpdateTable操作之间至少需要相隔10分钟。
            updateTable(client, TABLE_NAME);

            // update table完之后查看表的属性
            describeTable(client, TABLE_NAME);

            // list table查看表的列表
            listTable(client);

            deleteTable(client, TABLE_NAME);

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
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
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

    private static void describeTable(OTSClient client, String tableName) {
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTableName(tableName);
        DescribeTableResult result = client.describeTable(request);
        TableMeta tableMeta = result.getTableMeta();
        System.out.println("表的名称：" + tableMeta.getTableName());
        System.out.println("表的主键：");
        for (String keyName : tableMeta.getPrimaryKey().keySet()) {
            System.out.println(keyName + " : "
                    + tableMeta.getPrimaryKey().get(keyName));
        }
        ReservedThroughputDetails reservedThroughputDetails = result.getReservedThroughputDetails();
        System.out.println("表的预留读吞吐量："
                + reservedThroughputDetails.getCapacityUnit().getReadCapacityUnit());
        System.out.println("表的预留写吞吐量："
                + reservedThroughputDetails.getCapacityUnit().getWriteCapacityUnit());
        System.out.println("最后一次上调预留读写吞吐量的时间：" + reservedThroughputDetails.getLastIncreaseTime());
        System.out.println("最后一次下调预留读写吞吐量的时间：" + reservedThroughputDetails.getLastDecreaseTime());
        System.out.println("UTC自然日内总的下调预留读写吞吐量的次数："
                + reservedThroughputDetails.getNumberOfDecreasesToday());
    }

    private static void updateTable(OTSClient client, String tableName)
            throws InterruptedException {
        // 将表的CU下调为:(50, 50), 但是由于刚创建表，所以需要10分钟之后才能调整
        System.out.println("等待10分钟之后调整表的CU设置。");
        Thread.sleep(10 * 60000);
        UpdateTableRequest request = new UpdateTableRequest();
        request.setTableName(tableName);
        ReservedThroughputChange cuChange = new ReservedThroughputChange();
        cuChange.setReadCapacityUnit(50); // 若想单独调整写CU，则无须设置读CU
        cuChange.setWriteCapacityUnit(50); // 若想单独调整读CU，则无须设置写CU
        request.setReservedThroughputChange(cuChange);
        UpdateTableResult result = client.updateTable(request);

        ReservedThroughputDetails reservedThroughputDetails = result.getReservedThroughputDetails();
        System.out.println("表的预留读吞吐量："
                + reservedThroughputDetails.getCapacityUnit().getReadCapacityUnit());
        System.out.println("表的预留写吞吐量："
                + reservedThroughputDetails.getCapacityUnit().getWriteCapacityUnit());
        System.out.println("最后一次上调预留读写吞吐量的时间：" + reservedThroughputDetails.getLastIncreaseTime());
        System.out.println("最后一次下调预留读写吞吐量的时间：" + reservedThroughputDetails.getLastDecreaseTime());
        System.out.println("UTC自然日内总的下调预留读写吞吐量的次数："
                + reservedThroughputDetails.getNumberOfDecreasesToday());
    }

    private static void listTable(OTSClient client) {
        ListTableResult result = client.listTable();
        System.out.println("表的列表如下：");
        for (String tableName : result.getTableNames()) {
            System.out.println(tableName);
        }
    }

    private static void deleteTable(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        DeleteTableRequest request = new DeleteTableRequest();
        request.setTableName(tableName);
        client.deleteTable(request);

        System.out.println("表已删除");
    }
}
