package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.service.VerificationService;
import com.example.it_iap.util.AesUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final CacheRepository cacheRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesUtil aesUtil;

    public String createSecret(String secret, String cacheKey, VerificationPurpose purpose) {
        String key = purpose.getPrefix() + cacheKey;
        Duration ttl = purpose.getTtl();
        String secretEncrypt = aesUtil.encrypt(secret);
        cacheRepository.save(
                key,
                secretEncrypt,
                ttl);
        return secretEncrypt;
    }

    public String createOtp(String cacheKey, VerificationPurpose purpose) {
        String otp = generateOtp();
        String key = purpose.getPrefix() + cacheKey;
        Duration ttl = purpose.getTtl();

        cacheRepository.save(
                key,
                passwordEncoder.encode(otp),
                ttl);
        return otp;
    }

    public boolean verifyOtp(String cacheKey, String inputOtp, VerificationPurpose purpose) {
        String key = purpose.getPrefix() + cacheKey;
        String hashedOtp = cacheRepository.get(key)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED));

        boolean matched = passwordEncoder.matches(inputOtp, hashedOtp);

        if (matched) {
            cacheRepository.delete(key);
        }
        return matched;
    }

    public boolean hasActiveOtp(String cacheKey, VerificationPurpose purpose) {
        String key = purpose.getPrefix() + cacheKey;
        return cacheRepository.exists(key);
    }

    private String generateOtp() {
        return String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
