package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.domain.Dish;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.domain.SetmealDish;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.callback.LanguageCallback;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> get(int page, int pageSize, String name){
        //分页构造器
        Page pageInfo = new Page(page,pageSize);
        Page<SetmealDto> pageSetmealDto = new Page<>();
        //条件查询
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(name != null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,lqw);

        //把传来的数据拷贝到pageSetmealDto，忽略records里的数据
        BeanUtils.copyProperties(pageInfo,pageSetmealDto,"records");
        //把records数据拿出来
        List<Setmeal> records = pageInfo.getRecords();
        //对records进行处理
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto SetmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, SetmealDto);
            Long categoryId = item.getCategoryId();//获得分类id
            Category category = categoryService.getById(categoryId);//根据分类id获得分类数据
            if (category != null){
                String categoryName = category.getName();//获得分类名称
                SetmealDto.setCategoryName(categoryName);//把分类名称给SetmealDto
            }
            return SetmealDto;//把处理过的数据传出去
        }).collect(Collectors.toList());
        pageSetmealDto.setRecords(list);//把处理后的records数据给pageSetmealDto
        return R.success(pageSetmealDto);//返回处理过的pageSetmealDto
    }

    public R<String> Update(Long id){

        return null;
    }

    /**
     * 新增套装
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> Save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWhit(setmealDto);
        return R.success("新增套餐成功");
    }
    /**
     * 删除套餐和批量删除
     * @param ids
     */
    @DeleteMapping
    public R<String> delete( @RequestParam List<Long> ids){
        setmealService.deleteWhit(ids);
        return R.success("删除套餐成功");
    }
    /**
     * 修改套餐状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("修改菜品状态{}->{}", status, ids);
        // 修改setmeal表，id在ids中的status，为前端url传来的status。update  setmeal set status where id in ()
        //拼接sql
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", status).in("id", ids);
        // 更新状态。
        if (!setmealService.update(updateWrapper)) {
            return R.error("修改状态失败");
        }
        return R.success("修改状态成功");
    }
    @GetMapping("/list")
    public R<List<Setmeal>> List(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper();
        //条件
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //排序条件
        lqw.orderByAsc().orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(lqw);
        /*List<SetmealDto> Setmealdishlist = list.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long setmealId = item.getId();

            //获得菜品信息
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);

            return setmealDto;
        }).collect(Collectors.toList());*/
        return R.success(list);
    }

    /**
     * 根据id查询套餐内容
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Setmeal> get(@PathVariable Long id){
        Setmeal serviceById = setmealService.get(id);
        return R.success(serviceById);
    }
}
