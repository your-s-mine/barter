package com.barter.domain.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.member.dto.CreateFavoriteKeywordReqDto;
import com.barter.domain.member.dto.FindFavoriteKeywordResDto;
import com.barter.domain.member.service.FavoriteKeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-keywords")
public class FavoriteKeywordController {

	private final FavoriteKeywordService favoriteKeywordService;

	@PostMapping
	public ResponseEntity<Void> createFavoriteKeyword(@RequestBody CreateFavoriteKeywordReqDto req) {
		// TODO: 인증/인가 개발 완료 시 교체 예정
		Long memberId = 1L;
		favoriteKeywordService.createFavoriteKeyword(memberId, req);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}

	@GetMapping
	public ResponseEntity<List<FindFavoriteKeywordResDto>> findFavoriteKeywords() {
		// TODO: 인증/인가 개발 완료 시 교체 예정
		Long memberId = 1L;
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(favoriteKeywordService.findFavoriteKeywords(memberId));
	}
}
