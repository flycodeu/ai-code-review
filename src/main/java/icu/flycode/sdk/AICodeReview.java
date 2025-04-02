package icu.flycode.sdk;


import icu.flycode.sdk.config.GlobalConfigManager;
import icu.flycode.sdk.domain.Chat.OpenAiInfo;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;
import icu.flycode.sdk.instrcture.git.GitCommand;
import icu.flycode.sdk.instrcture.openai.IOpenAiService;
import icu.flycode.sdk.instrcture.openai.service.DeepSeekChatServiceImpl;
import icu.flycode.sdk.instrcture.wechat.WechatPushMessage;
import icu.flycode.sdk.service.impl.OpenAiCodeReviewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AICodeReview {
    private static final Logger logger = LoggerFactory.getLogger(AICodeReview.class);

    public static void main(String[] args) throws Exception {
        String apiHost = getEnv("API_HOST");
        String apiKey = getEnv("API_KEY");
        String githubToken = getEnv("CODE_TOKEN");
        String reviewUrl = getEnv("REVIEW_URL");
        String templateId = getEnv("TEMPLATE_ID");
        String toUser = getEnv("TO_USER");
        String wxAppid = getEnv("WX_APPID");
        String wxSecret = getEnv("WX_SECRET");
        String commitBranch = getEnv("COMMIT_BRANCH");
        String commitAuthor = getEnv("COMMIT_AUTHOR");
        String commitProject = getEnv("COMMIT_PROJECT");
        String commitMessage = getEnv("COMMIT_MESSAGE");

        GitInfo gitInfo = new GitInfo(commitProject, commitAuthor, commitBranch, commitMessage, githubToken, reviewUrl);
        WechatInfo wechatInfo = new WechatInfo(wxAppid, wxSecret, toUser, templateId);
        OpenAiInfo openAiInfo = new OpenAiInfo(apiKey, apiHost);
        GlobalConfigManager.getInstance().init(wechatInfo, gitInfo, openAiInfo);

        GitCommand gitCommand = new GitCommand();
        IOpenAiService openAiService = new DeepSeekChatServiceImpl();
        WechatPushMessage wechatPushMessage = new WechatPushMessage();
        OpenAiCodeReviewServiceImpl openAiCodeReviewService = new OpenAiCodeReviewServiceImpl(gitCommand,openAiService, wechatPushMessage);
        openAiCodeReviewService.exec();
        logger.info("code review finish");
    }

    public static String getEnv(String key) {
        String token = System.getenv(key);
        if (null == token || token.isEmpty()) {
            throw new RuntimeException(key + ":value is empty");
        }
        return token;
    }
}
