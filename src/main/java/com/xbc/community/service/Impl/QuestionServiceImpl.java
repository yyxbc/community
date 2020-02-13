package com.xbc.community.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.mapper.QuestionMapper;
import com.xbc.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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
                log.error("问题未找到,{}",question);
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
            log.error("问题未找到哦,{}",id);
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
        System.out.println(tags.toString()+question.getTag());
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

    @Override
    public List<Question> findall() {
        return questionMapper.findall();
    }

    @Override
    public PageInfo<Question> findByTag(String tag, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Question> list = questionMapper.findByTag(tag);
        PageInfo<Question> questions = new PageInfo(list);
        return questions;
    }

    @Override
    public int delete(Integer id) {
       int num = questionMapper.delete(id);
       //int num1 = commentMapper.delete(id);
        //if(num==num1==1){ num=1}else{num0}
        return num;
    }
}
