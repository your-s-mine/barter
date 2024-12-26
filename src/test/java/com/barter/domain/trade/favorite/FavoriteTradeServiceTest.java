package com.barter.domain.trade.favorite;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.favorite.dto.CreateFavoriteTradeReqDto;
import com.barter.domain.trade.favorite.dto.FavoriteTradeResDto;
import com.barter.domain.trade.favorite.repository.FavoriteTradeRepository;
import com.barter.domain.trade.favorite.service.FavoriteTradeService;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.member.entity.FavoriteTrade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteTradeServiceTest {

    @Mock
    private FavoriteTradeRepository favoriteTradeRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FavoriteTradeService favoriteTradeService;

    @Test
    @DisplayName("관심 거래 생성 성공")
    void createFavoriteTrade_WhenValidInput_ShouldReturnFavoriteTradeResDto() {
        // given
        VerifiedMember verifiedMember = VerifiedMember.builder()
                .id(1L)
                .email("test@email.com")
                .build();

        CreateFavoriteTradeReqDto requestDto = CreateFavoriteTradeReqDto.builder()
                .tradeStatus(TradeStatus.IN_PROGRESS) // IN_PROGRESS로 설정
                .tradeId(100L)
                .build();

        Member member = Member.builder()
                .id(1L)
                .email("test@email.com")
                .build();

        FavoriteTrade favoriteTrade = FavoriteTrade.builder()
                .id(1L)
                .member(member)
                .tradeStatus(TradeStatus.IN_PROGRESS)
                .tradeId(100L)
                .build();

        when(memberRepository.findById(verifiedMember.getId())).thenReturn(Optional.of(member));
        when(favoriteTradeRepository.existsByMemberAndTradeStatusAndTradeId(member, TradeStatus.IN_PROGRESS, 100L))
                .thenReturn(false);
        when(favoriteTradeRepository.save(any(FavoriteTrade.class))).thenReturn(favoriteTrade);

        // when
        FavoriteTradeResDto responseDto = favoriteTradeService.createFavoriteTrade(verifiedMember, requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getMemberId()).isEqualTo(member.getId());
        assertThat(responseDto.getTradeStatus()).isEqualTo(TradeStatus.IN_PROGRESS);
        assertThat(responseDto.getTradeId()).isEqualTo(100L);

        verify(memberRepository).findById(verifiedMember.getId());
        verify(favoriteTradeRepository).existsByMemberAndTradeStatusAndTradeId(member, TradeStatus.IN_PROGRESS, 100L);
        verify(favoriteTradeRepository).save(any(FavoriteTrade.class));
    }

    @Test
    @DisplayName("관심 거래 생성 실패 - 이미 등록된 항목")
    void createFavoriteTrade_WhenAlreadyExists_ShouldThrowException() {
        // given
        VerifiedMember verifiedMember = VerifiedMember.builder()
                .id(1L)
                .email("test@email.com")
                .build();

        CreateFavoriteTradeReqDto requestDto = CreateFavoriteTradeReqDto.builder()
                .tradeStatus(TradeStatus.IN_PROGRESS) // IN_PROGRESS로 설정
                .tradeId(100L)
                .build();

        Member member = Member.builder()
                .id(1L)
                .email("test@email.com")
                .build();

        when(memberRepository.findById(verifiedMember.getId())).thenReturn(Optional.of(member));
        when(favoriteTradeRepository.existsByMemberAndTradeStatusAndTradeId(member, TradeStatus.IN_PROGRESS, 100L))
                .thenReturn(true); // 이미 등록된 상태로 설정

        // when & then
        assertThatThrownBy(() -> favoriteTradeService.createFavoriteTrade(verifiedMember, requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 관심 거래로 등록된 항목입니다.");

        verify(memberRepository).findById(verifiedMember.getId());
        verify(favoriteTradeRepository).existsByMemberAndTradeStatusAndTradeId(member, TradeStatus.IN_PROGRESS, 100L);
        verify(favoriteTradeRepository, never()).save(any(FavoriteTrade.class)); // 저장은 호출되지 않음
    }
}
