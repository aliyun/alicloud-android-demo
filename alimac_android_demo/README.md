# 阿里云移动加速Demo APP Android版
[![maven version](https://img.shields.io/badge/Maven-3.0.11-brightgreen.svg)](https://mhub.console.aliyun.com/#/download) [![platform](https://img.shields.io/badge/platform-android-lightgrey.svg)](https://developer.android.com/index.html) [![language](https://img.shields.io/badge/language-java-orange.svg)](http://www.oracle.com/technetwork/java/index.html) [![website](https://img.shields.io/badge/website-mac-red.svg)](https://www.aliyun.com/product/mac)


<div align="center">
<img src="assets/logo.png">
</div>

移动加速（Mobile Accelerator）是阿里云针对移动应用推出的无线加速产品，旨在依托阿里云遍布全网的加速节点，海量带宽网络等优越的基础设施，为开发者提供更快、更稳定的网络接入能力，有效提升移动应用的可用性和用户体验。

## 产品特性

-   协议优化——链路加速，采用深度优化定制协议。
-   就近接入——加速节点全网覆盖，基于IP精准调度就近接入。
-   弱网优化——提升弱网环境下连通率。
-   动静态加速——同时支持动态内容加速和静态内容加速，静态内容高效缓存。
-   内容优化——智能压缩，请求合并。
-   防流量劫持——有效防止流量劫持。




## 使用方法

### 1.添加域名

您首先需要登入移动加速控制台，选择“域名管理”，按照[《配置样例》](https://help.aliyun.com/document_detail/34932.html?spm=5176.doc27122.6.563.90WQfd)配置域名。



### 2.添加移动加速域名

在控制台中选择“增值服务”-“移动服务”，添加移动加速域名，将域名与app关联起来，如果没有创建app也可以跳转到产品列表中进行添加。



### 3. 下载Demo工程

将工程克隆或下载到本地：

```shell
git clone https://github.com/aliyun/alicloud-android-demo.git
```

并通过Android Studio加载后您可以看到如下目录：

![appkey](http://test-bucket-lingbo.oss-cn-hangzhou.aliyuncs.com/mpush3.png)

其中`alimac_android_demo`即为移动加速的Demo APP。

alimac_android_demo已经完成了移动加速SDK的集成工作，但我们还是建议您仔细阅读移动加速的集成文档

>[Android SDK配置文档](https://help.aliyun.com/document_detail/27122.html?spm=5176.8121017.391236.4.4HW7Tx)

**当您在使用您自己的APP集成移动加速遇到问题时，您可以对比下demo APP的配置情况。**

###  4. 配置APP信息

#### 4.1 配置AppKey、AppSecret

为了使Demo APP能够正常运行，您还需要配置您的appkey/appsecret信息。您可以在应用管理控制台，在创建的APP中找到它们，如图所示：

![appkey](http://test-bucket-lingbo.oss-cn-hangzhou.aliyuncs.com/mpush2.png)

在MacConfig时传入。

### 5. 运行程序

若程序编译通过，且运行时有以下log说明成功：

```
[DHandler] url: https://xxx/xxx.html AccSuccess: 1 reqSuccess: 1
```

其中，AccSuccess为`1`表示加速成功，reqSuccess为`1`表示请求成功

过滤和查看tag为`mac`的日志，例如控制台通过adb logcat -s mac来过滤




## 联系我们

-   官网：[移动加速](https://www.aliyun.com/product/mac)
-   钉钉技术支持： 11774339（钉钉群号）
-   官方技术博客：[阿里云移动服务](https://yq.aliyun.com/teams/32)

