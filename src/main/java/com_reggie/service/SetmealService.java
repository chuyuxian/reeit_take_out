package com_reggie.service;

import com_reggie.dto.SetmealDto;
import com_reggie.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 陈臣
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-11-24 16:20:34
*/
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐，同时也需要删除套餐和菜品的关联数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);
}
