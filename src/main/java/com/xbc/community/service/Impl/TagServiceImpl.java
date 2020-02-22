package com.xbc.community.service.Impl;

import com.xbc.community.bean.Tag;
import com.xbc.community.dto.TagDTO;
import com.xbc.community.mapper.TagMapper;
import com.xbc.community.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    TagMapper tagMapper;

    @Override
    public int updateTag(String categoryName, String tag) {
        return tagMapper.update(categoryName,tag);
    }

    @Override
    public List<TagDTO> getAll() {
        List<Tag> tags =tagMapper.findall();
        List<TagDTO> tagDTOS =new ArrayList<>();
        for(Tag tag:tags){
            TagDTO tagDTO = new TagDTO();
            tagDTO.setCategoryName(tag.getCategoryName());
            String[] taglist = StringUtils.split(tag.getTags(),",");
            System.out.println(taglist.toString());
            tagDTO.setTags(Arrays.asList(taglist));
            tagDTOS.add(tagDTO);
            System.out.println(tagDTO);
        }
        return tagDTOS;
    }

    @Override
    public List<String> findByCategoryName(String categoryName) {
            Tag tag =tagMapper.findByCategoryName(categoryName);
            String[] taglist = StringUtils.split(tag.getTags(),",");
            System.out.println(taglist.toString());
            List<String> tags=Arrays.asList(taglist);
        return tags;
    }
}
