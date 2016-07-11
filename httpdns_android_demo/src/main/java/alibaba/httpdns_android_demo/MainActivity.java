package alibaba.httpdns_android_demo;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.Toast;

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
    public final static String accountID = "139450";
    private Button normalSceneBtn;
    private Button httpsSceneBtn;
    private Button sniSceneBtn;
    private Button webviewSceneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化httpdns
        httpdns = HttpDns.getService(getApplicationContext(), accountID);
        // 预解析热点域名
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("www.apple.com")));
        // 允许过期IP以实现懒加载策略
        httpdns.setExpiredIPEnabled(true);

        normalSceneBtn = (Button) findViewById(R.id.normal_scene_btn);
        httpsSceneBtn = (Button) findViewById(R.id.https_scene_btn);
        sniSceneBtn = (Button) findViewById(R.id.sni_scene_btn);
        webviewSceneBtn = (Button) findViewById(R.id.webview_scene_btn);

        normalSceneBtn.setOnClickListener(sceneClickListener);
        httpsSceneBtn.setOnClickListener(sceneClickListener);
        sniSceneBtn.setOnClickListener(sceneClickListener);
        webviewSceneBtn.setOnClickListener(sceneClickListener);

    }

    private View.OnClickListener sceneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            if (view == normalSceneBtn) {
                intent.setClass(getApplicationContext(), NormalActivity.class);
            } else if (view == httpsSceneBtn) {
                intent.setClass(getApplicationContext(), HttpsActivity.class);
            } else if (view == sniSceneBtn) {
                intent.setClass(getApplicationContext(), SNIActivity.class);
            } else if (view == webviewSceneBtn) {
                intent.setClass(getApplicationContext(), WebviewActivity.class);
            }
            startActivity(intent);
        }
    };

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
