package com.doll.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.entity.Auction;
import com.doll.mapper.AuctionMapper;
import org.springframework.stereotype.Service;

@Service
public class AuctionService extends ServiceImpl<AuctionMapper, Auction> {
}
