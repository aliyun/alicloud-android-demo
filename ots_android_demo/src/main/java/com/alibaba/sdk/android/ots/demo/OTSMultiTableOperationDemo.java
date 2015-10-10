package com.alibaba.sdk.android.ots.demo;

import com.aliyun.openservices.ots.ClientException;
import com.aliyun.openservices.ots.OTSClient;
import com.aliyun.openservices.ots.OTSErrorCode;
import com.aliyun.openservices.ots.OTSException;
import com.aliyun.openservices.ots.ServiceException;
import com.aliyun.openservices.ots.model.BatchGetRowRequest;
import com.aliyun.openservices.ots.model.BatchGetRowResult;
import com.aliyun.openservices.ots.model.BatchWriteRowRequest;
import com.aliyun.openservices.ots.model.BatchWriteRowResult;
import com.aliyun.openservices.ots.model.BatchWriteRowResult.RowStatus;
import com.aliyun.openservices.ots.model.CapacityUnit;
import com.aliyun.openservices.ots.model.ColumnValue;
import com.aliyun.openservices.ots.model.CreateTableRequest;
import com.aliyun.openservices.ots.model.DeleteTableRequest;
import com.aliyun.openservices.ots.model.MultiRowQueryCriteria;
import com.aliyun.openservices.ots.model.PrimaryKeyType;
import com.aliyun.openservices.ots.model.PrimaryKeyValue;
import com.aliyun.openservices.ots.model.Row;
import com.aliyun.openservices.ots.model.RowPrimaryKey;
import com.aliyun.openservices.ots.model.RowPutChange;
import com.aliyun.openservices.ots.model.TableMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OTSMultiTableOperationDemo {
    private static final String COLUMN_GID_NAME = "gid";
    private static final String COLUMN_UID_NAME = "uid";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_ADDRESS_NAME = "address";
    private static final String tableNamePrefix = "SampleTable_";
    private static final int TABLE_COUNT = 5;

    public static void run(final String endPoint, final String accessKeyId,
                           final String accessKeySecret, final String instanceName) {

        OTSClient client = new OTSClient(endPoint, accessKeyId, accessKeySecret, instanceName);

        try {
            // 创建表
            for (int i = 0; i < TABLE_COUNT; i++) {
                createTable(client, tableNamePrefix + i);
            }

            // 注意：创建表只是提交请求，OTS创建表需要一段时间。
            // 这里简单地等待30秒，请根据您的实际逻辑修改。
            Thread.sleep(30000);

            // 向多个表插入多行数据
            batchWriteRow(client);
            // 从多个表读取多行数据
            batchGetRow(client);
        } catch (ServiceException e) {
            e.printStackTrace();
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
        } finally {
            // 不留垃圾。
            try {
                for (int i = 0; i < TABLE_COUNT; i++) {
                    deleteTable(client, tableNamePrefix + i);
                }
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
        tableMeta.addPrimaryKeyColumn(COLUMN_UID_NAME, PrimaryKeyType.INTEGER);
        tableMeta.addPrimaryKeyColumn(COLUMN_GID_NAME, PrimaryKeyType.STRING);
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

    private static void batchWriteRow(OTSClient client) throws OTSException,
            ClientException {
        System.out.println("\n############# 开始BatchWriteRow操作 #############");
        BatchWriteRowRequest request = new BatchWriteRowRequest();
        // 向TABLE_COUNT张表中插入数据， 每张表插入10行。
        for (int i = 0; i < TABLE_COUNT; ++i) {
            for (int j = 0; j < 10; ++j) {

                RowPutChange rowPutChange = new RowPutChange(tableNamePrefix
                        + i);
                RowPrimaryKey primaryKey = new RowPrimaryKey();
                primaryKey.addPrimaryKeyColumn(COLUMN_UID_NAME,
                        PrimaryKeyValue.fromLong(1));
                primaryKey.addPrimaryKeyColumn(
                        COLUMN_GID_NAME,
                        PrimaryKeyValue.fromString(String.format("%03d", j)));
                rowPutChange.setPrimaryKey(primaryKey);

                rowPutChange.addAttributeColumn(COLUMN_ADDRESS_NAME,
                        ColumnValue.fromString("中国某地"));
                rowPutChange.addAttributeColumn(COLUMN_NAME_NAME,
                        ColumnValue.fromString("张三" + j));
                request.addRowPutChange(rowPutChange);
            }
        }
        // batchWriteRow接口会返回一个结果集， 结果集中包含的结果个数与插入的行数相同。 结果集中的结果不一定都是成功，
        // 用户需要自己对不成功的操作进行重试。
        BatchWriteRowResult result = client.batchWriteRow(request);
        BatchWriteRowRequest failedOperations = null;
        int succeedCount = 0;

        int retryCount = 0;
        do {
            Map<String, List<RowStatus>> status = result.getPutRowStatus();
            failedOperations = new BatchWriteRowRequest();
            for (Entry<String, List<RowStatus>> entry : status.entrySet()) {
                String tableName = entry.getKey();
                List<RowStatus> statuses = entry.getValue();
                for (int index = 0; index < statuses.size(); index++) {
                    RowStatus rowStatus = statuses.get(index);
                    if (!rowStatus.isSucceed()) {
                        // 操作失败， 需要放到重试列表中再次重试
                        // 需要重试的操作可以从request中获取参数
                        failedOperations.addRowPutChange(request
                                .getRowPutChange(tableName, index));
                    } else {
                        succeedCount++;
                        System.out.println("本次操作消耗的写CapacityUnit为：" + rowStatus.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit());
                    }
                }
            }

            if (failedOperations.isEmpty() || ++retryCount > 3) {
                break; // 如果所有操作都成功了或者重试次数达到上线， 则不再需要重试。
            }

            // 如果有需要重试的操作， 则稍微等待一会后再次重试， 否则继续出错的概率很高。
            try {
                Thread.sleep(100); // 100ms后继续重试
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            request = failedOperations;
            result = client.batchWriteRow(request);
        } while (true);

        System.out.println(String.format("成功插入%d行数据。", succeedCount));
    }

    private static void batchGetRow(OTSClient client) throws OTSException,
            ClientException {
        // 从每张表中查询插入的10行数据， 由于一个BatchGetRow最多接受10行数据，所以每张表分开查
        for (int i = 0; i < TABLE_COUNT; ++i) {
            batchGetRow(client, tableNamePrefix + i);
        }
    }

    private static void batchGetRow(OTSClient client, String tableName)
            throws OTSException, ClientException {
        System.out.println("\n############# 开始BatchGetRow操作 #############");
        BatchGetRowRequest request = new BatchGetRowRequest();
        // 从每张表中查询插入的10行数据， 由于一个BatchGetRow最多接受10行数据，所以每张表分开查
        MultiRowQueryCriteria tableRows = new MultiRowQueryCriteria(tableName);
        for (int j = 0; j < 10; ++j) {
            RowPrimaryKey primaryKeys = new RowPrimaryKey();
            primaryKeys.addPrimaryKeyColumn(COLUMN_UID_NAME,
                    PrimaryKeyValue.fromLong(1));
            primaryKeys.addPrimaryKeyColumn(
                    COLUMN_GID_NAME,
                    PrimaryKeyValue.fromString(String.format("%03d", j)));

            tableRows.addRow(primaryKeys);
        }
        tableRows.addColumnsToGet(new String[] { COLUMN_NAME_NAME,
                COLUMN_GID_NAME, COLUMN_UID_NAME, COLUMN_ADDRESS_NAME });
        request.addMultiRowQueryCriteria(tableRows);

        BatchGetRowResult result = client.batchGetRow(request);
        BatchGetRowRequest failedOperations = null;

        List<Row> succeedRows = new ArrayList<Row>();

        int retryCount = 0;
        do {
            failedOperations = new BatchGetRowRequest();

            Map<String, List<BatchGetRowResult.RowStatus>> status = result
                    .getTableToRowsStatus();
            for (Entry<String, List<BatchGetRowResult.RowStatus>> entry : status
                    .entrySet()) {
                tableName = entry.getKey();
                tableRows = new MultiRowQueryCriteria(tableName);
                List<BatchGetRowResult.RowStatus> statuses = entry
                        .getValue();
                for (int index = 0; index < statuses.size(); index++) {
                    BatchGetRowResult.RowStatus rowStatus = statuses
                            .get(index);
                    if (!rowStatus.isSucceed()) {
                        // 操作失败， 需要放到重试列表中再次重试
                        // 需要重试的操作可以从request中获取参数
                        tableRows.addRow(request
                                .getPrimaryKey(tableName, index));
                    } else {
                        succeedRows.add(rowStatus.getRow());
                        System.out.println("成功读取一行数据:");
                        System.out.println("uid信息为：" + rowStatus.getRow().getColumns().get(COLUMN_UID_NAME));
                        System.out.println("gid信息为：" + rowStatus.getRow().getColumns().get(COLUMN_GID_NAME));
                        System.out.println("name信息为：" + rowStatus.getRow().getColumns().get(COLUMN_NAME_NAME));
                        System.out.println("address信息为：" + rowStatus.getRow().getColumns().get(COLUMN_ADDRESS_NAME));
                        System.out.println("本次读操作消耗的读CapacityUnti为：" + rowStatus.getConsumedCapacity().getCapacityUnit().getReadCapacityUnit());
                    }
                }

                if (!tableRows.getRowKeys().isEmpty()) {
                    tableRows.addColumnsToGet(new String[] { COLUMN_NAME_NAME,
                            COLUMN_GID_NAME, COLUMN_UID_NAME });
                    failedOperations.addMultiRowQueryCriteria(tableRows);
                }
            }

            if (failedOperations.isEmpty() || ++retryCount > 3) {
                break; // 如果所有操作都成功了或者重试次数达到上线， 则不再需要重试。
            }

            // 如果有需要重试的操作， 则稍微等待一会后再次重试， 否则继续出错的概率很高。
            try {
                Thread.sleep(100); // 100ms后继续重试
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            request = failedOperations;
            result = client.batchGetRow(request);
        } while (true);

        System.out.println(String.format("查询成功%d行数据。", succeedRows.size()));
    }
}
