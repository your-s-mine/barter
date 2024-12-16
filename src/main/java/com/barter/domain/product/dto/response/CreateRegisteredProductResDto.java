package com.barter.domain.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRegisteredProductResDto {

	private Long registeredProductId;
	private String name;
	private String description;
	private List<String> images;
	private String status;
	private Long memberId;
	private LocalDateTime createdAt;

	public static CreateRegisteredProductResDto from(RegisteredProduct registeredProduct) {
		return CreateRegisteredProductResDto.builder()
			.registeredProductId(registeredProduct.getId())
			.name(registeredProduct.getName())
			.description(registeredProduct.getDescription())
			.images(registeredProduct.getImages())
			.status(registeredProduct.getStatus().name())
			.memberId(registeredProduct.getMember().getId())
			.createdAt(registeredProduct.getCreatedAt())
			.build();
	}
}
