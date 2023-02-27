package com_reggie.filter;

import com.alibaba.fastjson.JSON;
import com_reggie.common.BaseContext;
import com_reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        log.info("拦截到请求：{}",request.getRequestURI());

        //1、获取本次请求的URL
        String requestURI = request.getRequestURI();

        //定义不需要处理的请求路径,
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端发送短信，如果不加上，登录发送请求后端接收不到
                "/user/login" //移动端登录
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理直接放行
        if (check) {
//            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、判断后端系统员工用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {

            Long empId =(Long) request.getSession().getAttribute("employee");
            log.info("用户已登录，用户id为{}",empId);
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //4-2、判断前端用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null) {

            Long empId =(Long) request.getSession().getAttribute("user");
            log.info("用户已登录，用户id为{}",empId);
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回登录结果,通过输出流方式向客户端页面响应数据（这个是有前端拦截器时写的）
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查当前请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            //match:匹配的意思
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
