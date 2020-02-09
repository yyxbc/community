package com.xbc.community.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.mapper.QuestionMapper;
import com.xbc.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionMapper questionMapper;

    @Override
    public void createOrUpdate(Question question) {
        if (question.getId() == null || question.getId().equals("")) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        } else {
            question.setGmtModified(System.currentTimeMillis());
            int updated = questionMapper.update(question);
            if (updated != 1) {
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }

    }

    @Override
    public PageInfo<Question> findall(int pageNo, int pageSize) {

        PageHelper.startPage(pageNo, pageSize);
        List<Question> list = questionMapper.findall();
        PageInfo<Question> questions = new PageInfo(list);
        return questions;
    }

    @Override
    public PageInfo<Question> findall(Integer id, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Question> list = questionMapper.findallById(id);
        PageInfo<Question> questions = new PageInfo(list);
        return questions;
    }

    @Override
    public PageInfo<Question> findall(String search, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        Question question =new Question();
        String[] tags =StringUtils.split(search," ");
        search = Arrays.stream(tags).collect(Collectors.joining("|"));
        question.setTitle(search);
        List<Question> list = questionMapper.findallBySearch(question);
        PageInfo<Question> questions = new PageInfo(list);
        return questions;
    }

    @Override
    public Question findById(Integer id) {
        Question question = questionMapper.findById(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        return question;
    }

    @Override
    public void incView(Question question) {
        questionMapper.incView(question);
    }

    @Override
    public void incCommentCount(Question question) {
        questionMapper.incCommentCount(question);
    }

    @Override
    public void incLikeCount(Question question) {
        questionMapper.incLikeCount(question);
    }

    @Override
    public List<Question> selectRelated(Question question) {
        if(StringUtils.isEmpty(question.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(question.getTag(),",");
        System.out.println(tags+question.getTag());
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question regex=new Question();
        regex.setTag(regexpTag);
        regex.setId(question.getId());
        List<Question> questions =questionMapper.findByRelated(regex);
        for(Question q:questions){
            System.out.println(q);
        }
        return questions;
    }
}
