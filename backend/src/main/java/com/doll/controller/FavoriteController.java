package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Favorite;
import com.doll.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @PostMapping
    public Result<Void> toggle(@RequestBody Favorite favorite) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, favorite.getUserId())
               .eq(Favorite::getProductId, favorite.getProductId());
        Favorite existing = favoriteMapper.selectOne(wrapper);
        
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
        } else {
            favoriteMapper.insert(favorite);
        }
        return Result.success();
    }

    @GetMapping("/user/{userId}")
    public Result<List<Favorite>> getByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        return Result.success(favoriteMapper.selectList(wrapper));
    }
}
