package com.barter.domain.search;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.barter.domain.search.repository.SearchKeywordRepository;
import com.barter.domain.search.service.SearchService;

@SpringBootTest
public class SearchIntegrationTest {

	@Autowired
	SearchService searchService;
	@Autowired
	SearchKeywordRepository searchKeywordRepository;

	@Test
	@DisplayName("동시성 제어 테스트 - 다수의 사용자가 등록되지 않은 같은 검색어로 검색")
	void test() {

		String word = "sameWord";

		final int numberOfThreads = 2;

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

		// 모든 스레드가 시작할 때까지 대기하는 CyclicBarrier 설정
		CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					barrier.await();

					searchService.searchKeywordAndFindTrades(word);

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Long savedCount = searchKeywordRepository.countByword(word);
		assertThat(savedCount).isEqualTo(1);
	}
}
