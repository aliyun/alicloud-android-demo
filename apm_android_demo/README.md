# 阿里云应用性能监控Demo APP Android版

EMAS 应用监控是面向客户端的全方位监控服务平台，覆盖移动端和Web/H5端。基于阿里巴巴深厚的技术沉淀，提供稳定高效的监控服务，帮助开发者实时掌握应用性能与稳定性情况，快速构建“感知 > 定位 > 修复”运维闭环，保障应用质量，优化用户体验。

## 产品特性

- **崩溃分析**——实时监控应用崩溃，提供详细的崩溃堆栈信息，快速定位问题根因。
- **性能监控**——监控应用启动时间、页面加载时间、网络请求性能等关键指标。
- **移动日志**——实时收集应用运行日志，支持远程日志查看和分析。
- **实时告警**——支持多种告警方式，第一时间发现应用异常。
- **多维度分析**——按版本、渠道、设备等维度分析应用质量。

## 使用方法

### 1 创建APP

您首先需要登入EMAS控制台，创建一个APP实体以对应您准备使用的Demo APP。关于APP创建的指引文档可以参考：

>[创建App](https://help.aliyun.com/document_detail/436513.html)

### 2. 下载Demo工程

将工程克隆或下载到本地：

```shell
git clone https://github.com/aliyun/alicloud-android-demo.git
```

其中`apm_android_demo`即为应用性能监控的Demo APP。

apm_android_demo已经完成了应用性能监控SDK的集成工作，但我们还是建议您仔细阅读应用性能监控的集成文档

>[Android SDK配置文档](https://help.aliyun.com/document_detail/2880529.html)

**当您在使用您自己的APP集成应用性能监控遇到问题时，您可以对比下demo APP的配置情况。**

### 3. 配置APP信息

#### 3.1 配置AppKey、AppSecret

为了使Demo APP能够正常运行，您还需要配置您的 AppKey / AppSecret 信息。您可以在EMAS控制台，您在第一步创建的APP中找到它们。

>[如何获取您的 AppKey / AppSecret](https://help.aliyun.com/document_detail/436513.html)

在Application的onCreate方法中初始化：

```java
// 初始化公共组件
AliHaAdapter.getInstance().openDebug(true);
AliHaConfig config = new AliHaConfig();
config.appKey = "your_app_key";
config.appVersion = "your_app_version";
config.channel = "your_channel";
AliHaAdapter.getInstance().start(config);
```

#### 3.2 Maven依赖配置

在app的build.gradle文件中添加以下依赖：

```gradle
dependencies {
    // 1、公共依赖
    implementation('com.aliyun.ams:alicloud-android-ha-adapter:1.1.5.4-open') { transitive = true }
    
    // 2、崩溃分析，不接入可注释掉
    implementation('com.aliyun.ams:alicloud-android-ha-crashreporter:1.3.0') { transitive = true }
    
    // 3、移动日志，不接入可注释掉
    implementation('com.aliyun.ams:alicloud-android-tlog:1.1.4.5-open') { transitive = true }
    
    // 4、性能监控，不接入可注释掉
    implementation('com.aliyun.ams:alicloud-android-apm:1.1.1.0-open') { transitive = true }
}
```


### 4. 功能验证

#### 4.1 崩溃分析验证
- 点击Demo中的"模拟崩溃"按钮
- 重启应用后，崩溃信息会自动上报到控制台

#### 4.2 性能监控验证
- 应用启动时会自动收集启动性能数据
- 网络请求会自动监控响应时间和成功率
- 页面切换会记录页面加载时间

#### 4.3 日志收集验证
- 应用运行过程中的日志会自动收集
- 可在控制台查看实时日志信息

## 控制台功能

登录[EMAS控制台](https://emas.console.aliyun.com/)，在应用性能监控模块可以查看：

- **概览**：应用整体健康度、崩溃率、性能指标等
- **崩溃分析**：崩溃详情、堆栈信息、影响用户数等
- **性能分析**：启动耗时、页面性能、网络性能等
- **日志查询**：实时日志、历史日志查询和分析
- **告警配置**：设置崩溃率、性能指标等告警规则

## 联系我们

- 官网：[应用性能监控](https://www.aliyun.com/product/emascrash/apm)
- 文档：[EMAS帮助中心](https://help.aliyun.com/product/434086.html)