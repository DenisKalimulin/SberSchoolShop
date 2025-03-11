package ru.kalimulin.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(clazz.cast(value));
        } catch (ClassCastException e) {
            return Optional.empty(); // Возвращаем пустой Optional при ошибке приведения типов
        }
    }

    @Override
    public <T> void put(String key, T value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(1));
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }
}