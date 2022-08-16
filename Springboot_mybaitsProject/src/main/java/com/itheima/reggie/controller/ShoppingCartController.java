package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    //15112345678
    @Autowired
    private ShoppingCartService shoppingCartService;

    @DeleteMapping("/clean")
    public R<String> clean(ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        Long UserId = BaseContext.getCurrentId();
        lqw.eq(ShoppingCart::getUserId,UserId);
        shoppingCartService.remove(lqw);
        return R.success("购物车清空成功");
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        Long UserId = BaseContext.getCurrentId();
        lqw.eq(ShoppingCart::getUserId,UserId);
        lqw.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }
    @PostMapping("/sub")
    public R sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lqw);
        //判断
        if (shoppingCartServiceOne != null && shoppingCartServiceOne.getNumber() > 1){
            /*if (shoppingCartServiceOne.getDishFlavor() == (shoppingCart.getDishFlavor())){

            }*/
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }
        else if (shoppingCartServiceOne.getNumber() == 1) {
            shoppingCartService.remove(lqw);
        }
        else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne=shoppingCart;
        }
        return R.success(shoppingCartServiceOne);
    }
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //设置用户ID，指定当前是哪个用户的购物车
        Long UserId = BaseContext.getCurrentId();
        shoppingCart.setUserId(UserId);

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper();
        Long dishId = shoppingCart.getDishId();

        //判断添加到购物车的是菜品还是套餐
        if (dishId != null){
            //菜品
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else {
            //套餐
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lqw);
        //判断
        if (shoppingCartServiceOne != null){
            /*if (shoppingCartServiceOne.getDishFlavor() == (shoppingCart.getDishFlavor())){

            }*/
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne=shoppingCart;
        }
        return R.success(shoppingCartServiceOne);
    }
}
