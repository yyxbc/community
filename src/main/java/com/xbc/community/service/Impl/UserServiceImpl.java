package com.xbc.community.service.Impl;

import com.xbc.community.bean.User;
import com.xbc.community.mapper.UserMapper;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByToken(String token) {
        User user = userMapper.findByToken(token);
        return user;
    }

    @Override
    public void insert(User user) {
    userMapper.inser(user);
    }
}
