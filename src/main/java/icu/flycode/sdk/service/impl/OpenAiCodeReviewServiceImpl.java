package icu.flycode.sdk.service.impl;

import icu.flycode.sdk.config.GlobalConfigManager;
import icu.flycode.sdk.domain.Chat.*;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;
import icu.flycode.sdk.instrcture.git.GitCommand;
import icu.flycode.sdk.instrcture.openai.IOpenAiService;
import icu.flycode.sdk.instrcture.wechat.WechatPushMessage;
import icu.flycode.sdk.service.AbstractOpenAiCodeReviewService;

import java.io.IOException;
import java.util.ArrayList;

public class OpenAiCodeReviewServiceImpl extends AbstractOpenAiCodeReviewService {


    public OpenAiCodeReviewServiceImpl(GitCommand gitCommand, IOpenAiService openAiService, WechatPushMessage wechatPushMessage) {
        super(gitCommand, openAiService, wechatPushMessage);
    }

    @Override
    protected String getDiffCode() throws Exception {
        String hashCode = gitCommand.getGitInfo("%h");
        return gitCommand.getDiffCode(hashCode);
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.DEEPSEEK_CHAT.getCode());
        chatCompletionRequest.setMessages(new ArrayList<Prompt>() {
            {
                add(new Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: "));
                add(new Prompt("user", diffCode));
            }
        });
        ChatCompletionSyncResponse codeReview = openAiService.getCodeReview(chatCompletionRequest);
        return codeReview.getChoices().get(0).getMessage().getContent();
    }

    @Override
    protected String recordCodeReview(String recommend) throws Exception {
        GitInfo gitInfo = GlobalConfigManager.getInstance().getGitInfo();
        return gitCommand.writeLogs(gitInfo.getGithubToken(), recommend);
    }

    @Override
    protected void pushMessage(String logUrl) throws IOException {
        WechatInfo wechatInfo = GlobalConfigManager.getInstance().getWechatInfo();
        wechatPushMessage.pushWxMessage(logUrl);
    }
}
