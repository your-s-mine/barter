package com.barter.domain.search.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.search.dto.SearchTradeReqDto;
import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
	private final SearchService searchService;

	@PostMapping("/trades")
	public ResponseEntity<List<SearchTradeResDto>> findTrades(@RequestParam String word,
		@RequestBody SearchTradeReqDto reqDto) {
		if (word == null || word.isBlank()) {
			throw new IllegalArgumentException("검색어는 필수입니다.");
		}
		return new ResponseEntity<>(searchService.searchKeywordAndFindTrades(word, reqDto), HttpStatus.OK);
	}

	@PostMapping("/popular/{location}")
	public ResponseEntity<List<String>> findPopularKeywords(@PathVariable String location) {
		return new ResponseEntity<>(searchService.findPopularKeywords(location), HttpStatus.OK);
	}
}
