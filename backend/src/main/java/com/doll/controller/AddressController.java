package com.doll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doll.common.Result;
import com.doll.entity.Address;
import com.doll.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressMapper addressMapper;

    @GetMapping("/user/{userId}")
    public Result<List<Address>> getByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).orderByDesc(Address::getIsDefault).orderByDesc(Address::getCreateTime);
        return Result.success(addressMapper.selectList(wrapper));
    }

    @GetMapping("/default/{userId}")
    public Result<Address> getDefault(@PathVariable Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).eq(Address::getIsDefault, 1);
        Address address = addressMapper.selectOne(wrapper);
        return Result.success(address);
    }

    @PostMapping
    public Result<Address> create(@RequestBody Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getUserId, address.getUserId()).eq(Address::getIsDefault, 1);
            List<Address> defaultAddresses = addressMapper.selectList(wrapper);
            for (Address addr : defaultAddresses) {
                addr.setIsDefault(0);
                addressMapper.updateById(addr);
            }
        }
        addressMapper.insert(address);
        return Result.success(address);
    }

    @PutMapping("/{id}")
    public Result<Address> update(@PathVariable Long id, @RequestBody Address address) {
        address.setId(id);
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getUserId, address.getUserId()).eq(Address::getIsDefault, 1).ne(Address::getId, id);
            List<Address> defaultAddresses = addressMapper.selectList(wrapper);
            for (Address addr : defaultAddresses) {
                addr.setIsDefault(0);
                addressMapper.updateById(addr);
            }
        }
        addressMapper.updateById(address);
        return Result.success(address);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressMapper.deleteById(id);
        return Result.success();
    }
}
