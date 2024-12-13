package com.barter.domain.trade.favorite.service;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.trade.enums.TradeType;
import com.barter.domain.trade.favorite.dto.CreateFavoriteTradeReqDto;
import com.barter.domain.trade.favorite.dto.FavoriteTradeResDto;
import com.barter.domain.trade.favorite.entity.FavoriteTrade;
import com.barter.domain.trade.favorite.repository.FavoriteTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteTradeService {

    private final FavoriteTradeRepository favoriteTradeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavoriteTradeResDto createFavoriteTrade(VerifiedMember verifiedMember, CreateFavoriteTradeReqDto requestDto) {
        // 사용자 조회
        Member member = memberRepository.findById(verifiedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 중복 확인
        if (favoriteTradeRepository.existsByMemberAndTradesTypeAndTradesId(member, requestDto.getTradesType(), requestDto.getTradesId())) {
            throw new IllegalStateException("이미 관심 거래로 등록된 항목입니다.");
        }

        // 관심 거래 생성
        FavoriteTrade favoriteTrade = FavoriteTrade.builder()
                .member(member)
                .tradesType(requestDto.getTradesType())
                .tradesId(requestDto.getTradesId())
                .build();

        favoriteTradeRepository.save(favoriteTrade);
        return FavoriteTradeResDto.from(favoriteTrade);
    }

    @Transactional(readOnly = true)
    public List<FavoriteTradeResDto> findFavoriteTrades(VerifiedMember verifiedMember) {
        // 사용자 조회
        Member member = memberRepository.findById(verifiedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 관심 거래 조회
        return favoriteTradeRepository.findByMember(member).stream()
                .map(FavoriteTradeResDto::from)
                .collect(Collectors.toList());
    }
}
