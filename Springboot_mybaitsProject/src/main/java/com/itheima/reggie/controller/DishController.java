package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.DishFlavor;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> Save(@RequestBody DishDto dishDto){
        dishService.SaveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> Update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageDishDto = new Page<>();

        //条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);

        //进行分页查询
        dishService.page(pageInfo,lqw);

        //对象拷贝,忽略records
        BeanUtils.copyProperties(pageInfo,pageDishDto,"records");

        //获得records
        List<Dish> records = pageInfo.getRecords();

        //对records进行处理
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                //获得category里分类名称
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //把处理过的records给pageDishDto
        pageDishDto.setRecords(list);

        return R.success(pageDishDto);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
//        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //查询状态等于1的菜品
//        lqw.eq(Dish::getStatus,1);
//        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(lqw);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态等于1的菜品
        lqw.eq(Dish::getStatus,1);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lqw);
        List<DishDto> dishDtoList = list.stream().map((item) ->{

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                //获得category里分类名称
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //获得菜品id
            Long dishId = item.getId();

            //获得口味数据
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}
