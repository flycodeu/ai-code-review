package icu.flycode.sdk.domain;

public enum Model {

    DEEPSEEK_CHAT("deepseek-chat","DeepSeeChat")
    ;
    private final String code;
    private final String info;

    Model(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
