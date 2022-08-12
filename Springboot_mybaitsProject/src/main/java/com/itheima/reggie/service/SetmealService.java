package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.Setmeal;
import com.itheima.reggie.dto.SetmealDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWhit(SetmealDto setmealDto);
    public void deleteWhit(List<Long> ids);
    public SetmealDto get( Long id);
}
