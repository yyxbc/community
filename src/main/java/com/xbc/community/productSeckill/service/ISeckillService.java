package com.xbc.community.productSeckill.service;

import com.github.pagehelper.PageInfo;
import com.xbc.community.productSeckill.entity.Result;
import com.xbc.community.productSeckill.entity.Seckill;
import com.xbc.community.productSeckill.response.SeckillInfoResponse;

import java.util.List;

public interface ISeckillService {

    /**
     * 查询全部的秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 查询秒杀售卖商品
     *
     * @param seckillId
     * @return
     */
    Long getSeckillCount(long seckillId);

    /**
     * 删除秒杀售卖商品记录
     *
     * @param seckillId
     * @return
     */
    //void deleteSeckill(long seckillId);
    public SeckillInfoResponse startSeckill(int stallActivityId, int purchaseNum, String openId, String formId,
                                            long addressId, String shareCode, String shareSource, String userCode);

    public boolean checkStartSeckill(int stallActivityId);

    public Result startSeckilDBPCC_TWO(long seckillId, long userId, String orderCode);

    Seckill findById(long parseLong);

    List<Seckill> findAll();

    int insert(Seckill seckill);

    int updateall(Seckill seckill);

    int delteByid(String[] idArray);

    PageInfo<Seckill> findall(int pageNo, int pageSize);
}
