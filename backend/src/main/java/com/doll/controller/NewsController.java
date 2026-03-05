package com.doll.controller;

import com.doll.common.Result;
import com.doll.entity.News;
import com.doll.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;

    @PostMapping
    public Result<News> create(@RequestBody News news) {
        newsService.save(news);
        return Result.success(news);
    }

    @GetMapping("/list")
    public Result<List<News>> list() {
        return Result.success(newsService.list());
    }

    @PutMapping("/{id}")
    public Result<News> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        newsService.updateById(news);
        return Result.success(news);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<News> getById(@PathVariable Long id) {
        return Result.success(newsService.getById(id));
    }
}
