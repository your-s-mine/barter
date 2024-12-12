package com.barter.interceptor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.barter.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private final JwtUtil jwtUtil;

	@Override
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler
	) throws RuntimeException {
		try {
			String headerToken = request.getHeader(AUTHORIZATION_HEADER);
			String accessToken = Optional.ofNullable(jwtUtil.substringToken(headerToken))
				.orElseThrow();
			this.jwtUtil.validateToken(accessToken);
			return true;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "토큰이 없습니다.");
		}
	}
}
