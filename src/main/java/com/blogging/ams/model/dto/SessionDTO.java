package com.blogging.ams.model.dto;

/**
 * @author techoneduan
 * @date 2019/4/29
 */
public class SessionDTO {

    private String sessionId;

    /**
     * 默认guest
     */
    private String authority;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
