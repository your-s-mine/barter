package com.barter.domain.trade.periodtrade.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.barter.domain.trade.periodtrade.entity.PeriodTrade;
import com.barter.domain.trade.periodtrade.entity.QPeriodTrade;
import com.querydsl.core.types.dsl.BooleanExpression;
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

		if (pageable.getPageNumber() == 0) {
			return jpaqUeryFactory
				.selectFrom(qPeriodTrade)
				.orderBy(qPeriodTrade.updatedAt.desc())
				.limit(pageable.getPageSize())
				.fetch();
		}

		Long lastId = getLastIdForPage(pageable);

		if (lastId == null) {
			return List.of();
		}

		System.out.println("lastId = " + lastId);

		return jpaqUeryFactory
			.selectFrom(qPeriodTrade)
			.where(lastIdCondition(lastId))
			.orderBy(qPeriodTrade.updatedAt.desc())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private Long getLastIdForPage(Pageable pageable) {
		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		return jpaqUeryFactory
			.select(qPeriodTrade.id)
			.from(qPeriodTrade)
			.orderBy(qPeriodTrade.updatedAt.desc())
			.offset((long)pageNumber * pageSize)
			.limit(1)
			.fetchOne();
	}

	private BooleanExpression lastIdCondition(Long lastId) {
		if (lastId == null) {
			return null;
		}
		return qPeriodTrade.id.lt(lastId);
	}

}
