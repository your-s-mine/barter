package com.barter.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpReqDto {

	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "유효한 이메일 형식이어야 합니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "비밀번호는 숫자와 문자를 포함해야 합니다.")
	private String password;

	@NotBlank(message = "닉네임은 필수 입력값입니다.")
	private String nickname;
}
