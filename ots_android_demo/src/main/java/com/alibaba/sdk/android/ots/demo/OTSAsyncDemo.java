package com.alibaba.sdk.android.ots.demo;

import com.aliyun.openservices.ots.ClientException;
import com.aliyun.openservices.ots.OTSAsync;
import com.aliyun.openservices.ots.OTSClientAsync;
import com.aliyun.openservices.ots.OTSException;
import com.aliyun.openservices.ots.internal.OTSCallback;
import com.aliyun.openservices.ots.model.CapacityUnit;
import com.aliyun.openservices.ots.model.ColumnValue;
import com.aliyun.openservices.ots.model.CreateTableRequest;
import com.aliyun.openservices.ots.model.CreateTableResult;
import com.aliyun.openservices.ots.model.DeleteTableRequest;
import com.aliyun.openservices.ots.model.DeleteTableResult;
import com.aliyun.openservices.ots.model.OTSContext;
import com.aliyun.openservices.ots.model.OTSFuture;
import com.aliyun.openservices.ots.model.PrimaryKeyType;
import com.aliyun.openservices.ots.model.PrimaryKeyValue;
import com.aliyun.openservices.ots.model.PutRowRequest;
import com.aliyun.openservices.ots.model.PutRowResult;
import com.aliyun.openservices.ots.model.ReservedThroughput;
import com.aliyun.openservices.ots.model.RowPrimaryKey;
import com.aliyun.openservices.ots.model.RowPutChange;
import com.aliyun.openservices.ots.model.TableMeta;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by pengqun on 15/8/9.
 */
public class OTSAsyncDemo {
    private static final String TABLE_NAME = "SampleTable";

    public static void run(final String endPoint, final String accessKeyId,
                           final String accessKeySecret, final String instanceName) {

        OTSAsync ots = new OTSClientAsync(endPoint, accessKeyId, accessKeySecret, instanceName);

        try {
            // 创建表
            createTable(ots);
            Thread.sleep(5 * 1000); // 创建完表之后等待一段时间

            // 异步接口提供两种方式来实现异步，一种是Future，参考以下Future的示例；另一种是Callback，参考以下Callback的示例。
            // 通过Future来调用异步接口。
            putRowAsyncWithFuture(ots);

            // 通过Callback来调用异步接口。
            putRowAsyncWithCallback(ots);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 测试完毕，删除表（注意：删除表后数据不可恢复，谨慎操作）
            OTSFuture<DeleteTableResult> future = ots.deleteTable(new DeleteTableRequest(TABLE_NAME), null);
            future.get();

            // 释放OTS的资源，若不释放，可能会造成资源泄露
            ots.shutdown();
        }
    }

    private static void createTable(OTSAsync ots) {
        // 设置表的主键（必选）：示例中表的主键有两列，名称分别为PK0和PK1，类型分别为INTEGER和STRING
        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        tableMeta.addPrimaryKeyColumn("PK0", PrimaryKeyType.INTEGER);
        tableMeta.addPrimaryKeyColumn("PK1", PrimaryKeyType.STRING);

        // 设置表的预留吞吐量（必选）
        ReservedThroughput reservedThroughput = new ReservedThroughput();
        reservedThroughput.setCapacityUnit(new CapacityUnit(5000, 5000));

        CreateTableRequest request = new CreateTableRequest(tableMeta);
        request.setReservedThroughput(reservedThroughput);
        OTSFuture<CreateTableResult> future = ots.createTable(request, null);
        future.get();
    }

    private static RowPutChange getRow(RowPrimaryKey primaryKey) {
        RowPutChange rowChange = new RowPutChange(TABLE_NAME);
        rowChange.setPrimaryKey(primaryKey);
        rowChange.addAttributeColumn("Column0", ColumnValue.fromLong(1));
        rowChange.addAttributeColumn("Column1", ColumnValue.fromString("HelloWorld"));

        return rowChange;
    }

    private static void putRowAsyncWithFuture(OTSAsync ots) throws Exception {
        RowPrimaryKey primaryKey1 = new RowPrimaryKey();
        primaryKey1.addPrimaryKeyColumn("PK0", PrimaryKeyValue.fromLong(2014));
        primaryKey1.addPrimaryKeyColumn("PK1", PrimaryKeyValue.fromString("ots"));

        RowPrimaryKey primaryKey2 = new RowPrimaryKey();
        primaryKey2.addPrimaryKeyColumn("PK0", PrimaryKeyValue.fromLong(2015));
        primaryKey2.addPrimaryKeyColumn("PK1", PrimaryKeyValue.fromString("ots"));

        // 构造第一行的请求
        RowPutChange row1 = getRow(primaryKey1);
        PutRowRequest request1 = new PutRowRequest();
        request1.setRowChange(row1);

        // 构造第二行的请求
        RowPutChange row2 = getRow(primaryKey2);
        PutRowRequest request2 = new PutRowRequest();
        request2.setRowChange(row2);

        // 以异步的方式同时发送两个不同行的写请求，同时写的两行具有不同的主键，避免行写入冲突
        OTSFuture<PutRowResult> f1 = ots.putRow(request1, null);
        OTSFuture<PutRowResult> f2 = ots.putRow(request2, null);

        // 等待请求完成有以下几种方式。

        // 1. 使用isDone接口来判断是否完成请求
        System.out.println("第一行写入是否完成：" + f1.isDone());
        System.out.println("第二行写入是否完成:" + f2.isDone());

        // 2. 通过get接口并设置超时时间来同步的等待请求完成，若请求在规定时间内完成，则返回结果，否则抛出TimeoutException异常。
        PutRowResult r1 = f1.get(10000, TimeUnit.MILLISECONDS);
        PutRowResult r2 = f2.get(10000, TimeUnit.MILLISECONDS);

        // 3. 通过get接口同步的等待请求完成，若请求完成并成功执行，则返回结果，否则抛出执行错误的异常。
        r1 = f1.get();
        r2 = f1.get();
        System.out.println("第一行写入是否完成：" + f1.isDone());
        System.out.println("第二行写入是否完成:" + f2.isDone());
    }

    private static void putRowAsyncWithCallback(OTSAsync ots) throws Exception {
        RowPrimaryKey primaryKey1 = new RowPrimaryKey();
        primaryKey1.addPrimaryKeyColumn("PK0", PrimaryKeyValue.fromLong(2014));
        primaryKey1.addPrimaryKeyColumn("PK1", PrimaryKeyValue.fromString("ots"));

        RowPrimaryKey primaryKey2 = new RowPrimaryKey();
        primaryKey2.addPrimaryKeyColumn("PK0", PrimaryKeyValue.fromLong(2015));
        primaryKey2.addPrimaryKeyColumn("PK1", PrimaryKeyValue.fromString("ots"));

        // 构造第一行的请求
        RowPutChange row1 = getRow(primaryKey1);
        PutRowRequest request1 = new PutRowRequest();
        request1.setRowChange(row1);

        // 构造第二行的请求
        RowPutChange row2 = getRow(primaryKey2);
        PutRowRequest request2 = new PutRowRequest();
        request2.setRowChange(row2);

        class PutRowCallback implements OTSCallback<PutRowRequest, PutRowResult> {
            private final CountDownLatch cdl;
            private boolean succeed = false;

            private PutRowCallback(CountDownLatch cdl) {
                this.cdl = cdl;
            }

            @Override
            public void onCompleted(OTSContext<PutRowRequest, PutRowResult> otsContext) {
                succeed = true;
                cdl.countDown();
            }

            @Override
            public void onFailed(OTSContext<PutRowRequest, PutRowResult> otsContext, OTSException ex) {
                cdl.countDown();
            }

            @Override
            public void onFailed(OTSContext<PutRowRequest, PutRowResult> otsContext, ClientException ex) {
                cdl.countDown();
            }
        }

        CountDownLatch cdl = new CountDownLatch(2);
        PutRowCallback cb1 = new PutRowCallback(cdl);
        PutRowCallback cb2 = new PutRowCallback(cdl);

        // 异步的发送两个写请求
        ots.putRow(request1, cb1);
        ots.putRow(request2, cb2);

        // 等待两个请求执行完成
        cdl.await();
        System.out.println("第一行是否写入成功：" + cb1.succeed);
        System.out.println("第二行是否写入成功：" + cb2.succeed);
    }
}
