package com.barter.domain.notification.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.barter.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query(value = "SELECT n FROM Notification AS n WHERE n.notificationType = 'ACTIVITY'")
	Page<Notification> findAllActivityNotification(Pageable pageable);
}
