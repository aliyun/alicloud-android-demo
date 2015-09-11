package alibaba.mas_restful_demo;

import java.security.MessageDigest;
import java.util.List;

/**
 * Created by ryan on 25/8/15.
 */
public class MASRestfulEnterpriseEditionUpload {

    MASRestfulEnterpriseEditionUpload() {
        System.out.println("MASRESTful Enterprise init.");
    }

    public static void main(String appKey, String appSecret) {
        appKey = "23223100";
        appSecret = "832f592be971c1fb048814a66b74bc30";
        String charset = "UTF-8";
        // RESTful service.
        String requestURL = "http://adash.mas.aliyuncs.com:80/rest/restful";

        try {
            MASRestfulEnterpriseEditionLog log1 = new MASRestfulEnterpriseEditionLog();
            log1.client_ip = "102.35.23.5";
            log1.protocol_version = "2.3.3";
            log1.imei = "c1976429369bfe063ed8b3409db7c7e7d87196d9";
            log1.imsi = "F575328F-D1D6-4FBD-B2F8-2CF19869188C";
            log1.brand = "Apple";
            log1.cpu = "S5PC110";
            log1.device_id = "VUSi+UoDVXQDACdLLA0ki5nR";
            log1.device_model = "iPhone3";
            log1.resolution = "960*640";
            log1.carrier = "中国移动";
            log1.access = "Wi-Fi";
            log1.access_subtype = "Edge";
            log1.channel = "0";
            log1.app_key = "23223100";
            log1.app_version = "company_0.1";
            log1.long_login_nick = "hello";
            log1.user_nick = "hello";
            log1.phone_number = "";
            log1.country = "中国";
            log1.language = "english";
            log1.os = "Android";
            log1.os_version = "4.2.2";
            log1.sdk_type = "0";
            log1.sdk_version = "0";
            log1.reserve1 = "";
            log1.reserve2 = "";
            log1.reserve3 = "";
            log1.reserve4 = "";
            log1.reserve5 = "";
            log1.reserves = "";
            log1.local_time = "2015-07-20 19:56:02";
            log1.local_timestamp = "1437393362166";
            log1.page = "main";
            log1.event_id = "66602";
            log1.arg1 = "NetReqEvent";
            log1.arg2 = "";
            log1.arg3 = "3.26";
            log1.args = "";
            log1.day = "1437390000";
            log1.hour = "1437390000";
            log1.server_timestamp = "1437393477";
            log1.seq = "0";
            log1.assemblyLog();

            // Sign the log.
            MessageDigest md = MessageDigest.getInstance("MD5");
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            String type = "OfflineProcessing";
            String content = log1.getFormattedLog();

            byte[] lsignData = MultipartUtility.getSign(appKey, appSecret, type, content);
            String queryString = "?ak=" + appKey + "&s=" + MultipartUtility.toHex(lsignData);
            MultipartUtility multipart = new MultipartUtility(requestURL + queryString, charset);
            multipart.addHeaderField("Test-Header", "Header-Value");
            multipart.addFormField(type, content);

            List<String> response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
