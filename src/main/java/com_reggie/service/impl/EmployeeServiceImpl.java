package com_reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.pojo.Employee;
import com_reggie.service.EmployeeService;
import com_reggie.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
    @Autowired
    private EmployeeMapper mapper;
}




