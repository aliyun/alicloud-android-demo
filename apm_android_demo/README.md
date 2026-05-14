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

#### 3.1 配置AppKey、AppSecret、AppRsaSecret

为了使Demo APP能够正常运行，您还需要配置您的 AppKey / AppSecret / AppRsaSecret 信息。您可以在EMAS控制台，您在第一步创建的APP中找到它们。

>[如何获取您的 AppKey / AppSecret](https://help.aliyun.com/zh/document_detail/436513.html#51afd0e8508wo)

在 `apm_android_demo/src/main/java/com/aliyun/apm/android/demo/MainApplication.kt` 中替换以下占位内容：

```kotlin
val appKey = "your app key" // 请把这里改成控制台上的 "AppKey"
val appSecret = "your app secret" // 请把这里改成控制台上的 "AppSecret"
val appRsaSecret = "your app rsa secret" // 请把这里改成控制台上的 "AppRsaSecret"
```

Demo 会在 `MainApplication.onCreate()` 中通过 `ApmOptions` 初始化移动监控 SDK，并启用崩溃分析、内存分析、远程日志和性能分析组件。

#### 3.2 配置包名

将 `apm_android_demo/build.gradle` 文件中的 `applicationId` 参数改成所创建App的包名：

```gradle
android {
    namespace 'com.aliyun.apm.android.demo'

    defaultConfig {
        applicationId "com.aliyun.apm.android.demo" // 填写所创建App的包名
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
    ......
}
```

如果控制台应用绑定了签名，请同步使用匹配的签名配置。

#### 3.3 Maven依赖配置

当前Demo已在 `apm_android_demo/build.gradle` 中完成移动监控SDK和Gradle插件配置：

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.aliyun.emas.apm' version '3.2.0'
}

def apmSdkVersion = "2.8.0"

dependencies {
    implementation("com.aliyun.ams:alicloud-apm:${apmSdkVersion}")
}
```

Gradle插件用于网络监控、符号化文件处理等构建期能力；`alicloud-apm` 是当前Demo使用的移动监控SDK依赖。

### 4. 功能验证

#### 4.1 崩溃分析验证
- 点击Demo中的 `Java崩溃-空指针`、`Native崩溃-SIGSEGV`、`卡顿` 或 `自定义异常` 按钮
- 也可以进入 `其他类型错误` 页面，触发 IllegalState、数组越界、类型转换、SIGABRT、SIGBUS、SIGILL、ANR、OOM 等异常
- 崩溃类事件会导致应用退出，重启应用后崩溃信息会自动上报到控制台

#### 4.2 性能监控验证
- 点击 `启动分析` 查看冷启动、热启动验证说明，热启动需要将App切到后台再切回前台
- 点击 `页面分析` 进入页面并上下滑动，页面分析数据会在App退至后台时统一上报
- 点击 `网络分析` 进入网络分析页，可选择 OkHttp 或 HttpUrlConnection 发起请求，也可以触发网络错误和HTTP错误

#### 4.3 日志收集验证
- 点击 `日志回捞` 写入一条可回捞日志，并在EMAS控制台远程日志模块按设备创建回捞任务
- 点击 `主动上报` 写入日志并主动上报
- 部分数据会在App退至后台后统一上报，通常需要等待1到2分钟后在控制台查看

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
- 钉钉技术支持：35248489（钉钉群号）
