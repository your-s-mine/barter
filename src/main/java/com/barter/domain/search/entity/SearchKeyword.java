package com.barter.domain.search.entity;

import com.barter.domain.BaseTimeStampEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SEARCH_KEYWORDS")
public class SearchKeyword extends BaseTimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String word;

	@Column(nullable = false)
	private Long count = 0L;

	@Builder
	public SearchKeyword(String word, Long count) {
		this.word = word;
		this.count = count;
	}

	public void updateCount(Long newCount) {
		this.count = newCount;
	}
}
