package com.doll.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.entity.Product;
import com.doll.mapper.ProductMapper;
import com.doll.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<Product> searchProducts(String keyword, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(Product::getName, keyword).or().like(Product::getDescription, keyword));
        wrapper.eq(Product::getStatus, 1);
        return page(pageParam, wrapper);
    }

    @Override
    public IPage<Product> getRecommendProducts(Long userId, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1).orderByDesc(Product::getCreateTime);
        return page(pageParam, wrapper);
    }
}
