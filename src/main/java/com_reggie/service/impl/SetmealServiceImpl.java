package com_reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.common.CustomException;
import com_reggie.dto.SetmealDto;
import com_reggie.pojo.Setmeal;
import com_reggie.pojo.SetmealDish;
import com_reggie.service.SetmealDishService;
import com_reggie.service.SetmealService;
import com_reggie.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author 陈臣
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作-->setmeal,执行insert操作
        this.save(setmealDto);

        //保存套餐和菜品的关联信息，操作-->setmeal_dish，执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时也需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //1、查询套餐状态，确实是否可以删除（售卖中不可以删除）
        LambdaQueryWrapper<Setmeal> SetmealQwueryWrapper = new LambdaQueryWrapper<>();
        //将需要删除的id传进去
        SetmealQwueryWrapper.in(Setmeal::getId,ids);
        //将需要查询的状态值传进去，1：代表售卖中
        SetmealQwueryWrapper.eq(Setmeal::getStatus,1);

        long count = this.count(SetmealQwueryWrapper);
        //如果是大于1，说明查出来数据了，有售卖中的数据，不能删除，抛出一个业务异常
        if (count > 0) {
            //2、如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //3、如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //4、删除关系表中的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealQueryWrapper);

    }


}




