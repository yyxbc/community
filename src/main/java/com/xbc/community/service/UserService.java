package com.xbc.community.service;

import com.xbc.community.bean.User;

public interface UserService {
    User findByToken(String token);

    public void insert(User user);
}
