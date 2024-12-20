package com.barter.domain.search.dto;

import java.util.List;

import com.barter.domain.product.enums.RegisteredStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConvertProductDto {
	private Long id;
	private String name;
	private String description;
	private List<String> images;
	private RegisteredStatus status;
}
