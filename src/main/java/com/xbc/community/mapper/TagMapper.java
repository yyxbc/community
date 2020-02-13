package com.xbc.community.mapper;

import com.xbc.community.bean.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("select * from tag")
    List<Tag> findall();
    @Update("update tag set tags=#{tags}")
    int update(Tag tag);
}
