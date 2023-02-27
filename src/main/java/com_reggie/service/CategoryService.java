package com_reggie.service;

import com_reggie.pojo.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 陈臣
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-11-24 16:20:34
*/
public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除分类，看实现方法
     * @param id
     */
     void remove(Long id);

}
