package com_reggie.service;

import com_reggie.dto.DishDto;
import com_reggie.pojo.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 陈臣
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-11-24 16:20:34
*/

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish，dish_flavor
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 回显信息，根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    DishDto getByWithFlavor(Long id);

    /**
     * 修改信息，同时更新对应的口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 停售功能
     * @param id 状态id
     * @param ids 菜品id
     */
    void updateByDiscontinued(Long id,Long[] ids);
}
