package com.example.it_iap.util;

import java.util.Random;

public class RandomReplyIdentifyCode {
    public static String generate() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = rand.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        String randomString = sb.toString();
        return randomString;
    }
}
