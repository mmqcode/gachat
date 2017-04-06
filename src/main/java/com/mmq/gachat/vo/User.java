package com.mmq.gachat.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/15.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1l;

    private String username;

    private String password;

    private String email;

    private String nickName;

    private String headImage;

    private String regTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }
}
