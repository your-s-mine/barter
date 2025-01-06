package com.barter.lock;

import java.util.function.Supplier;

public interface DistributeLockService {
	<T> T process(String rockName, int timeoutSeconds, Supplier<T> supplier) throws Throwable;
}
