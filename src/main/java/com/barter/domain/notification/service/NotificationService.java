package com.barter.domain.notification.service;

import org.springframework.stereotype.Service;

import com.barter.domain.notification.respository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
}
