package com.doll.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.entity.News;
import com.doll.mapper.NewsMapper;
import org.springframework.stereotype.Service;

@Service
public class NewsService extends ServiceImpl<NewsMapper, News> {
}
