package com.xbc.community.controller;

import com.xbc.community.bean.Comment;
import com.xbc.community.bean.User;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.service.CommentService;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;

    @PostMapping("/comment")
    @ResponseBody
    public Object post(@RequestBody Comment comment, @SessionAttribute(value = "user",required = false)User user) {
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(comment==null||comment.getContent()==null||comment.getContent()==""){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        System.out.println(user);
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setLikeCount(0);
        System.out.println(comment);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    @GetMapping("/comment/{id}")
    @ResponseBody
    public Object post(@PathVariable(name = "id") Integer id) {
        List<Comment> comments2 = commentService.listByCommentId(id);
        return comments2;
    }
}
