package alibaba.httpdns_android_demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class WebviewActivity extends Activity {
    private WebView webView;
    private static final String targetUrl = "http://www.apple.com";
    private static final String TAG = "WebviewScene";
    private static HttpDnsService httpdns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_webview);

        // 初始化httpdns
        httpdns = HttpDns.getService(getApplicationContext(), MainActivity.accountID);
        // 预解析热点域名
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("www.apple.com")));

        webView = (WebView) this.findViewById(R.id.wv_container);
        webView.setWebViewClient(new WebViewClient() {
            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme().trim();
                String method = request.getMethod();
                Map<String, String> headerFields = request.getRequestHeaders();
                String url = request.getUrl().toString();
                Log.e(TAG, "url:" + url);
                // 无法拦截body，拦截方案只能正常处理不带body的请求；
                if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                        && method.equalsIgnoreCase("get")) {
                    try {
                        URLConnection connection = recursiveRequest(url, headerFields, null);

                        if (connection == null) {
                            Log.e(TAG, "connection null");
                            return super.shouldInterceptRequest(view, request);
                        }

                        // 注*：对于POST请求的Body数据，WebResourceRequest接口中并没有提供，这里无法处理
                        String contentType = connection.getContentType();
                        String mime = getMime(contentType);
                        String charset = getCharset(contentType);
                        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
                        int statusCode = httpURLConnection.getResponseCode();
                        String response = httpURLConnection.getResponseMessage();
                        Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                        Set<String> headerKeySet = headers.keySet();
                        Log.e(TAG, "code:" + httpURLConnection.getResponseCode());
                        Log.e(TAG, "mime:" + mime + "; charset:" + charset);


                        // 无mime类型的请求不拦截
                        if (TextUtils.isEmpty(mime)) {
                            Log.e(TAG, "no MIME");
                            return super.shouldInterceptRequest(view, request);
                        } else {
                            // 二进制资源无需编码信息
                            if (!TextUtils.isEmpty(charset) || (isBinaryRes(mime))) {
                                WebResourceResponse resourceResponse = new WebResourceResponse(mime, charset, httpURLConnection.getInputStream());
                                resourceResponse.setStatusCodeAndReasonPhrase(statusCode, response);
                                Map<String, String> responseHeader = new HashMap<String, String>();
                                for (String key: headerKeySet) {
                                    // HttpUrlConnection可能包含key为null的报头，指向该http请求状态码
                                    responseHeader.put(key, httpURLConnection.getHeaderField(key));
                                }
                                resourceResponse.setResponseHeaders(responseHeader);
                                return resourceResponse;
                            } else {
                                Log.e(TAG, "non binary resource for " + mime);
                                return super.shouldInterceptRequest(view, request);
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // API < 21 只能拦截URL参数
                return super.shouldInterceptRequest(view, url);
            }
        });
        webView.loadUrl(targetUrl);
    }

    /**
     * 从contentType中获取MIME类型
     * @param contentType
     * @return
     */
    private String getMime(String contentType) {
        if (contentType == null) {
            return null;
        }
        return contentType.split(";")[0];
    }

    /**
     * 从contentType中获取编码信息
     * @param contentType
     * @return
     */
    private String getCharset(String contentType) {
        if (contentType == null) {
            return null;
        }

        String[] fields = contentType.split(";");
        if (fields.length <= 1) {
            return null;
        }

        String charset = fields[1];
        if (!charset.contains("=")) {
            return null;
        }
        charset = charset.substring(charset.indexOf("=") + 1);
        return charset;
    }

    /**
     * 是否是二进制资源，二进制资源可以不需要编码信息
     * @param mime
     * @return
     */
    private boolean isBinaryRes(String mime) {
        if (mime.startsWith("image")
                || mime.startsWith("audio")
                || mime.startsWith("video")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * header中是否含有cookie
     * @param headers
     */
    private boolean containCookie(Map<String, String> headers) {
        for (Map.Entry<String, String> headerField : headers.entrySet()) {
            if (headerField.getKey().contains("Cookie")) {
                return true;
            }
        }
        return false;
    }

    public URLConnection recursiveRequest(String path, Map<String, String> headers, String reffer) {
        HttpURLConnection conn;
        URL url = null;
        try {
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            // 异步接口获取IP
            String ip = httpdns.getIpByHostAsync(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                Log.d(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                String newUrl = path.replaceFirst(url.getHost(), ip);
                conn = (HttpURLConnection) new URL(newUrl).openConnection();

                if (headers != null) {
                    for (Map.Entry<String, String> field : headers.entrySet()) {
                        conn.setRequestProperty(field.getKey(), field.getValue());
                    }
                }
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            } else {
                return null;
            }
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(false);
            if (conn instanceof HttpsURLConnection) {
                final HttpsURLConnection httpsURLConnection = (HttpsURLConnection)conn;
                WebviewTlsSniSocketFactory sslSocketFactory = new WebviewTlsSniSocketFactory((HttpsURLConnection) conn);

                // sni场景，创建SSLScocket
                httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                // https场景，证书校验
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        String host = httpsURLConnection.getRequestProperty("Host");
                        if (null == host) {
                            host = httpsURLConnection.getURL().getHost();
                        }
                        return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                    }
                });
            }
            int code = conn.getResponseCode();// Network block
            if (needRedirect(code)) {
                // 原有报头中含有cookie，放弃拦截
                if (containCookie(headers)) {
                    return null;
                }

                String location = conn.getHeaderField("Location");
                if (location == null) {
                    location = conn.getHeaderField("location");
                }

                if (location != null) {
                    if (!(location.startsWith("http://") || location
                            .startsWith("https://"))) {
                        //某些时候会省略host，只返回后面的path，所以需要补全url
                        URL originalUrl = new URL(path);
                        location = originalUrl.getProtocol() + "://"
                                + originalUrl.getHost() + location;
                    }
                    Log.e(TAG, "code:" + code + "; location:" + location + "; path" + path);
                    return recursiveRequest(location, headers, path);
                } else {
                    // 无法获取location信息，让浏览器获取
                    return null;
                }
            } else {
                // redirect finish.
                Log.e(TAG, "redirect finish");
                return conn;
            }
        } catch (MalformedURLException e) {
            Log.w(TAG, "recursiveRequest MalformedURLException");
        } catch (IOException e) {
            Log.w(TAG, "recursiveRequest IOException");
        } catch (Exception e) {
            Log.w(TAG, "unknow exception");
        }
        return null;
    }

    private boolean needRedirect(int code) {
        return code >= 300 && code < 400;
    }

    class WebviewTlsSniSocketFactory extends SSLSocketFactory {
        private final String TAG = WebviewTlsSniSocketFactory.class.getSimpleName();
        HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        private HttpsURLConnection conn;

        public WebviewTlsSniSocketFactory(HttpsURLConnection conn) {
            this.conn = conn;
        }

        @Override
        public Socket createSocket() throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return null;
        }

        // TLS layer

        @Override
        public String[] getDefaultCipherSuites() {
            return new String[0];
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return new String[0];
        }

        @Override
        public Socket createSocket(Socket plainSocket, String host, int port, boolean autoClose) throws IOException {
            String peerHost = this.conn.getRequestProperty("Host");
            if (peerHost == null)
                peerHost = host;
            Log.i(TAG, "customized createSocket. host: " + peerHost);
            InetAddress address = plainSocket.getInetAddress();
            if (autoClose) {
                // we don't need the plainSocket
                plainSocket.close();
            }
            // create and connect SSL socket, but don't do hostname/certificate verification yet
            SSLCertificateSocketFactory sslSocketFactory = (SSLCertificateSocketFactory) SSLCertificateSocketFactory.getDefault(0);
            SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(address, port);

            // enable TLSv1.1/1.2 if available
            ssl.setEnabledProtocols(ssl.getSupportedProtocols());

            // set up SNI before the handshake
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.i(TAG, "Setting SNI hostname");
                sslSocketFactory.setHostname(ssl, peerHost);
            } else {
                Log.d(TAG, "No documented SNI support on Android <4.2, trying with reflection");
                try {
                    java.lang.reflect.Method setHostnameMethod = ssl.getClass().getMethod("setHostname", String.class);
                    setHostnameMethod.invoke(ssl, peerHost);
                } catch (Exception e) {
                    Log.w(TAG, "SNI not useable", e);
                }
            }

            // verify hostname and certificate
            SSLSession session = ssl.getSession();

            if (!hostnameVerifier.verify(peerHost, session))
                throw new SSLPeerUnverifiedException("Cannot verify hostname: " + peerHost);

            Log.i(TAG, "Established " + session.getProtocol() + " connection with " + session.getPeerHost() +
                    " using " + session.getCipherSuite());

            return ssl;
        }
    }
}
