package alibaba.httpdns_android_demo;

import android.app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.sdk.android.httpdns.DegradationFilter;
import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String APPLE_URL = "www.apple.com";
    private static final String TAOBAO_URL = "m.taobao.com";
    private static final String DOUBAN_URL = "dou.bz";
    private static final String ALIYUN_URL = "aliyun.com";
    private static final String HTTP_SCHEMA = "http://";
    private static final String HTTPS_SCHEMA = "https://";
    private static final String TAG = "httpdns_android_demo";

    private Button btnNormalScene;
    private Button btnHttpsScene;
    private Button btnSniScene;
    private Button btnWebviewScene;
    private Button btnPreResove;
    private Button btnSetDegrationFilter;
    private TextView tvConsoleText;
    private Handler mHandler;

    private static ExecutorService pool = Executors.newSingleThreadExecutor();
    private static final String HTTPDNS_RESULT = "httpdns_result";
    private static final int SHOW_CONSOLE_TEXT = 10000;
    private static HttpDnsService httpdns;
    // 可以更换成您的accountID
    public static final String accountID = "139450";

    private void initViews() {
        btnNormalScene = (Button) findViewById(R.id.btnNormalScene);
        btnHttpsScene = (Button) findViewById(R.id.btnHttpsScene);
        btnSniScene = (Button) findViewById(R.id.btnSniScene);
        btnWebviewScene = (Button) findViewById(R.id.btnWebviewScene);
        btnPreResove = (Button) findViewById(R.id.btnPreResove);
        btnSetDegrationFilter = (Button) findViewById(R.id.btnDegrationFilter);
        tvConsoleText = (TextView) findViewById(R.id.tvConsoleText);

        btnNormalScene.setOnClickListener(this);
        btnHttpsScene.setOnClickListener(this);
        btnSniScene.setOnClickListener(this);
        btnWebviewScene.setOnClickListener(this);
        btnPreResove.setOnClickListener(this);
        btnSetDegrationFilter.setOnClickListener(this);
    }

    private void initHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_CONSOLE_TEXT:
                        tvConsoleText.append(msg.getData().getString(HTTPDNS_RESULT) + "\n");
                        break;
                }
            }
        };
    }

    private void initHttpDns() {
        // 初始化httpdns
        httpdns = HttpDns.getService(getApplicationContext(), accountID);
        this.setPreResoveHosts();
        // 允许过期IP以实现懒加载策略
        httpdns.setExpiredIPEnabled(true);
    }

    private void sendConsoleMessage(String message) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString(HTTPDNS_RESULT, message);
            msg.setData(bundle);
            msg.what = SHOW_CONSOLE_TEXT;
            mHandler.sendMessage(msg);
        }
    }

    private void clearConsoleText() {
        if (tvConsoleText != null) {
            tvConsoleText.setText("");
        }
    }

    /**
     * 通过IP直连方式发起普通请求示例
     * 1. 通过IP直连时,为绕开服务域名检查,需要在Http报头中设置Host字段
     */
    private void normalReqeust() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 发送网络请求
                    String originalUrl = HTTP_SCHEMA + TAOBAO_URL;
                    URL url = new URL(originalUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 异步接口获取IP
                    String ip = httpdns.getIpByHostAsync(url.getHost());

                    if (ip != null) {
                        // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                        Log.d(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                        sendConsoleMessage("Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");

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

                    Log.d(TAG, "Response: " + response.toString());
                    sendConsoleMessage("Get response from " + url.getHost() + ". Please check response detail from log.");
                } catch (Throwable throwable) {
                    Log.e(TAG, "normal request failed.", throwable);
                    sendConsoleMessage("Normal request failed." + " Please check error detail from log.");
                }
            }
        });

    }

    /**
     * 通过IP直连方式发起https请求示例
     * 1. 通过IP直连时,为绕开服务域名检查,需要在Http报头中设置Host字段
     * 2. 为通过证书检查,需要自定义证书验证逻辑
     */
    private void httpsRequest() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String originalUrl = HTTPS_SCHEMA + TAOBAO_URL + "/?sprefer=sypc00";
                    URL url = new URL(originalUrl);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    // 同步接口获取IP
                    String ip = httpdns.getIpByHostAsync(url.getHost());
                    if (ip != null) {
                        // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                        Log.d(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                        sendConsoleMessage("Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");

                        String newUrl = originalUrl.replaceFirst(url.getHost(), ip);
                        conn = (HttpsURLConnection) new URL(newUrl).openConnection();
                        // 设置HTTP请求头Host域
                        conn.setRequestProperty("Host", url.getHost());
                    }
                    final HttpsURLConnection finalConn = conn;
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
                            String host = finalConn.getRequestProperty("Host");
                            if (null == host) {
                                host = finalConn.getURL().getHost();
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
                    sendConsoleMessage("Get reponse from " + url.getHost() + ". Please check response detail from log.");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendConsoleMessage("Get reponse failed. Please check error detail from log.");
                }
            }
        });
    }

    private boolean needRedirect(int code) {
        return code >= 300 && code < 400;
    }

    /**
     * 通过IP直连方式发起SNI请求代码示例
     */
    private void sniRequest() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                recursiveRequest(HTTPS_SCHEMA + DOUBAN_URL + "/23o8PS", null);
            }
        });
    }

    private void recursiveRequest(String path, String reffer) {
        URL url;
        HttpsURLConnection conn = null;
        try {
            url = new URL(path);
            conn = (HttpsURLConnection) url.openConnection();
            // 同步接口获取IP
            String ip = httpdns.getIpByHostAsync(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                Log.d(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                sendConsoleMessage("Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                String newUrl = path.replaceFirst(url.getHost(), ip);
                conn = (HttpsURLConnection) new URL(newUrl).openConnection();
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            }
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(false);
            HttpDnsTLSSniSocketFactory sslSocketFactory = new HttpDnsTLSSniSocketFactory(conn);
            conn.setSSLSocketFactory(sslSocketFactory);
            final HttpsURLConnection finalConn = conn;
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
                    String host = finalConn.getRequestProperty("Host");
                    if (null == host) {
                        host = finalConn.getURL().getHost();
                    }
                    return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                }
            });
            int code = conn.getResponseCode();// Network block
            if (needRedirect(code)) {
                //临时重定向和永久重定向location的大小写有区分
                String location = conn.getHeaderField("Location");
                if (location == null) {
                    location = conn.getHeaderField("location");
                }
                if (!(location.startsWith(HTTP_SCHEMA) || location
                        .startsWith(HTTPS_SCHEMA))) {
                    //某些时候会省略host，只返回后面的path，所以需要补全url
                    URL originalUrl = new URL(path);
                    location = originalUrl.getProtocol() + "://"
                            + originalUrl.getHost() + location;
                }
                recursiveRequest(location, path);
            } else {
                // redirect finish.
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                int len;
                byte[] buff = new byte[4096];
                StringBuilder response = new StringBuilder();
                while ((len = dis.read(buff)) != -1) {
                    response.append(new String(buff, 0, len));
                }
                Log.d(TAG, "Response: " + response.toString());
                sendConsoleMessage("Get reponse from " + url.getHost() + ". Please check response detail from log.");
            }
        } catch (MalformedURLException e) {
            Log.w(TAG, "recursiveRequest MalformedURLException", e);
        } catch (IOException e) {
            Log.w(TAG, "recursiveRequest IOException");
        } catch (Exception e) {
            Log.w(TAG, "unknow exception");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 设置预解析域名列表代码示例
     */
    private void setPreResoveHosts() {
        // 设置预解析域名列表
        // 可以替换成您在后台配置的域名
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList(APPLE_URL, ALIYUN_URL, TAOBAO_URL,  DOUBAN_URL)));
        sendConsoleMessage("设置预解析域名成功");
    }

    /**
     * 自定义降级逻辑代码示例
     */
    private void setDegrationFilter() {
        DegradationFilter filter = new DegradationFilter() {
            @Override
            public boolean shouldDegradeHttpDNS(String hostName) {
                // 此处可以自定义降级逻辑，例如www.taobao.com不使用HttpDNS解析
                // 参照HttpDNS API文档，当存在中间HTTP代理时，应选择降级，使用Local DNS
                return hostName.equals(DOUBAN_URL);
            }
        };
        // 将filter传进httpdns，解析时会回调shouldDegradeHttpDNS方法，判断是否降级
        httpdns.setDegradationFilter(filter);
        sendConsoleMessage("自定义降级逻辑成功");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_main);
        initViews();
        initHttpDns();
        initHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.action_clear_tips:
                this.clearConsoleText();
                break;
            case R.id.action_helper:
                startActivity(new Intent(this, HelperActivity.class));
                break;
            case R.id.action_about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNormalScene:
                this.normalReqeust();
                break;
            case R.id.btnHttpsScene:
                this.httpsRequest();
                break;
            case R.id.btnSniScene:
                this.sniRequest();
                break;
            case R.id.btnWebviewScene:
                Intent intent = new Intent(this, WebviewActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDegrationFilter:
                this.setDegrationFilter();
                break;
            case R.id.btnPreResove:
                this.setPreResoveHosts();
                break;
        }
    }
}
