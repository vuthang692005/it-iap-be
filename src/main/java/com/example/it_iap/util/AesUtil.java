package com.example.it_iap.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class AesUtil {

    private final String algorithm = "AES";
    private final String transformation = "AES/GCM/NoPadding";
    private final int ivLength = 12;
    private final int tagLength = 128;

    private final String key;

    public AesUtil(@Value("${spring.security.aes.key}") String key) {
        this.key = key;
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[ivLength];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            SecretKey secretKey = createKey();

            Cipher cipher = Cipher.getInstance(transformation);
            GCMParameterSpec spec = new GCMParameterSpec(tagLength, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Encrypt failed. Reason: " + e.getMessage(), e);
        }
    }

    public String decrypt(String cipherText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[ivLength];
            buffer.get(iv);

            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            SecretKey secretKey = createKey();

            Cipher cipher = Cipher.getInstance(transformation);
            GCMParameterSpec spec = new GCMParameterSpec(tagLength, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Decrypt failed. Reason: " + e.getMessage(), e);
        }
    }

    private SecretKey createKey() {
        if (key == null) {
            throw new IllegalStateException("AES key has not been initialized properly.");
        }

        byte[] keyBytes = HexFormat.of().parseHex(key);

        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 16, 24 or 32 bytes.");
        }

        return new SecretKeySpec(keyBytes, algorithm);
    }
}