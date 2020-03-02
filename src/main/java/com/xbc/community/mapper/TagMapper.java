package com.xbc.community.mapper;

import com.xbc.community.bean.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("select * from tag")
    List<Tag> findall();

    @Update("update tag set tags=#{tags} where category_name =#{categoryName}")
    int update(String categoryName,String tags);

    @Select("select * from tag where category_name =#{categoryName}")
    Tag findByCategoryName(String categoryName);

    @Insert("insert into tag(category_name,tags) values(#{categoryName},#{tags})")
    int insert(Tag tag);

    @Delete("delete  from tag where id =#{id}")
    int delete(Integer id);

    @Select("select * from tag where id =#{id}")
    Tag findById(Integer id);
}
