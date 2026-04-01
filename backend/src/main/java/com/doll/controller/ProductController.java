package com.doll.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Product;
import com.doll.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public Result<IPage<Product>> search(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.searchProducts(keyword, page, size));
    }

    @GetMapping("/recommend")
    public Result<IPage<Product>> recommend(@RequestParam Long userId,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.getRecommendProducts(userId, page, size));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        if (product.getStock() == null || product.getStock() < 0) {
            return Result.error("库存不能小于0");
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        productService.save(product);
        return Result.success(product);
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        if (product.getStock() != null && product.getStock() < 0) {
            return Result.error("库存不能小于0");
        }
        product.setId(id);
        productService.updateById(product);
        return Result.success(product);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        productService.enrichHotMetrics(product);
        return Result.success(product);
    }

    @PostMapping("/{id}/click")
    public Result<Void> recordClick(@PathVariable Long id) {
        productService.recordProductClick(id);
        return Result.success();
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<Product>> getBySeller(@PathVariable Long sellerId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, sellerId).orderByDesc(Product::getCreateTime);
        List<Product> products = productService.list(wrapper);
        productService.enrichHotMetrics(products);
        return Result.success(products);
    }
}
