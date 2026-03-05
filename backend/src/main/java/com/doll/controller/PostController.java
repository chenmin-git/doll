package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Post;
import com.doll.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public Result<Post> create(@RequestBody Post post) {
        postService.save(post);
        return Result.success(post);
    }

    @GetMapping("/list")
    public Result<List<Post>> list() {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1).orderByDesc(Post::getCreateTime);
        return Result.success(postService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<Post> getById(@PathVariable Long id) {
        return Result.success(postService.getById(id));
    }
}
