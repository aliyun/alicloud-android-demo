# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep HttpDNS classes
-keep class com.alibaba.sdk.android.httpdns.** { *; }
-keep class com.aliyun.ams.** { *; }

# Keep LittleProxy classes
-keep class org.littleshoot.proxy.** { *; }
-keep class io.netty.** { *; }

# Keep WebKit classes
-keep class androidx.webkit.** { *; }

# Keep project classes
-keep class com.aliyun.httpdns.android.demo.webview.** { *; }

# Keep Gson classes if used
-keep class com.google.gson.** { *; }

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keep class com.squareup.okhttp3.** { *; }