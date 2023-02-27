package com_reggie.service;

import com_reggie.pojo.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 陈臣
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-11-24 16:20:34
*/
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

}
