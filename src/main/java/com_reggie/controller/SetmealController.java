package com_reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_reggie.common.R;
import com_reggie.dto.SetmealDto;
import com_reggie.pojo.Category;
import com_reggie.pojo.Setmeal;
import com_reggie.service.CategoryService;
import com_reggie.service.SetmealDishService;
import com_reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 套餐新增功能
     * @param setmealDto
     * @return
     */
    @PostMapping
    private R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐管理...");
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐新增成功");
    }

    /**
     * 套餐管理分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    private R<Page> page(Integer page,Integer pageSize,String name) {
        //1、创建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page,pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加查询条件，根据更新时间降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //2、对象拷贝
        //为什么忽略records？这个records中泛型是page的，而我们是dtoPage的，泛型不一样，会报错的，需要自己设置这个里面的值
        //处理好的值在92行
        BeanUtils.copyProperties(page,dtoPage,"records");
        //获取records里面的值，进行处理
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = new ArrayList<>();
        for (Setmeal record : records) {
            //创建实体类对象
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝，将里面除了套餐分类的数据拷贝，套餐分类需要自己处理获取
            BeanUtils.copyProperties(record,setmealDto);
            //获取分类id，查询对应分类数据
            Long categoryId = record.getCategoryId();
            //根据分类的id，查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                //将获取到的套餐分类名称放入进去
                setmealDto.setCategoryName(categoryName);
            }
            //将处理好的数据放入集合中
            list.add(setmealDto);
        }
       /* 这个是老师的写法
       List<SetmealDto> list = records.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());*/
        //将处理好放入集合中数据，再次放入records其中就可以了，因为我们返回的是page类型的，集合不能返回
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除功能：涉及了多删除
     * @param ids
     * @return
     */
    @DeleteMapping
    private R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }

    /**
     * 修改状态功能
     * @param statusId
     * @param ids
     * @return
     */
    @PostMapping("/status/{id}")
    private R<String> status(@PathVariable("id") Long statusId,Long[] ids) {
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        //下面不多说了，直接看DishController中的status方法
        queryWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,statusId);
        setmealService.update(queryWrapper);

        return R.success("状态修改成功");
    }


    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    private R<List<Setmeal>> list(Setmeal setmeal) {
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //根据id来查询，先判断是否有值，这个是针对套餐的
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //根据状态来查询，停售的不查询
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        //根据更新时间降序来排
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }


}
