package alibaba.httpdns_android_demo;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String targetUrl = "http://www.apple.com";
    private static final String TAG = "httpdns_android_demo";
    private static HttpDnsService httpdns;
    private static String accountID = "139450";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 在webview场景下使用HTTPDNS
        httpdns = HttpDns.getService(getApplicationContext(), accountID);
        // 预解析热点域名
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("www.apple.com")));
        // 允许过期IP以实现懒加载策略
        httpdns.setExpiredIPEnabled(true);
        webView = (WebView) this.findViewById(R.id.wv_container);
        webView.setWebViewClient(new WebViewClient() {
            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request != null && request.getUrl() != null && request.getMethod().equalsIgnoreCase("get")) {
                    String scheme = request.getUrl().getScheme().trim();
                    String url = request.getUrl().toString();
                    Log.d(TAG, "request.getUrl().toString(): " + url);
                    // 假设我们对所有css文件的网络请求进行httpdns解析
                    if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) && request.getUrl().toString().contains(".css")) {
                        try {
                            URL oldUrl = new URL(url);
                            URLConnection connection = oldUrl.openConnection();
                            // 异步获取域名解析结果
                            String ip = httpdns.getIpByHostAsync(oldUrl.getHost());
                            if (ip != null) {
                                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                                Log.d(TAG, "Get IP: " + ip + " for host: " + oldUrl.getHost() + " from HTTPDNS successfully!");
                                String newUrl = url.replaceFirst(oldUrl.getHost(), ip);
                                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                                // 设置HTTP请求头Host域
                                connection.setRequestProperty("Host", oldUrl.getHost());
                            }
                            Log.d(TAG, "ContentType: " + connection.getContentType());
                            return new WebResourceResponse("text/css", "UTF-8", connection.getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null) {
                    String scheme = Uri.parse(url).getScheme().trim();
                    Log.d(TAG, "url: " + url);
                    // 假设我们对所有css文件的网络请求进行httpdns解析
                    if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) && url.contains(".css")) {
                        try {
                            URL oldUrl = new URL(url);
                            URLConnection connection = oldUrl.openConnection();
                            // 异步获取域名解析结果
                            String ip = httpdns.getIpByHostAsync(oldUrl.getHost());
                            if (ip != null) {
                                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                                Log.d(TAG, "Get IP: " + ip + " for host: " + oldUrl.getHost() + " from HTTPDNS successfully!");
                                String newUrl = url.replaceFirst(oldUrl.getHost(), ip);
                                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                                // 设置HTTP请求头Host域
                                connection.setRequestProperty("Host", oldUrl.getHost());
                            }
                            Log.d(TAG, "ContentType: " + connection.getContentType());
                            return new WebResourceResponse("text/css", "UTF-8", connection.getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        });
        webView.loadUrl(targetUrl);

        // 在Native场景下使用HTTPDNS
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkRequestUsingHttpDNS.main(getApplicationContext());
            }
        }).start();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
