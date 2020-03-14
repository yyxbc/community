package com.xbc.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@MapperScan("com.xbc.community.mapper","com.xbc.community.productSeckill.mapper")
@SpringBootApplication
@EnableScheduling
//@EnableCaching
@Configuration
@EnableTransactionManagement
@EnableAsync
public class CommunityApplication {


    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
