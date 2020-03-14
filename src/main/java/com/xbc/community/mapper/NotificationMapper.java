package com.xbc.community.mapper;

import com.xbc.community.bean.Notification;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper {
    @Insert("insert into notification(notifier,receiver,outerid,type,gmt_create,status,outer_title,notifier_name)values(#{notifier},#{receiver},#{outerid},#{type},#{gmtCreate},#{status},#{outerTitle},#{notifierName})")
   void insert(Notification notification);

    @Select("select * from notification where receiver =#{id} order by gmt_create desc")
    List<Notification> listById(Integer id);

    @Select("select count(*) from notification where status =0 and receiver =#{id}")
    Long unreadCountById(Integer id);

    @Select("select * from notification where id=#{id}")
    Notification findById(Integer id);

    @Update("update notification set status=#{status} where id=#{id}")
    int updateById(Notification notification);
}
