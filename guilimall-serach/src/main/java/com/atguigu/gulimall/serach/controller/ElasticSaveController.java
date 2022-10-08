package com.atguigu.gulimall.serach.controller;


import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.serach.service.ProductSaveService;
import jdk.internal.org.objectweb.asm.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {


    @Autowired
    ProductSaveService productSaveService;

    //上架商品
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        Boolean aBoolean = false;
        try {
            aBoolean = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            log.error("elasticSaveController商品上架错误:{}", e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());

        }
        if (!aBoolean) {
            return R.ok();
        } else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

}
