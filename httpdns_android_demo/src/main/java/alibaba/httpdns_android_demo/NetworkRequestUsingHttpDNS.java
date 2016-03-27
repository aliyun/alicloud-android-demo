package alibaba.httpdns_android_demo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.sdk.android.httpdns.DegradationFilter;
import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class NetworkRequestUsingHttpDNS {

    private static HttpDnsService httpdns;
    private static String accountID = "139450";
    private static final String[] TEST_URL = {"http://www.aliyun.com", "http://www.taobao.com"};

    public static void main(final Context ctx) {
        try {
            // 设置APP Context和Account ID，并初始化HTTPDNS
            httpdns = HttpDns.getService(ctx, accountID);
            // DegradationFilter用于自定义降级逻辑
            // 通过实现shouldDegradeHttpDNS方法，可以根据需要，选择是否降级
            DegradationFilter filter = new DegradationFilter() {
                @Override
                public boolean shouldDegradeHttpDNS(String hostName) {
                    // 此处可以自定义降级逻辑，例如www.taobao.com不使用HttpDNS解析
                    // 参照HttpDNS API文档，当存在中间HTTP代理时，应选择降级，使用Local DNS
                    return hostName.equals("www.taobao.com") || detectIfProxyExist(ctx);
                }
            };
            // 将filter传进httpdns，解析时会回调shouldDegradeHttpDNS方法，判断是否降级
            httpdns.setDegradationFilter(filter);
            // 设置预解析域名列表
            httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("www.aliyun.com", "www.taobao.com")));

            // 发送网络请求
            String originalUrl = "http://www.aliyun.com";
            URL url = new URL(originalUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 同步接口获取IP
            String ip = httpdns.getIpByHost(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                Log.d("HTTPDNS Demo", "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                String newUrl = originalUrl.replaceFirst(url.getHost(), ip);
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            }
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            int len;
            byte[] buff = new byte[4096];
            StringBuilder response = new StringBuilder();
            while ((len = dis.read(buff)) != -1) {
                response.append(new String(buff, 0, len));
            }
            Log.e("HTTPDNS Demo", "Response: " + response.toString());

            // 允许返回过期的IP
            httpdns.setExpiredIPEnabled(true);
            // 异步接口获取IP
            ip = httpdns.getIpByHostAsync(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                Log.d("HTTPDNS Demo", "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                String newUrl = originalUrl.replaceFirst(url.getHost(), ip);
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            }
            len = 0;
            response = new StringBuilder();
            dis = new DataInputStream(conn.getInputStream());
            while ((len = dis.read(buff)) != -1) {
                response.append(new String(buff, 0, len));
            }
            Log.e("HTTPDNS Demo", "Response: " + response.toString());

            // 测试黑名单中的域名
            ip = httpdns.getIpByHostAsync("www.taobao.com");
            if (ip == null) {
                Log.d("HTTPDNS Demo", "由于在降级策略中过滤了www.taobao.com，无法从HTTPDNS服务中获取对应域名的IP信息");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测系统是否已经设置代理，请参考HttpDNS API文档。
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
