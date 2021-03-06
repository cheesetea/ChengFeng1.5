package com.beautifulsoup.chengfeng.service.impl;

import com.beautifulsoup.chengfeng.controller.vo.ProductSimpleVo;
import com.beautifulsoup.chengfeng.controller.vo.PurchaseProductVo;
import com.beautifulsoup.chengfeng.pojo.PurchaseProduct;
import com.beautifulsoup.chengfeng.repository.ProductRepository;
import com.beautifulsoup.chengfeng.service.PurchaseProductService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseProductServiceImpl implements PurchaseProductService {

    @Autowired
    private ProductRepository productRepository;


    @Override
    public List<ProductSimpleVo> findSimpleProductsByCategoryId(Integer categoryId, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest=PageRequest.of(pageNum-1,pageSize, Sort.Direction.DESC,"goodRatio");
        List<PurchaseProduct> byCategoryId = productRepository.findByCategoryId(categoryId, pageRequest);
        List<ProductSimpleVo> productSimpleVos=Lists.newArrayList();
        byCategoryId.stream().forEach(purchaseProduct -> {
            ProductSimpleVo productSimpleVo=new ProductSimpleVo();
            BeanUtils.copyProperties(purchaseProduct,productSimpleVo);
            productSimpleVos.add(productSimpleVo);
        });
        return productSimpleVos;
    }

    @Override
    public PurchaseProductVo findProductDetailInfoById(Integer productId) {
        Optional<PurchaseProduct> optional = productRepository.findById(productId);
        if (optional.isPresent()){
            PurchaseProductVo purchaseProductVo=new PurchaseProductVo();
            BeanUtils.copyProperties(optional.get(),purchaseProductVo);
            return purchaseProductVo;
        }
        return null;
    }

    @Override
    public List<ProductSimpleVo> findProductByBanner(Integer pageNum, Integer pageSize,String keyword) {
//        PageRequest pageRequest=PageRequest.of(pageNum,pageSize, Sort.Direction.DESC,"goodRatio");
        List<PurchaseProduct> products = productRepository.findByDetail(keyword);
        List<ProductSimpleVo> productVos=Lists.newArrayList();
        products.parallelStream().forEach(product->{
            ProductSimpleVo simpleVo=new ProductSimpleVo();
            BeanUtils.copyProperties(product,simpleVo);
            if (!CollectionUtils.isEmpty(product.getPurchaseProductSkus())){
                simpleVo.setProductSku(product.getPurchaseProductSkus().get(0));
            }
            productVos.add(simpleVo);
        });
        return productVos;
    }
}
