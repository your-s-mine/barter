package com.barter.lock.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcLockRepositoryImpl implements JdbcLockRepository {
	private final JdbcTemplate jdbcTemplate;

	@Override
	public Long getLock(String key, int timeoutSeconds) {
		String sql = "select GET_LOCK(?, ?)";
		return jdbcTemplate.queryForObject(sql, Long.class, key, timeoutSeconds);
	}

	@Override
	public void releaseLock(String key) {
		String sql = "select RELEASE_LOCK(?)";
		jdbcTemplate.queryForObject(sql, Long.class, key);
	}
}
