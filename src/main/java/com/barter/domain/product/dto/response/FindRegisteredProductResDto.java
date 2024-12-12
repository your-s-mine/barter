package com.barter.domain.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.barter.domain.product.entity.RegisteredProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindRegisteredProductResDto {

	private Long id;
	private String name;
	private String description;
	private String images;
	private List<String> imageNames;
	private LocalDateTime createdAt;
	private String status;

	public static FindRegisteredProductResDto from(RegisteredProduct product) {
		return FindRegisteredProductResDto.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			// .images(product.getImages())	등록 물품 단건 조회시 수정할 계획입니다.
			// .imageNames(product.getImages())
			.createdAt(product.getCreatedAt())
			.status(product.getStatus().name())
			.build();
	}
}
