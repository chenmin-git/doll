package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Cart;
import com.doll.service.CartService;
import com.doll.service.ProductService;
import com.doll.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @PostMapping
    public Result<Cart> add(@RequestBody Cart cart) {
        if (cart.getUserId() == null || cart.getProductId() == null) {
            return Result.error("参数缺失");
        }
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            return Result.error("商品数量必须大于0");
        }

        Product product = productService.getById(cart.getProductId());
        if (product == null || product.getStatus() != 1) {
            return Result.error("商品已下架或不存在");
        }
        if (product.getStock() == null || product.getStock() <= 0) {
            return Result.error("库存不足");
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId()).eq(Cart::getProductId, cart.getProductId());
        List<Cart> existingList = cartService.list(wrapper);
        if (existingList != null && !existingList.isEmpty()) {
            Cart primary = existingList.get(0);
            int mergedQuantity = 0;
            for (Cart existing : existingList) {
                mergedQuantity += existing.getQuantity() == null ? 0 : existing.getQuantity();
            }
            int nextQuantity = mergedQuantity + cart.getQuantity();
            if (nextQuantity > product.getStock()) {
                return Result.error("加入失败，超过库存上限");
            }
            primary.setQuantity(nextQuantity);
            cartService.updateById(primary);

            // 历史脏数据去重：只保留一条购物车记录
            if (existingList.size() > 1) {
                for (int i = 1; i < existingList.size(); i++) {
                    cartService.removeById(existingList.get(i).getId());
                }
            }
            return Result.success(primary);
        }

        if (cart.getQuantity() > product.getStock()) {
            return Result.error("加入失败，超过库存上限");
        }
        cartService.save(cart);
        return Result.success(cart);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Cart>> getByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> list = cartService.list(wrapper);

        Map<Long, Cart> merged = new LinkedHashMap<>();
        List<Long> duplicateIds = new ArrayList<>();
        for (Cart cart : list) {
            if (cart.getProductId() == null) {
                continue;
            }
            Cart existed = merged.get(cart.getProductId());
            if (existed == null) {
                merged.put(cart.getProductId(), cart);
            } else {
                int quantity = (existed.getQuantity() == null ? 0 : existed.getQuantity())
                        + (cart.getQuantity() == null ? 0 : cart.getQuantity());
                existed.setQuantity(quantity);
                duplicateIds.add(cart.getId());
            }
        }

        if (!duplicateIds.isEmpty()) {
            for (Cart cart : merged.values()) {
                cartService.updateById(cart);
            }
            cartService.removeByIds(duplicateIds);
        }

        return Result.success(new ArrayList<>(merged.values()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Cart> update(@PathVariable Long id, @RequestBody Cart cart) {
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            return Result.error("商品数量必须大于0");
        }

        Cart existing = cartService.getById(id);
        if (existing == null) {
            return Result.error("购物车记录不存在");
        }
        Product product = productService.getById(existing.getProductId());
        if (product == null || product.getStatus() != 1) {
            return Result.error("商品已下架或不存在");
        }
        if (product.getStock() == null || cart.getQuantity() > product.getStock()) {
            return Result.error("数量超过库存上限");
        }

        existing.setQuantity(cart.getQuantity());
        cartService.updateById(existing);
        return Result.success(existing);
    }
}
