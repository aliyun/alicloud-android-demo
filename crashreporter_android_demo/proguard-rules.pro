# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lixin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 3
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-overloadaggressively
-allowaccessmodification
-useuniqueclassmembernames

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class com.alibaba.ha.**{*;}
-keep class com.taobao.tlog.**{*;}
-keep class com.ut.device.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-keep public class com.alibaba.mtl.** { *;}
-keep public class com.ut.mini.** { *;}
-keep class com.alibaba.motu.crashreporter.**{ *;}
-keep class com.uc.crashsdk.**{*;}
-keep class com.ali.telescope.**{ *;}
-keep class libcore.io.**{*;}
-keep class android.app.**{*;}
-keep class dalvik.system.**{*;}
-keep class com.taobao.tao.log.**{*;}
-keep class com.taobao.android.tlog.**{*;}
-keep class com.alibaba.motu.**{*;}
-keep class com.uc.crashsdk.**{*;}
-dontwarn com.taobao.orange.**
-dontwarn com.taobao.android.**
-dontwarn com.alibaba.ha.adapter.**
-dontwarn com.taobao.monitor.adapter.**
-dontwarn com.alibaba.fastjson.**
-dontwarn com.ali.alihadeviceevaluator.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.**
