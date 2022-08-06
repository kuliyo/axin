package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController{
    @Autowired
    private CategoryService categoryService;

    /**
     *新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> Save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        //添加排序条件，更加Sort进行升序排序
        lqw.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> Delete(Long ids){

//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("分类删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
}