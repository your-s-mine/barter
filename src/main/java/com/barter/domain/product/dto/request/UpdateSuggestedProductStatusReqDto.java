package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateSuggestedProductStatusReqDto {

	@NotNull
	private Long id;

	@NotNull
	private String status;

	// 인증/인가 전 테스트를 위해 RequestBody 에 요청 회원의 ID 를 전달받도록 함, 이후 삭제 예정
	@NotNull
	private Long memberId;
}
