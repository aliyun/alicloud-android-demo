package alibaba.httpdns_android_demo;

import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SNIActivity extends AppCompatActivity {
    private static final String TAG = "SNIScene";
    private static HttpDnsService httpdns;
    private HttpsURLConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sni);
        getSupportActionBar().setTitle(R.string.sni_scene);
        // 初始化httpdns
        httpdns = HttpDns.getService(getApplicationContext(), MainActivity.accountID);
        // 预解析热点域名
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("dou.bz", "www.douban.com")));
        new Thread(new Runnable() {
            @Override
            public void run() {
                SNIActivity.this.recursiveRequest("https://dou.bz/23o8PS", null);
            }
        }).start();
    }

    public void recursiveRequest(String path, String reffer) {
        URL url = null;
        try {
            url = new URL(path);
            conn = (HttpsURLConnection) url.openConnection();
            // 同步接口获取IP
            String ip = httpdns.getIpByHostAsync(url.getHost());
            if (ip != null) {
                // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                Log.d(TAG, "Get IP: " + ip + " for host: " + url.getHost() + " from HTTPDNS successfully!");
                String newUrl = path.replaceFirst(url.getHost(), ip);
                conn = (HttpsURLConnection) new URL(newUrl).openConnection();
                // 设置HTTP请求头Host域
                conn.setRequestProperty("Host", url.getHost());
            }
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(false);
            TlsSniSocketFactory sslSocketFactory = new TlsSniSocketFactory(conn);
            conn.setSSLSocketFactory(sslSocketFactory);
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
            int code = conn.getResponseCode();// Network block
            if (needRedirect(code)) {
                //临时重定向和永久重定向location的大小写有区分
                String location = conn.getHeaderField("Location");
                if (location == null) {
                    location = conn.getHeaderField("location");
                }
                if (!(location.startsWith("http://") || location
                        .startsWith("https://"))) {
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
            }
        } catch (MalformedURLException e) {
            Log.w(TAG, "recursiveRequest MalformedURLException");
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

    private boolean needRedirect(int code) {
        return code >= 300 && code < 400;
    }
}

// 自己定制SSLSocketFactory，在createSocket时替换为HTTPDNS的IP，并进行SNI/HostNameVerify配置
class TlsSniSocketFactory extends SSLSocketFactory {
    private final String TAG = TlsSniSocketFactory.class.getSimpleName();
    HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    private HttpsURLConnection conn;

    public TlsSniSocketFactory(HttpsURLConnection conn) {
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
