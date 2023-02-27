package com_reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_reggie.pojo.AddressBook;
import com_reggie.service.AddressBookService;
import com_reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈臣
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-11-24 16:20:34
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




