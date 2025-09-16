package com.aliyun.emas.android.devops.demo.config;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by jason on 18/4/10.
 */

public class EMASInfo implements Serializable{
    public String AppKey;
    public String AppSecret;
    public String ChannelID;
    public boolean UseHTTP = true;
    public String StartActivity;
    public AccsInfo ACCS;
    public NetworkInfo Network;
    public MtopInfo MTOP;
    public ZCacheInfo ZCache;
    public HAInfo HA;

    public static class AccsInfo implements Serializable{
        public String Domain;

        @Override
        public String toString() {
            return "AccsInfo{" +
                    "Domain='" + Domain + '\'' +
                    '}';
        }
    }

    public static class NetworkInfo implements Serializable{
        public Map<String, String> IPStrategy;

        @Override
        public String toString() {
            return "NetworkInfo{" +
                    "IPStrategy=" + IPStrategy +
                    '}';
        }
    }

    public static class MtopInfo implements Serializable{
        public String Domain;
        public String APIDomain;

        @Override
        public String toString() {
            return "MtopInfo{" +
                    "Domain='" + Domain + '\'' +
                    ", APIDomain='" + APIDomain + '\'' +
                    '}';
        }
    }

    public static class ZCacheInfo implements Serializable{
        public String URL;

        @Override
        public String toString() {
            return "ZCacheInfo{" +
                    "URL='" + URL + '\'' +
                    '}';
        }
    }

    public static class HAInfo implements Serializable{
        public String OSSBucketName;
        public String UniversalHost;
        public String RSAPublicKey;

        @Override
        public String toString() {
            return "HAInfo{" +
                    "OSSBucketName='" + OSSBucketName + '\'' +
                    ", UniversalHost='" + UniversalHost + '\'' +
                    ", RSAPublicKey='" + RSAPublicKey + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EMASInfo{" +
                "AppKey='" + AppKey + '\'' +
                ", AppSecret='" + AppSecret + '\'' +
                ", ChannelID='" + ChannelID + '\'' +
                ", UseHTTP='" + UseHTTP + '\'' +
                ", StartActivity='" + StartActivity + '\'' +
                ", ACCS=" + ACCS +
                ", Network=" + Network +
                ", MTOP=" + MTOP +
                ", ZCache=" + ZCache +
                ", HA=" + HA +
                '}';
    }
}
