package com.barter.lock;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.barter.lock.repository.JdbcLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MySqlLockService implements DistributeLockService {

	private final JdbcLockRepository jdbcLockRepository;

	public <T> T process(String rockName, int timeoutSeconds, Supplier<T> supplier) throws Throwable {
		try {
			jdbcLockRepository.getLock(rockName, timeoutSeconds);
			return supplier.get();
		} finally {
			jdbcLockRepository.releaseLock(rockName);
		}
	}
}
