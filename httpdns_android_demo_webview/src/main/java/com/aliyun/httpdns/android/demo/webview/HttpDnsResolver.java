package com.aliyun.httpdns.android.demo.webview;

import android.content.Context;
import android.util.Log;
import org.littleshoot.proxy.HostResolver;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.net.InetAddress;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.alibaba.sdk.android.httpdns.RequestIpType;
import com.alibaba.sdk.android.httpdns.HTTPDNSResult;

/**
 * 自定义DNS解析器，使用阿里云HTTPDNS SDK
 * 实现LittleProxy的HostResolver接口
 * 核心功能：每次重新查询HTTPDNS，记录失败IP避免重试，失败后降级到系统DNS
 */
public class HttpDnsResolver implements HostResolver {

    private static final String TAG = "HttpDnsResolver";

    private HttpDnsService httpDnsService;

    // 只记录每个域名的不可用IP集合
    private final ConcurrentHashMap<String, Set<String>> unavailableIps = new ConcurrentHashMap<>();

    private final ExecutorService dnsExecutor = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "DNS-Resolver");
        thread.setDaemon(true);
        return thread;
    });

    private final Context context;
    
    public HttpDnsResolver(Context context) {
        this.context = context.getApplicationContext();
        initializeHttpDnsService();
    }

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
        try {
            Log.d(TAG, "Resolving " + host + ":" + port);

            if (WebviewProxyApplication.isValid() && httpDnsService != null) {
                List<String> httpdnsIps = queryHttpDnsWithAllIps(host);
                if (!httpdnsIps.isEmpty()) {
                    Log.i(TAG, "HTTPDNS resolved " + host + " to " + httpdnsIps.size() + " IPs");

                    Set<String> unavailable = unavailableIps.get(host);
                    for (String ip : httpdnsIps) {
                        if (unavailable == null || !unavailable.contains(ip)) {
                            Log.d(TAG, "Using HTTPDNS IP: " + host + " -> " + ip);
                            return new InetSocketAddress(ip, port);
                        }
                    }

                    Log.w(TAG, "All HTTPDNS IPs unavailable for " + host + ", fallback to system DNS");
                } else {
                    Log.d(TAG, "HTTPDNS returned empty result for " + host + ", fallback to system DNS");
                }
            } else {
                Log.d(TAG, "HTTPDNS not available, using system DNS for " + host);
            }

            List<String> systemIps = performSystemDnsQueryAsync(host);
            if (!systemIps.isEmpty()) {
                Log.i(TAG, "System DNS resolved " + host + " to " + systemIps.size() + " IPs");
                String systemIp = systemIps.get(0);
                Log.d(TAG, "Using system DNS IP: " + host + " -> " + systemIp);
                return new InetSocketAddress(systemIp, port);
            }

            throw new UnknownHostException("All DNS queries failed for " + host);

        } catch (Exception e) {
            Log.e(TAG, "Failed to resolve " + host, e);
            throw new UnknownHostException("Failed to resolve " + host + ": " + e.getMessage());
        }
    }
    
    /**
     * 异步执行系统DNS查询
     * 这个方法现在只在HTTPDNS完全失败时才会被调用
     */
    private List<String> performSystemDnsQueryAsync(String host) {
        try {
            Future<List<String>> future = dnsExecutor.submit(() -> {
                try {
                    return performSystemDnsQuerySync(host);
                } catch (Exception e) {
                    Log.e(TAG, "System DNS query failed for " + host, e);
                    throw new RuntimeException(e);
                }
            });

            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, "System DNS query timeout for " + host, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 同步执行系统DNS查询
     * 返回所有可用的IP地址
     */
    private List<String> performSystemDnsQuerySync(String host) throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName(host);
        if (addresses.length > 0) {
            List<String> ips = new ArrayList<>();
            for (InetAddress addr : addresses) {
                if (addr.getHostAddress() != null) {
                    ips.add(addr.getHostAddress());
                }
            }
            Log.d(TAG, "System DNS resolved " + host + " to " + String.join(", ", ips));
            return ips;
        } else {
            throw new UnknownHostException("No IP found for " + host);
        }
    }
    
    private List<String> queryHttpDnsWithAllIps(String host) {
        try {
            HTTPDNSResult httpdnsResult = null;

            if (httpDnsService != null) {
                httpdnsResult = httpDnsService.getHttpDnsResultForHostSyncNonBlocking(
                    host,
                    RequestIpType.auto
                );
            }

            if (httpdnsResult != null) {
                List<String> allIps = new ArrayList<>();

                if (httpdnsResult.getIps() != null && httpdnsResult.getIps().length > 0) {
                    allIps.addAll(Arrays.asList(httpdnsResult.getIps()));
                }

                if (httpdnsResult.getIpv6s() != null && httpdnsResult.getIpv6s().length > 0) {
                    allIps.addAll(Arrays.asList(httpdnsResult.getIpv6s()));
                }

                if (!allIps.isEmpty()) {
                    int ttl = httpdnsResult.getTtl();
                    Log.i(TAG, "HTTPDNS resolved " + host + " to " + allIps.size() + " IPs, TTL: " + ttl + "s");
                    return allIps;
                }

                Log.d(TAG, "HTTPDNS returned empty result for " + host);
                return new ArrayList<>();
            } else {
                Log.d(TAG, "HTTPDNS no result for " + host);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e(TAG, "HTTPDNS query failed for " + host, e);
            return new ArrayList<>();
        }
    }
    
    private void initializeHttpDnsService() {
        try {
            if (WebviewProxyApplication.isValid()) {
                Log.i(TAG, "Initializing HTTPDNS service");

                httpDnsService = HttpDns.getService(WebviewProxyApplication.accountId);

                if (httpDnsService != null) {
                    Log.i(TAG, "HTTPDNS service initialized successfully");
                    Log.d(TAG, "AccountID: " + WebviewProxyApplication.accountId);
                } else {
                    Log.w(TAG, "HTTPDNS service initialization failed, using system DNS");
                }
            } else {
                Log.d(TAG, "HTTPDNS config invalid, using system DNS");
            }
        } catch (Exception e) {
            Log.e(TAG, "HTTPDNS service initialization failed", e);
            httpDnsService = null;
        }
    }
    
    public boolean isHttpDnsReady() {
        return httpDnsService != null && WebviewProxyApplication.isValid();
    }

    public void cleanup() {
        if (dnsExecutor != null && !dnsExecutor.isShutdown()) {
            dnsExecutor.shutdown();
            try {
                if (!dnsExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    dnsExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                dnsExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        unavailableIps.clear();
        Log.i(TAG, "HttpDnsResolver resources cleaned up");
    }
    public void markIpUnavailable(String host, String ip) {
        unavailableIps.computeIfAbsent(host, k -> ConcurrentHashMap.newKeySet()).add(ip);
        Log.w(TAG, "Marked IP as unavailable: " + host + " -> " + ip);
    }
}