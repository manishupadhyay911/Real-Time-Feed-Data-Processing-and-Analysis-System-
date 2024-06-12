package com.example.finaldemo.utility.cache.jedis;

import com.example.finaldemo.utility.cache.CacheUtility;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Objects;
import java.util.Set;
@Component
public class JedisCacheUtility implements CacheUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisCacheUtility.class);
    private static final int CACHE_TTL = 3600;

    private final JedisPool jedisPool;

    /**
     * <li> MaxTotal: This controls the max number of connections that can be created at a given time. If not set, the default value is 8. Set this
     * parameter based on the number of HTTP threads of the web container and reserved connections. </li>
     * <li> MaxIdle: This is the max number of connections that can be idle in the pool without being immediately evicted (closed). If not set, the
     * default value is 8. It's recommended to keep this value same as maxTotal to help avoid connection ramp-up costs.  If a connection is idle
     * for a long time, it will still be evicted until the idle connection count hits minIdle (described below). </li>
     * <li> MinIdle: This is the number of "warm" connections (e.g. ready for immediate use) that remain in the pool even when load has reduced. If
     * not set, the default is 0. In performance sensitive scenarios it's recommended to keep this parameter to the value of maxIdle to prevent the
     * impact caused by frequent connection quantity changes. </li>
     * <li> TestWhileIdle: This indicates whether to use the ping command to monitor the connection validity during idle resource monitoring,
     * any invalid detected connections will be destroyed. The default value is false. </li>
     * <li> TimeBetweenEvictionRuns: Time interval for detecting and validating idle connections. A periodic PING is sent to validate the
     * connection, if connection becomes invalid it will be released based on the eviction policy. </li>
     *
     */
    public JedisCacheUtility() {

        this.jedisPool = new JedisPool("127.0.0.1", 6379);

    }

    /**
     * Returns true if connection with Redis server is established for given host and port.
     *
     * @return Boolean value true indicating connection is established successfully, false otherwise
     */
    public boolean ping() {
        long startTs = System.nanoTime();
        try (Jedis jedis = this.getJedisResource()) {
            boolean pong = Objects.equals(jedis.ping(), "PONG");
            LOGGER.info("Received cache response in {} ms.", (System.nanoTime() - startTs) / 1e6);
            return pong;
        } catch (JedisException exception) {
            return false;
        }
    }

    /**
     * Returns true if given key exists in redis cache, false otherwise.
     *
     * @param key Key to check if it exists
     * @return Boolean value true indicating value exists in redis cache, false otherwise
     */
    public boolean exists(String key) {
        try (Jedis jedis = this.getJedisResource()) {
            return jedis.exists(key);
        }
    }

    /**
     * Returns value from redis for a give key.
     *
     * @param key Key for which you want to get cached value
     * @return The cached value for the above key if found, null otherwise
     */
    @Override
    public @Nullable String getValue(String key) {
        try (Jedis jedis = this.getJedisResource()) {
            String value = jedis.get(key);
            LOGGER.trace("Fetching value: {} for key: {} from Redis", value, key);
            return value;
        }
    }

    /**
     * Sets key: value pair in Redis cache.
     *
     * @param key   Key for which you want to set cache value
     * @param value Value for above key that you want to save in cache
     */
    @Override
    public void putValue(String key, String value) {
        this.putValue(key, value, CACHE_TTL);
    }

    /**
     * Sets key: value pair in Redis cache with given expire time for cache.
     *
     * @param key        Key for which you want to set cache value
     * @param value      Value for above key that you want to save in cache
     * @param expireTime Time units (seconds) after which the cache will expire
     */
    @Override
    public void putValue(String key, String value, Integer expireTime) {
        try (Jedis jedis = this.getJedisResource()) {
            jedis.setex(key, expireTime, value);
            LOGGER.trace("Setting value: {} for key: {} with expiry time: {} seconds to Redis", value, key, expireTime);
        }
    }

    /**
     * Sets key: value pair in Redis cache if no such key exists.
     *
     * @param key   Key for which you want to set cache value
     * @param value Value for above key that you want to save in cache
     */
    @Override
    public boolean putValueIfAbsent(String key, String value) {
        return this.putValueIfAbsent(key, value, CACHE_TTL);
    }

    /**
     * Sets key: value pair in Redis cache with given expiry time if no cache exists for given key.
     *
     * @param key        Key for which you want to set cache value
     * @param value      Value for above key that you want to save in cache
     * @param expireTime Time units (seconds) after which the cache will expire
     * @return A boolean value indicating cache was set, false otherwise
     */
    @Override
    public boolean putValueIfAbsent(String key, String value, Integer expireTime) {
        try (Jedis jedis = this.getJedisResource()) {
            boolean isValueSet;
            isValueSet = jedis.setnx(key, value) == 1L;

            if (isValueSet) {
                jedis.expire(key, expireTime);
                LOGGER.trace("Setting value: {} for key: {} with expiry time: {} seconds to Redis", value, key, expireTime);
            } else {
                LOGGER.trace("Skipped setting value: {} for key: {}. Key already exists", value, key);
            }
            return isValueSet;
        }
    }

    /**
     * Clears all cached values from redis with key starting with 'key' parameter.
     *
     * @param key Keys that will be cleared for key*
     */
    @Override
    public void clearCache(String key) {
        LOGGER.trace("Clearing redis cache with keys starts with: {}", key);
        try (Jedis jedis = this.getJedisResource()) {
            Set<String> keys = jedis.keys(key + "*");
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
            LOGGER.trace("Successfully cleared keys: {}", keys);
        }
    }

    public void shutdown() {
        try {
            LOGGER.info("Shutting down Jedis connection pool.");
            this.jedisPool.close();
        } catch (Exception e) {
            LOGGER.error("Unable to close Jedis pool", e);
        }
    }

    private Jedis getJedisResource() {
        return this.jedisPool.getResource();
    }
}
