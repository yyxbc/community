//package com.xbc.community.productSeckill.config;
//import com.xbc.community.productSeckill.interceptor.LimitInterceptor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Component
//public class WebConfig implements WebMvcConfigurer {
//
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		//多个拦截器组成一个拦截器链
//		registry.addInterceptor(new LimitInterceptor(3000, LimitInterceptor.LimitType.DROP)).addPathPatterns("/**");
//		WebMvcConfigurer.super.addInterceptors(registry);
//	}
//
//}
