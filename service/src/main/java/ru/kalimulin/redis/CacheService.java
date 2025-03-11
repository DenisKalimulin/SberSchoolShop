package ru.kalimulin.redis;

import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, Class<T> clazz);
    <T> void put(String key, T value);
    void evict(String key);
}