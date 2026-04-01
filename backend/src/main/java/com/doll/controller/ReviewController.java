package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Review;
import com.doll.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Result<Review> create(@RequestBody Review review) {
        if (review.getOrderId() == null || review.getBuyerId() == null || review.getProductId() == null || review.getSellerId() == null) {
            return Result.error("评价参数不完整");
        }
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, review.getOrderId())
                .eq(Review::getBuyerId, review.getBuyerId());
        if (reviewService.count(wrapper) > 0) {
            return Result.error("该订单已评价，不能重复提交");
        }
        reviewService.save(review);
        return Result.success(review);
    }

    @GetMapping("/product/{productId}")
    public Result<List<Review>> getByProduct(@PathVariable Long productId) {
        return Result.success(reviewService.getByProductId(productId));
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<Review>> getBySeller(@PathVariable Long sellerId) {
        return Result.success(reviewService.getBySellerId(sellerId));
    }

    @GetMapping("/buyer/{buyerId}")
    public Result<List<Review>> getByBuyer(@PathVariable Long buyerId) {
        return Result.success(reviewService.getByBuyerId(buyerId));
    }
}
