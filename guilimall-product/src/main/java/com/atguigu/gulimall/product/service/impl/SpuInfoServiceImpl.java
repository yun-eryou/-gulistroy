package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {



    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;


    @Autowired
    SkuImagesService skuImagesService;


    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;


    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    // TODO 更多补充，在高级部分
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1,保存spu的基本信息  pms_sku_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2,保存spu的描述图片  pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);


        // 3,保存spu的图集   pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        // 4,保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            attrValueEntity.setAttrName(byId.getAttrName());
            attrValueEntity.setAttrValue(attr.getAttrValues());
            attrValueEntity.setQuickShow(attr.getShowDesc());
            attrValueEntity.setSpuId(spuInfoEntity.getId());
            return attrValueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);



        // 4.5 保存spu的积分信息：gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }

        //5, 保存当前spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        if (skus!=null && skus.size()>0){
            skus.forEach(item->{
                String defaultImage="";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg()==1){
                        defaultImage=image.getImgUrl();
                    }
                }
                SkuInfoEntity infoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,infoEntity);
                infoEntity.setBrandId(spuInfoEntity.getBrandId());
                infoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                infoEntity.setSaleCount(0l);
                infoEntity.setSpuId(spuInfoEntity.getId());
                infoEntity.setSkuDefaultImg(defaultImage);
                //  5.1> sku的基本信息 pms_sku_info
                skuInfoService.saveSkuInfo(infoEntity);

                Long skuId = infoEntity.getSkuId();

                List<SkuImagesEntity> skuImagesEntityList = item.getImages().stream().map(img -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setImgUrl(img.getImgUrl());
                    imagesEntity.setDefaultImg(img.getDefaultImg());

                    return imagesEntity;
                }).filter(entity->{
                    // 返回true就是需要的数据，返回false将会被过滤掉
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                // 5.2> sku的图片信息    pms_sku_images
                skuImagesService.saveBatch(skuImagesEntityList);
                //TODO 没有路径的图片不需要保存


                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                // 5.3> sku的销售属性 pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);


                // 5.4 sku的优惠满减信息   gulimall_sms-> sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount()>0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode()!=0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }


            });


        }







        return;
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * status:
         * 0
         * key:
         * brandId:
         * 6
         * catelogId:
         * 225
         * page:
         * 1
         * limit:
         * 10
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.and(w->{
                w.eq("publish_status",status);
            });
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }



        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {


        // 1,查出当前spuid对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus=   skuInfoService.getSkusByASpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        // todo  查出当前sku所有可以被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds= attrService.selectSearchAttrIds(attrIds);

        Set<Long> idSet=new HashSet<>(searchAttrIds);


        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        // TODO 2.2发送远程调用库存系统，查询是否有库存
        Map<Long, Boolean> stockMap=null;
        try {
            R r = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> listTypeReference = new TypeReference<List<SkuHasStockVo>> (){
            };
            stockMap= r.getData(listTypeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));

        }catch (Exception e){
            log.error("库存服务查询出现问题：原因{}",e);
        }

        // 2,封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            //2.1,组装我们需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,skuEsModel);

            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            // 设置库存信息
            if (finalStockMap ==null){
                skuEsModel.setHasStock(true);
            }else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // todo 2.3 热度评分，0
            skuEsModel.setHotScore(0L);
            // todo 2.4 查询品牌和分类的名字信息
            BrandEntity byId = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(byId.getName());
            skuEsModel.setBrandImg(byId.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());


            // 设置检索属性
            skuEsModel.setAttrs(attrsList);




            return skuEsModel;
        }).collect(Collectors.toList());



        // todo 3，将数据发给es进行保存 ，发给gulimall-search
        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode()==0) {
            // 远程调用成功
            // TODO 4 修改当前spu的状态，修改成已上架
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            // 远程调用失败
            //TODO 5，重复调用，接口幂等性：，重试机制
        }
    }


}