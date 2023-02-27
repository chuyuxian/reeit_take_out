package com_reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMeta0bjectHandler implements MetaObjectHandler {
    @Autowired
    HttpServletRequest request;
    /**
     * 插入（新增）操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入公共字段...");
        /**
         * 为什么要修改时间和修改人？
         *  因为在数据库中设置不是null键，也就是不能为空，那么创建的时间也就相当于修改时间了，修改人也是这样的，
         *  为空报错，只能填进去
         */
        //创建时间
        metaObject.setValue("createTime", LocalDateTime.now());
        //修改时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        //创建人
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        //修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    /**
     * 更新（修改）操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新公共字段...");
        //修改时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        //修改人
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
