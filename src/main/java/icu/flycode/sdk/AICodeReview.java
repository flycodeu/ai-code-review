package icu.flycode.sdk;

import com.alibaba.fastjson2.JSON;
import icu.flycode.sdk.domain.ChatCompletionRequest;
import icu.flycode.sdk.domain.ChatCompletionSyncResponse;
import icu.flycode.sdk.domain.Model;
import icu.flycode.sdk.domain.Prompt;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AICodeReview {

    public static void main(String[] args) throws Exception {
        // 1. 作者名
        String author = getGitInfo("%an");
        System.out.println("Author: " + author);
        // 2. 日期
        String date = getGitInfo("%cd");
        System.out.println("Date: " + date);
        // 3. 描述
        String description = getGitInfo("%s");
        System.out.println("Description: " + description);
        // 4. 哈希值，用于获取提交代码
        String hashCode = getGitInfo("%h");
        System.out.println("Hash Code: " + hashCode);

        // 5. 获取提交代码
        String diffCode = getDiffCode(hashCode);
        System.out.println(diffCode);

        // 6. 代码评审
        String apikey = getEnv("API_KEY");
        String codeReview = getCodeReview(apikey, diffCode);

        // 7. 创建仓库文件
        String codeToken = getEnv("GITHUB_TOKEN");
        String s = writeLogs(codeToken, codeReview);
        System.out.println(s);
    }

    public static String getEnv(String key) {
        String token = System.getenv(key);
        if (null == token || token.isEmpty()) {
            throw new RuntimeException(key + ":value is empty");
        }
        return token;
    }

    public static String getGitInfo(String tags) throws IOException {
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:" + tags);
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();
        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        return logReader.readLine();
    }

    public static String getDiffCode(String lastCommitHash) throws Exception {
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

    private static String getCodeReview(String apikey, String diffCode) throws Exception {
        // 1. 获取key

        // 2. 建立连接
        URL url = new URL("https://api.deepseek.com/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apikey);
        connection.setDoOutput(true);

        // 3. 发送请求
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.DEEPSEEK_CHAT.getCode());
        chatCompletionRequest.setMessages(new ArrayList<Prompt>() {
            {
                add(new Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: "));
                add(new Prompt("user", diffCode));
            }
        });

        // 4. 获取返回
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
        // 解析数据
        ChatCompletionSyncResponse chatCompletionSyncResponse = JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
        String returnContent = chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent();


        System.out.println(returnContent);
        return returnContent;
    }

    /**
     * 编写Git日志
     *
     * @param token GitHub的token
     * @param log   日志内容
     * @return
     * @throws GitAPIException
     */
    private static String writeLogs(String token, String log) throws Exception {
        // 1. 创建Git对象
        Git git = Git.cloneRepository()
                .setURI("https://github.com/flycodeu/openai-code-review-logs.git")
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
        return "https://github.com/flycodeu/openai-code-review-logs/blob/master/" + dateFolderName + "/" + fileName;
    }
}
