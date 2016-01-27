# alicloud-android-demo

本工程给出了[阿里云移动服务](http://dpa.console.aliyun.com/)相关产品的使用DEMO，欢迎参考与使用。

注：demo中的账号信息配置只为方便demo例程的运行，真实产品中我们建议您使用安全黑匣子或其他方式保障密钥的安全性。

## man_restful_demo

man_restful_demo给出了移动数据分析服务（Mobile Analytics）RESTful接口的使用示例。

请在AndroidManifest.xml文件中添加您的账户信息以便DEMO能正常运行。

```
// *字段请用您的账号信息替换
<meta-data android:name="com.alibaba.app.appkey" android:value="********"></meta-data>
<meta-data android:name="com.alibaba.app.appsecret" android:value="********************************"></meta-data>
```

## oss_android_demo

oss_android_demo给出了OSS Android SDK的使用示例。

请在AndroidManifest.xml文件中添加您的账户信息以便DEMO能正常运行。

```
// *字段请用您的账号信息替换
<meta-data android:name="com.alibaba.app.ossak" android:value="*********"></meta-data>
<meta-data android:name="com.alibaba.app.osssk" android:value="***********"></meta-data>
<meta-data android:name="com.alibaba.app.ossbucketname" android:value="******"></meta-data>``
```

## man_android_demo

man_android_demo给出了移动数据分析服务（Mobile Analytics） Android SDK的使用示例。

请在AndroidManifest.xml文件中添加您的账户信息以便DEMO能正常运行。

```
// *字段请用您的账号信息替换
<meta-data android:name="com.alibaba.app.appkey" android:value="********"></meta-data>
<meta-data android:name="com.alibaba.app.appsecret" android:value="********"></meta-data>
```

## mac_android_demo

mac_android_demo给出了移动加速服务 (Mobile Accelerator) Android SDK的使用示例。

请在AndroidManifest.xml文件中添加您的账户信息以便DEMO能正常运行。同时，您需要到[MAC控制台](http://mac.console.aliyun.com)登录您的账户，在对应App中添加您需要加速的域名，例如需要添加DEMO中Benchmark用到的域名macapibm.ams.aliyuncs.com。

```
// *字段请用您的账号信息替换
<meta-data android:name="com.alibaba.app.appkey" android:value="********"></meta-data>
<meta-data android:name="com.alibaba.app.appsecret" android:value="********"></meta-data>
```

DEMO中包含了Benchmark对比MAC和原生链路的平均RT对比，逻辑如下：

1. 每次随机并发1到6个请求，其中最后一个是API请求（包含Header ```Cache-Control: no-cache, no-store, max-age=0, must-revalidate```），其余为静态资源请求（大小为20k，包含Header ```Cache-Control: max-age=60```）
2. api请求的url是[http://macapibm.ams.aliyuncs.com/api_request](http://macapibm.ams.aliyuncs.com/api_request), 静态资源请求的url是每次从[http://macimg0bm.ams.aliyuncs.com/static_file/20k](http://macimg0bm.ams.aliyuncs.com/static_file/20k)和[http://macimg1bm.ams.aliyuncs.com/static_file/20k](http://macimg1bm.ams.aliyuncs.com/static_file/20k)中随机选择一个
3. 每隔10s内的随机时间并发一次
4. 一共发200个请求
5. 统计所有HTTP状态码为200 OK的请求RT平均值

## ots_android_demo

ots_android_demo给出了OTS Android SDK的使用示例。

请在AndroidManifest.xml文件中添加您的账户信息以便DEMO能正常运行。

```
// *字段请用您的账号信息替换
<meta-data android:name="com.alibaba.app.accesskeyid" android:value="********"></meta-data>
<meta-data android:name="com.alibaba.app.accesskeysecret" android:value="********"></meta-data>
<meta-data android:name="com.alibaba.app.endpoint" android:value="********"></meta-data>
<meta-data android:name="com.alibaba.app.instancename" android:value="********"></meta-data>
```

## httpdns_restful_demo

httpdns_restful_demo给出了HTTPDNS服务的使用示例。


