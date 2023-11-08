package com.deer.base.config;

import com.deer.base.session.UserInfoIntercepter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public UserInfoIntercepter userInfoIntercepter() {
        return new UserInfoIntercepter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoIntercepter());
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
