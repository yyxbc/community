package com.xbc.community.service;

import com.xbc.community.bean.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "user")
public interface UserService {

    @Cacheable(value = "#p0")
    User findByToken(String token);

    public User insert(User user);

    User insertOrUpdate(User user);

    public User update(User user);

    User findByUser(User user);

    List<User> findAll();
}
