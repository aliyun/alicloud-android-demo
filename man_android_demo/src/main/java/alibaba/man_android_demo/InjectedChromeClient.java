package alibaba.man_android_demo;

import android.net.Uri;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.alibaba.sdk.android.man.MANHitBuilders;

/**
 * Created by Pon on 2017/9/6.
 */

public class InjectedChromeClient extends WebChromeClient {
    private final String TAG = "InjectedChromeClient";

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Uri uri = Uri.parse(message);

        if ( uri.getScheme() == "jsbridge" ) {
            if ( uri.getPath() == "custom" ) {
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
            return false;
        }
        return super.onJsAlert(view, url, message, result);
    }
}
