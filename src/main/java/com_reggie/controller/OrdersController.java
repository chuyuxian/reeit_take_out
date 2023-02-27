package com_reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_reggie.common.R;
import com_reggie.dto.OrdersDto;
import com_reggie.pojo.OrderDetail;
import com_reggie.pojo.Orders;
import com_reggie.service.OrderDetailService;
import com_reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     *  订单明细分页查询
     *      表没弄明白，页面展示的名字是菜品名字
     * @param page
     * @param pageSize
     * @param number 订单号
     * @param beginTime 开始日期
     * @param endTime 结束日期
     * @return
     */
    @GetMapping("/page")
    private R<Page> page(Integer page, Integer pageSize,Long number,LocalDateTime beginTime,LocalDateTime endTime) {
        //1、创建分页构造器
        Page<Orders> pageInfo = new Page(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //2、创建条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //查询订单号
        queryWrapper.eq(number!=null,Orders::getNumber,number);
        //查询开始日期和结束日期
        queryWrapper.eq(beginTime!=null,Orders::getOrderTime,beginTime);
        queryWrapper.eq(endTime!=null,Orders::getCheckoutTime,endTime);
        //添加排序条件
        queryWrapper.orderByAsc(Orders::getOrderTime);
        //执行分页查询
        ordersService.page(pageInfo,queryWrapper);

        //4、对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        //获取pageinfo中records的数据
        List<Orders> records = pageInfo.getRecords();
        //把records的数据循环拿出来
        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //拷贝对象数据给ordersDto中
            BeanUtils.copyProperties(item,ordersDto);
            //获取用户id
            Long userId = item.getUserId();
            //根据用户id进行查询
            OrderDetail orderDetail = orderDetailService.getById(userId);
            String name = orderDetail.getName();
            //将查询获取到的用户名放入
            ordersDto.setUserName(name);
            //返回对象
            return ordersDto;

        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    private R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);

        return R.success("下单成功");
    }

}
