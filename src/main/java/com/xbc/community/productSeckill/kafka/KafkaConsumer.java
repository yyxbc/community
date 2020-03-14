package com.xbc.community.productSeckill.kafka;

import com.alibaba.fastjson.JSONObject;
import com.xbc.community.productSeckill.entity.PaymentInfo;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.lock.RedissonDistributedLocker;
import com.xbc.community.productSeckill.payment.service.PaymentService;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.response.SeckillInfoResponse;
import com.xbc.community.productSeckill.service.ISeckillService;
import com.xbc.community.productSeckill.service.SorderService;
import com.xbc.community.productSeckill.utils.SerialNo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 消费者 spring-kafka 2.0 + 依赖JDK8
 * @author Guoqing
 */
@Component
public class KafkaConsumer {
	
	private Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private RedissonDistributedLocker redissonDistributedLocker;

	@Autowired
	ISeckillService seckillService;

	@Autowired
	PaymentService paymentService;
	@Autowired
	SorderService sorderService;
	
    /**
     * 监听seckill主题,有消息就读取
     * 主要消费秒杀进入到下订单操作的队列数据，此处的数据已经过滤了绝大部分请求，只有真正得到下单机会的用户才会进入到这一环节
     * @param message
     */
    @KafkaListener(topics = {"seckill"})
    public void receiveMessage(String message){
    	try {
			//收到通道的消息之后执行秒杀操作
			logger.info(message);
			JSONObject json = JSONObject.parseObject(message);
			long stallActivityId = json.getLong("stallActivityId");
			int purchaseNum = json.getInteger("purchaseNum");
			String openId = json.getString("openId");
//			long addressId = json.getLong("addressId");
//			String formId = json.getString("formId");
//			String shareCode = json.getString("shareCode");
//			String shareSource = json.getString("shareSource");
//			String userCode = json.getString("userCode");
			//生成订单，模拟生成订单编码
			String orderCode = "J"+ SerialNo.getUNID();
			seckillService.startSeckilDBPCC_TWO(stallActivityId,Long.valueOf(openId),orderCode);
			//删除redis中的key，让轮询接口发现，该订单已经处理完成
			redisUtil.removeValue("LOCK_POLLING_" + stallActivityId + "_" + openId);
			//并将orderId_orderCode放入缓存，有效时间10分钟（因为支付有效时间为10分钟）
			redisUtil.cacheValue("SECKILL_ORDERID_" + stallActivityId + "_" + openId, orderCode, 600);
			String lockKey = "marketOrder"+stallActivityId;	//控制锁的颗粒度(摊位活动ID)
			boolean isGetLock = redissonDistributedLocker.tryLock(lockKey, 1L, 1L);	//最多等待1S，每次操作预计的超时时间1S
        	if(isGetLock) {
        		try {
					//扣减真实库存
					redisUtil.decr("SECKILL_REAL_STOCKNUM_" + stallActivityId, purchaseNum);
				}finally{
					redissonDistributedLocker.unlock(lockKey);
				}
        	}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 与上述方法不同，该方法还包含库存校验等逻辑操作
     */
    @KafkaListener(topics = {"seckill_queue"})
    public void receiveMessage2(String message) {
    	JSONObject json = JSONObject.parseObject(message);
		long stallActivityId = json.getLong("stallActivityId");
		int purchaseNum = json.getInteger("purchaseNum");
		String openId = json.getString("openId");
		String lockKey = "marketOrder"+stallActivityId;//控制锁的颗粒度(摊位活动ID)
		redissonDistributedLocker.lock(lockKey, 1L);
		try{
			JSONObject result = new JSONObject();
			SeckillInfoResponse response = new SeckillInfoResponse();
			String redisStock = Integer.toString( (Integer)redisUtil.getValue("SECKILL_STOCKNUM_" + stallActivityId));
			int surplusStock = Integer.parseInt(redisStock == null ? "0" : redisStock);	//剩余库存
			//如果剩余库存大于购买数量，则获得下单资格，并生成唯一下单资格码
			if( surplusStock >= purchaseNum ) {
				response.setIsSuccess(true);
				response.setResponseCode(0);
				response.setResponseMsg("您已获得下单资格，请尽快下单");
				response.setRefreshTime(0);
				String code = SerialNo.getUNID();
				response.setOrderQualificationCode(code);
				//将下单资格码维护到redis中，用于下单时候的检验；有效时间10分钟；
				redisUtil.cacheValue("SECKILL_QUALIFICATION_CODE_" + stallActivityId + "_" + openId, code, 10*60);
				//维护一个key，防止获得下单资格用户重新抢购，当支付过期之后应该维护删除该标志
				redisUtil.cacheValue("SECKILL_LIMIT_" + stallActivityId + "_" + openId, "true", 3600*24*7);
				//扣减锁定库存
				//save
				//seckillService.startSeckilDBPCC_TWO(stallActivityId,Long.valueOf(openId),code);
				redisUtil.decr("SECKILL_STOCKNUM_" + stallActivityId, purchaseNum);
			}else {
				response.setIsSuccess(false);
				response.setResponseCode(6102);
				response.setResponseMsg("秒杀失败，商品已经售罄");
				response.setRefreshTime(0);
			}
			result.put("response", response);
			//将信息维护到redis中
			redisUtil.cacheValue("SECKILL_QUEUE_"+stallActivityId+"_"+openId, result.toJSONString(), 3600*24*7);
		}finally{
			redissonDistributedLocker.unlock(lockKey);
		}
    }

	@KafkaListener(topics = {"PAYMENT_CHECK_QUEUE"})
	public void consumePaymentCheckResult(String message)  {
		JSONObject json = JSONObject.parseObject(message);
		String out_trade_no = json.getString("out_trade_no");
		Integer count = 0;
		if(json.getString("count")!=null){
			count = Integer.parseInt(""+json.getString("count"));
		}

		// 调用paymentService的支付宝检查接口
		System.out.println("进行延迟检查，调用支付检查的接口服务");
		Map<String,Object> resultMap = paymentService.checkAlipayPayment(out_trade_no);

		if(resultMap!=null&&!resultMap.isEmpty()){
			String trade_status = (String)resultMap.get("trade_status");
			// 根据查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据和后续任务
			if(StringUtils.isNotBlank(trade_status)&&trade_status.equals("TRADE_SUCCESS")){
				// 支付成功，更新支付发送支付队列
				PaymentInfo paymentInfo = new PaymentInfo();
				paymentInfo.setOrderSn(out_trade_no);
				paymentInfo.setPaymentStatus("已支付");
				paymentInfo.setAlipayTradeNo((String)resultMap.get("trade_no"));// 支付宝的交易凭证号
				paymentInfo.setCallbackContent((String)resultMap.get(("call_back_content")));//回调请求字符串
				paymentInfo.setCallbackTime(new Date());
				System.out.println("已经支付成功，调用支付服务，修改支付信息和发送支付成功的队列");
				paymentService.updatePayment(paymentInfo);
				count=0;
				return;
			}
		}

		if(count>0){
			// 继续发送延迟检查任务，计算延迟时间等
			System.out.println("没有支付成功，检查剩余次数为"+count+",继续发送延迟检查任务");
			count--;
			paymentService.sendDelayPaymentResultCheckQueue(out_trade_no,count);
		}else{
			System.out.println("检查剩余次数用尽，结束检查");
		}


	}

	@KafkaListener(topics = {"PAYHMENT_SUCCESS_QUEUE"})
	public void paysucess(String message)  {
		JSONObject json = JSONObject.parseObject(message);
		String outTradeNo = json.getString("out_trade_no");
		Sorder sorder =sorderService.findOrderByCode(outTradeNo);
		if(redisUtil.containsValueKey("SECKILL_ORDERID_" + Integer.parseInt(sorder.getCouponId()) + "_" + sorder.getUserId())){
			redisUtil.removeValue("SECKILL_ORDERID_" + Integer.parseInt(sorder.getCouponId()) + "_" + sorder.getUserId());
		}
	}
}