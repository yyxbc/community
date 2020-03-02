package com.xbc.community.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.User;
import com.xbc.community.mapper.UserMapper;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public User insert(User user) {
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        User dbUser = userMapper.findByUsername(user.getUsername());
        if (dbUser != null) {
            return null;
        }
        int id = userMapper.insert(user);
        user.setId(id);
        return user;
    }

    @Override
    public User insertOrUpdate(User user) {
        User dbuser = userMapper.findByUsername(user.getUsername());
        if (dbuser == null) {
            insert(user);
        } else {
            update(user);
        }
        return user;
    }

    @Override
    public int update(User user) {
        User dbuser = userMapper.findById(user.getId());
        dbuser.setGmtModified(System.currentTimeMillis());
        dbuser.setAvatarUrl(user.getAvatarUrl());
        dbuser.setUsername(user.getUsername());
        dbuser.setToken(user.getToken());
        dbuser.setPhone(user.getPhone());
        dbuser.setEmail(user.getEmail());

        return userMapper.update(dbuser);

    }

    @Override
    public User findByUser(User user) {
        return userMapper.findByUser(user);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    @Transactional
    public int delteByid(String[] idArray) {
        int count =0;
        for (String id:idArray){ ;
           int i= userMapper.delete(Integer.parseInt(id));
           count+=i;
        }
        return count;
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public PageInfo<User> findall(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<User> list = userMapper.findAll();
        PageInfo<User> users = new PageInfo(list);
        return users;
    }
}
