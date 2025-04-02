package icu.flycode.sdk.domain.Template;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板消息
 */
@Data
public class TemplateMessage {
    /**
     * 发送者id
     */
    private String touser;

    /**
     * 模板id
     */
    private String template_id;

    /**
     * 跳转url
     */
    private String url;

    /**
     * 存储数据
     */
    private Map<String, Map<String, String>> data = new HashMap<>();

    /**
     * 存放key-value数据
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {{
            put("value", value);
        }});
    }
}
