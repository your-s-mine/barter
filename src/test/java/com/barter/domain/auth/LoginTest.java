package com.barter.domain.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.auth.dto.SignInReqDto;
import com.barter.domain.auth.dto.SignInResDto;
import com.barter.domain.auth.service.AuthService;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.exception.customexceptions.AuthException;
import com.barter.exception.customexceptions.MemberException;
import com.barter.security.JwtUtil;
import com.barter.security.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LoginTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("로그인 성공 - 올바른 이메일과 비밀번호")
	void signIn_WhenCredentialsAreValid_ShouldReturnToken() {
		// given
		SignInReqDto req = SignInReqDto.builder()
			.email("test@email.com")
			.password("Password123!")
			.build();

		Member member = Member.builder()
			.id(1L)
			.email("test@email.com")
			.password("encodedPassword")
			.nickname("nickname")
			.build();

		when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(member));
		when(passwordEncoder.matches(req.getPassword(), member.getPassword())).thenReturn(true);
		when(jwtUtil.createToken(member.getId(), member.getEmail())).thenReturn("test-token");

		// when
		SignInResDto res = authService.signIn(req);

		// then
		assertThat(res.getAccessToken()).isEqualTo("test-token");
		verify(memberRepository).findByEmail(req.getEmail());
		verify(passwordEncoder).matches(req.getPassword(), member.getPassword());
		verify(jwtUtil).createToken(member.getId(), member.getEmail());
	}

	@Test
	@DisplayName("로그인 실패 - 존재하지 않는 이메일")
	void signIn_WhenEmailDoesNotExist_ShouldThrowException() {
		// given
		SignInReqDto req = SignInReqDto.builder()
			.email("nonexistent@email.com")
			.password("Password123!")
			.build();

		when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> authService.signIn(req))
			.isInstanceOf(MemberException.class)
			.hasMessageContaining("존재하지 않는 멤버입니다.");
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 불일치")
	void signIn_WhenPasswordDoesNotMatch_ShouldThrowException() {
		// given
		SignInReqDto req = SignInReqDto.builder()
			.email("test@email.com")
			.password("WrongPassword!")
			.build();

		Member member = Member.builder()
			.id(1L)
			.email("test@email.com")
			.password("encodedPassword")
			.nickname("nickname")
			.build();

		when(memberRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(member));
		when(passwordEncoder.matches(req.getPassword(), member.getPassword())).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> authService.signIn(req))
			.isInstanceOf(AuthException.class)
			.hasMessageContaining("비밀번호가 올바르지 않습니다.");
	}
}
