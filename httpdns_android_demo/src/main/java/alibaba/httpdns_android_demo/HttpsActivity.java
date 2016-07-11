package alibaba.httpdns_android_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HttpsActivity extends AppCompatActivity {
    private static final String TAG = "HttpsScene";
    private static HttpDnsService httpdns;
    private HttpsURLConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https);
        getSupportActionBar().setTitle(R.string.https_scene);

        // 初始化httpdns
        httpdns = HttpDns.getService(getApplicationContext(), MainActivity.accountID);
        // 预解析热点域名
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("m.taobao.com")));

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsActivity.this.connectHttps();
            }
        }).start();
    }

    private void connectHttps() {
        try {
            String originalUrl = "https://m.taobao.com/?sprefer=sypc00";
            URL url = new URL(originalUrl);
            conn = (HttpsURLConnection) url.openConnection();
            // 同步接口获取IP
            String ip = httpdns.getIpByHostAsync(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                Log.d(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                String newUrl = originalUrl.replaceFirst(url.getHost(), ip);
                conn = (HttpsURLConnection) new URL(newUrl).openConnection();
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            }
            conn.setHostnameVerifier(new HostnameVerifier() {
                /*
                 * 关于这个接口的说明，官方有文档描述：
                 * This is an extended verification option that implementers can provide.
                 * It is to be used during a handshake if the URL's hostname does not match the
                 * peer's identification hostname.
                 *
                 * 使用HTTPDNS后URL里设置的hostname不是远程的主机名(如:m.taobao.com)，与证书颁发的域不匹配，
                 * Android HttpsURLConnection提供了回调接口让用户来处理这种定制化场景。
                 * 在确认HTTPDNS返回的源站IP与Session携带的IP信息一致后，您可以在回调方法中将待验证域名替换为原来的真实域名进行验证。
                 *
                 */
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    String host = conn.getRequestProperty("Host");
                    if (null == host) {
                        host = conn.getURL().getHost();
                    }
                    return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                }
            });
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            int len;
            byte[] buff = new byte[4096];
            StringBuilder response = new StringBuilder();
            while ((len = dis.read(buff)) != -1) {
                response.append(new String(buff, 0, len));
            }
            Log.d(TAG, "Response: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
