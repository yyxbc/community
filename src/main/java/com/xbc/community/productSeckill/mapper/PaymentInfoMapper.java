package com.xbc.community.productSeckill.mapper;

import com.xbc.community.productSeckill.entity.PaymentInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentInfoMapper {
    @Insert("insert into payment_info(order_sn,order_id,alipay_trade_no,total_amount,subject,payment_status,create_time,callback_time,callback_content) values(#{orderSn},#{orderId},#{alipayTradeNo},#{totalAmount},#{Subject},#{paymentStatus},#{createTime},#{callbackTime},#{callbackContent})")
    void save(PaymentInfo paymentInfo);

    @Select("select * from payment_info   WHERE order_sn=#{orderSn}")
    PaymentInfo getByOrderSn(PaymentInfo paymentInfoParam);

    @Update("update payment_info set payment_status=#{paymentStatus}, alipay_trade_no=#{alipayTradeNo},callback_content=#{callbackContent},callback_time=#{callbackTime} WHERE order_sn=#{orderSn}")
    int update(PaymentInfo paymentInfo);

    @Select("select count(*) from payment_info WHERE order_sn=#{orderSn}")
    int findByOrderSn(String orderSn);

    @Delete("delete from payment_info where order_sn=#{orderSn}")
    int delByOrderSn(String orderSn);
}
