package com.xbc.community.mapper;

import com.xbc.community.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user(name,account_id,token,gmt_create,gmt_modified) values(#{name}," +
            "#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    public void inser(User user);

    @Select("select * from user where token =#{token}")
    User findByToken(String token);
}
