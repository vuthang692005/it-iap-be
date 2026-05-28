package com.example.it_iap.entity.enums;

import com.example.it_iap.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.example.it_iap.oauth2.userInfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public enum AuthProvider {
    // Đăng ký các nhà cung cấp và class xử lý dữ liệu thô tương ứng
    GOOGLE(GoogleOAuth2UserInfo::new); // tương đương attributes -> new GoogleOAuth2UserInfo(attributes)

    // "Nhà máy" nhận Map dữ liệu từ Provider và trả về Object UserInfo đã chuẩn hóa
    private final Function<Map<String, Object>, OAuth2UserInfo> factory;

    // Hàm tiện ích để thực hiện việc khởi tạo UserInfo
    public OAuth2UserInfo getUserInfo(Map<String, Object> attributes) {
        return factory.apply(attributes);
    }

    // Chuyển đổi String từ Request thành Enum an toàn (không gây crash nếu sai tên)
    public static AuthProvider from(String value) {
        if (value == null) return null;

        try {
            return AuthProvider.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
