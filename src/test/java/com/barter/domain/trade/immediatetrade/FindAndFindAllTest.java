package com.barter.domain.trade.immediatetrade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.barter.domain.member.entity.Member;
import com.barter.domain.product.dto.request.CreateRegisteredProductReqDto;
import com.barter.domain.product.entity.RegisteredProduct;
import com.barter.domain.product.repository.RegisteredProductRepository;
import com.barter.domain.product.repository.SuggestedProductRepository;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.trade.enums.TradeStatus;
import com.barter.domain.trade.immediatetrade.dto.request.CreateImmediateTradeReqDto;
import com.barter.domain.trade.immediatetrade.dto.response.FindImmediateTradeResDto;
import com.barter.domain.trade.immediatetrade.entity.ImmediateTrade;
import com.barter.domain.trade.immediatetrade.repository.ImmediateTradeRepository;
import com.barter.domain.trade.immediatetrade.service.ImmediateTradeService;

@ExtendWith(MockitoExtension.class)
public class FindAndFindAllTest {
	@Mock
	ImmediateTradeRepository immediateTradeRepository;

	@InjectMocks
	ImmediateTradeService immediateTradeService;

	Member member;
	CreateRegisteredProductReqDto createRegisteredProductReqDto;
	RegisteredProduct registeredProduct;
	CreateImmediateTradeReqDto createImmediateTradeReqDto;
	ImmediateTrade immediateTrade;

	@BeforeEach
	void setUp() {
		member = Member.createBasicMember("test@test.com", "1234", "test");
		createRegisteredProductReqDto = new CreateRegisteredProductReqDto(
			"등록 상품 제목", "등록 상품 설명");

		List<String> images = new ArrayList<>();
		images.add("testImage");

		registeredProduct = RegisteredProduct.create(createRegisteredProductReqDto, member, images);

		createImmediateTradeReqDto = new CreateImmediateTradeReqDto(registeredProduct, "즉시 교환 제목", "즉시 교환 설명");

		immediateTrade = ImmediateTrade.builder()
			.title(createImmediateTradeReqDto.getTitle())
			.description(createImmediateTradeReqDto.getDescription())
			.product(registeredProduct)
			.status(TradeStatus.PENDING)
			.viewCount(0)
			.build();
	}

	@Test
	@DisplayName("즉시 교환 단건 조회")
	void test() {
		// given
		Long id = 1L;
		when(immediateTradeRepository.findById(id)).thenReturn(Optional.ofNullable(immediateTrade));

		// when
		FindImmediateTradeResDto resDto = immediateTradeService.find(id);

		// then
		assertThat(resDto.getTitle()).isEqualTo("즉시 교환 제목");
		assertThat(resDto.getDescription()).isEqualTo("즉시 교환 설명");
		assertThat(resDto.getProductId()).isEqualTo(registeredProduct.getId());
		verify(immediateTradeRepository).findById(id);
	}

	@Test
	@DisplayName("즉시 교환 다건 조회")
	void testFindImmediateTrades() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		List<ImmediateTrade> immediateTrades = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			immediateTrades.add(
				ImmediateTrade.builder()
					.title("즉시 교환 제목 " + i)
					.description("즉시 교환 설명 " + i)
					.product(registeredProduct)
					.status(TradeStatus.PENDING)
					.viewCount(i)
					.build()
			);
		}

		Page<ImmediateTrade> immediateTradePage = new PageImpl<>(immediateTrades, pageable, 10);

		when(immediateTradeRepository.findAll(pageable)).thenReturn(immediateTradePage);

		// when
		PagedModel<FindImmediateTradeResDto> result = immediateTradeService.findImmediateTrades(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("즉시 교환 제목 1");
		assertThat(result.getContent().get(9).getViewCount()).isEqualTo(10);
		assertThat(result.getContent()).hasSize(10);

		verify(immediateTradeRepository).findAll(pageable);
	}
}
