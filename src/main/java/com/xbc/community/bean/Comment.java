package com.xbc.community.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Comment implements Serializable {
    private Integer id;
    private Integer parentId;
    private Integer type;
    private String content;
    private Integer commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private int likeCount;
    private int commentCount;
    private User user;
}
