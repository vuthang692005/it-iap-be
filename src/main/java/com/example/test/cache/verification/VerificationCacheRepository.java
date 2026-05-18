package com.example.test.cache.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VerificationCacheRepository {
    private final StringRedisTemplate redisTemplate;

    public void save(UUID userId, String hashedOtp, VerificationPurpose purpose) {
        redisTemplate.opsForValue()
                .set(purpose.getPrefix() + userId, hashedOtp, purpose.getTtl());
    }

    public Optional<String> get(UUID userId, VerificationPurpose purpose) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(purpose.getPrefix() + userId)
        );
    }

    public void delete(UUID userId, VerificationPurpose purpose) {
        redisTemplate.delete(purpose.getPrefix() + userId);
    }
}
