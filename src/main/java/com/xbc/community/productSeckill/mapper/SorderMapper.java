package com.xbc.community.productSeckill.mapper;

import com.xbc.community.productSeckill.entity.Sorder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SorderMapper {
    @Select("select * from sorder where order_sn = #{code}")
    Sorder findOrderByCode(String code);

    @Select("select * from sorder where user_id = #{userId} and delete_status=0")
    List<Sorder> findallByuserid(String userId);

    @Insert("insert into sorder(user_id,coupon_id,order_sn,create_time,member_username,pay_amount,pay_type,status,order_type,delete_status,payment_time,modify_time) " +
            "values(#{userId},#{couponId},#{orderSn},#{createTime},#{memberUsername},#{payAmount},#{payType},#{status},#{orderType},#{deleteStatus},#{paymentTime},#{modifyTime})")
    int save(Sorder sorder);

    @Select("SELECT count(*) FROM sorder WHERE seckill_id=#{seckillId} and order_type =1")
    Long getSeckillCount(long seckillId);


    @Update("UPDATE sorder SET pay_type=#{payType},status=#{status},payment_time=#{paymentTime},modify_time=#{modifyTime} WHERE order_sn = #{orderSn}")
    int updateOrderByPay(Sorder sorder);

    @Update("UPDATE sorder SET delete_status=1 where order_sn=#{outTradeNo}")
    int del(String outTradeNo);
    @Select("select * from sorder")
    List<Sorder> findAll();
    @Select("select * from sorder where id = #{id}")
    Sorder findById(String id);

    @Update("UPDATE sorder SET delete_status=1 where id=#{id} and delete_status=0")
    int delById(String id);
}
