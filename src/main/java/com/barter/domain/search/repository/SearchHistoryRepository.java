package com.barter.domain.search.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.search.entity.SearchHistory;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

	@Query("SELECT COUNT(h) FROM SearchHistory h WHERE h.searchKeyword.id = :keywordId AND h.searchedAt >= :since")
	Long countRecentSearches(@Param("keywordId") Long keywordId, @Param("since") LocalDateTime since);
}

