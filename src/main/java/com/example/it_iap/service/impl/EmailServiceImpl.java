package com.example.it_iap.service.impl;

import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EmailServiceImpl")
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerifyOtp(String to, String fullName, String otp, VerificationPurpose purpose) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("otp", otp);
            context.setVariable("dateNow", LocalDate.now().format(dateFormatter));
            context.setVariable("timeNow", LocalTime.now().format(timeFormatter));
            context.setVariable("ttlMinutes", purpose.getTtl().toMinutes());

            String html = templateEngine.process(
                    purpose.getTemplateName(),
                    context
            );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);

            try {
                helper.setFrom(fromEmail, "IT-IAP");
            } catch (UnsupportedEncodingException e) {
                helper.setFrom(fromEmail);
            }

            helper.setSubject(purpose.getEmailSubject());
            helper.setText(html, true);

            javaMailSender.send(message);

        } catch (MessagingException | MailException e) {
            log.error("Gửi mail xác thực OTP thất bại. Email={}, Purpose={}", to, purpose.name(), e);
        }
    }
}
