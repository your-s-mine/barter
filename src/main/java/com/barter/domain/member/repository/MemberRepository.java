package com.barter.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
