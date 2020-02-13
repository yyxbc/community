package com.xbc.community.mapper;

import com.xbc.community.bean.Question;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);

    @Select("select q.*,u.* from question q inner join user u on q.creator=u.id order by q.gmt_create desc")
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

    @Select("select * from question  where creator =#{id} ")
    @ResultMap(value = "userMap")
    List<Question> findallById(Integer id);

    @Select("select * from question where id=#{id}")
    @ResultMap(value = "userMap")
    Question findById(Integer id);

    @Update("update question set title=#{title},description=#{description},gmt_modified=#{gmtModified},tag=#{tag} where id =#{id}")
    int update(Question question);

    @Update("update question set view_count = view_count + 1 where id =#{id}")
    int incView(Question question);

    @Update("update question set comment_count = comment_count + 1 where id =#{id}")
    int incCommentCount(Question question);

    @Update("update question set like_count = like_count + 1 where id =#{id}")
    int incLikeCount(Question question);

    @Select("select * from question where id != #{id} and tag regexp #{tag}")
    @ResultMap(value = "userMap")
    List<Question> findByRelated(Question question);

    @Select("select * from question where title regexp #{title}")
    @ResultMap(value = "userMap")
    List<Question> findallBySearch(Question question);

    @Select("select * from question where tag regexp #{tag}")
    @ResultMap(value = "userMap")
    List<Question> findByTag(String tag);

    @Delete("delete * from question where id =#{id}")
    int delete(Integer id);
}
