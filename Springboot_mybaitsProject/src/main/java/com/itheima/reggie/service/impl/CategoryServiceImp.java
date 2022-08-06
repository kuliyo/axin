package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Exception.CustomEXception;
import com.itheima.reggie.dao.CategoryDao;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImp extends ServiceImpl<CategoryDao,Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除id，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {

        //查询当前分类是否关联菜品，如果已经关联，就抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1 > 0){
            //已经关联菜品，抛出一个业务异常
            throw new CustomEXception("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联套餐，如果已经关联，就抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            //已经关联套餐，抛出一个业务异常
            throw new CustomEXception("当前分类下关联了套餐，不能删除");
        }

        //正常删除分类
        super.removeById(ids);
    }
}
