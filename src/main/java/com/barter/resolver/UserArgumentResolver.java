package com.barter.resolver;

import com.barter.annotation.CustomUser;
import com.barter.domain.member.entity.Member;
import com.barter.security.JwtUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    public UserArgumentResolver(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CustomUser 어노테이션이 있는 파라미터인지 확인
        return parameter.hasParameterAnnotation(CustomUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
        String authHeader = webRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null; // 토큰이 없거나 올바르지 않으면 null 반환
        }

        // "Bearer " 제거하고 순수 토큰 값만 추출
        String token = authHeader.substring(7);

        // JWT에서 사용자 정보 추출
        if (!jwtUtil.validateToken(token)) {
            return null; // 토큰이 유효하지 않으면 null 반환
        }

        String email = jwtUtil.extractEmail(token);
        String nickname = jwtUtil.extractNickname(token);

        // Member 객체 생성 후 반환
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }
}
