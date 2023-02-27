package com_reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.pojo.DishFlavor;
import com_reggie.service.DishFlavorService;
import com_reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




