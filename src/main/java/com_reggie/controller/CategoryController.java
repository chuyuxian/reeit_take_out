package com_reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_reggie.common.R;
import com_reggie.pojo.Category;
import com_reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService service;
    @Autowired
    HttpServletRequest request;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    private R<String> save(@RequestBody Category category) {
        service.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize) {
        //1、创建分页构造器
        Page pageInfo = new Page(page,pageSize);
        //2、创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //3、添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //4、进行分页查询
        service.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    private R<String> delete(Long ids) {
        //1、调用自定义方法，根据id删除
        service.remove(ids);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    private R<String> update(@RequestBody Category category) {
        service.updateById(category);
        return R.success("分类修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    private R<List<Category>> list(Category category) {
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = service.list(queryWrapper);
        return R.success(list);
    }


}
