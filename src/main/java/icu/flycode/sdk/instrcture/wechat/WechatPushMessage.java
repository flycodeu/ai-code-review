package icu.flycode.sdk.instrcture.wechat;

import com.alibaba.fastjson2.JSON;
import icu.flycode.sdk.config.GlobalConfigManager;
import icu.flycode.sdk.domain.Chat.OpenAiInfo;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.TemplateMessage;
import icu.flycode.sdk.domain.Template.WechatInfo;
import icu.flycode.sdk.domain.Template.enums.TemplateKey;
import icu.flycode.sdk.utils.WxTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 微信模板消息
 */
public class WechatPushMessage {

    public void pushWxMessage(String logUrl) throws IOException {
        WechatInfo wechatInfo = GlobalConfigManager.getInstance().getWechatInfo();
        GitInfo gitInfo = GlobalConfigManager.getInstance().getGitInfo();
        OpenAiInfo openAiInfo = GlobalConfigManager.getInstance().getOpenAiInfo();
        // 1. 获取token
        String appId = wechatInfo.getAppId();
        String sercet = wechatInfo.getSercet();
        String accessToken = WxTokenUtils.getAccessToken(appId, sercet);
        // 2. 构建url请求
        String urlStr = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setDoOutput(true);

        // 3. 编写请求数据
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setTouser(wechatInfo.getToUser());
        templateMessage.setTemplate_id(wechatInfo.getTemplateId());
        templateMessage.setUrl(logUrl);


        templateMessage.put(TemplateKey.REPO_NAME.getCode(), gitInfo.getProject());
        templateMessage.put(TemplateKey.COMMIT_AUTHOR.getCode(), gitInfo.getCommitter());
        templateMessage.put(TemplateKey.COMMIT_MESSAGE.getCode(), gitInfo.getCommitMessage());
        templateMessage.put(TemplateKey.BRANCH_NAME.getCode(), gitInfo.getBranchName());

        try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] bytes = JSON.toJSONString(templateMessage).getBytes(StandardCharsets.UTF_8);
            os.write(bytes, 0, bytes.length);
        }

        // 4. 发送请求并获取响应
        try (Scanner scanner = new Scanner(httpURLConnection.getInputStream(), StandardCharsets.UTF_8.name())) {
            String response = scanner.useDelimiter("\\A").next();
            System.out.println(response);
        }
    }
}