package alibaba.mas_restful_demo;

import java.security.MessageDigest;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by ryan on 25/8/15.
 */
public class MASRestfulRawEditionUpload {

    MASRestfulRawEditionUpload() {
        System.out.println("MASRESTful raw init.");
    }

    public static void main(String appKey, String appSecret) {
        appKey = "23223100";
        appSecret = "832f592be971c1fb048814a66b74bc30";
        String charset = "UTF-8";
        // RESTful service.
        String requestURL = "http://adash.mas.aliyuncs.com:80/rest/restful";

        try {
            String type = "Raw";
            String content = "Please input the content you want to post";

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
