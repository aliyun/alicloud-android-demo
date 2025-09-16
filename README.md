## EMAS Android Demo

阿里巴巴应用研发平台（Enterprise Mobile Application Studio，简称EMAS），是面向全端场景（移动App、H5应用、小程序、Web应用、PC应用等）的一站式应用研发平台。EMAS基于广泛的云原生技术（Backend as a Service、Serverless、DevOps、低代码等），致力于为企业、开发者提供一站式的应用研发运营管理服务，涵盖开发、测试、运维、运营等应用全生命周期。

## 快速开始

1. **克隆项目**
   ```bash
   git clone https://github.com/aliyun/alicloud-android-demo.git
   ```

2. **选择产品Demo**
   - 每个子目录对应一个EMAS产品的Android Demo
   - 详细的集成说明请查看各产品目录下的README.md

3. **配置密钥**
   - HTTPDNS无需创建应用，请按照[README](httpdns_android_demo/README.md)说明进行配置
   - 登录[EMAS控制台](https://emas.console.aliyun.com/)创建应用
   - 获取对应的AppKey、AppSecret等配置信息
   - 按照各Demo的README说明进行配置


EMAS 管理地址：[EMAS控制台](https://emas.console.aliyun.com/)

SDK下载：请参考EMAS快速入门中 ->下载SDK [地址](https://help.aliyun.com/document_detail/436513.html) 


> 注：demo中的账号信息配置只为方便demo例程的运行，真实产品中，建议您使用安全黑匣子或其他方式保障密钥的安全性。



### 一、HTTPDNS
------

HTTPDNS是面向多端应用（移动端APP，PC客户端应用）的域名解析服务，解析请求基于HTTP(S)协议，有效解决了传统域名解析容易被劫持、解析不准确、更新不及时、服务不稳定等问题。

- **Android Demo**: [httpdns_android_demo](https://github.com/aliyun/alicloud-android-demo/tree/master/httpdns_android_demo)

- **Flutter Demo**: [alicloud-flutter-demo](https://github.com/aliyun/alicloud-flutter-demo)

- **产品官网**: [地址](https://www.aliyun.com/product/httpdns)

- **配置说明**: 请阅读`httpdns_android_demo/README.md`，Demo无需在控制台单独创建应用，也无需获取AppKey、AppSecret即可体验



### 二、移动推送
------

移动推送是提供给移动开发者的移动端消息推送服务，通过在App中集成推送功能，进行高效、精准、实时的App消息推送，从而使业务及时触达用户，提高用户粘性。

- **Android Demo**: [mpush_android_demo](https://github.com/aliyun/alicloud-android-demo/tree/master/mpush_android_demo)

- **React Native 插件**: [alibabacloud-push-reactnative-plugin](https://github.com/aliyun/alibabacloud-push-reactnative-plugin)

- **Flutter 插件**: [alibabacloud-push-flutter-plugin](https://github.com/aliyun/alibabacloud-push-flutter-plugin)

- **产品官网**: [地址](https://www.aliyun.com/product/cps)


### 三、应用性能监控
------

EMAS 应用监控是面向客户端的全方位监控服务平台，覆盖移动端和Web/H5端。基于阿里巴巴深厚的技术沉淀，提供稳定高效的监控服务，帮助开发者实时掌握应用性能与稳定性情况，快速构建"感知 > 定位 > 修复"运维闭环，保障应用质量，优化用户体验。

- **Android Demo**: [apm_android_demo](https://github.com/aliyun/alicloud-android-demo/tree/master/apm_android_demo)

- **产品官网**: [地址](https://www.aliyun.com/product/emascrash/apm)



### 四、移动用户反馈
------

移动用户反馈服务（Mobile Feedback）面向企业客户和移动开发者的移动应用提供 App 运营服务，用于设置 App 内部用户反馈页面、收集/管理 App 内部及外部市场的用户反馈，以便及时响应、解决用户问题、提升服务质量和用户满意度。

- **Android Demo**: [feedback_android_demo](https://github.com/aliyun/alicloud-android-demo/tree/master/feedback_android_demo)

- **产品官网**: [地址](https://www.aliyun.com/product/feedback)



### 五、移动热修复
------

移动热修复（Mobile Hotfix）是面向Android应用提供的在线热修复服务方案，产品基于阿里巴巴的Sophix技术，提供细粒度热修复能力，无需等待发版即可实时修复应用线上问题，用户全程无感知。

- **Android Demo**: [hotfix_android_demo](https://github.com/aliyun/alicloud-android-demo/tree/master/hotfix_android_demo)

- **产品官网**: [地址](https://www.aliyun.com/product/hotfix)



### 六、移动DevOps
------

移动DevOps包括云构建和云发布，是面向多端应用场景（包括但不限于移动App、H5应用、小程序、Web应用、PC应用等），通过自动化流程串联应用完整生命周期（研发、测试、灰度、分发、监控、反馈）的一站式研发支撑平台，帮助企业实现交付流程化、自动化、数字化。

- **Android Demo**: [devops_android_demo](https://github.com/aliyun/alicloud-android-demo/tree/master/devops_android_demo)

- **产品官网**: [地址](https://www.aliyun.com/product/emascrash/mobile_devops)
