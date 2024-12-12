package com.barter.domain.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.auth.dto.VerifiedMember;
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
	public ResponseEntity<Void> createFavoriteKeyword(
		VerifiedMember verifiedMember,
		@RequestBody CreateFavoriteKeywordReqDto req
	) {
		favoriteKeywordService.createFavoriteKeyword(verifiedMember, req);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}

	@GetMapping
	public ResponseEntity<List<FindFavoriteKeywordResDto>> findFavoriteKeywords(VerifiedMember verifiedMember) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(favoriteKeywordService.findFavoriteKeywords(verifiedMember));
	}

	@DeleteMapping("/{memberFavoriteKeywordId}")
	public ResponseEntity<Void> deleteFavoriteKeyword(
		VerifiedMember verifiedMember,
		@PathVariable Long memberFavoriteKeywordId
	) {
		favoriteKeywordService.deleteFavoriteKeyword(verifiedMember, memberFavoriteKeywordId);
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}
}
