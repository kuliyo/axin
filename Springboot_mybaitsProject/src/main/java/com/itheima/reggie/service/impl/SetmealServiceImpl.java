package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Exception.CustomEXception;
import com.itheima.reggie.dao.SetmealDao;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.domain.SetmealDish;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWhit(SetmealDto setmealDto) {

        this.save(setmealDto);
        //获得套餐id
        Long setmealId = setmealDto.getId();
        //获得套餐里菜品数据
        List<SetmealDish> category = setmealDto.getSetmealDishes();
        //将套餐id放进套餐里菜品数据中
        category = category.stream().map((item) ->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(category);
    }

    /**
     * 删除套餐和批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWhit(List<Long> ids) {

        if(ids.size() == 0){
            throw new CustomEXception("请选择要删除的套餐");
        }
        //查询套餐状态，确定是否可用
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        lqw.eq(Setmeal::getStatus,1);
        int count = this.count(lqw);
        if (count > 0){
            throw new CustomEXception("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐中的数据
        this.removeByIds(ids);

        //删除关系表中的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper);
    }

    /**
     * 根据id查询套餐内容
     * @param id
     * @return
     */
    @Override
    public SetmealDto get(Long id) {
        //获得套餐数据
        Setmeal setmeal = this.getById(id);

        //把setmeal的数据拷贝到setmealDto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //把菜品数据放进setmealDto的setmealDishes中
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }
}
