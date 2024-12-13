package com.barter.domain.trade.favorite.repository;

import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.member.entity.FavoriteTrade; // 통합된 엔티티 참조
import com.barter.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteTradeRepository extends JpaRepository<FavoriteTrade, Long> {
    // 사용자의 관심 거래 조회
    List<FavoriteTrade> findByMember(Member member);

    // 중복 확인 (필드명 변경 반영)
    boolean existsByMemberAndTradeStatusAndTradeId(Member member, TradeStatus tradeStatus, Long tradeId);
}
