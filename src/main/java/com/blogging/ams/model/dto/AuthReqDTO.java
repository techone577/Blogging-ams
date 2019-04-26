package com.blogging.ams.model.dto;

/**
 * @author techoneduan
 * @date 2019/4/26
 */
public class AuthReqDTO {

    private String name;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
