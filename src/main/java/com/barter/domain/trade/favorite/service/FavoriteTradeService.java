package com.barter.domain.trade.favorite.service;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.trade.favorite.dto.CreateFavoriteTradeReqDto;
import com.barter.domain.trade.favorite.dto.FavoriteTradeResDto;
import com.barter.domain.trade.favorite.repository.FavoriteTradeRepository;
import com.barter.domain.member.entity.FavoriteTrade;

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
        if (favoriteTradeRepository.existsByMemberAndTradeStatusAndTradeId(member, requestDto.getTradeStatus(), requestDto.getTradeId())) {
            throw new IllegalStateException("이미 관심 거래로 등록된 항목입니다.");
        }

        // 관심 거래 생성
        FavoriteTrade favoriteTrade = FavoriteTrade.builder()
                .member(member)
                .tradeStatus(requestDto.getTradeStatus()) // 필드명 수정
                .tradeId(requestDto.getTradeId()) // 필드명 수정
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

    // 관심 거래 삭제
    @Transactional
    public void deleteFavoriteTrade(VerifiedMember verifiedMember, Long favoriteTradeId) {
        // 사용자 조회
        Member member = memberRepository.findById(verifiedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 관심 거래 조회 및 소유 확인
        FavoriteTrade favoriteTrade = favoriteTradeRepository.findByIdAndMember(favoriteTradeId, member)
                .orElseThrow(() -> new IllegalArgumentException("해당 관심 거래를 찾을 수 없습니다."));

        // 삭제
        favoriteTradeRepository.delete(favoriteTrade);
    }
}
