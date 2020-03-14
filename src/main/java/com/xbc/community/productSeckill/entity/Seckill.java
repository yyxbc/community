package com.xbc.community.productSeckill.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Seckill implements Serializable {
	private long seckillId;
	private String name;
	private int number;
	private Timestamp startTime;
	private Timestamp endTime;
	private Timestamp createTime;
	private int type;
	private BigDecimal price;
}
