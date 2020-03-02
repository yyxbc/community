package com.xbc.community.service;

import com.xbc.community.dto.HotTagDTO;
import com.xbc.community.dto.TagDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
@CacheConfig(cacheNames = "tag")
public interface TagService {

    int updateTag(String categoryName, String tag);

    List<TagDTO> getAll();

    List<String> findByCategoryName(String categoryName);

    int delete(Integer id);

    TagDTO findById(Integer id);

    int insert(String categoryName, String tag);

    @Cacheable(value = "#p0")
    HotTagDTO getCount(String tag);
}
