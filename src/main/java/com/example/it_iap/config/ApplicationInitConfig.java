package com.example.it_iap.config;

import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@Slf4j(topic = "ApplicationInitConfig")
@RequiredArgsConstructor
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username:vumitha2005@gmail.com}")
    private String email;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(
            UserRepository userRepository){
        return args -> {
            if(!userRepository.existsByEmail("vumitha2005@gmail.com")){
                Set<Role> roles = new HashSet<>();
                roles.add(Role.ADMIN);
                roles.add(Role.USER);

                User user = new User();
                user.setRoles(roles);
                user.setPassword(passwordEncoder.encode("admin12345"));
                user.setFullName("admin");
                user.setEmail(email);
                user.setVerifyEmail(true);

                userRepository.save(user);

                log.info("Người dùng admin đã được tạo với email và mật khẩu mặc định: {} và admin12345, vui lòng đổi mật khẩu", email);
            }
        };
    }
}
