package alibaba.man_android_demo;

/**
 * Created by Pon on 2017/9/6.
 */

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.webkit.WebChromeClient;

import com.alibaba.sdk.android.man.MANHitBuilders;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;

import java.net.URLDecoder;

public class MANH5Demo extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_h5_demo);

        Button returnButton = (Button) findViewById(R.id.H5WebViewDemoReturn);
        returnButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MANH5Demo.this.finish();
            }
        } );

        WebView webView = (WebView) findViewById(R.id.h5DemoWebview);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Uri uri = Uri.parse(message);

                if ( uri.getScheme().equals( "jsbridge" )  ) {
                    if ( uri.getHost().equals( "custom" )  ) {
                        // 事件名称：play_music
                        MANHitBuilders.MANCustomHitBuilder hitBuilder = new MANHitBuilders.MANCustomHitBuilder("playmusic");
                        // 可使用如下接口设置时长：3分钟
                        hitBuilder.setDurationOnEvent(3 * 60 * 1000);
                        // 设置关联的页面名称：聆听
                        hitBuilder.setEventPage("Listen");
                        // 设置属性：类型摇滚
                        hitBuilder.setProperty("type", "rock");
                        // 设置属性：歌曲标题
                        hitBuilder.setProperty("title", "wonderful tonight");
                        // 发送自定义事件打点
                        MANService manService = MANServiceProvider.getService();
                        manService.getMANAnalytics().getDefaultTracker().send(hitBuilder.build());
                    }
                    return true;
                }
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.loadUrl("file:///android_asset/web/h5demo.html");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
