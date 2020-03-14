package com.xbc.community.productSeckill.mapper;

import com.xbc.community.productSeckill.entity.Seckill;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface SeckillMapper {
    @Update("UPDATE seckill  SET number=number-1 WHERE seckill_id=#{seckillId} AND number>0")
    int Update(long seckillId);

    @Select("select * from seckill where type =1")
    List<Seckill> findAll();

    @Insert("insert into seckill(name,number,start_time,end_time,create_time,type,price) " +
            "values(#{name},#{number},#{startTime},#{endTime},#{createTime},#{type},#{price})")
    int save(Seckill seckill);

    @Select("select * from seckill WHERE seckill_id=#{seckillId}")
    Seckill findById(long seckillId);

    @Select("select price from seckill where seckill_id = #{seckillId}")
    BigDecimal getPriceByid(long seckillId);

    @Update("update seckill set name=#{name},number=#{number},start_time=#{startTime},end_time=#{endTime},type=#{type},price=#{price} where seckill_id=#{seckillId}")
    int updateall(Seckill seckill);
    @Delete(" DELETE FROM seckill WHERE seckill_id = #{parseInt}")
    int delete(int parseInt);

    @Update("UPDATE seckill  SET number=number+1 WHERE seckill_id=#{couponId}")
    int incrnum(int couponId);
}
