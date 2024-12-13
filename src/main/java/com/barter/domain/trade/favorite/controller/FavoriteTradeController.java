package com.barter.domain.trade.favorite.controller;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.trade.favorite.dto.CreateFavoriteTradeReqDto;
import com.barter.domain.trade.favorite.dto.FavoriteTradeResDto;
import com.barter.domain.trade.favorite.service.FavoriteTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteTradeController {

    private final FavoriteTradeService favoriteTradeService;

    @PostMapping
    public ResponseEntity<FavoriteTradeResDto> createFavoriteTrade(
            VerifiedMember verifiedMember,
            @RequestBody CreateFavoriteTradeReqDto requestDto) {
        FavoriteTradeResDto responseDto = favoriteTradeService.createFavoriteTrade(verifiedMember, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteTradeResDto>> findFavoriteTrades(VerifiedMember verifiedMember) {
        List<FavoriteTradeResDto> favorites = favoriteTradeService.findFavoriteTrades(verifiedMember);
        return ResponseEntity.ok(favorites);
    }
}
