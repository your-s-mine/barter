package com.barter.domain.trade.donationtrade.entity;

import com.barter.domain.member.entity.Member;

import jakarta.persistence.Entity;
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
@Table(name = "DONATION_PRODUCT_MEMBERS")
public class DonationProductMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Member member;
	@ManyToOne
	private DonationTrade donationTrade;

	@Builder
	public DonationProductMember(Long id, Member member, DonationTrade donationTrade) {
		this.id = id;
		this.member = member;
		this.donationTrade = donationTrade;
	}
}
