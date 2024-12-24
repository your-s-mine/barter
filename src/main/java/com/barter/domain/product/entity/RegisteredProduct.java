package com.barter.domain.product.entity;

import static com.barter.exception.enums.ExceptionCode.*;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.dto.request.UpdateRegisteredProductInfoReqDto;
import com.barter.domain.product.enums.RegisteredStatus;
import com.barter.exception.customexceptions.AuthException;
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
@Table(name = "REGISTERED_PRODUCTS")
public class RegisteredProduct extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> images;    // 이전 회의에서 이미지 JSON 타입으로 DB 에 저장한다고 해서 수정했습니다.
	@Enumerated(EnumType.STRING)
	private RegisteredStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public RegisteredProduct(
		Long id, String name, String description, List<String> images, Member member, RegisteredStatus status
	) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.images = images;
		this.member = member;
		this.status = status;
	}

	public static RegisteredProduct create(
		CreateRegisteredProductReqDto request, Member member, List<String> images
	) {
		return RegisteredProduct.builder()
			.name(request.getName())
			.description(request.getDescription())
			.images(images)
			.member(member)
			.status(RegisteredStatus.PENDING)
			.build();
	}

	// 아래의 validateOwner() 의 경우 RegisteredProduct 를 패치 조인할 때 쓸수 있는 것 같아 따로 메서드 추가했습니다.
	// 저의 경우 RegisteredProduct 의 회원 ID 가 파라미터로 전달된 ID 와 같은지만 비교하면 되기 때문입니다.
	public void checkPermission(Long memberId) {
		if (!this.member.getId().equals(memberId)) {
			throw new ProductException(ExceptionCode.NOT_OWNER_REGISTERED_PRODUCT);
		}
	}

	public void validateOwner(Long userId) {
		if (!member.isEqualsId(userId)) {
			throw new AuthException(NO_AUTHORITY);
		}
	}

	public void checkPossibleUpdate() {
		if (this.status != RegisteredStatus.PENDING) {
			throw new ProductException(ExceptionCode.REGISTERED_PRODUCT_INFO_UPDATE_IMPOSSIBLE);
		}
	}

	public void updateInfo(UpdateRegisteredProductInfoReqDto request) {
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
		this.status = RegisteredStatus.findRegisteredStatus(status);
	}

	public void checkPossibleDelete() {
		if (this.status != RegisteredStatus.PENDING && this.status != RegisteredStatus.COMPLETED) {
			throw new ProductException(ExceptionCode.NOT_VALID_STATUS_REGISTERED_PRODUCT_DELETE);
		}
	}

	public void changeStatusRegistering() {
		this.status = RegisteredStatus.REGISTERING;
	}

	public void changeStatusPending() {
		this.status = RegisteredStatus.PENDING;
	}

	public void validatePendingStatusBeforeUpload() {
		if (!status.equals(RegisteredStatus.PENDING)) {
			throw new IllegalArgumentException("PENDING 상태만 업로드 가능합니다.");
		}
	}

	public void changeStatusCompleted() {
		this.status = RegisteredStatus.COMPLETED;
	}

	public void changeStatusAccepted() {
		this.status = RegisteredStatus.ACCEPTED;
	}
}


