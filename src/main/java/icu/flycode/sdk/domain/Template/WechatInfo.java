package icu.flycode.sdk.domain.Template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信通用appid
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatInfo {
    /**
     * appId
     */
    private String appId;

    /**
     * secret
     */
    private String sercet;

    private String toUser;

    private String templateId;
}
