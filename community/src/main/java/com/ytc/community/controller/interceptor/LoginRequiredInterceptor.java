package com.ytc.community.controller.interceptor;

import com.ytc.community.annotation.LoginRequired;
import com.ytc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断 handler 是不是方法类型
        if (handler instanceof HandlerMethod){
            // 强制转换成 HandlerMethed 方法。
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 把所有的方法都取出来
            Method method = handlerMethod.getMethod();
            // 判断是不是又 LoginRequired 注解的方法。
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 如果有  而且 用户没有登录，那么就重定向到login页面。
            if (loginRequired != null && hostHolder.getUser() == null){
                // request.getContextPath() 可以取出来域名  localhost:8080
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

        }
        return true;
    }
}
