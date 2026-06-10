package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final CacheRepository verificationCacheRepository;
    private final PasswordEncoder passwordEncoder;

    public String createOtp(UUID uuid, VerificationPurpose purpose) {
        String otp = generateOtp();
        String key = purpose.getPrefix() + uuid;
        Duration ttl = purpose.getTtl();
        verificationCacheRepository.save(
                key,
                passwordEncoder.encode(otp),
                ttl
        );
        return otp;
    }

    public boolean verifyOtp(UUID uuid, String inputOtp, VerificationPurpose purpose) {
        String key = purpose.getPrefix() + uuid;
        String hashedOtp = verificationCacheRepository.get(key)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED));

        boolean matched = passwordEncoder.matches(inputOtp, hashedOtp);

        if (matched) {
            verificationCacheRepository.delete(key);
        }
        return matched;
    }

    public boolean hasActiveOtp(UUID uuid, VerificationPurpose purpose) {
        String key = purpose.getPrefix() + uuid;
        return verificationCacheRepository.exists(key);
    }

    private String generateOtp() {
        return String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1000000)
        );
    }
}
