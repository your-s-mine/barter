package com.barter.domain.trade.favorite.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.trade.favorite.dto.CreateFavoriteTradeReqDto;
import com.barter.domain.trade.favorite.dto.FavoriteTradeResDto;
import com.barter.domain.trade.favorite.service.FavoriteTradeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favorite-trades")
@RequiredArgsConstructor
public class FavoriteTradeController {

	private final FavoriteTradeService favoriteTradeService;

	// 관심 거래 생성
	@PostMapping
	public ResponseEntity<FavoriteTradeResDto> createFavoriteTrade(
			VerifiedMember verifiedMember, // 인증 정보를 자동으로 주입
			@RequestBody CreateFavoriteTradeReqDto requestDto) {
		// 서비스 호출
		FavoriteTradeResDto responseDto = favoriteTradeService.createFavoriteTrade(verifiedMember, requestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 관심 거래 조회
	@GetMapping
	public ResponseEntity<List<FavoriteTradeResDto>> findFavoriteTrades(
			VerifiedMember verifiedMember) { // 인증 정보를 자동으로 주입
		// 서비스 호출
		List<FavoriteTradeResDto> favorites = favoriteTradeService.findFavoriteTrades(verifiedMember);
		return ResponseEntity.ok(favorites);
	}

	// 관심 거래 삭제
	@DeleteMapping("/{favoriteTradeId}")
	public ResponseEntity<Void> deleteFavoriteTrade(
			VerifiedMember verifiedMember,
			@PathVariable Long favoriteTradeId) {
		// 서비스 호출
		favoriteTradeService.deleteFavoriteTrade(verifiedMember, favoriteTradeId);
		return ResponseEntity.noContent().build();
	}
}
