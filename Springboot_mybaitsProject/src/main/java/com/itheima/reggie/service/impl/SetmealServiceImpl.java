package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.SetmealDao;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
}
