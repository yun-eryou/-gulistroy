package com.atguigu.gulimall.serach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class GuilimallSerachApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuilimallSerachApplication.class, args);
    }

}
