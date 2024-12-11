package com.barter.domain.notification.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, SseEmitterRepository {
}
