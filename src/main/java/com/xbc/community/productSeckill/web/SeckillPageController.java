package com.xbc.community.productSeckill.web;

import com.xbc.community.productSeckill.entity.Result;
import com.xbc.community.productSeckill.entity.Seckill;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.service.ISeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api(tags = "秒杀商品")
@Controller
public class SeckillPageController {

	@Autowired
	private ISeckillService seckillService;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
    RedisTemplate<Object,Object> redisTemplate;
	
	@ApiOperation(value = "秒杀商品列表", nickname = "小柒2012")
	@GetMapping("/list")
	@ResponseBody
	public Result list() {
		//返回JSON数据、前端VUE迭代即可
		List<Seckill>  List = seckillService.getSeckillList();

		for(Seckill s: List){
			if(redisUtil.getValue(s.getSeckillId()+"")==null){
				redisUtil.containsValueKey(s.getSeckillId()+"");
				redisUtil.cacheValue(s.getSeckillId()+"",s,600);
				System.out.println(redisUtil.getValue(s.getSeckillId()+""));
			}

		}
		return Result.ok(List);
	}

	@RequestMapping("/product")
	public String product(Model model) {
		System.out.println(1);
		model.addAttribute("section","product");
		List<Seckill>  List = seckillService.getSeckillList();
		System.out.println(List);
		for(Seckill l:List){
			if(!redisUtil.containsValueKey("SECKILL_REAL_STOCKNUM_" + l.getSeckillId())&&!redisUtil.containsValueKey("SECKILL_STOCKNUM_" + l.getSeckillId())){
				redisUtil.incr("SECKILL_STOCKNUM_" + l.getSeckillId(), l.getNumber());
				redisUtil.incr("SECKILL_REAL_STOCKNUM_" + l.getSeckillId(), l.getNumber());
			}
		}
		model.addAttribute("seckills",List);
		return "product";
	}
}
