package com.barter.domain.trade.periodtrade.IntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class PeriodTradeSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private String generateJwtToken() {
		return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ3b290YWVwYXJrQG5hdmVyLmNvbSIsImV4cCI6MTczNDU3NzMwNiwiaWF0IjoxNzM0NTczNzA2fQ.0Ya1aQR8QEDBKavbmjGKZcfHCXzrMV2xaWgW-qAmKnQ";  // 실제로 사용하는 JWT 토큰을 반환하도록 수정
	}

	@Test
	public void testMessageResponseTime() throws Exception {

		String jwtToken = generateJwtToken();

		// 메시지 전송 및 응답 시간 측정
		int iterations = 100; // 반복 횟수
		long totalTime = 0;
		long maxTime = Long.MIN_VALUE;  // 최대 시간 초기화
		long minTime = Long.MAX_VALUE;  // 최소 시간 초기화

		for (int i = 0; i < iterations; i++) {
			long startTime = System.currentTimeMillis();

			mockMvc.perform(get("/period-trades/10/suggestion")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + jwtToken) // JWT 토큰을 Authorization 헤더에 추가
			).andExpect(status().isOk());

			long endTime = System.currentTimeMillis();
			long responseTime = endTime - startTime;
			totalTime += responseTime;

			// 최대 시간 갱신
			if (responseTime > maxTime) {
				maxTime = responseTime;
			}

			// 최소 시간 갱신
			if (responseTime < minTime) {
				minTime = responseTime;
			}
		}

		long averageTime = totalTime / iterations;
		System.out.println("TOTAL TIME: " + totalTime + "ms");
		System.out.println("Average Response Time: " + averageTime + " ms");
		System.out.println("Maximum Response Time: " + maxTime + " ms");
		System.out.println("Minimum Response Time: " + minTime + " ms");
	}
}
