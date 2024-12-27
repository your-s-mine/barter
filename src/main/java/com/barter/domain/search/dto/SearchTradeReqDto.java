package com.barter.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchTradeReqDto {

	String address1;
	Double longitude;
	Double latitude;
}
