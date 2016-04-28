# 云推送用户体验Demo Android版-push2.0

## 云推送 安卓Demo QuickStart

#### 0x00 前⾔

看到该⽂档，说明您已经准备开始体验阿里云推送的便捷服务，在将`SDK`应⽤于您的`App`之前，建议您先使用该`App Demo`简单的熟悉⼀下SDK的使用⽅法和效果。根据下⾯的步骤进⾏操作，您最后可以制作出⼀个可以运行的`App Demo`，Let's go！

#### 0x01 注意事项

⾸先要告诉您的是，使用本`Demo App`体验云推送服务的时候，最好在您的账号中建⽴⼀个全新的`App`，专用于体验和测试本`Demo App`，防⽌测试的过程中，由于不慎操作，意外的把⼀些测试消息推送到您的用户终端中，导致您的损失。当然，创建`App`的步骤，您已经很熟悉了，那么我们先建立⼀个专⻔用于测试`Demo`的`App`，再继续我们的操作吧～

#### 0x02 需要准备的材料

- `AppKey`/`AppSecret`： 创建`App`后，在『应用证书』即可拿到
- 在【App详情】页的【应用配置】中将新建应用的`package`修改为： `com.alibaba.cloudpushdemo`
- `OneSDK` [在下一步操作详细说明]
- `App Demo`源码

#### 0x03 修改代码 & 打包`APK`

在`IDE`（`Android Studio`）中导入源码之后，打包，然后在【SDK下载】⻚面中上传apk包，之后下载最新的`OneSDK` 包。

由于源码中已经付了一份`OneSDK`（可能不是新版），只需要修改`AndroidManifest.xml`中的`com.alibaba.app.appkey`和`com.alibaba.app.appsecret`（上一步已经准备好）。
接下来编译出`APK`包安装到手机使用，具体使用方式见下方【Demo使用详解】。

#### 0x04 补充

- ￼源码中最小编译安卓版本为`9`，`Gradle`版本为`2.2.1`
- 如果推送消息和推送通知出现问题，请访问 诊断助⼿ 并将简单诊断结果截图交给客服。

￼
## Demo使用详解

#### 1、首页：请重点关注 查看`DeviceID`，设置中心

![](docs/push_demo_index.png)

#### 2、查看`DeviceID`：可以调用`SDK`接口获取设备`ID`，在控制台可以查看设备状态，单独发送通知/消息给该设备。

```java
// DeviceActivity
AlibabaSDK.getService(CloudPushService.class).getDeviceId();
```

![](docs/device_id.png)
![](docs/congsole_device_status.png)

#### 3、设置中心：将设备绑定`Account`和`Tag`，可以根据账号和`Tag`来推送消息

![](docs/setting.png)

```java
// SettingsActivity
AlibabaSDK.getService(CloudPushService.class).bindAccount(useraccount, new CommonCallback()
AlibabaSDK.getService(CloudPushService.class).addTag(userlabel, new CommonCallback()
```

#### 4、接收消息 & 通知

![](docs/push_message_to_device.png)

![](docs/push_notification_to_account.png)

