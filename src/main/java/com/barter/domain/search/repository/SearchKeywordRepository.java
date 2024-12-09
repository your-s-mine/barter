package com.barter.domain.search.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.search.entity.SearchKeyword;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
	Optional<SearchKeyword> findByWord(String word);
}
