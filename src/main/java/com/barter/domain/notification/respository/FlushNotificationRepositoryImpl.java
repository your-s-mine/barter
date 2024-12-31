package com.barter.domain.notification.respository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.barter.domain.notification.entity.Notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FlushNotificationRepositoryImpl implements FlushNotificationRepository {

	private final JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public void bulkInsert(List<Notification> notifications) {
		String sql = "INSERT INTO "
			+ "notifications (created_at, updated_at, is_read, member_id, message, notification_type, trade_id, trade_type)"
			+ " VALUES (now(), now(), ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, notifications, notifications.size(),
			(PreparedStatement ps, Notification notification) -> {
				ps.setBoolean(1, notification.isRead());
				ps.setLong(2, notification.getMemberId());
				ps.setString(3, notification.getMessage());
				ps.setString(4, notification.getNotificationType().name());
				ps.setLong(5, notification.getTradeId());
				ps.setString(6, notification.getTradeType().name());
			});
	}
}
