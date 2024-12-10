package com.barter.resolver;

import com.barter.annotation.CustomUser;
import com.barter.domain.member.entity.Member;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

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
        // 헤더에서 사용자 ID 가져오기
        String userId = webRequest.getHeader("User-Id");
        if (userId == null) {
            return null; // 예외를 던지거나 null 처리
        }

        // 필요한 경우 Member 객체를 데이터베이스에서 가져오기
        return Member.builder()
                .id(Long.valueOf(userId)) // ID를 설정
                .nickname("ExampleUser") // 예시 닉네임
                .email("example@example.com") // 예시 이메일
                .build();
    }
}
