package com.xbc.community.service;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.User;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

@CacheConfig(cacheNames = "user")
public interface UserService {


    User findByToken(String token);

    public User insert(User user);

    User insertOrUpdate(User user);

    public int update(User user);

    User findByUser(User user);

    List<User> findAll();

    int delteByid(String[] idArray);

    User findById(Integer id);

    PageInfo<User> findall(int pageNo, int pageSize);
}
