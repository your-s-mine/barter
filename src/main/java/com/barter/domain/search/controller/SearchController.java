package com.barter.domain.search.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.search.dto.SearchTradeResDto;
import com.barter.domain.search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
	private final SearchService searchService;

	@GetMapping("/{word}")
	public ResponseEntity<List<SearchTradeResDto>> findTrades(@PathVariable String word) {
		return new ResponseEntity<>(searchService.searchKeywordAndFindTrades(word), HttpStatus.OK);
	}

	@GetMapping("/popular")
	public ResponseEntity<List<String>> findPopularKeywords() {
		return new ResponseEntity<>(searchService.findPopularKeywords(), HttpStatus.OK);
	}
}
