# alicloud-android-demo

本工程给出了[阿里云移动服务](http://dpa.console.aliyun.com/)相关产品的使用DEMO，欢迎参考与使用。

注：demo中的账号信息配置只为方便demo例程的运行，真实产品中我们建议您使用安全黑匣子或其他方式保障密钥的安全性。

## man_restful_demo

man_restful_demo给出了数据分析服务（Mobile Analytics）RESTful接口的使用示例。

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
