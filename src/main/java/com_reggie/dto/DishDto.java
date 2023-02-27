package com_reggie.dto;

import com_reggie.pojo.Dish;
import com_reggie.pojo.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    /**
     * 菜品对应的口味数据
     * 这个类相当于是继承了Dish中的属性，然后在其中添加和Dish不对应的属性进行封装
     *  大部分数据都是Dish中的，只有一小部分是不对应，那么就可以使用这个类，也可以自己写一个
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
