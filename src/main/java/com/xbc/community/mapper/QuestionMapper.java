package com.xbc.community.mapper;

import com.xbc.community.bean.Question;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);

    @Select("select q.*,u.* from question q inner join user u on q.creator=u.id")
    @Results(id = "userMap", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "title", property = "title"),
            @Result(column = "description", property = "description"),
            @Result(column = "tag", property = "tag"),
            @Result(column = "gmt_create", property = "gmtCreate"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "creator", property = "creator"),
            @Result(column = "view_count", property = "viewCount"),
            @Result(column = "comment_count", property = "commentCount"),
            @Result(column = "like_count", property = "likeCount"),
            @Result(column = "creator", property = "user", one = @One(select = "com.xbc.community.mapper.UserMapper.findById", fetchType = FetchType.EAGER))
    }
    )
    List<Question> findall();

    @Select("select * from question  where creator =#{id}")
    @ResultMap(value = "userMap")
    List<Question> findallById(Integer id);

    @Select("select * from question where id=#{id}")
    @ResultMap(value = "userMap")
    Question findById(Integer id);
}
