package com.barter.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedissonLockService {

	private final RedissonClient redissonClient;

	public <T> T process(String methodName, Long tradeId, int timeoutSeconds, Supplier<T> supplier) throws Throwable {
		final RLock lock = redissonClient.getLock(String.format(methodName, tradeId));
		try {
			boolean available = lock.tryLock(timeoutSeconds, TimeUnit.SECONDS);
			if (!available) {
				throw new IllegalArgumentException("Lock 획득에 실패하였습니다.");
			}
			return supplier.get();
		} finally {
			lock.unlock();
		}
	}
}
