package alibaba.httpdns_restful_demo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkRequestUsingHttpDNS {

    private static HttpDNS httpdnsService = HttpDNS.getInstance();

    public static void main(Context ctx) {
        try {
            HttpDNS.HttpDNSLog.enableLog(true);

            byte[] buff = new byte[4096];
            HttpURLConnection conn = getHttpURLConnection("http://m.aliyun.com", ctx);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                StringBuilder sb = new StringBuilder();
                while ((len = dis.read(buff)) != -1) {
                    sb.append(new String(buff, 0, len));
                }
                Log.d("HttpDNS Demo", "Get result: " + sb.toString());
            }
            conn.disconnect();

            Thread.sleep(3000);

            conn = getHttpURLConnection("http://m.aliyun.com", ctx);
            responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                StringBuilder sb = new StringBuilder();
                while ((len = dis.read(buff)) != -1) {
                    sb.append(new String(buff, 0, len));
                }
                Log.d("HttpDNS Demo", "Get result: " + sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpURLConnection getHttpURLConnection(String urlString, Context appContext)
            throws IOException {
        URL url = new URL(urlString);

        // 默认情况下当系统使用代理时，走原生DNS逻辑。
        if (!detectIfProxyExist(appContext)) {
            HttpURLConnection conn;

            String dstIp = httpdnsService.getIpByHost(url.getHost());
            if (dstIp != null) {
                Log.d("HttpDNS Demo", "Get IP from HttpDNS, " + url.getHost() + ": " + dstIp);
                urlString = urlString.replaceFirst(url.getHost(), dstIp);
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                // 设置HTTP请求头HOST域
                conn.setRequestProperty("Host", url.getHost());
                return conn;
            }
        } else {
            Log.d("HttpDNS Demo", "Found proxy, downgrade to local DNS.");
        }

        return (HttpURLConnection) url.openConnection();
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
