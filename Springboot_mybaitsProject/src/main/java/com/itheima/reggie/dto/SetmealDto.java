package com.itheima.reggie.dto;

import com.itheima.reggie.dao.SetmealDishDao;
import com.itheima.reggie.domain.Setmeal;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDishDao> setmealDishes;

    private String categoryName;
}
