package com.xbc.community.productSeckill.web;

import com.alibaba.fastjson.JSONObject;
import com.xbc.community.productSeckill.entity.Result;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.enums.SeckillStatEnum;
import com.xbc.community.productSeckill.kafka.KafkaSender;
import com.xbc.community.productSeckill.lock.RedissonDistributedLocker;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.response.BaseResponse;
import com.xbc.community.productSeckill.response.ResponseBean;
import com.xbc.community.productSeckill.response.SeckillInfoResponse;
import com.xbc.community.productSeckill.response.StockNumResponse;
import com.xbc.community.productSeckill.service.ISeckillService;
import com.xbc.community.productSeckill.service.SorderService;
import com.xbc.community.productSeckill.utils.AssertUtil;
import com.xbc.community.productSeckill.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Api(tags = "分布式秒杀")
@RestController
@CrossOrigin
@RequestMapping("/api/seckill")
public class SeckillDistributedController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SeckillDistributedController.class);

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    //调整队列数 拒绝服务
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000));

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private SorderService sorderService;

    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private RedissonDistributedLocker redissonDistributedLocker;
    @Autowired
    RedisUtil redisUtil;

    @ApiOperation(value = "秒杀四(Kafka分布式队列)", nickname = "科帮网")
    @PostMapping("/startKafkaQueue")
    public Result startKafkaQueue(long seckillId) {
        //redisUtil.cacheValue(seckillId+"", null);//秒杀结束
        //seckillService.deleteSeckill(seckillId);
        //System.out.println(seckillId);
        final long killId = seckillId;
        LOGGER.info("开始秒杀四");
        if(redisUtil.containsValueKey("seckill_count")){
                    redisUtil.incr("seckill_count",1);
                }else{
                    redisUtil.cacheValue("seckill_count",1);
        }
        if(redisUtil.containsValueKey("seckill_stocknum")){
            int seckill_stocknum = (int)redisUtil.getValue("seckill_stocknum");
            if(seckill_stocknum>0) {
                redisUtil.decr("seckill_stocknum",1);
            }else{
                return Result.error();
            }
        }else{
            redisUtil.cacheValue("seckill_stocknum",100);
        }
        int count = (int) redisUtil.getValue("seckill_count");
        System.out.println(count);
        if(count>1000){
            return Result.error();
        }else{
            LOGGER.info("第" + count + "个请求进入到了消息队列");
            kafkaSender.sendChannelMess("seckill", killId + ";" + count);
        }


//        for (int i = 0; i < 2000; i++) {
//            final long userId = i;
//            Runnable task = () -> {
//                //思考如何返回给用户信息ws
//                if(redisUtil.containsValueKey("seckill_count")){
//                    redisUtil.incr("seckill_count",1);
//                }else{
//                    redisUtil.cacheValue("seckill_count",1);
//                }
//                kafkaSender.sendChannelMess("seckill", killId + ";" + userId);
//
//            };
//            executor.execute(task);
//        }
      //  try {
            //Thread.sleep(10000);
            //redisUtil.cacheValue(String.valueOf(killId), null);
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            LOGGER.info("一共秒杀出{}件商品", seckillCount);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return Result.ok();
    }

    @ApiOperation(value="设置活动库存",nickname="Guoqing")
    @RequestMapping(value="/setStockNum", method= RequestMethod.POST)
    public BaseResponse setStockNum(@RequestBody JSONObject jsonObject) {

        int stockNum = jsonObject.containsKey("stockNum")?jsonObject.getInteger("stockNum"):0;
        int stallActivityId = jsonObject.containsKey("stallActivityId") ? jsonObject.getInteger("stallActivityId") : -1;
        AssertUtil.isTrue(stallActivityId != -1, "非法参数");
        redisUtil.incr("SECKILL_STOCKNUM_" + stallActivityId, stockNum);
        redisUtil.incr("SECKILL_REAL_STOCKNUM_" + stallActivityId, stockNum);

        return new BaseResponse();
    }

    /**
     * 查看活动库存情况
     * @param jsonObject
     * @return
     */
    @ApiOperation(value="查看活动库存",nickname="Guoqing")
    @RequestMapping(value="/getStockNum", method= RequestMethod.POST)
    public StockNumResponse getStockNum(@RequestBody JSONObject jsonObject) {
        StockNumResponse response = new StockNumResponse();
        int stallActivityId = jsonObject.containsKey("stallActivityId") ? jsonObject.getInteger("stallActivityId") : -1;
        AssertUtil.isTrue(stallActivityId != -1, "非法参数");
        String stockNum = Integer.toString( (Integer) redisUtil.getValue("SECKILL_STOCKNUM_" + stallActivityId));
        String realStockNum = Integer.toString( (Integer) redisUtil.getValue("SECKILL_REAL_STOCKNUM_" + stallActivityId));
        response.setStockNum(Long.parseLong(stockNum));
        response.setRealStockNum(Long.parseLong(realStockNum));
        return response;
    }

    /**
     * 06.04-去秒杀，创建秒杀订单
     * 通过分布式锁的方式控制，控制库存不超卖
     * <p>Title: testSeckill</p>
     * <p>Description: 秒杀下单</p>
     * @param jsonObject
     * @return
     */
    @ApiOperation(value="去秒杀--先分布式锁模式",nickname="Guoqing")
    @RequestMapping(value="/goSeckill", method= RequestMethod.POST)
    public SeckillInfoResponse goSeckill(@RequestBody JSONObject jsonObject) {
        int stallActivityId = jsonObject.containsKey("stallActivityId") ? jsonObject.getInteger("stallActivityId") : -1;		//活动Id
        AssertUtil.isTrue(stallActivityId != -1, "非法參數");
        int purchaseNum = jsonObject.containsKey("purchaseNum") ? jsonObject.getInteger("purchaseNum") : 1;		//购买数量
        AssertUtil.isTrue(purchaseNum != -1, "非法參數");
        String openId = jsonObject.containsKey("openId") ? jsonObject.getString("openId") : null;
        AssertUtil.isTrue(!StringUtil.isEmpty(openId), 1101, "非法參數");
        String formId = jsonObject.containsKey("formId") ? jsonObject.getString("formId") : null;
        AssertUtil.isTrue(!StringUtil.isEmpty(formId), 1101, "非法參數");
        long addressId = jsonObject.containsKey("addressId") ? jsonObject.getLong("addressId") : -1;
        AssertUtil.isTrue(addressId != -1, "非法參數");
        //通过分享入口进来的参数
        String shareCode =  jsonObject.getString("shareCode");
        String shareSource =  jsonObject.getString("shareSource");
        String userCode =  jsonObject.getString("userId");

        //这里拒绝多余的请求，比如库存100，那么超过500或者1000的请求都可以拒绝掉，利用redis的原子自增
        long count = redisUtil.incr("SECKILL_COUNT_" + stallActivityId,1);
        if( count > 1000 ) {
            SeckillInfoResponse response = new SeckillInfoResponse();
            response.setIsSuccess(false);
            response.setResponseCode(6405);
            response.setResponseMsg( "活动太火爆，已经售罄啦！");
            return response;
        }
        LOGGER.info("第" + count + "个请求进入到了消息队列");

        return seckillService.startSeckill(stallActivityId, purchaseNum, openId, formId, addressId, shareCode, shareSource, userCode);
    }

    /**
     * 秒杀接口，先将请求放入队列模式
     * @param jsonObject
     * @return
     */
    @ApiOperation(value="去秒杀--先队列模式",nickname="Guoqing")
    @RequestMapping(value="/goSeckillByQueue", method= RequestMethod.POST)
    public BaseResponse goSeckillByQueue(@RequestBody JSONObject jsonObject) {
        int stallActivityId = jsonObject.containsKey("stallActivityId") ? jsonObject.getInteger("stallActivityId") : -1;		//活动Id
        AssertUtil.isTrue(stallActivityId != -1, "非法參數");
        int purchaseNum = jsonObject.containsKey("purchaseNum") ? jsonObject.getInteger("purchaseNum") : 1;		//购买数量
        AssertUtil.isTrue(purchaseNum != -1, "非法參數");
        String openId = jsonObject.containsKey("openId") ? jsonObject.getString("openId") : null;
        AssertUtil.isTrue(!StringUtil.isEmpty(openId), 1101, "非法參數");
//        String formId = jsonObject.containsKey("formId") ? jsonObject.getString("formId") : null;
//        AssertUtil.isTrue(!StringUtil.isEmpty(formId), 1101, "非法參數");
//        long addressId = jsonObject.containsKey("addressId") ? jsonObject.getLong("addressId") : -1;
//        AssertUtil.isTrue(addressId != -1, "非法參數");


        JSONObject jsonStr = new JSONObject();
        jsonStr.put("stallActivityId", stallActivityId);
        jsonStr.put("purchaseNum", purchaseNum);
        jsonStr.put("openId", openId);
//        jsonStr.put("addressId", addressId);
//        jsonStr.put("formId", formId);
        //判断秒杀活动是否开始
        if( !seckillService.checkStartSeckill(stallActivityId) ) {
            return new BaseResponse(false, 6205, "秒杀活动尚未开始，请稍等！");
        }
        //这里拒绝多余的请求，比如库存100，那么超过500或者1000的请求都可以拒绝掉，利用redis的原子自增操作
        long count = redisUtil.incr("SECKILL_COUNT_" + stallActivityId,1);
        if( count > 2000 ) {
            return new BaseResponse(false, 6405, "活动太火爆，已经售罄啦！");
        }
        LOGGER.info("第" + count + "个请求进入到了消息队列");
        //做用户重复购买校验
        if( redisUtil.containsValueKey("SECKILL_LIMIT_" + stallActivityId + "_" + openId) ) {
            return new BaseResponse(false, 6105, "您正在参与该活动，不能重复购买！");
        }
        //放入kafka消息队列
        kafkaSender.sendChannelMess("seckill_queue", jsonStr.toString());
        return new BaseResponse();
    }


    /**
     * 轮询请求  判断是否获得下单资格
     * @param jsonObject
     * @return
     */
    @ApiOperation(value="轮询接口--先队列模式",nickname="Guoqing")
    @RequestMapping(value="/seckillPollingQueue", method= RequestMethod.POST)
    public SeckillInfoResponse seckillPollingQueue(@RequestBody JSONObject jsonObject) {
        int stallActivityId = jsonObject.containsKey("stallActivityId") ? jsonObject.getInteger("stallActivityId") : -1;		//活动Id
        AssertUtil.isTrue(stallActivityId != -1, "非法參數");
        String openId = jsonObject.containsKey("openId") ? jsonObject.getString("openId") : null;
        AssertUtil.isTrue(!StringUtil.isEmpty(openId), 1101, "非法參數");

        SeckillInfoResponse response = new SeckillInfoResponse();
        //是否存在下单资格码的key
        if( redisUtil.containsValueKey("SECKILL_QUEUE_"+stallActivityId+"_"+openId) ){
            String result =(String) redisUtil.getValue("SECKILL_QUEUE_"+stallActivityId+"_"+openId);
            response = JSONObject.parseObject(JSONObject.parseObject(result).getJSONObject("response").toJSONString(), SeckillInfoResponse.class);
        } else {
            response.setIsSuccess(true);
            response.setResponseCode(0);
            response.setResponseMsg("活动太火爆，排队中...");
            response.setRefreshTime(0);
        }
        return response;
    }

    /**
     * 根据获取到的下单资格码创建订单
     * @param jsonObject
     * @return
     */
    @ApiOperation(value="先队列模式--下单接口",nickname="Guoqing")
    @RequestMapping(value="/createOrder", method= RequestMethod.POST)
    public ResponseBean createOrder(@RequestBody JSONObject jsonObject) {
        int stallActivityId = jsonObject.containsKey("stallActivityId") ? jsonObject.getInteger("stallActivityId") : -1;		//活动Id
        AssertUtil.isTrue(stallActivityId != -1, "非法參數");
        String openId = jsonObject.containsKey("openId") ? jsonObject.getString("openId") : null;
        AssertUtil.isTrue(!StringUtil.isEmpty(openId), 1101, "非法參數");
        String orderQualificationCode = jsonObject.containsKey("orderQualificationCode") ? jsonObject.getString("orderQualificationCode") : null;
        AssertUtil.isTrue(!StringUtil.isEmpty(orderQualificationCode), 1101, "非法參數");

        //校验下单资格码
        String redisQualificationCode = (String) redisUtil.getValue("SECKILL_QUALIFICATION_CODE_" + stallActivityId + "_" + openId);
        System.out.println(redisQualificationCode);
        if(StringUtils.isEmpty(redisQualificationCode) || !orderQualificationCode.equals(redisQualificationCode) ) {
            redisUtil.removeValue("SECKILL_QUALIFICATION_CODE_" + stallActivityId + "_" + openId);
            return new ResponseBean(false, 6305, "您的资格码已经过期！");
            //删除redis中的key，让轮询接口发现，该订单已经处理完成
        }else {
            Result result = seckillService.startSeckilDBPCC_TWO(stallActivityId,Long.valueOf(openId),redisQualificationCode);
            if(result.equals(Result.ok(SeckillStatEnum.SUCCESS))){
                //redisUtil.decr("SECKILL_REAL_STOCKNUM_" + stallActivityId, 1);
                Sorder sorder =sorderService.findOrderByCode(redisQualificationCode);
                redisUtil.removeValue("SECKILL_QUALIFICATION_CODE_" + stallActivityId + "_" + openId);
                redisUtil.removeValue("SECKILL_QUEUE_"+stallActivityId+"_"+openId);
                //并将orderId_orderCode放入缓存，有效时间10分钟（因为支付有效时间为10分钟）
                redisUtil.cacheValue("SECKILL_ORDERID_" + stallActivityId + "_" + openId, JSONObject.toJSONString(sorder), 600);
                //延时10分钟查这个key还在就得删除订单不在就支付成功
                return new ResponseBean(true,0,"下单成功",sorder);
            }else{
                return new ResponseBean(false,1,"下单失败");
            }
            //走后续的下单流程，并校验真实库存；该接口的流量已经是与真实库存几乎相匹配的流量值，按理不应该存在超高并发
        }
    }

    @ApiOperation(value="test",nickname="Guoqing")
    @GetMapping(value="/test")
    public void test() throws InterruptedException {
        final int[] counter = {0};

        for (int i= 0; i < 300; i++){

            new Thread(new Runnable() {

                @Override

                public void run() {
                    boolean isGetLock = redissonDistributedLocker.tryLock("test0001", 3L, 1L);
                    LOGGER.info(isGetLock + "");
                    if(isGetLock) {
                        try {
                            int a = counter[0];
                            counter[0] = a + 1;
                            LOGGER.info(a + "");
                        } finally {
                            redissonDistributedLocker.unlock("test0001");
                        }
                    }
                }
            }).start();

        }

        // 主线程休眠，等待结果
        Thread.sleep(10000);
        LOGGER.info(counter[0] + "");
    }

    @ApiOperation(value="test1",nickname="Guoqing")
    @GetMapping(value="/test1")
    public void test1() throws InterruptedException {
        final int[] counter = {0};

        for (int i= 0; i < 100; i++){

            new Thread(new Runnable() {

                @Override

                public void run() {
                    try {
                        redissonDistributedLocker.lock("test0002", 1L);
                        LOGGER.info(redissonDistributedLocker.isLocked("test0002") + "");
                        int a = counter[0];
                        counter[0] = a + 1;
                        LOGGER.info(a + "");
                    } finally {
                        redissonDistributedLocker.unlock("test0002");
                    }
                }
            }).start();

        }

        // 主线程休眠，等待结果
        Thread.sleep(10000);
        LOGGER.info(counter[0] + "");
    }

//    @ApiOperation(value="test2",nickname="Guoqing")
//    @GetMapping(value="/test2")
//    public void test2() throws InterruptedException {
//        LOGGER.info(SystemUtil.getJavaRuntimeInfo().toString());
//        LOGGER.info(SystemUtil.getJavaInfo().toString());
//        LOGGER.info(SystemUtil.getJvmInfo().toString());
//        LOGGER.info(SystemUtil.getJavaSpecInfo().toString());
//        LOGGER.info(SystemUtil.getRuntimeInfo().toString());
//    }
}
