package com.barter.event.trade;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.barter.common.KeywordHelper;
import com.barter.domain.member.entity.FavoriteKeyword;
import com.barter.domain.member.entity.MemberFavoriteKeyword;
import com.barter.domain.member.repository.FavoriteKeywordRepository;
import com.barter.domain.member.repository.MemberFavoriteKeywordRepository;
import com.barter.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "TradeNotificationEventListener")
@Component
@RequiredArgsConstructor
public class TradeNotificationEventListener {

	private final FavoriteKeywordRepository favoriteKeywordRepository;
	private final MemberFavoriteKeywordRepository memberFavoriteKeywordRepository;
	private final NotificationService notificationService;

	@EventListener
	public void sendNotificationToMember(TradeNotificationEvent event) {
		List<String> keywords = KeywordHelper.extractKeywords(event.getProductName());
		List<FavoriteKeyword> existsKeywords = favoriteKeywordRepository.findByKeywordIn(keywords);
		List<MemberFavoriteKeyword> keywordMembers = memberFavoriteKeywordRepository
			.findByFavoriteKeywordIn(existsKeywords);
		keywordMembers.forEach(member -> {
			// TODO: 알림 전송 개발되면 추가 예정.
		});
	}
}
