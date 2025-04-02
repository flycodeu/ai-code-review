package icu.flycode.sdk.service;

import icu.flycode.sdk.domain.Chat.OpenAiInfo;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;
import icu.flycode.sdk.instrcture.git.GitCommand;
import icu.flycode.sdk.instrcture.openai.IOpenAiService;
import icu.flycode.sdk.instrcture.wechat.WechatPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractOpenAiCodeReviewService implements IOpenAiCodeReviewService {
    private final Logger logger = LoggerFactory.getLogger(AbstractOpenAiCodeReviewService.class);

    protected GitCommand gitCommand;

    protected IOpenAiService openAiService;

    protected WechatPushMessage wechatPushMessage;

    public AbstractOpenAiCodeReviewService(GitCommand gitCommand, IOpenAiService openAiService, WechatPushMessage wechatPushMessage) {
        this.gitCommand = gitCommand;
        this.openAiService = openAiService;
        this.wechatPushMessage = wechatPushMessage;
    }

    @Override
    public void exec() {
        try {
            // 1.获取提交代码
            String diffCode = getDiffCode();
            logger.info("openai-code-review diffCode:{}", diffCode);
            // 2. 开始评审代码
            String recommend = codeReview(diffCode);
            logger.info("openai-code-review recommend:{}", recommend);
            // 3. 记录评审日志，返回日志地址
            String logUrl = recordCodeReview(recommend);
            logger.info("openai-code-review logUrl:{}", logUrl);
            // 4. 发送消息通知
            pushMessage(logUrl);
        } catch (Exception e) {
            logger.error("openai-code-review error", e);
        }
    }

    protected abstract String getDiffCode() throws Exception;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract String recordCodeReview(String recommend) throws Exception;

    protected abstract void pushMessage(String logUrl) throws IOException;
}
