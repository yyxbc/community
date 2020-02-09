package com.xbc.community.mapper;

import com.xbc.community.bean.Comment;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("insert into comment(parent_id,type,commentator,gmt_create,gmt_modified,like_count,content) values(#{parentId},#{type},#{commentator},#{gmtCreate},#{gmtModified},#{likeCount},#{content})")
    Integer insert(Comment comment);

    @Select("select * from comment where parent_id = #{id} order by gmt_create desc")
    @Results(id="commentMap",value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "type", property = "type"),
            @Result(column = "content", property = "content"),
            @Result(column = "gmt_create", property = "gmtCreate"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "commentator", property = "commentator"),
            @Result(column = "like_count", property = "likeCount"),
            @Result(column = "comment_count", property = "commentCount"),
            @Result(column = "commentator", property = "user", one = @One(select = "com.xbc.community.mapper.UserMapper.findById", fetchType = FetchType.EAGER))

    })
    List<Comment> listByQuestionId(Integer id);

    @Select("select * from comment where parent_id = #{id} and type = 2 order by gmt_create desc")
    @ResultMap(value = "commentMap")
    List<Comment> listByCommentId(Integer id);

    @Select("select * from comment where id = #{id}")
    @ResultMap(value = "commentMap")
    Comment findById(Integer id);

    @Update("update comment set like_count = like_count + 1 where id =#{id}")
    int incLikeCount(Comment comment);
    @Update("update comment set comment_count = comment_count + 1 where id =#{id}")
    int incCommentCount(Comment comment1);
}
