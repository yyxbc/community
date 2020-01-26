package com.xbc.community.service;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;

public interface QuestionService {
    void create(Question question);
    PageInfo<Question> findall(int pageNo, int pageSize);
    PageInfo<Question> findall(Integer id,int pageNo, int pageSize);

    Question findById(Integer id);
}
