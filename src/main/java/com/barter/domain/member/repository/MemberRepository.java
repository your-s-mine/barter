package com.barter.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.member.entity.Member;
import com.barter.domain.oauth.enums.OAuthProvider;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<Member> findByProviderAndProviderId(OAuthProvider provider, String providerId);

	boolean existsByProviderId(String providerId);
}
