package com.atguigu.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;



@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.coupon.dao")
@SpringBootApplication
public class GuliCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliCouponApplication.class, args);
    }

}
