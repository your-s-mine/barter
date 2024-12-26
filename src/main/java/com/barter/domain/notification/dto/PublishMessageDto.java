package com.barter.domain.notification.dto;

import com.barter.domain.notification.dto.response.SendEventResDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishMessageDto {

	private String eventName;
	private SendEventResDto data;

	public static PublishMessageDto from(String eventName, SendEventResDto data) {
		return PublishMessageDto.builder()
			.eventName(eventName)
			.data(data)
			.build();
	}
}
