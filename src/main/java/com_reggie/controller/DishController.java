package com_reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_reggie.common.R;
import com_reggie.dto.DishDto;
import com_reggie.pojo.Category;
import com_reggie.pojo.Dish;
import com_reggie.pojo.DishFlavor;
import com_reggie.service.CategoryService;
import com_reggie.service.DishFlavorService;
import com_reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    private R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    private R<Page> page(Integer page, Integer pageSize, String name) {
        //1、创建分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> DishDtoPageInfo = new Page<>(page, pageSize);
        //2、创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //3、添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //4、添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //5、执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //6、进行对象拷贝,BeanUtils是一个工具类
        //第一个属性:拷贝的对象
        //第二个属性:将拷贝对象的数据给的对象
        //第三个属性：忽略的属性，不拷这个属性
        BeanUtils.copyProperties(pageInfo,DishDtoPageInfo,"records");

        List<Dish> records = pageInfo.getRecords();
        ArrayList<DishDto> dishDtos = new ArrayList<>();
        //循环records，创建DishDto对象，并添加到该列表中
        for(Dish dish : records){
            //1.创建DishDto对象
            DishDto dishDto = new DishDto();
            //2.拷贝对象,将dish数据拷贝给dishDto
            BeanUtils.copyProperties(dish,dishDto);
            //3.获取菜品分类id
            Long categoryId = dish.getCategoryId();
            //4.根据菜品分类id获取分类对象，获得分类名称
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            if(categoryName!=null){
                dishDto.setCategoryName(categoryName);
            }
            //将dishDto数据传给dishDtos集合中
            dishDtos.add(dishDto);
        }
        //再将dishDtos中的数据传DishDtoPageInfo
        DishDtoPageInfo.setRecords(dishDtos);


        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        DishDtoPageInfo.setRecords(list);

        return R.success(DishDtoPageInfo);
    }

    /**
     * 回显修改菜品信息和对应的口味信息
     *      前端的路径信息为：http://localhost:8080/dish/1413385247889891338
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品和口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    private R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }


    /**
     * 停售功能
     *  问题：数据能传过来，但是因为是数组的原因，导致失败了
     * @param statusId 状态的id
     * @param ids 停售的id
     * @return
     */
    @PostMapping("/status/{id}")
    private R<String> status(@PathVariable("id") Long statusId,Long[] ids) {
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        //将修改状态的id放入in中
        queryWrapper.in(Dish::getId,ids);
        //将修改状态的值放入
        queryWrapper.set(Dish::getStatus,statusId);
        //调用修改方法
        dishService.update(queryWrapper);
        //返回提示
        return R.success("状态修改成功");
    }

    /**
     * 根据添加查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    private R<List<Dish>> list(Dish dish) {
//        //创建条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!= null,Dish::getCategoryId,dish.getCategoryId());
//        //只查询状态为1（起售状态）
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件，如果一致，那么根据修改时间排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
    @GetMapping("/list")
    private R<List<DishDto>> list(Dish dish) {
        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!= null,Dish::getCategoryId,dish.getCategoryId());
        //只查询状态为1（起售状态）
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件，如果一致，那么根据修改时间排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //要发送的sql：select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorsList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorsList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
