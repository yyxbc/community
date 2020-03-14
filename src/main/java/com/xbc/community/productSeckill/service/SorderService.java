package com.xbc.community.productSeckill.service;

import com.github.pagehelper.PageInfo;
import com.xbc.community.productSeckill.entity.Sorder;

import java.util.List;

public interface SorderService {

    int updateOrderByPay(Sorder sorder);

    Sorder findOrderByCode(String redisQualificationCode);

    List<Sorder> findallByuserid(String userId);

    int del(String outTradeNo);

    List<Sorder> findAll();

    int delteByid(String[] idArray);

    PageInfo<Sorder> findall(int start, int length);

    Sorder findById(String id);

    PageInfo<Sorder> findallByuserid(String valueOf, int pageNo, int pageSize);
}
