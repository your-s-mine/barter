package com.barter.domain.product.entity;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> images;    // 이전 회의에서 이미지 JSON 타입으로 DB 에 저장한다고 해서 수정했습니다.
	@Enumerated(EnumType.STRING)
	private RegisteredStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public RegisteredProduct(
		String name, String description, List<String> images, Member member, RegisteredStatus status
	) {
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
			throw new IllegalArgumentException("권한이 없습니다.");
		}
	}

	public void validateOwner(Long userId) {
		if (!member.isEqualsId(userId)) {
			throw new IllegalArgumentException("권한이 없습니다.");
		}
	}

	public void updateInfo(UpdateRegisteredProductInfoReqDto request) {
		if (this.status != RegisteredStatus.PENDING) {
			throw new IllegalArgumentException("PENDING 상태인 경우에만 등록 물품을 수정할 수 있습니다.");
		}

		this.name = request.getName();
		this.description = request.getDescription();
		// this.images = request.getImages();  등록 상품 정보 수정 리팩토링시 해결할 계획입니다.
	}

	public void updateStatus(String status) {
		this.status = RegisteredStatus.findRegisteredStatus(status);
	}

	public void checkPossibleDelete() {
		if (this.status != RegisteredStatus.PENDING) {
			throw new IllegalArgumentException("PENDING 상태인 경우에만 등록 물품을 삭제할 수 있습니다.");
		}
	}

	public void changStatusRegistering() {
		this.status = RegisteredStatus.REGISTERING;
	}

	public void changeStatusAccepted() {
		this.status = RegisteredStatus.ACCEPTED;
	}

	public void changeStatusPending() {
		this.status = RegisteredStatus.PENDING;
	}
}


