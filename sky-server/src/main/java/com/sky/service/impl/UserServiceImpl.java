package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    public User wxlogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());

        if (openid == null) {
            throw new RuntimeException("微信登录失败");
        }

        User user = userMapper.getByOpenid(openid);

        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .name(userLoginDTO.getNickname())
                    .avatar(userLoginDTO.getAvatarUrl())
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        } else if (userLoginDTO.getNickname() != null || userLoginDTO.getAvatarUrl() != null) {
            //更新已有用户的头像和昵称
            if (userLoginDTO.getNickname() != null) {
                user.setName(userLoginDTO.getNickname());
            }
            if (userLoginDTO.getAvatarUrl() != null) {
                user.setAvatar(userLoginDTO.getAvatarUrl());
            }
            userMapper.update(user);
        }

        return user;
    }

    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        log.info("微信接口返回结果：{}", json);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
