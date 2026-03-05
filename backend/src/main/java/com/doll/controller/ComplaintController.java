package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Complaint;
import com.doll.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaint")
@CrossOrigin
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public Result<Complaint> submit(@RequestBody Complaint complaint) {
        complaintService.save(complaint);
        return Result.success(complaint);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Complaint>> getByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getSubmitterId, userId);
        return Result.success(complaintService.list(wrapper));
    }

    @PutMapping("/{id}")
    public Result<Complaint> handle(@PathVariable Long id, @RequestBody Complaint complaint) {
        complaint.setId(id);
        complaintService.updateById(complaint);
        return Result.success(complaint);
    }

    @GetMapping("/list")
    public Result<List<Complaint>> list() {
        return Result.success(complaintService.list());
    }
}
