package com.xbc.community.productSeckill.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @param
 * @return
 */
@Data
public class PaymentInfo {
    private Integer  id;
    private String orderSn;
    private String orderId;
    private String alipayTradeNo;
    private BigDecimal totalAmount;
    private String Subject;
    private String paymentStatus;
    private Date createTime;
    private Date callbackTime;
    private String callbackContent;
}
