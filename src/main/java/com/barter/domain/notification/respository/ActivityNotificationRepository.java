package com.barter.domain.notification.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barter.domain.notification.entity.ActivityNotification;

public interface ActivityNotificationRepository extends JpaRepository<ActivityNotification, Long> {
}
