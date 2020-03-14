package com.xbc.community.productSeckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.productSeckill.entity.Result;
import com.xbc.community.productSeckill.entity.Seckill;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.enums.SeckillStatEnum;
import com.xbc.community.productSeckill.kafka.KafkaSender;
import com.xbc.community.productSeckill.lock.RedissonDistributedLocker;
import com.xbc.community.productSeckill.mapper.SeckillMapper;
import com.xbc.community.productSeckill.mapper.SorderMapper;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.response.SeckillInfoResponse;
import com.xbc.community.productSeckill.service.ISeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service("seckillService")
public class SeckillServiceImpl implements ISeckillService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillServiceImpl.class);
    /**
     * 思考：为什么不用synchronized
     * service 默认是单例的，并发下lock只有一个实例
     */
    private Lock lock = new ReentrantLock(true);//互斥锁 参数默认false，不公平锁
    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private SorderMapper sorderMapper;
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private RedissonDistributedLocker redissonDistributedLocker;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.findAll();
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillMapper.findById(seckillId);
    }

    @Override
    public Long getSeckillCount(long seckillId) {
//		String nativeSql = "SELECT count(*) FROM success_killed WHERE seckill_id=?";
//		Object object =  dynamicQuery.nativeQueryObject(nativeSql, new Object[]{seckillId});
        Long num = sorderMapper.getSeckillCount(seckillId);
        return num;
    }

//    @Override
//    @Transactional
//    public void deleteSeckill(long seckillId) {
//        String nativeSql = "DELETE FROM  success_killed WHERE seckill_id=?";
//        dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
//        nativeSql = "UPDATE seckill SET number =100 WHERE seckill_id=?";
//        dynamicQuery.nativeExecuteUpdate(nativeSql, new Object[]{seckillId});
//    }

    @Override
    @Transactional
    public SeckillInfoResponse startSeckill(int stallActivityId, int purchaseNum, String openId, String formId, long addressId,
                                            String shareCode, String shareSource, String userCode) {
        SeckillInfoResponse response = new SeckillInfoResponse();
        //判断秒杀活动是否开始
        if (!checkStartSeckill(stallActivityId)) {
            response.setIsSuccess(false);
            response.setResponseCode(6205);
            response.setResponseMsg("秒杀活动尚未开始，请稍等！");
            response.setRefreshTime(0);
            return response;
        }
        LOGGER.info("开始获取锁资源...");
        String lockKey = "SECKILL_" + stallActivityId;
        try {
            redissonDistributedLocker.lock(lockKey, 2L);
            LOGGER.info("获取到锁资源...");
            //做用户重复购买校验
            if (redisUtil.containsValueKey("SECKILL_LIMIT_" + stallActivityId + "_" + openId)) {
                LOGGER.info("已经检测到用户重复购买...");
                response.setIsSuccess(false);
                response.setResponseCode(6105);
                response.setResponseMsg("您正在参与该活动，不能重复购买");
                response.setRefreshTime(0);
            } else {
                String redisStock = Integer.toString((Integer) redisUtil.getValue("SECKILL_STOCKNUM_" + stallActivityId));
                int surplusStock = Integer.parseInt(redisStock == null ? "0" : redisStock);    //剩余库存
                //如果剩余库存大于购买数量，则进入消费队列
                if (surplusStock >= purchaseNum) {
                    try {
                        //锁定库存，并将请求放入消费队列
                        redisUtil.decr("SECKILL_STOCKNUM_" + stallActivityId, purchaseNum);
                        JSONObject jsonStr = new JSONObject();
                        jsonStr.put("stallActivityId", stallActivityId);
                        jsonStr.put("purchaseNum", purchaseNum);
                        jsonStr.put("openId", openId);
                        jsonStr.put("addressId", addressId);
                        jsonStr.put("formId", formId);
                        jsonStr.put("shareCode", shareCode);
                        jsonStr.put("shareSource", shareSource);
                        jsonStr.put("userCode", userCode);
                        //放入kafka消息队列
                        kafkaSender.sendChannelMess("seckill", jsonStr.toString());
//						messageQueueService.sendMessage("bm_market_seckill", jsonStr.toString(), true);
                        //此处还应该标记一个seckillId和openId的唯一标志来给轮询接口判断请求是否已经处理完成，需要在下单完成之后去维护删除该标志，并且创建一个新的标志，并存放orderId
                        redisUtil.cacheValue("LOCK_POLLING_" + stallActivityId + "_" + openId, "true");
                        //维护一个key，防止用户在该活动重复购买，当支付过期之后应该维护删除该标志
                        redisUtil.cacheValue("SECKILL_LIMIT_" + stallActivityId + "_" + openId, "true", 3600 * 24 * 7);

                        response.setIsSuccess(true);
                        response.setResponseCode(6101);
                        response.setResponseMsg("排队中，请稍后");
                        response.setRefreshTime(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.setIsSuccess(false);
                        response.setResponseCode(6102);
                        response.setResponseMsg("秒杀失败，商品已经售罄");
                        response.setRefreshTime(0);
                    }
                } else {
                    //需要在消费端维护一个真实的库存损耗值，用来显示是否还有未完成支付的用户
                    String redisRealStock = Integer.toString((Integer) redisUtil.getValue("SECKILL_REAL_STOCKNUM_" + stallActivityId));
                    int realStock = Integer.parseInt(redisRealStock == null ? "0" : redisRealStock);    //剩余的真实库存
                    if (realStock > 0) {
                        response.setIsSuccess(false);
                        response.setResponseCode(6103);
                        response.setResponseMsg("秒杀失败，还有部分订单未完成支付，超时将返还库存");
                        response.setRefreshTime(0);
                    } else {
                        response.setIsSuccess(false);
                        response.setResponseCode(6102);
                        response.setResponseMsg("秒杀失败，商品已经售罄");
                        response.setRefreshTime(0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setIsSuccess(false);
            response.setResponseCode(6102);
            response.setResponseMsg("秒杀失败，商品已经售罄");
            response.setRefreshTime(0);
        } finally {
            LOGGER.info("开始释放锁资源...");
            redissonDistributedLocker.unlock(lockKey);  //释放锁
        }
        return response;
    }

    @Override
    public boolean checkStartSeckill(int stallActivityId) {
        //此处已经省略了业务代码，良好的操作时应该将秒杀活动的开始时间在新增/编辑主数据的是维护到redis中，并维护好key值，此处取出，然后做出判断
        //默认为开始了
        return true;
    }

    @Override
    @Transactional
    public Result startSeckilDBPCC_TWO(long seckillId, long userId, String orderCode) {
        //单用户抢购一件商品没有问题、但是抢购多件商品不建议这种写法
        int count = seckillMapper.Update(seckillId);
        if (count > 0) {
            BigDecimal price =seckillMapper.getPriceByid(seckillId);
            Sorder order = new Sorder();
            order.setOrderSn(orderCode);
            order.setCouponId(String.valueOf(seckillId));
            order.setUserId(String.valueOf(userId));
            order.setOrderType((short) 1);
            order.setStatus("0");
            order.setDeleteStatus(0);
            order.setPayAmount(price);
            order.setPayType(0);
            order.setCreateTime(new Timestamp(new Date().getTime()));
            order.setModifyTime(new Timestamp(new Date().getTime()));
            int i = sorderMapper.save(order);
            if (i > 0) {
                return Result.ok(SeckillStatEnum.SUCCESS);
            } else {
                return Result.error(SeckillStatEnum.END);
            }
        } else {
            return Result.error(SeckillStatEnum.END);
        }
    }

    @Override
    public Seckill findById(long parseLong) {
        return seckillMapper.findById(parseLong);
    }

    @Override
    public List<Seckill> findAll() {
        return seckillMapper.findAll();
    }

    @Override
    public int insert(Seckill seckill) {
        return seckillMapper.save(seckill);
    }

    @Override
    public int updateall(Seckill seckill) {
        return seckillMapper.updateall(seckill);
    }

    @Override
    @Transactional
    public int delteByid(String[] idArray) {
        int count =0;
        for (String id:idArray){ ;
            int i= seckillMapper.delete(Integer.parseInt(id));
            count+=i;
        }
        return count;
    }
    @Override
    public PageInfo<Seckill> findall(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Seckill> list = seckillMapper.findAll();
        PageInfo<Seckill> seckillPageInfo = new PageInfo(list);
        return seckillPageInfo;
    }
}
