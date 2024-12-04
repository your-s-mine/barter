package com.barter.domain.product.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REGISTERED_PRODUCTS")
public class RegisteredProduct extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private String images;
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public RegisteredProduct(String name, String description, String images, Member member) {
		this.name = name;
		this.description = description;
		this.images = images;
		this.member = member;
	}

	public static RegisteredProduct create(CreateRegisteredProductReqDto request, Member member) {
		return RegisteredProduct.builder()
			.name(request.getName())
			.description(request.getDescription())
			.images(request.getImages())
			.member(member)
			.build();
	}
}


