package com.barter.domain.member.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.enums.JoinPath;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBERS")
public class Member extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;
	private String nickname;
	private String password;
	private String oauthId;
	private String profileImageUrl;
	private String address;
	@Enumerated(EnumType.STRING)
	private JoinPath joinPath;

	@Builder
	public Member(Long id, String email, String nickname, String password, String oauthId, String profileImageUrl,
				  String address, JoinPath joinPath) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.oauthId = oauthId;
		this.profileImageUrl = profileImageUrl;
		this.address = address;
		this.joinPath = joinPath;
	}

	// 회원 생성 메서드
	public static Member createBasicMember(String email, String hashedPassword, String nickname) {
		return Member.builder()
				.email(email)
				.password(hashedPassword)
				.nickname(nickname)
				.joinPath(JoinPath.BASIC)
				.build();
	}

	// 도메인 메서드
	public void updateInfo(String nickname, String profileImageUrl, String address, String password) {
		if (nickname != null && !nickname.isBlank()) {
			this.nickname = nickname;
		}
		if (profileImageUrl != null && !profileImageUrl.isBlank()) {
			this.profileImageUrl = profileImageUrl;
		}
		if (address != null && !address.isBlank()) {
			this.address = address; // address 필드 업데이트
		}
		if (password != null && !password.isBlank()) {
			this.password = password; // 암호화 로직 추가 필요
		}
	}

	// OAuth 사용자 확인
	public boolean isOAuthUser() {
		return oauthId != null && !oauthId.isBlank();
	}

	// ID 비교
	public boolean isEqualsId(Long userId) {
		return id.equals(userId);
	}
}
