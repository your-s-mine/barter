package com.barter.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.member.entity.FavoriteKeyword;

public interface FavoriteKeywordRepository extends JpaRepository<FavoriteKeyword, Long> {
	Optional<FavoriteKeyword> findByKeyword(String keyword);
}
