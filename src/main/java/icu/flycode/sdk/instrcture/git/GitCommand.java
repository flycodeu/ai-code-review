package icu.flycode.sdk.instrcture.git;

import icu.flycode.sdk.config.GlobalConfigManager;
import icu.flycode.sdk.domain.Chat.OpenAiInfo;
import icu.flycode.sdk.domain.Git.GitInfo;
import icu.flycode.sdk.domain.Template.WechatInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Git操作
 */
public class GitCommand {


    /**
     * 根据tags获取git信息，可以集成到Actions中
     *
     * @param tags 占位符，例如%h
     * @return
     * @throws IOException
     */
    public String getGitInfo(String tags) throws IOException {
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:" + tags);
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();
        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        return logReader.readLine();
    }

    /**
     * 获取提交代码记录
     *
     * @param lastCommitHash 哈希值
     * @return 历史记录
     * @throws Exception
     */
    public String getDiffCode(String lastCommitHash) throws Exception {
        ProcessBuilder diffProcessBuilder = new ProcessBuilder("git", "diff", lastCommitHash + "^", lastCommitHash);
        diffProcessBuilder.directory(new File("."));
        Process process = diffProcessBuilder.start();
        BufferedReader diffReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder processOutput = new StringBuilder();
        while ((line = diffReader.readLine()) != null) {
            processOutput.append(line).append("\n");
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Diff process exited with code " + exitCode);
        }
        return processOutput.toString();
    }

    /**
     * 将生成的AI文本写入文件，提交到到指定GitHub仓库
     */
    /**
     * 编写Git日志
     *
     * @param token GitHub的token
     * @param log   日志内容
     * @return
     * @throws GitAPIException
     */
    public String writeLogs(String token, String log) throws Exception {
        GitInfo gitInfo = GlobalConfigManager.getInstance().getGitInfo();
        // 1. 创建Git对象
        Git git = Git.cloneRepository()
                .setURI(gitInfo.getGithubReviewUrl() + ".git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();


        // 2. 创建文件夹
        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            dateFolder.mkdirs();
        }

        // 3. 创建文件
        String fileName = System.currentTimeMillis() + ".md";
        File file = new File(dateFolder, fileName);
        // 4. 写入日志
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(log);
        }

        // 5. 提交、推送文件到仓库
        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("Add " + fileName).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();

        // 6. 返回提交地址
        return gitInfo.getGithubReviewUrl() + "/blob/master/" + dateFolderName + "/" + fileName;
    }


}