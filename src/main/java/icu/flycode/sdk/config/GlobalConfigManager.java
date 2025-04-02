package icu.flycode.sdk.config;

import icu.flycode.sdk.domain.Chat.OpenAiInfo;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;

/**
 * 全局配置管理器
 */
public class GlobalConfigManager {

    // 单例实例
    private static volatile GlobalConfigManager INSTANCE;

    // 全局配置
    private WechatInfo wechatInfo;
    private GitInfo gitInfo;
    private OpenAiInfo openAiInfo;

    // 私有构造函数，防止外部实例化
    private GlobalConfigManager() {}

    /**
     * 获取单例实例（双检锁实现）
     *
     * @return 单例对象
     */
    public static GlobalConfigManager getInstance() {
        if (INSTANCE == null) { // 第一次检查
            synchronized (GlobalConfigManager.class) { // 加锁
                if (INSTANCE == null) { // 第二次检查
                    INSTANCE = new GlobalConfigManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化全局配置
     *
     * @param wechatInfo 微信配置信息
     * @param gitInfo    Git 项目信息
     * @param openAiInfo OpenAI 配置信息
     */
    public void init(WechatInfo wechatInfo, GitInfo gitInfo, OpenAiInfo openAiInfo) {
        this.wechatInfo = wechatInfo;
        this.gitInfo = gitInfo;
        this.openAiInfo = openAiInfo;
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