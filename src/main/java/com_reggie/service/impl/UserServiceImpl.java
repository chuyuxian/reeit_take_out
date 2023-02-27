package com_reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.pojo.User;
import com_reggie.service.UserService;
import com_reggie.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

}




