package com.blogging.ams.model.enums;

/**
 * @author techoneduan
 * @date 2018/12/13
 */

public enum ErrorCodeEnum {

    /**
     * BSP error
     */
    NO_SERVICE_AVAILABLR(0, "服务不可用"),

    /**
     * System error
     */
    UNKNOWN(10000, "未知异常"),
    PARAM_ILLEGAL_ERROR(10001, "参数非法"),
    ERROR_REPEAT_ACTION(10002, "重复执行"),
    SYSTEM_ERROR(10003, "系统异常"),
    INTERFACE_ERROR(10004, "接口异常"),
    DB_ERROR(10005, "数据库异常"),

    /**
     * account error
     */
    USER_NOT_EXIST_ERROR(20000, "用户不存在"),
    WRONG_PASSWORD_ERROR(20001, "密码错误"),
    USER_LOCKED_ERROR(20002,"用户冻结"),
    USER_ALREADY_EXIST_ERROR(20003,"用户已存在"),
    NEED_RELOGIN(20004,"需要重新登录");


    private int code;

    private String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
