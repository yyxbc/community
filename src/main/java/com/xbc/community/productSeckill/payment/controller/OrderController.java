package com.xbc.community.productSeckill.payment.controller;

import com.xbc.community.bean.User;
import com.xbc.community.productSeckill.entity.Sorder;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.service.SorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
@Controller
public class OrderController {
    @Autowired
    private SorderService sorderService;

    @Autowired
    RedisUtil redisUtil;

    @RequestMapping("order/index")
    //@LoginRequired(loginSuccess = true)
    public String index(String outTradeNo, String totalAmount, HttpServletRequest request, ModelMap modelMap){
        //String memberId = (String)request.getAttribute("memberId");
        //String nickname = (String)request.getAttribute("nickname");
        BigDecimal bd=new BigDecimal(totalAmount);
        //modelMap.put("nickname",nickname);
        modelMap.put("outTradeNo",outTradeNo);
        modelMap.put("totalAmount",bd);
        return "order/index";
    }

    @RequestMapping("orderList")
    //@LoginRequired(loginSuccess = true)
    public String index(@SessionAttribute(value = "user",required = false) User user, Model model){
        List<Sorder> orderList = sorderService.findallByuserid(String.valueOf(user.getId()));
        model.addAttribute("orderList",orderList);
        return "order/orderList";
    }

    @GetMapping("/delorder")
    //@LoginRequired(loginSuccess = true)
    public String delorder(String outTradeNo, Model model){
        Sorder sorder = sorderService.findOrderByCode(outTradeNo);
        String[] idArray  = String.valueOf(sorder.getId()).split(",");
        int num =sorderService.delteByid(idArray);
        if(num>0&&redisUtil.containsValueKey("SECKILL_LIMIT_" + Integer.parseInt(sorder.getCouponId()) + "_" + sorder.getUserId())){
            redisUtil.removeValue("SECKILL_LIMIT_" + Integer.parseInt(sorder.getCouponId()) + "_" + sorder.getUserId());
        if(redisUtil.containsValueKey("SECKILL_ORDERID_" + Integer.parseInt(sorder.getCouponId()) + "_" + sorder.getUserId())){
            redisUtil.removeValue("SECKILL_ORDERID_" + Integer.parseInt(sorder.getCouponId()) + "_" + sorder.getUserId());
        }
        }
        return "redirect:/profile/order";
    }
}
