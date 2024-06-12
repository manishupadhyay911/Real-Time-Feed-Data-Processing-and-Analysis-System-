package com.example.finaldemo.utility.cache;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

public interface CacheUtility {
    boolean ping();

    boolean exists(String key);

    @Nullable
    String getValue(String key);

    void putValue(String key, String value);

    void putValue(String key, String value, Integer expireTime);

    boolean putValueIfAbsent(String key, String value);

    boolean putValueIfAbsent(String key, String value, Integer expireTime);

    void clearCache(String key);
}
