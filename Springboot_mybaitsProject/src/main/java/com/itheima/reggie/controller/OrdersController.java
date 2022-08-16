package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("添加订单成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }
}
