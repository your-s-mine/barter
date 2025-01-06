package com.barter.lock.repository;

public interface JdbcLockRepository {
	Long getLock(String key, int timeoutSeconds);

	void releaseLock(String key);
}
