package icu.flycode.sdk.domain.Chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Prompt {

    private String role;

    private String content;
}
