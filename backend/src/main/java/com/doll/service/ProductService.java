package com.doll.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doll.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {
    IPage<Product> searchProducts(String keyword, Integer page, Integer size);
    IPage<Product> getRecommendProducts(Long userId, Integer page, Integer size);

    void recordProductClick(Long productId);

    Product enrichHotMetrics(Product product);

    void enrichHotMetrics(List<Product> products);
}
