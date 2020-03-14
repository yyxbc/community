package com.xbc.community.service;

import com.xbc.community.dto.HotTagDTO;
import com.xbc.community.dto.TagDTO;

import java.util.List;

public interface TagService {

    int updateTag(String categoryName, String tag);

    List<TagDTO> getAll();

    List<String> findByCategoryName(String categoryName);

    int delete(Integer id);

    TagDTO findById(Integer id);

    int insert(String categoryName, String tag);

    HotTagDTO getCount(String tag);
}
