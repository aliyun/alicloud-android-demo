package alibaba.mdns_android_demo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.alibaba.sdk.android.httpdns.util.HttpDnsLog;
import com.alibaba.sdk.android.tds.util.TDSLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MDNSAndroidDemo {

    public static final String TAG = "MDNSAndroidDemo";
    public static void main(final Context ctx) {
        HttpDnsLog.enableLog();
        TDSLog.enableLog();
        try {
            /**
             * 初始化Mobile DNS服务
             */
            // 初始化OneSDK
            AlibabaSDK.asyncInit(ctx, new InitResultCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "init onesdk success");
                    // AlibabaSDK初始化成功后，设置预解析域名
                    ArrayList<String> hostList = new ArrayList<String>();
                    hostList.add("www.aliyun.com");
                    hostList.add("www.taobao.com");
                    // 获取MDNS服务
                    HttpDnsService httpdns = AlibabaSDK.getService(HttpDnsService.class);
                    // 初始化MDNS服务
                    httpdns.init(ctx);
                    // 预解析域名
                    httpdns.setPreResolvedHosts(hostList);
                }

                @Override
                public void onFailure(int code, String message) {
                    Log.e(TAG, "init onesdk failed : " + message);
                }
            });

            Thread.sleep(3000);

            /**
             * 在网络请求中使用MDNS服务
             */
            String url = "http://www.aliyun.com";
            HttpURLConnection conn = getHttpURLConnection(url, ctx);
            if (conn.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String readLine;
                StringBuffer response = new StringBuffer();
                while (null != (readLine = bufferedReader.readLine())) {
                    response.append(readLine);
                }
                bufferedReader.close();
                System.out.println(response.toString());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 获取HttpURLConnection的封装函数
     */
    public static HttpURLConnection getHttpURLConnection(String urlString, Context appContext)
            throws IOException {
        URL url = new URL(urlString);

        // 在代理场景下降级为原生接口
        if (!detectIfProxyExist(appContext)) {
            HttpURLConnection conn = null;
            HttpDnsService httpdnsService = AlibabaSDK.getService(HttpDnsService.class);
            // 异步进行MDNS解析
            String dstIp = httpdnsService.getIpByHostAsync(url.getHost());
            // 从MDNS服务获取到解析的IP
            if (dstIp != null) {
                System.out.println("get IP(" + dstIp + ") for " + url.getHost() + " from MDNS");
                // 创建IP开头的访问url
                urlString = urlString.replaceFirst(url.getHost(), dstIp);
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                // 设置HTTP请求头HOST域为目标地址的域名，详情参见SDK文档5.1章节
                conn.setRequestProperty("Host", url.getHost());
                return conn;
            }
        }
        // 返回原生HttpURLConnection
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * 检测系统是否已经设置代理
     */
    public static boolean detectIfProxyExist(Context ctx) {
        boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyHost;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyHost = System.getProperty("http.proxyHost");
            String port = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt(port != null ? port : "-1");
        } else {
            proxyHost = android.net.Proxy.getHost(ctx);
            proxyPort = android.net.Proxy.getPort(ctx);
        }
        return proxyHost != null && proxyPort != -1;
    }
}
