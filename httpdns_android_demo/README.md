# 阿里云HttpDns Demo APP Android版


HTTPDNS使用HTTP协议进行域名解析，代替现有基于UDP的DNS协议，域名解析请求直接发送到阿里云的HTTPDNS服务器，从而绕过运营商的Local DNS，能够避免Local DNS造成的域名劫持问题和调度不精准问题。

## 产品特性

-   *防劫持*——绕过运营商Local DNS，避免域名劫持，让每一次访问都畅通无阻。
-   *精准调度*——基于访问的来源IP，获得最精准的解析结果，让客户端就近接入业务节点。
-   *低解析延迟*——配合端上策略（热点域名预解析、缓存DNS解析结果、解析结果懒更新）实现毫秒级低解析延迟的域名解析效果。
-   *快速生效*——避免Local DNS不遵循权威TTL，解析结果长时间无法更新的问题。
-   *降低解析失败率*——有效降低无线场景下解析失败的比率。
-   *稳定可靠*——99.9%的可用性，确保域名解析服务稳定可靠。


## 使用方法

### 1 启用产品

>[产品使用流程](https://help.aliyun.com/document_detail/2867674.html)


### 2. 控制台添加域名

HTTPDNS控制台：[链接](https://emas.console.aliyun.com/emasService/platformService/httpdns/domain)


点击`添加域名`完成域名添加

控制台上添加的域名，1分钟后会在HTTPDNS服务端生效。


### 3. 下载Demo工程

将工程克隆或下载到本地：

```shell
git clone https://github.com/aliyun/alicloud-android-demo.git
```


其中`httpdns_android_demo`即为HttpDns的Demo APP。

httpdns_android_demo已经完成了HttpDns SDK的集成工作，但我们还是建议您仔细阅读HttpDns的集成文档

>[Android SDK配置文档](https://help.aliyun.com/document_detail/435250.html)

**当您在使用您自己的APP集成HttpDns遇到问题时，您可以对比下demo APP的配置情况。**

### 4. 配置APP信息

HttpDns Demo可以直接运行，体验相关功能。同时您也可以把相关参数改成您自己的参数，再体验相关功能。

#### 4.1 配置accountID

将`aliyun-emas-services.json`文件中的`httpdns.accountId`设置成您自己的accountID。


### 5. 运行程序

运行程序，程序启动后，在输入框输入您在控制台配置的域名，点击`开始解析`按钮，如果解析结果不为空，则说明解析成功


## 联系我们

-   官网：[HTTPDNS](https://www.aliyun.com/product/httpdns)
