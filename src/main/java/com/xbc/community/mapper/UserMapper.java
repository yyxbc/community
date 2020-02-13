package com.xbc.community.mapper;

import com.xbc.community.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Insert("insert into user(username,password,token,gmt_create,gmt_modified,avatar_url) values(#{username}," +
            "#{password},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    public void insert(User user);

    @Select("select * from user where token =#{token}")
    User findByToken(String token);
    @Select("select * from user where id =#{id}")
    User findById(Integer id);

    @Select("select * from user where password =#{password}")
    User findByPassword(String password);
    @Update("update user set username=#{username},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} where id =#{id}")
    void update(User user);
}
