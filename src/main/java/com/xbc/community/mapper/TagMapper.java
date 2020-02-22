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

    @Update("update tag set tags=#{tags} where category_name =#{categoryName}")
    int update(String categoryName,String tags);

    @Select("select * from tag where category_name =#{categoryName}")
    Tag findByCategoryName(String categoryName);
}
