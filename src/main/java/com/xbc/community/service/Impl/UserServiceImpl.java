package com.xbc.community.service.Impl;

import com.xbc.community.bean.User;
import com.xbc.community.mapper.UserMapper;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByToken(String token) {
        User user = userMapper.findByToken(token);
        return user;
    }

    @Override
    public User insert(User user) {
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        User dbUser =userMapper.findByUsername(user.getUsername());
        if(dbUser!=null){
            return null;
        }
        int id = userMapper.insert(user);
        user.setId(id);
        return user;
    }

    @Override
    public User insertOrUpdate(User user){
       User dbuser = userMapper.findByUsername(user.getUsername());
       if(dbuser==null){
            insert(user);
       }else{
           update(user);
       }
       return user;
    }

    @Override
    public User update(User user){
        User dbuser = userMapper.findByUsername(user.getUsername());
            dbuser.setGmtModified(System.currentTimeMillis());
            dbuser.setAvatarUrl(user.getAvatarUrl());
            dbuser.setUsername(user.getUsername());
            dbuser.setToken(user.getToken());
             userMapper.update(dbuser);
            return dbuser;

    }

    @Override
    public User findByUser(User user) {
        return userMapper.findByUser(user);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

}
