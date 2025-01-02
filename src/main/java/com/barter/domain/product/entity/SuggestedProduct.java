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
			throw new ProductException(ExceptionCode.SUGGESTED_PRODUCT_INFO_UPDATE_IMPOSSIBLE);
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

	public void changeStatusPending() {
		this.status = SuggestedStatus.PENDING;
	}

	public void checkPossibleDelete() {
		if (this.status != SuggestedStatus.PENDING && this.status != SuggestedStatus.COMPLETED) {
			throw new ProductException(ExceptionCode.NOT_VALID_STATUS_SUGGESTED_PRODUCT_DELETE);
		}
	}

	public void changeStatusCompleted() {
		this.status = SuggestedStatus.COMPLETED;
	}
}
