package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginDTO implements Serializable {

    private String code;

    //用户头像
    private String avatarUrl;

    //用户昵称
    private String nickname;

}
