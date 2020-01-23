package com.xbc.community.service;

import com.xbc.community.bean.Question;

import java.util.List;

public interface QuestionService {
    void create(Question question);
    List<Question> findall();
}
