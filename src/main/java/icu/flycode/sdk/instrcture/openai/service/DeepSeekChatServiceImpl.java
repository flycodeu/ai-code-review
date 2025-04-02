package icu.flycode.sdk.instrcture.openai.service;

import com.alibaba.fastjson2.JSON;
import icu.flycode.sdk.config.GlobalConfigManager;
import icu.flycode.sdk.domain.Chat.*;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;
import icu.flycode.sdk.instrcture.openai.IOpenAiService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DeepSeekChatServiceImpl implements IOpenAiService {

    @Override
    public ChatCompletionSyncResponse getCodeReview(ChatCompletionRequest chatCompletionRequest) throws Exception {
        // 获取数据
        WechatInfo wechatInfo = GlobalConfigManager.getInstance().getWechatInfo();
        GitInfo gitInfo = GlobalConfigManager.getInstance().getGitInfo();
        OpenAiInfo openAiInfo = GlobalConfigManager.getInstance().getOpenAiInfo();

        // 1. 建立连接
        URL url = new URL(openAiInfo.getApiHost());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + openAiInfo.getApiKey());
        connection.setDoOutput(true);

        // 2. 获取返回
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line);
        }

        bufferedReader.close();
        connection.disconnect();
        // 3. 解析数据
        return JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
    }
}
