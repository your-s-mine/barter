package com.barter.domain.trade.periodtrade.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.entity.QPeriodTrade;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class PeriodTradeCustomRepositoryImpl implements PeriodTradeCustomRepository {

	private final JPAQueryFactory jpaqUeryFactory;
	private final QPeriodTrade qPeriodTrade = QPeriodTrade.periodTrade;

	public PeriodTradeCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaqUeryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public List<PeriodTrade> paginationCoveringIndex(Pageable pageable) {

		List<Long> ids = jpaqUeryFactory
			.select(qPeriodTrade.id)
			.from(qPeriodTrade)
			.orderBy(qPeriodTrade.updatedAt.desc())
			.limit(pageable.getPageSize())
			.offset((long)pageable.getPageNumber() * pageable.getPageSize())
			.fetch();

		if (ids.isEmpty()) {
			return new ArrayList<>();
		}

		return jpaqUeryFactory
			.selectFrom(qPeriodTrade)
			.where(qPeriodTrade.id.in(ids))
			.orderBy(qPeriodTrade.updatedAt.desc())
			.fetch();
	}

}
