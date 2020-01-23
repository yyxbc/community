package com.xbc.community.bean;

import lombok.Data;

import java.io.Serializable;
@Data
public class Question implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
