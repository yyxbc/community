package com.xbc.community.productSeckill.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.xbc.community.productSeckill.entity.PaymentInfo;
import com.xbc.community.productSeckill.entity.Seckill;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.kafka.KafkaSender;
import com.xbc.community.productSeckill.payment.config.AlipayConfig;
import com.xbc.community.productSeckill.payment.service.PaymentService;
import com.xbc.community.productSeckill.service.ISeckillService;
import com.xbc.community.productSeckill.service.SorderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private SorderService sorderService;
    @Autowired
    KafkaSender kafkaSender;

    @RequestMapping("alipay/callback/return")
    //@LoginRequired(loginSuccess = true)
    public String aliPayCallBackReturn(HttpServletRequest request, ModelMap modelMap){

        // 回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();


        // 通过支付宝的paramsMap进行签名验证，2.0版本的接口将paramsMap参数去掉了，导致同步请求没法验签
        if(StringUtils.isNotBlank(sign)){
            // 验签成功
            // 更新用户的支付状态

            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(out_trade_no);
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);// 支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content);//回调请求字符串
            paymentInfo.setCallbackTime(new Date());
            paymentService.updatePayment(paymentInfo);

            JSONObject jsonStr = new JSONObject();
            jsonStr.put("out_trade_no", paymentInfo.getOrderSn());
            kafkaSender.sendChannelMess("PAYHMENT_SUCCESS_QUEUE",jsonStr.toJSONString());
        }

        return "order/finish";
    }



    @RequestMapping("alipay/submit")
    //@LoginRequired(loginSuccess = true)
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap){

        // 获得一个支付宝请求的客户端(它并不是一个链接，而是一个封装好的http的表单请求)
        String form = null;
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
       Sorder sorder = sorderService.findOrderByCode(outTradeNo);
        // 回调函数
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);

        Seckill seckill =seckillService.getById(Long.parseLong(sorder.getCouponId()));
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",sorder.getPayAmount());
        map.put("subject",seckill.getName());

        String param = JSON.toJSONString(map);

        alipayRequest.setBizContent(param);

        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 生成并且保存用户的支付信息
       // OmsOrder omsOrder = orderService.getOrderByOutTradeNo(outTradeNo);
        //String nativeSql = "select * from sorder  WHERE order_sn=? ";//UPDATE锁表
        //Object sorder =  dynamicQuery.nativeQueryObject(nativeSql,new Object[]{outTradeNo});
        //Sorder sorder=seckillService.findOrderByCode(outTradeNo);

        char[] list =JSON.toJSONString(sorder).toCharArray();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(String.valueOf(sorder.getId()));
        paymentInfo.setOrderSn(sorder.getOrderSn());
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject(seckill.getName());
        paymentInfo.setTotalAmount(sorder.getPayAmount());
        paymentService.savePaymentInfo(paymentInfo);

        // 向消息中间件发送一个检查支付状态(支付服务消费)的延迟消息队列
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNo,5);

        // 提交请求到支付宝
        return form;
    }




    @RequestMapping("mx/submit")
   // @LoginRequired(loginSuccess = true)
    public String mx(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap){
        return null;
    }
}
