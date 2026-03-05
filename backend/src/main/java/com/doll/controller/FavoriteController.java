package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Favorite;
import com.doll.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public Result<Favorite> toggleFavorite(@RequestBody Favorite favorite) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, favorite.getUserId())
                .eq(Favorite::getProductId, favorite.getProductId());

        Favorite existing = favoriteService.getOne(wrapper);
        if (existing != null) {
            favoriteService.removeById(existing.getId());
            return Result.success(null); // Unfavorited
        } else {
            favoriteService.save(favorite);
            return Result.success(favorite); // Favorited
        }
    }

    @GetMapping("/user/{userId}")
    public Result<List<Favorite>> getByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        return Result.success(favoriteService.list(wrapper));
    }

    @GetMapping("/check")
    public Result<Boolean> isFavorited(@RequestParam Long userId, @RequestParam Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        return Result.success(favoriteService.getOne(wrapper) != null);
    }
}
