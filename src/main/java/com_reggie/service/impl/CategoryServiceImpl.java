package com_reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.common.CustomException;
import com_reggie.common.R;
import com_reggie.pojo.Category;
import com_reggie.pojo.Dish;
import com_reggie.pojo.Setmeal;
import com_reggie.service.CategoryService;
import com_reggie.mapper.CategoryMapper;
import com_reggie.service.DishService;
import com_reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断是否关联了分类和套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //1、查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper();
        //添加查询条件，根据分类id进行查询
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        long count1 = dishService.count(dishQueryWrapper);
        if (count1 > 0) {
            //已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //2、查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> SetmealQueryWrapper = new LambdaQueryWrapper();
        //添加查询条件，根据分类id进行查询
        SetmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(SetmealQueryWrapper);
        if (count2 > 0 ) {
            //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //3、正常删除分类
        super.removeById(id);
    }


}




