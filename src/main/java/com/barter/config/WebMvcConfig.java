package com.barter.config;

import com.barter.security.JwtInterceptor;
import com.barter.resolver.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final UserArgumentResolver userArgumentResolver; // ArgumentResolver 주입

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 모든 경로에 대해 Interceptor 적용
                .excludePathPatterns("/auth/signup", "/auth/login"); // 회원가입 및 로그인 경로는 제외
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver); // Custom ArgumentResolver 등록
    }
}
