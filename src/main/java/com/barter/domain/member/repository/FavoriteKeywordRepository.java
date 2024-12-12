package com.barter.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.barter.domain.member.entity.FavoriteKeyword;

public interface FavoriteKeywordRepository extends JpaRepository<FavoriteKeyword, Long> {
	Optional<FavoriteKeyword> findByKeyword(String keyword);

	List<FavoriteKeyword> findByKeywordIn(@Param("keyword") List<String> keywords);
}
