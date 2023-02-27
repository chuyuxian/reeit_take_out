package com_reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.pojo.ShoppingCart;
import com_reggie.service.ShoppingCartService;
import com_reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




