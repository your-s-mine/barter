package com.barter.domain.product.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.enums.RegisteredStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	@Enumerated(EnumType.STRING)
	private RegisteredStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public RegisteredProduct(String name, String description, String images, Member member, RegisteredStatus status) {
		this.name = name;
		this.description = description;
		this.images = images;
		this.member = member;
		this.status = status;
	}

	public static RegisteredProduct create(CreateRegisteredProductReqDto request, Member member) {
		return RegisteredProduct.builder()
			.name(request.getName())
			.description(request.getDescription())
			.images(request.getImages())
			.member(member)
			.status(RegisteredStatus.PENDING)
			.build();
	}

	public void validateOwner(Long userId) {
		if (!member.isEqualsId(userId)) {
			throw new IllegalArgumentException("권한이 없습니다.");
		}
	}

	public void updateInfo(UpdateRegisteredProductInfoReqDto request) {
		this.name = request.getName();
		this.description = request.getDescription();
		this.images = request.getImages();
	}
}


