package com_reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com_reggie.common.R;
import com_reggie.pojo.Employee;
import com_reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 * 员工
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    protected EmployeeService service;
    /**
     * @RestController: @Controller和 @ResponseBody结合体，ResponseBody作用：返回给前端json字符串
     *      因为我们需要给前端返回一些东西才可以确定是否成功或者失败，只写@Controller注解不能返回给前端JSON
     *          前端也无法获取到，要么是使用结合体，要么是在每个方法上写上 @ResponseBody注解
     *
     */


    /**
     * 员工登录功能
     * @param request
     * @param employee 获取请求体，前端传输过来的是{username=admin,password=123456}，
     *                 跟类中属性一致才可以接收，不一致接收不到数据
     * @return
     */
    @PostMapping("/login")
    private R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行m5加密处理
        String password = employee.getPassword();
        //使用工具类进行加密操作
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = service.getOne(queryWrapper);

        //3、如果没有查询到则返回登录页面，并显示登录失败
        if (emp == null) {
            return R.error("登录失败");
        }

        //4、密码对比，如果不一致返回登录页面，并显示登录失败
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果,1：表示可用，0：表示禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已封禁");
        }

        //6、登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        //R.success(emp)：把成功的结果返回给前端了，
        // emp包含了姓名之类的，用户名就可以通过这个返回的姓名进行动态加载
        return R.success(emp);
    }

    /**
     * 退出功能
     * @return
     */
    @PostMapping("/logout")
    private R<String> logout(HttpServletRequest request) {
        //1、清理session中的用户id
        request.getSession().removeAttribute("employee");

        //2、返回结果
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *  1、@RequestBody：因为前端传过来的是JSON格式的
     *  2、没写路径表示这个是默认，只要是/employee，post请求都可以访问这个，但是一般只有一个
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
//        log.info("新增员工，员工信息：{}",employee.toString());

        //1、设置密码初始值,需要给md5的加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //2、设置新增时间和更新时间,注释的方法全部使用了字段自动填充技术
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //3、获取当前登录用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //设置创建人的id
        //employee.setCreateUser(empId);
        //设置更新人的id
        //employee.setUpdateUser(empId);

        //4、调用新增方法
        service.save(employee);

        return R.success("新增员工成功！！");
    }

    /**
     * 员工信息分页查询
     * @param page 页数
     * @param pageSize 当前页面显示多少条数据
     * @param name 查询的名字
     * @return
     */
    @GetMapping("/page")
    private R<Page> page(Integer page,Integer pageSize,String name) {
//        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        //StringUtils.isNotEmpty(name)：表示name值不为空才添加进去，如果为空则不会执行改语句
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件，根据更新时间来决定谁是前排
        queryWrapper.orderByAsc(Employee::getUpdateTime);
        //执行查询
        service.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 通用的修改方法
     *      1、可以修改员工信息
     *      2、可以修改禁用和启用状态
     *      3、不需要写路径，因为这个前端也没有路径，后端页没有，会根据类的路径来找对应的请求方式，这个是put请求
     *  报错：在修改的时候其他数据都没问题，id出了问题，跟数据中的不一致
     *  原因：因为id是雪花算法，而前端js只能处理前十六位数字，js对long类型的数字丢失了精度，进行了四舍五入，
     *      导致提交的id跟数据库中的id不一致
     *  解决方法：在服务端给页面响应json数据时进行处理，将long类型数据统一转为String字符
     *
     *  使用消息转换器之前,id=3
     *  {"code":1,"msg":null,"data":{"records":[{"id":3,"name":"李四"
     *  使用消息转换器之后，id变为了字符串类型.id="3"
     *  {"code":1,"msg":null,"data":{"records":[{"id":"3","name":"李四"
     * @param employee
     * @return
     */
    @PutMapping
    private R<String> update(@RequestBody Employee employee) {
        //打印获取数据
        log.info(employee.toString());

        //设置更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        //设置更新人
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        //调用方法
        service.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 回显
     * 根据id查询员工信息
     *     前端路径：http://localhost:8080/employee/3
     *     没有写路径，直接写id的值即可
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    private R<Employee> getById(@PathVariable Long id) {
//        log.info("根据id查询员工信息....");
        Employee employee = service.getById(id);
        if (employee!=null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
