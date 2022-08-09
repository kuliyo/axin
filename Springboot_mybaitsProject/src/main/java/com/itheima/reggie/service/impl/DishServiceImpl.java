package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dao.DishDao;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService flavorService;
    /**
     * 新增菜品，保存菜品的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void SaveWithFlavor(DishDto dishDto) {

        this.save(dishDto);

        //获取菜品id
        Long Dishid = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //给口味数据添加菜品id
        flavors = flavors.stream().map((item) ->{
            item.setDishId(Dishid);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        flavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基础信息,从Dish表中查询
        Dish dish = this.getById(id);

        //把dish拷贝到dishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //条件：根据菜品id查对应的口味数据,从dish_flavor表中查询
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavor = flavorService.list(lqw);
        dishDto.setFlavors(flavor);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新Dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        flavorService.remove(lqw);

        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        //给口味数据添加菜品id
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        flavorService.saveBatch(flavors);
    }
}
