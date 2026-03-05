package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Cart;
import com.doll.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public Result<Cart> add(@RequestBody Cart cart) {
        cartService.save(cart);
        return Result.success(cart);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Cart>> getByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return Result.success(cartService.list(wrapper));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Cart> update(@PathVariable Long id, @RequestBody Cart cart) {
        cart.setId(id);
        cartService.updateById(cart);
        return Result.success(cart);
    }
}
