package com.atguigu.gulimall.serach.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.serach.config.GulimallElasticSerachConfig;
import com.atguigu.gulimall.serach.constant.EsConstant;
import com.atguigu.gulimall.serach.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient client;


    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        //保存到es中
        // 1.给es中建立索引product,建立好映射关系,使用kibana已经设置成功了

        //2.给es中保存数据(批量保存数据)
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            // 2.1 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String string = JSON.toJSONString(skuEsModel);
            indexRequest.source(string, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = client.bulk(bulkRequest, GulimallElasticSerachConfig.COMMON_OPTIONS);

        // TODO 判断是否有错误，如果存在错误进行错误处理
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成:{},返回数据：{}",collect,bulk.toString());


        return b;
    }
}
