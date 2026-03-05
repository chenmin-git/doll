package com.doll.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doll.entity.Review;
import java.util.List;

public interface ReviewService extends IService<Review> {
    List<Review> getByProductId(Long productId);

    List<Review> getBySellerId(Long sellerId);
}
