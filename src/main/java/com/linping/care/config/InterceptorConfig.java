package com.linping.care.config;

import com.linping.care.interceptors.JWTInterceptors;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new JWTInterceptors())
                // 拦截所有请求
                .addPathPatterns("/**")
                // 放行登录接口
                .excludePathPatterns("/user/login", "/user/refreshToken", "/user/register")
                // 放行静态资源
                .excludePathPatterns("/images/**")
                // 放行api测试请求
                .excludePathPatterns("/swagger-ui.html/**", "/doc.html/**", "/webjars/**", "/v3/**");
    }
}
