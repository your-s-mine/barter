package com.barter.domain.member.entity;

import com.barter.domain.BaseTimeStampEntity;
import com.barter.domain.oauth.enums.OAuthProvider;
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

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String password;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id")
	private Address address;

	@Enumerated(EnumType.STRING)
	private OAuthProvider provider;

	@Column(nullable = true)
	private String providerId; // OAuth 제공자 ID 추가

	@Builder
	public Member(Long id, String email, String nickname, String password, Address address, OAuthProvider provider, String providerId) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.address = address;
		this.provider = provider;
		this.providerId = providerId;
	}

	public static Member createBasicMember(String email, String hashedPassword, String nickname, Address address) {
		return Member.builder()
				.email(email)
				.password(hashedPassword)
				.nickname(nickname)
				.address(address)
				.provider(OAuthProvider.BASIC)
				.build();
	}

	public static Member createOAuthMember(String email, String nickname, String hashedPassword, OAuthProvider provider, String providerId) {
		return Member.builder()
				.email(email)
				.password(hashedPassword)
				.nickname(nickname)
				.provider(provider)
				.providerId(providerId)
				.build();
	}
}
