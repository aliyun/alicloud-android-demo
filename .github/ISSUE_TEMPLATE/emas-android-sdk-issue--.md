---
name: EMAS Android SDK issue模板
about: EMAS Android SDK issue模板
title: ''
labels: ''
assignees: ''

---

1. 产品：比如 移动推送 或者 移动热修复
2. sdk版本：可以贴上sdk的依赖树，比如

```
#./gradlew :app:dependencies
+--- com.aliyun.ams:alicloud-android-push:3.2.3
|    +--- com.aliyun.ams:alicloud-android-agoo:4.0.1-emas
|    |    \--- com.taobao.android:accs_sdk_taobao:4.0.1-emas
|    +--- com.taobao.android:accs_sdk_taobao:4.0.1-emas
|    +--- com.taobao.android:networksdk:3.5.5.2-open
|    +--- com.taobao.android:tnet4android:3.1.14.7-all
|    +--- com.aliyun.ams:alicloud-android-utils:1.1.6.4
|    |    \--- com.aliyun.ams:alicloud-android-ut:5.4.3 -> 5.4.4
|    |         \--- com.aliyun.ams:alicloud-android-utdid:2.5.1-proguard
|    +--- com.aliyun.ams:alicloud-android-ut:5.4.4 (*)
|    +--- com.aliyun.ams:alicloud-android-beacon:1.0.4.3
|    |    \--- com.aliyun.ams:alicloud-android-utdid:1.1.5.4 -> 2.5.1-proguard
|    \--- com.aliyun.ams:alicloud-android-utdid:2.5.1-proguard
+--- com.aliyun.ams:alicloud-android-third-push:3.2.3
+--- com.aliyun.ams:alicloud-android-third-push-fcm:3.2.3
|    \--- com.google.firebase:firebase-messaging:17.6.0
|         +--- com.google.android.gms:play-services-basement:16.0.1
|         |    \--- com.android.support:support-v4:26.1.0 -> 27.1.1 (*)
|         +--- com.google.android.gms:play-services-tasks:16.0.1
|         |    \--- com.google.android.gms:play-services-basement:16.0.1 (*)
|         +--- com.google.firebase:firebase-common:16.1.0
|         |    +--- com.google.android.gms:play-services-basement:16.0.1 (*)
|         |    +--- com.google.android.gms:play-services-tasks:16.0.1 (*)
|         |    \--- com.google.auto.value:auto-value-annotations:1.6
|         +--- com.google.firebase:firebase-iid:17.1.2
|         |    +--- com.google.android.gms:play-services-basement:16.0.1 (*)
|         |    +--- com.google.android.gms:play-services-stats:16.0.1
|         |    |    \--- com.google.android.gms:play-services-basement:16.0.1 (*)
|         |    +--- com.google.android.gms:play-services-tasks:16.0.1 (*)
|         |    +--- com.google.firebase:firebase-common:16.1.0 (*)
|         |    \--- com.google.firebase:firebase-iid-interop:16.0.1
|         |         +--- com.google.android.gms:play-services-base:16.0.1
|         |         |    +--- com.google.android.gms:play-services-basement:16.0.1 (*)
|         |         |    \--- com.google.android.gms:play-services-tasks:16.0.1 (*)
|         |         \--- com.google.android.gms:play-services-basement:16.0.1 (*)
|         \--- com.google.firebase:firebase-measurement-connector:17.0.1
|              \--- com.google.android.gms:play-services-basement:16.0.1 (*)
+--- com.aliyun.ams:alicloud-android-third-push-huawei:3.2.3
|    +--- com.aliyun.ams:huawei-push:2.6.3.305
|    \--- com.aliyun.ams:huawei-push-base:2.6.3.305
+--- com.aliyun.ams:alicloud-android-third-push-oppo:3.2.3
|    \--- com.aliyun.ams:opush:2.1.0-fix
+--- com.aliyun.ams:alicloud-android-third-push-vivo:3.2.3
|    \--- com.aliyun.ams:third_vivopush:2.9.0.1
+--- com.aliyun.ams:alicloud-android-third-push-meizu:3.2.3
|    \--- com.aliyun.ams:meizu-push:3.9.7
+--- com.aliyun.ams:alicloud-android-third-push-xiaomi:3.2.3
|    \--- com.xiaomi.mipush.sdk:mipush:3.8.2
```

3. 设备信息：品牌、机型、android版本
4. 问题描述：
5. 复现步骤：
6. 日志：设置日志等级，记录从应用启动到问题发送的日志
