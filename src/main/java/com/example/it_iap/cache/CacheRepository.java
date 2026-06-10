package com.example.it_iap.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CacheRepository {
    private final StringRedisTemplate redisTemplate;

    // Lưu nội dung vào Redis kèm thời gian hết hạn (TTL)
    public void save(String key ,String value, Duration ttl) {
        redisTemplate.opsForValue()
                .set(key, value, ttl);
    }

    // Lấy giá trị từ Redis (trả về Optional để an toàn, tránh Null)
    public Optional<String> get(String key) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(key)
        );
    }

    // Xóa dữ liệu khỏi Redis
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Kiểm tra dữ liệu có tồn tại/còn hiệu lực không
    public boolean exists(String key) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }
}
