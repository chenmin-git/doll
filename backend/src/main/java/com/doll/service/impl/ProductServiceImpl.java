package com.doll.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.entity.Product;
import com.doll.mapper.OrderItemMapper;
import com.doll.mapper.ProductMapper;
import com.doll.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final ConcurrentHashMap<Long, Integer> PRODUCT_CLICK_COUNTER = new ConcurrentHashMap<>();

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public IPage<Product> searchProducts(String keyword, Integer page, Integer size) {
        Integer safePage = page == null || page < 1 ? 1 : page;
        Integer safeSize = size == null || size < 1 ? 10 : size;
        String safeKeyword = keyword == null ? "" : keyword.trim();

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (!safeKeyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, safeKeyword).or().like(Product::getDescription, safeKeyword));
        }

        List<Product> all = list(wrapper);
        enrichHotMetrics(all);
        all.sort((a, b) -> {
            int scoreCompare = Integer.compare(getSafeInt(b.getHotScore()), getSafeInt(a.getHotScore()));
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            LocalDateTime bTime = b.getCreateTime() == null ? LocalDateTime.MIN : b.getCreateTime();
            LocalDateTime aTime = a.getCreateTime() == null ? LocalDateTime.MIN : a.getCreateTime();
            return bTime.compareTo(aTime);
        });

        long total = all.size();
        int fromIndex = (safePage - 1) * safeSize;
        List<Product> records = Collections.emptyList();
        if (fromIndex < all.size()) {
            int toIndex = Math.min(fromIndex + safeSize, all.size());
            records = all.subList(fromIndex, toIndex);
        }

        Page<Product> pageResult = new Page<>(safePage, safeSize, total);
        pageResult.setRecords(records);
        return pageResult;
    }

    @Override
    public IPage<Product> getRecommendProducts(Long userId, Integer page, Integer size) {
        Integer safePage = page == null || page < 1 ? 1 : page;
        Integer safeSize = size == null || size < 1 ? 10 : size;

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        List<Product> all = list(wrapper);
        enrichHotMetrics(all);
        all.sort((a, b) -> {
            int scoreCompare = Integer.compare(getSafeInt(b.getHotScore()), getSafeInt(a.getHotScore()));
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            LocalDateTime bTime = b.getCreateTime() == null ? LocalDateTime.MIN : b.getCreateTime();
            LocalDateTime aTime = a.getCreateTime() == null ? LocalDateTime.MIN : a.getCreateTime();
            return bTime.compareTo(aTime);
        });

        long total = all.size();
        int fromIndex = (safePage - 1) * safeSize;
        List<Product> records = Collections.emptyList();
        if (fromIndex < all.size()) {
            int toIndex = Math.min(fromIndex + safeSize, all.size());
            records = all.subList(fromIndex, toIndex);
        }

        Page<Product> pageResult = new Page<>(safePage, safeSize, total);
        pageResult.setRecords(records);
        return pageResult;
    }

    @Override
    public void recordProductClick(Long productId) {
        if (productId == null) {
            return;
        }
        PRODUCT_CLICK_COUNTER.merge(productId, 1, Integer::sum);
    }

    @Override
    public Product enrichHotMetrics(Product product) {
        if (product == null) {
            return null;
        }
        Map<Long, Integer> salesSummary = getSalesSummaryMap();
        fillHotMetrics(product, salesSummary);
        return product;
    }

    @Override
    public void enrichHotMetrics(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        Map<Long, Integer> salesSummary = getSalesSummaryMap();
        for (Product product : products) {
            fillHotMetrics(product, salesSummary);
        }
    }

    private Map<Long, Integer> getSalesSummaryMap() {
        List<Map<String, Object>> rows = orderItemMapper.selectProductSalesSummary();
        Map<Long, Integer> result = new HashMap<>();
        if (rows == null) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }
            Long productId = parseLong(row.get("productId"));
            Integer sales = parseInt(row.get("salesCount"));
            if (productId != null && sales != null) {
                result.put(productId, Math.max(sales, 0));
            }
        }
        return result;
    }

    private void fillHotMetrics(Product product, Map<Long, Integer> salesSummary) {
        if (product == null || product.getId() == null) {
            return;
        }
        int salesCount = getSafeInt(salesSummary.get(product.getId()));
        int clickCount = getSafeInt(PRODUCT_CLICK_COUNTER.get(product.getId()));
        product.setSalesCount(salesCount);
        product.setClickCount(clickCount);
        product.setHotScore(salesCount + clickCount);
    }

    private Integer parseInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ex) {
            return null;
        }
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ex) {
            return null;
        }
    }

    private int getSafeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
