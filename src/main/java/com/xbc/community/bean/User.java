package com.xbc.community.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String token;
    private String phone;
    private String email;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;
}
