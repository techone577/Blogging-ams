package com.blogging.ams.model.enums;

/**
 * @author techoneduan
 * @date 2019/4/29
 */
public enum AuthEnum {

    ROOT(0, "root"),
    GUEST(1, "guest");

    private int code;

    private String value;

    AuthEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
