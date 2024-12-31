package com.barter.domain.notification.respository;

import java.util.List;

import com.barter.domain.notification.entity.Notification;

public interface FlushNotificationRepository {

	void bulkInsert(List<Notification> notifications);
}
