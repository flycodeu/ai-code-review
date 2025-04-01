package icu.flycode.sdk.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Prompt {

    private String role;

    private String content;
}
