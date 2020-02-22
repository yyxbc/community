package com.xbc.community.service;

import com.xbc.community.bean.Comment;

import java.util.List;

public interface CommentService {

    void insert(Comment comment);


    List<Comment> listByQuestionId(Integer id);

    Comment findById(Integer id);

    List<Comment> listByCommentId(Integer id);

    void incLikeCount(Comment comment);
}
