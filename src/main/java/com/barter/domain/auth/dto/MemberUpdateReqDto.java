package com.barter.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberUpdateReqDto {

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    private String profileImageUrl;

    private String address;

    private String password; // 선택 사항
}
