package com.hmall.common.config;

import com.hmall.common.interceptors.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//由于网关微服务的底层并不是基于springmvc实现的，而网关微服务又依赖hm-common，所以网关微服务启动时会报错
//使用此注解配置当前配置类的生效环境为：存在DispatcherServlet类时生效。
//因springmvc底层为DispatcherServlet，如不存在该类，则代表当前服务没有使用springmvc，使得该类不生效
@ConditionalOnClass(DispatcherServlet.class)
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1.添加拦截器
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
