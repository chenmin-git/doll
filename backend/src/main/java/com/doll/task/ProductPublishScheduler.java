package com.doll.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.entity.Product;
import com.doll.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductPublishScheduler {

    @Autowired
    private ProductService productService;

    @Scheduled(fixedDelay = 30000)
    public void publishDueProducts() {
        LocalDateTime now = LocalDateTime.now();

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 2)
                .isNotNull(Product::getPublishTime)
                .le(Product::getPublishTime, now);

        List<Product> dueProducts = productService.list(wrapper);
        if (dueProducts.isEmpty()) {
            return;
        }

        for (Product product : dueProducts) {
            product.setStatus(1);
        }
        productService.updateBatchById(dueProducts);
    }
}
