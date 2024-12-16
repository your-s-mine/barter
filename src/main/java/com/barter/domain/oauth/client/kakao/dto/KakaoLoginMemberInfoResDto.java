package com.barter.domain.oauth.client.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoLoginMemberInfoResDto {

	private Long id;
	private KakaoMemberPropertiesResDto properties;
	private KakaoMemberAccountDto kakaoAccount;
}
