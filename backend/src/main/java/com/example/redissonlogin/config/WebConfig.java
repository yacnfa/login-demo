package com.example.redissonlogin.config;

import com.example.redissonlogin.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 token 校验拦截器，放行登录接口和静态资源。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    public WebConfig(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**")          // 拦截所有业务接口
                .excludePathPatterns("/api/login")  // 登录接口放行
                .excludePathPatterns("/api/health"); // 健康检查放行
    }
}
