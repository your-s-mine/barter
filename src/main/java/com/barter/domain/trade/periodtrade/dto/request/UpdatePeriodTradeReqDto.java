package com.barter.domain.trade.periodtrade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // for test
public class UpdatePeriodTradeReqDto {

	@NotBlank(message = "타이틀은 필수입니다.")
	@Size(min = 5, message = "타이틀은 5글자 이상이어야 합니다.")
	private String title;
	@NotBlank(message = "설명은 필수입니다.")
	@Size(min = 5, message = "설명은 5글자 이상이어야 합니다.")
	private String description;
	/*@NotNull(message = "수정할 기간 거래의 상품 id 값이 필요합니다.")
	private Long registeredProductId;*/
}
