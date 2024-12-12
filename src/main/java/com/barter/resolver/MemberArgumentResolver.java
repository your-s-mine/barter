package com.barter.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.security.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private final JwtUtil jwtUtil;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(VerifiedMember.class);
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		String headerToken = request.getHeader(AUTHORIZATION_HEADER);
		String token = jwtUtil.substringToken(headerToken);
		Claims memberClaims = jwtUtil.getMemberClaims(token);
		Long memberId = Long.parseLong(memberClaims.getSubject());
		String email = (String)memberClaims.get("email");
		return VerifiedMember.builder()
			.id(memberId)
			.email(email)
			.build();
	}
}
