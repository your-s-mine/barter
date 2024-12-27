package com.barter.domain.search.util;

import org.springframework.stereotype.Component;

@Component
public class DistanceCalculator {

	private static final double EARTH_RADIUS = 6371; // 지구 반지름 (km)

	public Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		// 위도와 경도를 라디안으로 변환
		double lat1Rad = Math.toRadians(lat1);
		double lon1Rad = Math.toRadians(lon1);
		double lat2Rad = Math.toRadians(lat2);
		double lon2Rad = Math.toRadians(lon2);

		// 위도와 경도의 차이
		double deltaLat = lat2Rad - lat1Rad;
		double deltaLon = lon2Rad - lon1Rad;

		// Haversine 공식
		double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
			Math.cos(lat1Rad) * Math.cos(lat2Rad) *
				Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		// 거리 계산
		return EARTH_RADIUS * c;
	}
}
