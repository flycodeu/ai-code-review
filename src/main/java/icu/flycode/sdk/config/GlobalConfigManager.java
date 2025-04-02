package icu.flycode.sdk.config;

import icu.flycode.sdk.domain.Chat.OpenAiInfo;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;

/**
 * 全局配置管理器，单例模式
 */
public class GlobalConfigManager {
    private static volatile GlobalConfigManager instance;

    private WechatInfo wechatInfo;

    private OpenAiInfo openAiInfo;

    private GitInfo gitInfo;

    public GlobalConfigManager() {
    }


    public static GlobalConfigManager getInstance() {
        if (instance == null) {
            synchronized (GlobalConfigManager.class) {
                instance = new GlobalConfigManager();
            }
        }
        return instance;
    }

    public void init(WechatInfo wechatInfo, GitInfo gitInfo, OpenAiInfo openAiInfo) {
        this.wechatInfo = wechatInfo;
        this.openAiInfo = openAiInfo;
        this.gitInfo = gitInfo;
    }

    /**
     * 获取微信配置信息
     *
     * @return WechatInfo
     */
    public WechatInfo getWechatInfo() {
        if (wechatInfo == null) {
            throw new IllegalStateException("WechatInfo has not been initialized.");
        }
        return wechatInfo;
    }

    /**
     * 获取 Git 配置信息
     *
     * @return GitInfo
     */
    public GitInfo getGitInfo() {
        if (gitInfo == null) {
            throw new IllegalStateException("GitInfo has not been initialized.");
        }
        return gitInfo;
    }

    /**
     * 获取 OpenAI 配置信息
     *
     * @return OpenAiInfo
     */
    public OpenAiInfo getOpenAiInfo() {
        if (openAiInfo == null) {
            throw new IllegalStateException("OpenAiInfo has not been initialized.");
        }
        return openAiInfo;
    }
}
