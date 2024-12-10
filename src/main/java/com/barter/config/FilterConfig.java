package com.barter.config;

import com.barter.security.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final JwtFilter jwtFilter;

    public FilterConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtFilter);
        registrationBean.addUrlPatterns("/auth/*"); // auth 경로에 필터 적용
        registrationBean.setOrder(1); // 필터 실행 순서

        // 회원가입과 로그인 경로는 필터에서 제외
        registrationBean.addInitParameter("excludeUrlPatterns", "/auth/signup,/auth/login");
        return registrationBean;
    }
}
