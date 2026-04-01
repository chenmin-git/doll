package com.doll.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.entity.Review;
import com.doll.entity.User;
import com.doll.mapper.ReviewMapper;
import com.doll.service.ReviewService;
import com.doll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private UserService userService;

    @Override
    public List<Review> getByProductId(Long productId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId);
        List<Review> list = this.list(wrapper);
        populateUserInfo(list);
        return list;
    }

    @Override
    public List<Review> getBySellerId(Long sellerId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getSellerId, sellerId);
        List<Review> list = this.list(wrapper);
        populateUserInfo(list);
        return list;
    }

    @Override
    public List<Review> getByBuyerId(Long buyerId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getBuyerId, buyerId);
        return this.list(wrapper);
    }

    private void populateUserInfo(List<Review> reviews) {
        for (Review review : reviews) {
            User user = userService.getById(review.getBuyerId());
            if (user != null) {
                review.setBuyerNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
                review.setBuyerAvatar(user.getAvatar());
            }
        }
    }
}
