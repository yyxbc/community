package com.xbc.community.mapper;

import com.xbc.community.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into user(username,password,token,gmt_create,gmt_modified,avatar_url,phone,email) values(#{username}," +
            "#{password},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl},#{phone},#{email})")
     int insert(User user);

    @Select("select * from user where token =#{token}")
    User findByToken(String token);

    @Select("select * from user where username =#{username}")
    User findByUsername(String username);

    @Select("select * from user where id =#{id}")
    User findById(Integer id);

    @Select("select * from user where password =#{password}")
    User findByPassword(String password);
    @Update("update user set username=#{username},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl},phone=#{phone},email=#{email} where id =#{id}")
    int update(User user);

    @Select("select * from user where username =#{username} and password =#{password}")
    User findByUser(User user);

    @Select("select * from user")
    List<User> findAll();

    @Delete(" DELETE FROM user WHERE id = #{id}")
    int delete(Integer id);
}
