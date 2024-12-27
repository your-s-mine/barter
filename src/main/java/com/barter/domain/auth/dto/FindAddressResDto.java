package com.barter.domain.auth.dto;

import com.barter.domain.member.entity.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindAddressResDto {

	private String address1;
	private String address2;

	@Builder
	public FindAddressResDto(String address1, String address2) {
		this.address1 = address1;
		this.address2 = address2;
	}

	public static FindAddressResDto from(Member member) {
		return FindAddressResDto.builder()
			.address1(member.getAddress().getAddress1())
			.address2(member.getAddress().getAddress2())
			.build();
	}
}
