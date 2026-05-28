package com.example.it_iap.oauth2;

import com.example.it_iap.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    // Đối tượng quan trọng nhất: Chứa toàn bộ thông tin người dùng từ Database
    private final User user;

    // Triển khai cho đúng Interface: Không dùng đến vì thông tin đã nằm trong object 'user'.
    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }

    // Triển khai cho đúng Interface: Không dùng đến vì thông tin đã nằm trong object 'user'.
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    // Triển khai cho đúng Interface: Không dùng đến vì thông tin đã nằm trong object 'user'.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
