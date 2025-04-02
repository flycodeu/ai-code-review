package icu.flycode.sdk.instrcture.openai;

import icu.flycode.sdk.domain.Chat.ChatCompletionRequest;
import icu.flycode.sdk.domain.Chat.ChatCompletionSyncResponse;
import icu.flycode.sdk.domain.Chat.OpenAiInfo;

/**
 * AI接口
 */
public interface IOpenAiService {

    ChatCompletionSyncResponse getCodeReview(ChatCompletionRequest chatCompletionRequest) throws Exception;
}
