package com.xbc.community.productSeckill.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Sorder implements Serializable {
    private long id;
    private String userId;
    private String couponId;
    private String orderSn;
    private Date createTime;
    private String memberUsername;
    private BigDecimal payAmount;
    private int payType;
    private String status;
    private int orderType;
    private int deleteStatus;
    private Date paymentTime;
    private Date modifyTime;
}
