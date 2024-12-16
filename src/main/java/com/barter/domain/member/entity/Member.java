package com.barter.domain.member.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.oauth.enums.OAuthProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "MEMBERS")
public class Member extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String email;
	private String nickname;
	private String password;
	@Enumerated(EnumType.STRING)
	private OAuthProvider provider;
	private String providerId;
	private String profileImage;

	@Builder
	public Member(Long id, String email, String nickname, String password, OAuthProvider provider, String providerId,
		String profileImage) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.provider = provider;
		this.providerId = providerId;
		this.profileImage = profileImage;
	}

	public static Member createBasicMember(String email, String hashedPassword, String nickname) {
		return Member.builder()
			.email(email)
			.password(hashedPassword)
			.nickname(nickname)
			.provider(OAuthProvider.BASIC)
			.build();
	}

	public boolean isEqualsId(Long userId) {
		return id.equals(userId);
	}
}
