package com.barter.domain.auth;

import com.barter.domain.auth.dto.SignUpReqDto;
import com.barter.domain.auth.service.AuthService;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.security.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SignInTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 실패 - 이메일 형식이 잘못된 경우")
    void signUp_WhenEmailIsInvalid_ShouldThrowException() {
        // given
        SignUpReqDto req = SignUpReqDto.builder()
                .email("invalid-email")
                .password("Password123!")
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효한 이메일 형식이어야 합니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호가 너무 짧은 경우")
    void signUp_WhenPasswordTooShort_ShouldThrowException() {
        // given
        SignUpReqDto req = SignUpReqDto.builder()
                .email("test@email.com")
                .password("short")
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 조건 불만족 (숫자 미포함)")
    void signUp_WhenPasswordLacksNumber_ShouldThrowException() {
        // given
        SignUpReqDto req = SignUpReqDto.builder()
                .email("test@email.com")
                .password("Password!")
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 숫자와 문자를 포함해야 합니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임이 비어 있는 경우")
    void signUp_WhenNicknameIsBlank_ShouldThrowException() {
        // given
        SignUpReqDto req = SignUpReqDto.builder()
                .email("test@email.com")
                .password("Password123!")
                .nickname("")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("닉네임은 필수 입력값입니다.");
    }
}
