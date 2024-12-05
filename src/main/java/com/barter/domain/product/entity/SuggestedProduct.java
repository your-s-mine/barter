package com.barter.domain.product.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.enums.SuggestedStatus;

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
@Table(name = "SUGGESTED_PRODUCTS")
public class SuggestedProduct extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private String images;
	@Enumerated(EnumType.STRING)
	private SuggestedStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public SuggestedProduct(String name, String description, String images, Member member, SuggestedStatus status) {
		this.name = name;
		this.description = description;
		this.images = images;
		this.member = member;
		this.status = status;
	}

	public static SuggestedProduct create(CreateSuggestedProductReqDto request, Member member) {
		return SuggestedProduct.builder()
			.name(request.getName())
			.description(request.getDescription())
			.images(request.getImages())
			.member(member)
			.status(SuggestedStatus.PENDING)
			.build();
	}

	public boolean validateProductStatus(SuggestedStatus status) {
		return status.equals(SuggestedStatus.PENDING);
	}

	public void changStatusSuggesting() {
		this.status = SuggestedStatus.SUGGESTING;
	}

	public void changStatusAccepted() {
		this.status = SuggestedStatus.ACCEPTED;
	}

	public void updateInfo(UpdateSuggestedProductInfoReqDto request) {
		if (this.status == SuggestedStatus.ACCEPTED) {
			throw new IllegalArgumentException("이미 제안이 승낙된 물품은 수정할 수 없습니다");
		}

		this.name = request.getName();
		this.description = request.getDescription();
		this.images = request.getImages();
	}
}
