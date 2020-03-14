package com.xbc.community.productSeckill.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xbc.community.productSeckill.entity.PaymentInfo;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.kafka.KafkaSender;
import com.xbc.community.productSeckill.mapper.PaymentInfoMapper;
import com.xbc.community.productSeckill.payment.service.PaymentService;
import com.xbc.community.productSeckill.service.SorderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    private SorderService sorderService;
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Override
    @Transactional
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        String orderSn = paymentInfo.getOrderSn();
        int num = paymentInfoMapper.findByOrderSn(orderSn);
        if(num>0){
            paymentInfoMapper.delByOrderSn(orderSn);
        }
        paymentInfoMapper.save(paymentInfo);
    }


    @Override
    @Transactional
    public void updatePayment(PaymentInfo paymentInfo) {
        // 幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfoResult =paymentInfoMapper.getByOrderSn(paymentInfoParam);
        if(StringUtils.isNotBlank(paymentInfoResult.getPaymentStatus())&&paymentInfoResult.getPaymentStatus().equals("已支付")){
            return;
        }else{
           // String orderSn = paymentInfo.getOrderSn();
           // dynamicQuery.update(paymentInfo);
            int num = paymentInfoMapper.update(paymentInfo);
                // 支付成功后，引起的系统服务-》订单服务的更新-》库存服务-》物流服务
                // 调用mq发送支付成功的消息
                //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本
            if(num>0){
                Sorder sorder =new Sorder();
                sorder.setOrderSn(paymentInfo.getOrderSn());
                sorder.setPayType(1);
                sorder.setStatus("1");
                sorder.setPaymentTime(paymentInfo.getCallbackTime());
                sorder.setModifyTime(new Date());
                sorderService.updateOrderByPay(sorder);
                JSONObject jsonStr = new JSONObject();
                jsonStr.put("out_trade_no", paymentInfo.getOrderSn());
                kafkaSender.sendChannelMess("PAYHMENT_SUCCESS_QUEUE",jsonStr.toJSONString());
            }


        }

    }

    @Override
    @Transactional
    public void sendDelayPaymentResultCheckQueue(String outTradeNo,int count) {
            //TextMessage textMessage=new ActiveMQTextMessage();//字符串文本
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("out_trade_no", outTradeNo);
        jsonStr.put("count", count);

        kafkaSender.sendChannelMess("PAYMENT_CHECK_QUEUE",jsonStr.toJSONString());
            // 为消息加入延迟时间
        //.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*60);
    }

    @Override
    public Map<String, Object> checkAlipayPayment(String out_trade_no) {

        Map<String,Object> resultMap = new HashMap<>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no",out_trade_no);
        request.setBizContent(JSON.toJSONString(requestMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("有可能交易已创建，调用成功");
            resultMap.put("out_trade_no",response.getOutTradeNo());
            resultMap.put("trade_no",response.getTradeNo());
            resultMap.put("trade_status",response.getTradeStatus());
            resultMap.put("call_back_content",response.getMsg());
        } else {
            System.out.println("有可能交易未创建，调用失败");

        }

        return resultMap;
    }
}
