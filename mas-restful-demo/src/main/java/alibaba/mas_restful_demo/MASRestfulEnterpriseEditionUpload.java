package alibaba.mas_restful_demo;

import java.security.MessageDigest;
import java.util.List;

/**
 * Created by ryan on 25/8/15.
 */
public class MASRestfulEnterpriseEditionUpload {

    MASRestfulEnterpriseEditionUpload() {
        System.out.println("MASRESTful init.");
    }

    static String toHex(byte[] digest) {
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < digest.length; offset ++) {
            i = digest[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        String str = buf.toString();
        return str;
    }

    public static void main(String appkey, String appsecret) {
        System.out.println("MASRESTfulEnterpriseEdition main");
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
            log1.access = "WiFi";
            log1.access_subtype = "Edge";
            log1.channel = "0";
            log1.app_key = "23223100";
            log1.app_version = "2.3.4";
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
            log1.local_time = "2015-07-20 19:56:02";
            log1.local_timestamp = "1437393362166";
            log1.page = "main";
            log1.event_id = "66602";
            log1.arg1 = "NetReqEvent";
            log1.arg2 = "";
            log1.arg3 = "";
            log1.args = "";
            log1.day = "1437390000";
            log1.hour = "1437390000";
            log1.server_timestamp = "1437393477";
            log1.seq = "0";
            log1.assemblyLog();

            MASRestfulEnterpriseEditionLog log2 = new MASRestfulEnterpriseEditionLog();
            log2.client_ip = "102.35.23.5";
            log2.protocol_version = "2.3.3";
            log2.imei = "c1976429369bfe063ed8b3409db7c7e7d87196d9";
            log2.imsi = "F575328F-D1D6-4FBD-B2F8-2CF19869188C";
            log2.brand = "Apple";
            log2.cpu = "S5PC110";
            log2.device_id = "VUSi+UoDVXQDACdLLA0ki5nR";
            log2.device_model = "iPhone3";
            log2.resolution = "960*640";
            log2.carrier = "中国移动";
            log2.access = "WiFi";
            log2.access_subtype = "Edge";
            log2.channel = "0";
            log2.app_key = "23223100";
            log2.app_version = "2.3.4";
            log2.long_login_nick = "hello";
            log2.user_nick = "hello";
            log2.phone_number = "";
            log2.country = "中国";
            log2.language = "english";
            log2.os = "Android";
            log2.os_version = "4.2.2";
            log2.sdk_type = "0";
            log2.sdk_version = "0";
            log2.reserve1 = "";
            log2.reserve2 = "";
            log2.reserve3 = "";
            log2.reserve4 = "";
            log2.reserve5 = "";
            log2.local_time = "2015-07-20 19:56:02";
            log2.local_timestamp = "1437393362166";
            log2.page = "main";
            log2.event_id = "66603";
            log2.arg1 = "NetReqEvent";
            log2.arg2 = "Hello World";
            log2.arg3 = "";
            log2.args = "";
            log2.day = "1437390000";
            log2.hour = "1437390000";
            log2.server_timestamp = "1437393477";
            log2.seq = "0";
            log2.assemblyLog();

            // Sign the log.
            MessageDigest md = MessageDigest.getInstance("MD5");
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            String[][] strArray = {{"Raw",log1.getFormattedLog()},{"OfflineProcessing;RealTimeProcessing",log2.getFormattedLog()}};
            String lsignData = "";
            for (int j = 0; j < strArray.length; j ++) {
                String res_content=strArray[j][1];
                byte[] bytesOfMessage = res_content.getBytes("UTF-8");
                byte[] thedigest = md.digest(bytesOfMessage);
                lsignData = lsignData + strArray[j][0] + toHex(thedigest);
            }
            lsignData = appkey + lsignData;
            byte[] byteOfmd5Message = lsignData.getBytes("UTF-8");
            byte[] sign_value = md.digest(byteOfmd5Message);
            String sha1_content = appsecret + toHex(sign_value) + appsecret;
            byte[] bytesOfsha1Message = sha1_content.getBytes("UTF-8");
            byte[] sha1str = sha1.digest(bytesOfsha1Message);


            String queryString = "?ak=" + appkey + "&s=" + toHex(sha1str);
            MultipartUtility multipart = new MultipartUtility(requestURL + queryString, charset);

            multipart.addHeaderField("Test-Header", "Header-Value");
            multipart.addFormField("Raw", log1.getFormattedLog());
            multipart.addFormField("OfflineProcessing;RealTimeProcessing", log2.getFormattedLog());

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
