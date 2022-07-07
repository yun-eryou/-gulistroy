package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class GuilimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuilimallOrderApplication.class, args);
    }

}
