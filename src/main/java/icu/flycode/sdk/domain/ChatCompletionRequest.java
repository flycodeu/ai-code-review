package icu.flycode.sdk.domain;

import lombok.Data;

import java.util.List;

/**
 * 构造发送内容
 */
@Data
public class ChatCompletionRequest {
    private String model = Model.DEEPSEEK_CHAT.getCode();
    private List<Prompt> messages;
}
