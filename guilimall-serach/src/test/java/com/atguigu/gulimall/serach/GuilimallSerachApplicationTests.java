package com.atguigu.gulimall.serach;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.serach.config.GulimallElasticSerachConfig;
import lombok.Data;
import lombok.ToString;
import org.apache.lucene.index.IndexReader;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.soap.SAAJResult;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.util.IdentityHashMap;
import java.util.Map;

@SpringBootTest
public class GuilimallSerachApplicationTests {


    @Autowired
    private RestHighLevelClient client;
    @Test
 public    void contextLoads() {
        System.out.println(client);
    }


    /**
     * 测试存储数据到es中
     */
    @Test
    public void testIndex() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        //indexRequest.source("userName","zhangsan","age",18,"gender","男");
        User user = new User();
        user.setUserName("张三");
        user.setAge(19);
        user.setGender("男");
        String string = JSON.toJSONString(user);
        indexRequest.source(string, XContentType.JSON);// 需要保存的内容

        // 执行操作
        IndexResponse index = client.index(indexRequest, GulimallElasticSerachConfig.COMMON_OPTIONS);


        System.out.println(index);


    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }



    @Test
    public void searchData() throws Exception{

        // 1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSl，检索条件
        // SearchSourceBuilder 是sourceBuilder封装的条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //1.1 构造检索条件
        builder.query(QueryBuilders.matchQuery("address","mill"));
        //1.2 按照年龄的值进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        builder.aggregation(ageAgg);
        // 1.3 计算平均工资
        AvgAggregationBuilder field = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(field);


        System.out.println("检索条件"+builder.toString());


        searchRequest.source(builder);
        // 2,执行检索
        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSerachConfig.COMMON_OPTIONS);

        // 3,分析结果 search
        System.out.println(searchResponse.toString());
        //JSON.parseObject(searchResponse.toString(), Map.class);
        // 3.1 获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {

            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account"+account);
        }

        //3.2 获取这次检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        //for (Aggregation aggregation : aggregations.asList()) {
        //    System.out.println("当前聚合"+aggregation.getName());
        //
        //}
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄="+keyAsString+"==>"+bucket.getDocCount());
        }
        Avg balanceAvg = aggregations.get("balanceAvg");

        System.out.println("平均薪资"+balanceAvg.getValue());
    }

    @ToString
    @Data
   static class Account{
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }



}
