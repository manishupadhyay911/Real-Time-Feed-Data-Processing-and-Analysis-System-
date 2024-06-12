package com.example.finaldemo.utility.mutex;

public interface Mutex {

    boolean acquireLock(String key, String requestId);

    boolean acquireLockWithoutAttempts(String key, String requestId);

    boolean releaseLock(String key, String requestId);
}
