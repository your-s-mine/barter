package com.barter.domain.member.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.member.enums.JoinPath;

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
	private String email;
	private String nickname;
	private String password;
	private String oauthId;
	private String profileImage;
	@Enumerated(EnumType.STRING)
	private JoinPath joinPath;

	@Builder
	public Member(Long id, String email, String nickname, String password, String oauthId, String profileImage,
		JoinPath joinPath) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.oauthId = oauthId;
		this.profileImage = profileImage;
		this.joinPath = joinPath;
	}

	public static Member createBasicMember(String email, String hashedPassword, String nickname) {
		return Member.builder()
			.email(email)
			.password(hashedPassword)
			.nickname(nickname)
			.joinPath(JoinPath.BASIC)
			.build();
	}

	public boolean isEqualsId(Long userId) {
		return id.equals(userId);
	}
}
