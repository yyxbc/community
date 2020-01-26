package com.xbc.community.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.mapper.QuestionMapper;
import com.xbc.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionMapper questionMapper;
    @Override
    public void create(Question question) {
        questionMapper.create(question);
    }

    @Override
    public PageInfo<Question> findall(int pageNo, int pageSize) {

        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionMapper.findall();
        PageInfo<Question> questions = new PageInfo(list);
        return questions;
    }

    @Override
    public PageInfo<Question> findall(Integer id, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionMapper.findallById(id);
        PageInfo<Question> questions = new PageInfo(list);
        return questions;
    }

    @Override
    public Question findById(Integer id) {
        Question question =questionMapper.findById(id);
        return question;
    }
}
