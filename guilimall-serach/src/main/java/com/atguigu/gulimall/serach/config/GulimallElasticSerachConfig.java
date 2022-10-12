package com.atguigu.gulimall.serach.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 这个配置类使用用来使用elasticsearch
 * 1，导入依赖
 * 2,编写配置，给容器中注入一个httpHighlevelClient
 * 3,操作es就需要查看官方文档了
 */

@Configuration
public class GulimallElasticSerachConfig {


    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder=RequestOptions.DEFAULT.toBuilder();

        COMMON_OPTIONS=builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(){
        RestHighLevelClient client=new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("39.105.30.41",9200,"http")));
        return client;
    }

}
