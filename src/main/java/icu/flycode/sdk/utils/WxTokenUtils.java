package icu.flycode.sdk.utils;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 获取微信token
 */
public class WxTokenUtils {

    private static final String GRANT_TYPE = "client_credential";

    private static final String URL_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s";

    /**
     * 获取token
     *
     * @param appId
     * @param secret
     * @return
     */
    public static String getAccessToken(String appId, String secret) {
        String urlStr = String.format(URL_TEMPLATE, GRANT_TYPE, appId, secret);
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                }
                bufferedReader.close();
                System.out.println("content = " + content);

                Token token = JSON.parseObject(content.toString(), Token.class);
                return token.getAccess_token();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * 返回的JSON数据格式
     */
    @Data
    public class Token {
        /**
         * 获取到的凭证
         */
        private String access_token;
        /**
         * 凭证有效时间，单位：秒
         */
        private String expires_in;
    }
}
