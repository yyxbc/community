package com.xbc.community.service;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;

import java.util.List;
public interface QuestionService {

    int createorupdate(Question question);

    void createOrUpdate(Question question);

    PageInfo<Question> findall(int pageNo, int pageSize);

    PageInfo<Question> findall(Integer id, int pageNo, int pageSize);

    PageInfo<Question> findall(String search, int pageNo, int pageSize);

    Question findById(Integer id);

    void incView(Question question);

    void incCommentCount(Question question);

    void incLikeCount(Question question);

    List<Question> selectRelated(Question question);

    List<Question> findall();

    PageInfo<Question> findByTag(String tag, int pageNo, int pageSize);

    int delete(Integer id);

    Integer findQuestionCountByTag(String hot);

    Integer findCommentCountByTag(String hot);

    Integer findviewCountByTag(String hot);

    PageInfo<Question> findBySort(String sort, int pageNo, int pageSize);

     PageInfo<Question> list(String search, String tag, String sort, int pageNo, int pageSize);

    int deleteMulti(String[] idArray);
}
