package com.barter.domain.trade.immediatetrade.dto.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CreateTradeSuggestProductReqDto {

	List<Long> suggestedProductIds = new ArrayList<>();
}