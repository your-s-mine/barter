package com.barter.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRegisteredProductReqDto {

	@NotNull
	@Size(min = 5)
	private String name;

	@NotNull
	@Size(min = 5)
	private String description;

	@NotNull
	private String images;

	// 인증/인가 전 테스트를 위해 RequestBody 에 요청 회원의 ID 를 전달받도록 함, 이후 삭제 예정
	@NotNull
	private Long memberId;
}
