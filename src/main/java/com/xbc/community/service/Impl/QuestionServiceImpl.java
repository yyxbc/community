package com.xbc.community.service.Impl;

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
    public List<Question> findall() {
        List<Question> questions = questionMapper.findall();
        return questions;
    }
}
