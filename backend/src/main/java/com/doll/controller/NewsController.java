package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.News;
import com.doll.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsMapper newsMapper;

    @GetMapping("/list")
    public Result<List<News>> list() {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(News::getCreateTime);
        return Result.success(newsMapper.selectList(wrapper));
    }

    @PostMapping
    public Result<News> create(@RequestBody News news) {
        newsMapper.insert(news);
        return Result.success(news);
    }

    @GetMapping("/{id}")
    public Result<News> getById(@PathVariable Long id) {
        return Result.success(newsMapper.selectById(id));
    }

    @PutMapping("/{id}")
    public Result<News> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        newsMapper.updateById(news);
        return Result.success(news);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        newsMapper.deleteById(id);
        return Result.success(true);
    }
}
