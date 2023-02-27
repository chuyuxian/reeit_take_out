package com_reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.dto.DishDto;
import com_reggie.mapper.DishMapper;
import com_reggie.pojo.Dish;
import com_reggie.pojo.DishFlavor;
import com_reggie.service.DishFlavorService;
import com_reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 陈臣
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //菜品Id，因为需要知道对应菜品是哪个，所以需要添加一下菜品id，因为继承了dish的实体类，所以可以直接get
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //新技术，将菜品id传入进去，懵
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        //保存口味数据，保存到DishFlavor（菜品口味表）
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Transactional
    @Override
    public DishDto getByWithFlavor(Long id) {
        //查询菜品基本信息，从dish表开始查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //拷贝对象，将dish中数据拷贝给dishDto
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //添加添加
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //调用方法
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //将查询到的数据传给dishDto中
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应的口味信息---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> dishDtoQueryWrapper = new LambdaQueryWrapper<>();
        dishDtoQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishDtoQueryWrapper);

        //添加当前提交过来的口味信息---dish_flavor表中insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 停售功能
     * @param id 状态id
     * @param ids 菜品id
     */
    @Override
    public void updateByDiscontinued(Long id, Long[] ids) {

    }
}




