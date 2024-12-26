package com.barter.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ADDRESS")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String postNum;

	@Column(nullable = false)
	private String address1;

	private String address2;

	@Builder
	public Address(String postNum, String address1, String address2) {
		this.postNum = postNum;
		this.address1 = address1;
		this.address2 = address2;
	}
}
