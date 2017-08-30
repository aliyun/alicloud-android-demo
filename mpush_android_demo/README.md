# 阿里云移动推送Demo APP Android版
[![maven version](https://img.shields.io/badge/Maven-3.0.11-brightgreen.svg)](https://mhub.console.aliyun.com/#/download) [![platform](https://img.shields.io/badge/platform-android-lightgrey.svg)](https://developer.android.com/index.html) [![language](https://img.shields.io/badge/language-java-orange.svg)](http://www.oracle.com/technetwork/java/index.html) [![website](https://img.shields.io/badge/website-CloudPush-red.svg)](https://www.aliyun.com/product/cps)


<div align="center">
<img src="assets/logo.png">
</div>

阿里移动推送（Alibaba Cloud Mobile Push）是基于大数据的移动智能推送服务，帮助App快速集成移动推送的功能，在实现高效、精确、实时的移动推送的同时，极大地降低了开发成本。让开发者最有效地与用户保持连接，从而提高用户活跃度、提高应用的留存率。

## 产品特性

-   *高效稳定*——与手机淘宝使用相同架构，基于阿里集团高可用通道。该通道日均消息发送量可达30亿，目前活跃使用的用户达1.8亿。
-   *高到达率*——Android 智能通道保活，多通道支持保证推送高到达率。
-   *精确推送*——基于阿里大数据处理技术，实现精确推送。
-   *应用内消息推送*——支持 Android 与 iOS 应用内私有通道，保证透传消息高速抵达。




## 使用方法

### 1 创建APP

您首先需要登入移动推送控制台，创建一个APP实体以对应您准备使用的Demo APP。关于APP创建的指引文档可以参考：

>[创建App](https://help.aliyun.com/document_detail/30054.html?spm=5176.doc30054.3.2.0xGnCV)

创建完APP后，您还需要配置您的Android平台的相关信息，如下图所示：

![appkey](http://test-bucket-lingbo.oss-cn-hangzhou.aliyuncs.com/mpush4.png)

![appkey](http://test-bucket-lingbo.oss-cn-hangzhou.aliyuncs.com/mpush5.png)

**其中PackageName为您的APP的标识信息，需要与您APP配置文件中的applicationID保持一致。**

### 2. 下载Demo工程

将工程克隆或下载到本地：

```shell
git clone https://github.com/aliyun/alicloud-android-demo.git
```

并通过Android Studio加载后您可以看到如下目录：

![appkey](http://test-bucket-lingbo.oss-cn-hangzhou.aliyuncs.com/mpush3.png)

其中`mpush_android_demo`即为移动推送的Demo APP。

mpush_android_demo已经完成了移动推送SDK的集成工作，但我们还是建议您仔细阅读移动推送的集成文档

>[Android SDK配置文档](https://help.aliyun.com/document_detail/51056.html)

**当您在使用您自己的APP集成移动推送遇到问题时，您可以对比下demo APP的配置情况。**

###  3. 配置APP信息

#### 3.1 配置AppKey、AppSecret

为了使Demo APP能够正常运行，您还需要配置您的appkey/appsecret信息。您可以在移动推送控制台，您在第一步创建的APP中找到它们，如图所示：

![appkey](http://test-bucket-lingbo.oss-cn-hangzhou.aliyuncs.com/mpush2.png)

在下述`AndroidManifest.xml`代码片段中用您的appkey/appsecret替换`********`字段占据的参数。

```xml
<meta-data android:name="com.alibaba.app.appkey" android:value="********"/> <!-- 请填写你自己的- appKey -->
<meta-data android:name="com.alibaba.app.appsecret" android:value="********"/> <!-- 请填写你自己的appSecret -->
```



#### 3.2 配置包名

将`build.gradle`文件中的`applicationId`参数改成所创建App的包名：

```xml
android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"

    defaultConfig {
        applicationId "********" // 填写所创建App的包名
        minSdkVersion 11
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
	......
}
```



### 4. 运行程序

若程序编译通过，且运行时`MainApplication.initCloudChannel`打印出类似如下日志，则说明集成成功：

```
08-23 11:30:02.741 25116-25116/cloud.aliyun.test I/Init: init cloudchannel success
```



### 5. 动态展示

![](assets/configdemo.gif)


## 联系我们

-   官网：[移动推送](https://www.aliyun.com/product/cps)
-   钉钉技术支持：11795523（钉钉群号）
-   官方技术博客：[阿里云移动服务](https://yq.aliyun.com/teams/32)

