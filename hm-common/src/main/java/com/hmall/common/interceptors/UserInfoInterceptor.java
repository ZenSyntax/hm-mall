package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //由于登录校验的逻辑已经在网关中实现了，故此处拦截器只是为每个微服务提供一个获取用户登录信息的功能，没有校验逻辑
        //1.获取登录用户信息
        String userInfo = request.getHeader("user-info");

        //2.判断是否获取了用户，如果有，则存入ThreadLocal
        if (!StrUtil.isBlank(userInfo)) {
            UserContext.setUser(Long.valueOf(userInfo));
            log.info("userId in interceptor: {}", UserContext.getUser());
        }
        //3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理用户
        UserContext.removeUser();
    }
}
