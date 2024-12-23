package com.barter.domain.product.entity;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateSuggestedProductReqDto;
import com.barter.domain.product.dto.request.UpdateSuggestedProductInfoReqDto;
import com.barter.domain.product.enums.SuggestedStatus;
import com.barter.exception.customexceptions.ProductException;
import com.barter.exception.enums.ExceptionCode;

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
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> images;    // 이전 회의에서 이미지 JSON 타입으로 DB 에 저장한다고 해서 수정했습니다.
	@Enumerated(EnumType.STRING)
	private SuggestedStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public SuggestedProduct(
		Long id, String name, String description, List<String> images, Member member, SuggestedStatus status
	) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.images = images;
		this.member = member;
		this.status = status;
	}

	public static SuggestedProduct create(
		CreateSuggestedProductReqDto request, Member member, List<String> images
	) {
		return SuggestedProduct.builder()
			.name(request.getName())
			.description(request.getDescription())
			.images(images)
			.member(member)
			.status(SuggestedStatus.PENDING)
			.build();
	}

	public void checkPermission(Long memberId) {
		if (!this.member.getId().equals(memberId)) {
			throw new ProductException(ExceptionCode.NOT_OWNER_SUGGESTED_PRODUCT);
		}
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

	public void checkPossibleUpdate() {
		if (this.status != SuggestedStatus.PENDING) {
			throw new IllegalArgumentException("PENDING 상태인 경우에만 제안 물품을 수정할 수 있습니다.");
		}
	}

	public void updateInfo(UpdateSuggestedProductInfoReqDto request) {
		this.name = request.getName();
		this.description = request.getDescription();
	}

	public void deleteImages(List<String> images) {
		this.images.removeAll(images);
	}

	public void updateImages(List<String> images) {
		this.images.addAll(images);
	}

	public void updateStatus(String status) {
		this.status = SuggestedStatus.findSuggestedStatus(status);
	}

	public void changeStatusPending() {
		this.status = SuggestedStatus.PENDING;
	}

	public void checkPossibleDelete() {
		if (this.status != SuggestedStatus.PENDING && this.status != SuggestedStatus.COMPLETED) {
			throw new IllegalArgumentException("PENDING 또는 COMPLETED 상태인 경우에만 제안 물품을 삭제할 수 있습니다.");
		}
	}

	public void changeStatusCompleted() {
		this.status = SuggestedStatus.COMPLETED;
	}
}
