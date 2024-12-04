package com.barter.domain.notification.controller;

import org.springframework.web.bind.annotation.RestController;

import com.barter.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;
}
