package com.example.finaldemo.utility.mutex.cache;

import com.example.finaldemo.utility.cache.CacheUtility;
import com.example.finaldemo.utility.mutex.Mutex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class CacheMutex implements Mutex {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheMutex.class);

    private final Integer lockDurationSeconds;
    private final Integer retryPollTimeMillis;
    private final Integer count;
    private final CacheUtility cache;

    public CacheMutex(CacheUtility cache) {
        this.cache = cache;
        this.lockDurationSeconds = 10;
        this.retryPollTimeMillis = 500;
        this.count = 10;
    }

    /**
     * Acquire lock with key (created using builder) by saving key value pair {key: requestId}} in Redis cache.
     * Lock duration will be set as per {@link #lockDurationSeconds}, if not set DEFAULT_LOCK_DURATION_SECONDS (10 seconds) will be used.
     * If another process has already acquired lock, the implementation will poll every {@link #retryPollTimeMillis} time for {@link #count} times.
     *
     * @param requestId Unique identifier to acquire lock.
     * @return Boolean value true signifying lock acquired, false otherwise.
     */
    @Override
    public boolean acquireLock(String key, String requestId) {
        int attempt = 1;

        while (attempt <= this.count) {
            LOGGER.trace("[Attempt-{}] Attempting to acquire lock with key: {}", attempt, key);

            boolean isSet = this.cache.putValueIfAbsent(key, requestId, this.lockDurationSeconds);
            if (isSet) {
                LOGGER.trace("[Attempt-{}] [Request id: {}] acquired lock with key: {}", attempt, requestId, key);
                return true;
            }

            String cachedRequestId = this.cache.getValue(key);
            if (cachedRequestId != null) {
                LOGGER.trace("[Attempt-{}] Request id: {} has already acquired lock for key: {}", attempt, cachedRequestId, key);

                if (attempt < this.count) {
                    LOGGER.trace("[RequestId-{}] Waiting for {} ms before reattempting to acquire lock with key: {}", requestId,
                        this.retryPollTimeMillis, key);

                    try {
                        Thread.sleep(this.retryPollTimeMillis);
                    } catch (InterruptedException e) {
                        LOGGER.error("[Attempt-{}] Thread interrupted while waiting to poll again.", attempt, e);
                        return false;
                    }
                }
                attempt++;
            }
        }

        LOGGER.trace("Failed to acquire lock after {} attempts, polling every {} ms", this.count, this.retryPollTimeMillis);
        return false;
    }

    /**
     * Acquire lock with key (created using builder) by saving key value pair {key: requestId}} in Redis cache.
     * Lock duration will be set as per {@link #lockDurationSeconds}, if not set DEFAULT_LOCK_DURATION_SECONDS (10 seconds) will be used.
     * If another process has already acquired lock, it will return false.
     *
     * @param requestId Unique identifier to acquire lock.
     * @return Boolean value true signifying lock acquired, false otherwise.
     */
    @Override
    public boolean acquireLockWithoutAttempts(String key, String requestId) {
        return this.cache.putValueIfAbsent(key, requestId, this.lockDurationSeconds);
    }

    /**
     * Release lock with key (created using builder) by removing key value pair {key: requestId}} from Redis cache.
     * Only a process with same request id can release lock, any attempts by other process to release lock will be ignored.
     *
     * @param requestId Unique identifier to release lock.
     * @return Boolean value true signifying lock is released with given request id, false otherwise.
     */
    @Override
    public boolean releaseLock(String key, String requestId) {
        String cachedRequestId = this.cache.getValue(key);

        if (cachedRequestId == null) {
            LOGGER.warn("[Request id: {}] Lock has already expired for key: {}", requestId, key);
            return true;
        }

        if (Objects.equals(requestId, cachedRequestId)) {
            this.cache.clearCache(key);
            LOGGER.trace("[Request id: {}] Released lock for key: {}", requestId, key);
            return true;
        }

        LOGGER.trace("[Request id: %s] Request doesn't have privilege to release lock.");
        return false;
    }
}
