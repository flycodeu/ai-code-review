package icu.flycode.sdk.domain.Chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiInfo {

    private String apiKey;

    private String apiHost;
}
