package com.alibaba.sdk.android.ots.demo;

import com.aliyun.openservices.ots.ClientException;
import com.aliyun.openservices.ots.OTSClient;
import com.aliyun.openservices.ots.OTSErrorCode;
import com.aliyun.openservices.ots.ServiceException;
import com.aliyun.openservices.ots.model.CapacityUnit;
import com.aliyun.openservices.ots.model.ColumnValue;
import com.aliyun.openservices.ots.model.Condition;
import com.aliyun.openservices.ots.model.CreateTableRequest;
import com.aliyun.openservices.ots.model.DeleteRowRequest;
import com.aliyun.openservices.ots.model.DeleteRowResult;
import com.aliyun.openservices.ots.model.DeleteTableRequest;
import com.aliyun.openservices.ots.model.GetRowRequest;
import com.aliyun.openservices.ots.model.GetRowResult;
import com.aliyun.openservices.ots.model.PrimaryKeyType;
import com.aliyun.openservices.ots.model.PrimaryKeyValue;
import com.aliyun.openservices.ots.model.PutRowRequest;
import com.aliyun.openservices.ots.model.PutRowResult;
import com.aliyun.openservices.ots.model.Row;
import com.aliyun.openservices.ots.model.RowDeleteChange;
import com.aliyun.openservices.ots.model.RowExistenceExpectation;
import com.aliyun.openservices.ots.model.RowPrimaryKey;
import com.aliyun.openservices.ots.model.RowPutChange;
import com.aliyun.openservices.ots.model.RowUpdateChange;
import com.aliyun.openservices.ots.model.SingleRowQueryCriteria;
import com.aliyun.openservices.ots.model.TableMeta;
import com.aliyun.openservices.ots.model.UpdateRowRequest;
import com.aliyun.openservices.ots.model.UpdateRowResult;


/**
 * 该示例代码包含了如何创建、删除OTS表；
 * 如何向表中插入一条数据；
 * 以及如何从表中根据指定条件查询一条数据。
 */
public class OTSSingleDataDemo {
    private static final String TABLE_NAME = "SampleTable";
    private static final String COLUMN_GID_NAME = "gid";
    private static final String COLUMN_UID_NAME = "uid";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_ADDRESS_NAME = "address";
    private static final String COLUMN_AGE_NAME = "age";
    private static final String COLUMN_MOBILE_NAME = "mobile";

    public static void run(final String endPoint, final String accessKeyId,
                           final String accessKeySecret, final String instanceName) {

        OTSClient client = new OTSClient(endPoint, accessKeyId, accessKeySecret, instanceName);

        try {
            // 创建表
            createTable(client, TABLE_NAME);

            // 注意：创建表只是提交请求，OTS创建表需要一段时间。
            // 这里简单地等待30秒，请根据您的实际逻辑修改。
            Thread.sleep(30000);

            // 插入一条数据。
            putRow(client, TABLE_NAME);
            // 再取回来看看。
            getRow(client, TABLE_NAME);
            // 改一下这条数据。
            updateRow(client, TABLE_NAME);
            // 删除这条数据。
            deleteRow(client, TABLE_NAME);

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

    private static void putRow(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        RowPutChange rowChange = new RowPutChange(tableName);
        RowPrimaryKey primaryKey = new RowPrimaryKey();
        primaryKey.addPrimaryKeyColumn(COLUMN_GID_NAME, PrimaryKeyValue.fromLong(1));
        primaryKey.addPrimaryKeyColumn(COLUMN_UID_NAME, PrimaryKeyValue.fromLong(101));
        rowChange.setPrimaryKey(primaryKey);
        rowChange.addAttributeColumn(COLUMN_NAME_NAME, ColumnValue.fromString("张三"));
        rowChange.addAttributeColumn(COLUMN_MOBILE_NAME, ColumnValue.fromString("111111111"));
        rowChange.addAttributeColumn(COLUMN_ADDRESS_NAME, ColumnValue.fromString("中国A地"));
        rowChange.addAttributeColumn(COLUMN_AGE_NAME, ColumnValue.fromLong(20));
        rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));

        PutRowRequest request = new PutRowRequest();
        request.setRowChange(rowChange);

        PutRowResult result = client.putRow(request);
        int consumedWriteCU = result.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit();

        System.out.println("成功插入数据, 消耗的写CapacityUnit为：" + consumedWriteCU);
    }

    private static void getRow(OTSClient client, String tableName)
            throws ServiceException, ClientException {

        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName);
        RowPrimaryKey primaryKeys = new RowPrimaryKey();
        primaryKeys.addPrimaryKeyColumn(COLUMN_GID_NAME, PrimaryKeyValue.fromLong(1));
        primaryKeys.addPrimaryKeyColumn(COLUMN_UID_NAME, PrimaryKeyValue.fromLong(101));
        criteria.setPrimaryKey(primaryKeys);
        criteria.addColumnsToGet(new String[]{
                COLUMN_NAME_NAME,
                COLUMN_ADDRESS_NAME,
                COLUMN_AGE_NAME
        });

        GetRowRequest request = new GetRowRequest();
        request.setRowQueryCriteria(criteria);
        GetRowResult result = client.getRow(request);
        Row row = result.getRow();

        int consumedReadCU = result.getConsumedCapacity().getCapacityUnit().getReadCapacityUnit();
        System.out.println("本次读操作消耗的读CapacityUnti为：" + consumedReadCU);
        System.out.println("name信息：" + row.getColumns().get(COLUMN_NAME_NAME));
        System.out.println("address信息：" + row.getColumns().get(COLUMN_ADDRESS_NAME));
        System.out.println("age信息：" + row.getColumns().get(COLUMN_AGE_NAME));
    }

    private static void updateRow(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        RowUpdateChange rowChange = new RowUpdateChange(tableName);
        RowPrimaryKey primaryKeys = new RowPrimaryKey();
        primaryKeys.addPrimaryKeyColumn(COLUMN_GID_NAME, PrimaryKeyValue.fromLong(1));
        primaryKeys.addPrimaryKeyColumn(COLUMN_UID_NAME, PrimaryKeyValue.fromLong(101));
        rowChange.setPrimaryKey(primaryKeys);
        // 更新以下三列的值
        rowChange.addAttributeColumn(COLUMN_NAME_NAME, ColumnValue.fromString("张三"));
        rowChange.addAttributeColumn(COLUMN_ADDRESS_NAME, ColumnValue.fromString("中国B地"));
        // 删除mobile和age信息
        rowChange.deleteAttributeColumn(COLUMN_MOBILE_NAME);
        rowChange.deleteAttributeColumn(COLUMN_AGE_NAME);

        rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_EXIST));

        UpdateRowRequest request = new UpdateRowRequest();
        request.setRowChange(rowChange);

        UpdateRowResult result = client.updateRow(request);
        int consumedWriteCU = result.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit();

        System.out.println("成功更新数据, 消耗的写CapacityUnit为：" + consumedWriteCU);
    }

    private static void deleteRow(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        RowDeleteChange rowChange = new RowDeleteChange(tableName);
        RowPrimaryKey primaryKeys = new RowPrimaryKey();
        primaryKeys.addPrimaryKeyColumn(COLUMN_GID_NAME, PrimaryKeyValue.fromLong(1));
        primaryKeys.addPrimaryKeyColumn(COLUMN_UID_NAME, PrimaryKeyValue.fromLong(101));
        rowChange.setPrimaryKey(primaryKeys);

        DeleteRowRequest request = new DeleteRowRequest();
        request.setRowChange(rowChange);

        DeleteRowResult result = client.deleteRow(request);
        int consumedWriteCU = result.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit();

        System.out.println("成功删除数据, 消耗的写CapacityUnit为：" + consumedWriteCU);
    }

    private static void deleteTable(OTSClient client, String tableName)
            throws ServiceException, ClientException {
        DeleteTableRequest request = new DeleteTableRequest();
        request.setTableName(tableName);
        client.deleteTable(request);

        System.out.println("表已删除");
    }
}
