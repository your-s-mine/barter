package com.barter.domain.notification.entity;

import com.barter.domain.BaseTimeStampEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ACTIVITY_NOTIFICATIONS")
public class ActivityNotification extends BaseTimeStampEntity {  // Notification 으로 변경

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String message;
	private String url;
	private boolean isRead;
	private Long memberId;    // 연관관계 매핑은 하지 않되, DB 연관관계(FK)는 맺는거로 생각했습니다.

	@Builder
	public ActivityNotification(String message, String url, boolean isRead, Long memberId) {
		this.message = message;
		this.url = url;
		this.isRead = isRead;
		this.memberId = memberId;
	}

	public static ActivityNotification create(String message, String url, Long memberId) {
		return ActivityNotification.builder()
			.message(message)
			.url(url)
			.isRead(false)
			.memberId(memberId)
			.build();
	}
}
