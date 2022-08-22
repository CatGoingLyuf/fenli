package com.lyuf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: 添加拦截器，拦截资源
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterfaceConfig())
//                .addPathPatterns("/fileAll.html")
                .addPathPatterns("/*")
                .excludePathPatterns("/login.html")
                .excludePathPatterns("/adminlogin.html");
    }
}
