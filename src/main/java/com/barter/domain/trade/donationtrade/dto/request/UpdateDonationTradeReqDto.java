package com.barter.domain.trade.donationtrade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateDonationTradeReqDto {

	@NotBlank(message = "제목은 반드시 작성하셔야 합니다.")
	@Size(min = 5, message = "제목은 5글자 이상만 가능합니다.")
	private String title;
	@NotBlank(message = "설명은 반드시 작성하셔야 합니다.")
	@Size(min = 5, message = "설명은 5글자 이상만 가능합니다.")
	private String description;

	@Builder
	public UpdateDonationTradeReqDto(String title, String description) {
		this.title = title;
		this.description = description;
	}
}
