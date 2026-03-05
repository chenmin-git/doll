package com.doll.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doll.entity.AfterSale;
import com.doll.mapper.AfterSaleMapper;
import com.doll.service.AfterSaleService;
import org.springframework.stereotype.Service;

@Service
public class AfterSaleServiceImpl extends ServiceImpl<AfterSaleMapper, AfterSale> implements AfterSaleService {
}
