package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author dujiahui
 * @email sunlightcs@gmail.com
 * @date 2022-07-07 17:44:28
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
