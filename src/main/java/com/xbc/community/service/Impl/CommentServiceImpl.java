package com.xbc.community.service.Impl;

import com.xbc.community.bean.Comment;
import com.xbc.community.bean.Notification;
import com.xbc.community.bean.Question;
import com.xbc.community.enums.NotificationStatusEnum;
import com.xbc.community.enums.NotificationTypeEnum;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.mapper.CommentMapper;
import com.xbc.community.mapper.NotificationMapper;
import com.xbc.community.mapper.QuestionMapper;
import com.xbc.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_ERROR);
        }
        if (comment.getType() == 2) {
            //回复评论
            Comment comment1 = commentMapper.findById(comment.getParentId());
            if (comment1 == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            Integer commentId = commentMapper.insert(comment);
            if(commentId!=1){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_INSERT_FAILED);
            }
           // System.out.println(comment);
            commentMapper.incCommentCount(comment1);
            //创建通知
            Integer type = NotificationTypeEnum.REPLY_COMMENT.getType();
            Integer creator = comment1.getCommentator();
            String outerTitle = comment1.getContent();
            String notifierName = comment.getUser().getUsername();
            Integer outerid =comment1.getParentId();
            createNotify(comment,type,creator, outerTitle,notifierName, outerid);
        } else {
            //回复问题
            Question question = questionMapper.findById(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            Integer commentId = commentMapper.insert(comment);
            System.out.println(comment);
            if(commentId!=1){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_INSERT_FAILED);
            }
            questionMapper.incCommentCount(question);
            //创建通知

            Integer type = NotificationTypeEnum.REPLY_QUESTION.getType();
            Integer creator = question.getCreator();
            String outerTitle = question.getTitle();
            String notifierName = comment.getUser().getUsername();
            System.out.println(notifierName);
            Integer outerid =comment.getParentId();
            createNotify(comment,type,creator,outerTitle, notifierName,outerid);

        }
    }

    public void createNotify(Comment comment, Integer type, Integer creator, String outerTitle, String notifierName, Integer outerid){
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(type);
        notification.setOuterid(outerid);
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(creator);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    @Override
    public List<Comment> listByQuestionId(Integer id) {
        List<Comment> comments = commentMapper.listByQuestionId(id);
        return comments;
    }

    @Override
    public Comment findById(Integer id) {
        return commentMapper.findById(id);
    }

    @Override
    public List<Comment> listByCommentId(Integer id) {
        List<Comment> comments = commentMapper.listByCommentId(id);
        return comments;
    }

    @Override
    public void incLikeCount(Comment comment) {
        commentMapper.incLikeCount(comment);
    }
}
