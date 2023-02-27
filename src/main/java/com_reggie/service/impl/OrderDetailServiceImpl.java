package com_reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.pojo.OrderDetail;
import com_reggie.service.OrderDetailService;
import com_reggie.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




