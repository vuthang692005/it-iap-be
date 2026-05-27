package com.example.it_iap.service.impl;

import com.example.it_iap.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EmailServiceImpl")
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendVerifyOtp(String to, String fullName, String otp, long ttlMinutes) {

        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("otp", otp);
            context.setVariable("ttlMinutes", ttlMinutes);

            String html = templateEngine.process(
                    "email/verify-otp",
                    context
            );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác thực tài khoản");
            helper.setText(html, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            log.error("Gửi mail xác thực OTP thất bại. Email={}", to, e);
        }
    }
}
