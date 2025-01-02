package com.barter.domain.notification.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barter.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationJdbcRepository {

	@Query(value = "SELECT n FROM Notification AS n WHERE n.notificationType = 'ACTIVITY' AND n.memberId = :memberId")
	Page<Notification> findAllActivityNotification(Pageable pageable, @Param("memberId") Long memberId);

	@Query(value = "SELECT n FROM Notification AS n WHERE n.notificationType = 'KEYWORD' AND n.memberId = :memberId")
	Page<Notification> findAllKeywordNotification(Pageable pageable, @Param("memberId") Long memberId);
}
