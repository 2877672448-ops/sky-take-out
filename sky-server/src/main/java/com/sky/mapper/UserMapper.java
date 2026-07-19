package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select *from user where openid=#{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Update("update user set name=#{name}, avatar=#{avatar} where id=#{id}")
    void update(User user);
}
