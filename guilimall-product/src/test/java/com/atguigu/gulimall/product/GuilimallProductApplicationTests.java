package com.atguigu.gulimall.product;


import com.atguigu.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


@Slf4j
@SpringBootTest
class GuilimallProductApplicationTests {


    @Autowired
    CategoryService categoryService;
    @Test
    public void testFindPath(){

        Long[] catelogPath = categoryService.findCatelogPath(225l);
        log.info("完整路径 ：{}", Arrays.toString(catelogPath));
    }



}



