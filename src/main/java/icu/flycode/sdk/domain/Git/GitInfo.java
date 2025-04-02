package icu.flycode.sdk.domain.Git;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取Git项目基础信息
 * @author flycode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitInfo {
    /**
     * 项目名
     */
    private String project;

    /**
     * 提交者
     */
    private String committer;

    /**
     * 分支
     */
    private String branchName;

    /**
     * 提交消息
     */
    private String commitMessage;

    /**
     * token
     */
    private String githubToken;


    /**
     * 回写日志的仓库地址
     */
    private String githubReviewUrl;
}
