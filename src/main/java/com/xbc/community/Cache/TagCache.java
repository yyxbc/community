package com.xbc.community.Cache;

import com.xbc.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public class TagCache {
    public static String filterInvalid(String tags,List<TagDTO> tagDTOS){
        String[] split = StringUtils.split(tags,",");
        List<String> tagList = tagDTOS.stream().flatMap(tag->tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t->!tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
