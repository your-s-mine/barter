package com.barter.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<com.barter.domain.member.entity.Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
