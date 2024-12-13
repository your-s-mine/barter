package com.barter.domain.trade.favorite.repository;

import com.barter.domain.trade.favorite.entity.FavoriteTrade;
import com.barter.domain.member.entity.Member;
import com.barter.domain.trade.enums.TradeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteTradeRepository extends JpaRepository<FavoriteTrade, Long> {
    List<FavoriteTrade> findByMember(Member member); // 사용자의 관심 거래 조회
    boolean existsByMemberAndTradesTypeAndTradesId(Member member, TradeType tradesType, Long tradesId); // 중복 확인
}
