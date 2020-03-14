package com.xbc.community.productSeckill.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.mapper.SeckillMapper;
import com.xbc.community.productSeckill.mapper.SorderMapper;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.service.SorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SorderServiceImpl implements SorderService {

    @Autowired
    SorderMapper sorderMapper;

    @Autowired
    SeckillMapper seckillMapper;

    @Autowired
    RedisUtil redisUtil;
    @Override
    public int updateOrderByPay(Sorder sorder) {
        return sorderMapper.updateOrderByPay(sorder);
    }

    @Override
    public Sorder findOrderByCode(String redisQualificationCode) {
        return sorderMapper.findOrderByCode(redisQualificationCode);
    }

    @Override
    public List<Sorder> findallByuserid(String userId) {
        return sorderMapper.findallByuserid(userId);
    }

    @Override
    public int del(String outTradeNo) {
        return sorderMapper.del(outTradeNo);
    }

    @Override
    public List<Sorder> findAll() {
        return sorderMapper.findAll();
    }

    @Override
    @Transactional
    public int delteByid(String[] idArray) {
        int count =0;
        for (String id:idArray){
           Sorder sorder = sorderMapper.findById(id);
            //System.out.println(sorder.getStatus());
            int i= sorderMapper.delById(id);
           if((sorder.getStatus()=="0"||sorder.getStatus().equals("0"))&&i>0){
               int num = seckillMapper.incrnum(Integer.parseInt(sorder.getCouponId()));
               System.out.println(1+""+num);
                if(num<1){
                    return count;
                }
               redisUtil.incr("SECKILL_STOCKNUM_" + Integer.parseInt(sorder.getCouponId()),1);
           }
            count+=i;
        }
        return count;
    }

    @Override
    public PageInfo<Sorder> findall(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Sorder> list = sorderMapper.findAll();
        PageInfo<Sorder> seckillPageInfo = new PageInfo(list);
        return seckillPageInfo;
    }

    @Override
    public Sorder findById(String id) {
        return sorderMapper.findById(id);
    }

    @Override
    public PageInfo<Sorder> findallByuserid(String id, int pageNo, int pageSize) {
            PageHelper.startPage(pageNo, pageSize);
            List<Sorder> list = sorderMapper.findallByuserid(id);
            PageInfo<Sorder> sorders = new PageInfo(list);
            return sorders;
    }
}
