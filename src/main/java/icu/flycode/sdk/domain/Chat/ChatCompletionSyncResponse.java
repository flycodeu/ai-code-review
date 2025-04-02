package icu.flycode.sdk.domain.Chat;

import lombok.Data;

import java.util.List;

/**
 * AI对话返回数据类
 *
 * @author flycode
 */
@Data
public class ChatCompletionSyncResponse {
    private String id;

    private String object;

    private String created;

    private String model;

    private List<Choices> choices;


    @Data
    public class Choices {
        private Integer index;
        private Message message;


        @Data
        public class Message {
            private String role;
            private String content;
        }

    }

}
