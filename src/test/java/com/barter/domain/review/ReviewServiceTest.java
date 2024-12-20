package com.barter.domain.review;

import com.barter.domain.auth.dto.VerifiedMember;
import com.barter.domain.member.entity.Member;
import com.barter.domain.member.repository.MemberRepository;
import com.barter.domain.product.entity.TradeProduct;
import com.barter.domain.product.repository.TradeProductRepository;
import com.barter.domain.review.dto.ReviewResponseDto;
import com.barter.domain.review.entity.Review;
import com.barter.domain.review.repository.ReviewRepository;
import com.barter.domain.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TradeProductRepository tradeProductRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_WhenValidInput_ShouldReturnReviewResponseDto() {
        // given
        VerifiedMember verifiedMember = VerifiedMember.builder()
                .id(1L)
                .email("reviewer@email.com")
                .build();

        Long revieweeId = 2L;
        Long tradeProductId = 3L;
        String content = "Excellent product and great transaction!";
        Double score = 5.0;

        when(memberRepository.findById(verifiedMember.getId())).thenReturn(Optional.of(mock(Member.class)));
        when(memberRepository.findById(revieweeId)).thenReturn(Optional.of(mock(Member.class)));
        when(tradeProductRepository.findById(tradeProductId)).thenReturn(Optional.of(mock(TradeProduct.class)));

        Review review = Review.builder()
                .reviewerId(verifiedMember.getId())
                .revieweeId(revieweeId)
                .tradeProductId(tradeProductId)
                .content(content)
                .score(score)
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        ReviewResponseDto responseDto = reviewService.createReview(
                verifiedMember,
                revieweeId,
                tradeProductId,
                content,
                score
        );

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getReviewerId()).isEqualTo(verifiedMember.getId());
        assertThat(responseDto.getRevieweeId()).isEqualTo(revieweeId);
        assertThat(responseDto.getTradeProductId()).isEqualTo(tradeProductId);
        assertThat(responseDto.getContent()).isEqualTo(content);
        assertThat(responseDto.getScore()).isEqualTo(score);

        verify(reviewRepository).save(any(Review.class));
        verify(memberRepository).findById(verifiedMember.getId());
        verify(memberRepository).findById(revieweeId);
        verify(tradeProductRepository).findById(tradeProductId);
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 유효하지 않은 점수")
    void createReview_WhenInvalidScore_ShouldThrowException() {
        // given
        VerifiedMember verifiedMember = VerifiedMember.builder()
                .id(1L)
                .email("reviewer@email.com")
                .build();

        Long revieweeId = 2L;
        Long tradeProductId = 3L;
        String content = "Excellent product and great transaction!";
        Double invalidScore = 6.0; // Invalid score

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(
                verifiedMember,
                revieweeId,
                tradeProductId,
                content,
                invalidScore
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("점수는 1에서 5 사이여야 합니다.");
    }
}
