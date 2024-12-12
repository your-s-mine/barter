package com.barter.domain.trade.immediatetrade;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

@ExtendWith(MockitoExtension.class)
public class find {
	@Mock
	ImmediateTradeRepository immediateTradeRepository;
	@Mock
	RegisteredProductRepository registeredProductRepository;
	@Mock
	TradeProductRepository tradeProductRepository;
	@Mock
	SuggestedProductRepository suggestedProductRepository;
	@InjectMocks
	ImmediateTradeService immediateTradeService;
	VerifiedMember member;


}
