package com.barter.domain.search.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	// 반환 타입은 뭘로 해야할까 ? 세 종류의 교환(trade)가 있음. 각 서비스와 레포지토리로 구분함. List 로 묶어서 보내봐야겠다.
	@GetMapping("/{word}")
	public ResponseEntity<List<SearchTradeResDto>> findTrades(@PathVariable String word) {
		return new ResponseEntity<>(searchService.createSearchKeywordAndFindTrades(word), HttpStatus.OK);
	}
}
