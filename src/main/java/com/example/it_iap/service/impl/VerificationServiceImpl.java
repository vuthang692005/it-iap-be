package com.example.it_iap.service.impl;

import com.example.it_iap.cache.verification.VerificationCacheRepository;
import com.example.it_iap.cache.verification.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final VerificationCacheRepository verificationCacheRepository;
    private final PasswordEncoder passwordEncoder;

    public String createOtp(UUID uuid, VerificationPurpose purpose) {
        String otp = generateOtp();
        verificationCacheRepository.save(
                uuid,
                passwordEncoder.encode(otp),
                purpose
        );
        return otp;
    }

    public boolean verifyOtp(UUID uuid, String inputOtp, VerificationPurpose purpose) {
        String hashedOtp = verificationCacheRepository.get(uuid, purpose)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED));

        boolean matched = passwordEncoder.matches(inputOtp, hashedOtp);

        if (matched) {
            verificationCacheRepository.delete(uuid, purpose);
        }
        return matched;
    }

    public boolean hasActiveOtp(UUID uuid, VerificationPurpose purpose) {
        return verificationCacheRepository.exists(uuid, purpose);
    }

    private String generateOtp() {
        return String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1000000)
        );
    }
}
