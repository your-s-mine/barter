package com.barter.domain.notification.respository;

import java.util.List;

import com.barter.domain.notification.entity.Notification;

public interface NotificationJdbcRepository {

	void bulkInsert(List<Notification> notifications);
}
