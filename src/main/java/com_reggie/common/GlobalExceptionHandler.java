package com_reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理
     *  SQLIntegrityConstraintViolationException：表示只处理这个异常
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
//        log.info(ex.getMessage());

        /**
         * 这个判断是异常：Duplicate entry 'lisi' for key 'idx_username'
         *  如果新增是重复需要给他们一个提示，写法看不懂，但是回来复制，或者自己网上搜一下就行了
         *
         *  ex.getMessage().contains("Duplicate entry")：表示的只处理异常中的带有 Duplicate entry的
         *      Duplicate entry：大概意思是重复
         */
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 业务异常处理
     *  关联：CategoryServiceImpl类中的remove方法；common包中的CustomException类
     *  作用：用于返回给前端提示信息
     *  CustomException：是一个自定义的异常，在common包中自定写的一个异常信息
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        return R.error(ex.getMessage());
    }
}
