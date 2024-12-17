package com.barter.domain.search.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.search.entity.SearchKeyword;

import jakarta.persistence.LockModeType;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM SearchKeyword s WHERE s.word = :word")
	Optional<SearchKeyword> findByWord(@Param("word") String word);

	List<SearchKeyword> findTop10ByOrderByCountDesc();

	Long countByword(String word);
}
