package com.doll.controller;

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
}
