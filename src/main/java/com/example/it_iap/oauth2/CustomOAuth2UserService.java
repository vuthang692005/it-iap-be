package com.example.it_iap.oauth2;

import com.example.it_iap.entity.User;
import com.example.it_iap.entity.UserOauth2Account;
import com.example.it_iap.entity.enums.AuthProvider;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.oauth2.userInfo.OAuth2UserInfo;
import com.example.it_iap.repository.UserOauth2AccountRepository;
import com.example.it_iap.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final UserOauth2AccountRepository userOauth2AccountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Lấy tên nhà cung cấp (ví dụ: "google") và chuyển thành chữ hoa ("GOOGLE")
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        // Xác định Provider tương ứng từ Enum (nếu không hỗ trợ sẽ trả về null)
        AuthProvider authProvider = AuthProvider.from(provider);

        // Kỹ thuật Factory: Tự động khởi tạo đúng class lấy UserInfo (ví dụ: GoogleOAuth2UserInfo)
        // để bóc tách dữ liệu thô (attributes) thành các trường email, name, avatar chuẩn hóa.
        OAuth2UserInfo oAuth2UserInfo = authProvider.getUserInfo(attributes);

        Optional<UserOauth2Account> userOauth2Account =
                userOauth2AccountRepository.findWithUserByProviderAndProviderId(authProvider, oAuth2UserInfo.getId());

        User user = null;

        // NGHIỆP VỤ LIÊN KẾT TÀI KHOẢN (ACCOUNT LINKING):
        // 1. Nếu Account OAuth2 đã tồn tại -> Lấy thẳng User đang liên kết ra dùng.
        if(userOauth2Account.isPresent()){
            user = userOauth2Account.get().getUser();
        }
        else{
            // 2. Nếu chưa từng đăng nhập bằng Account OAuth2 này:
            // Quét xem email này đã từng đăng ký bằng Form thủ công chưa.
            // - Nếu đã đăng ký bằng Form: Map account OAuth2 này vào User cũ đó luôn (Liên kết tài khoản).
            // - Nếu chưa: Tạo hẳn một User mới tinh.
            user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                    .orElseGet(() -> {
                        Set<Role> roles = new HashSet<>();
                        roles.add(Role.USER);

                        User newUser = new User();
                        newUser.setEmail(oAuth2UserInfo.getEmail());
                        newUser.setFullName(oAuth2UserInfo.getName());
                        newUser.setRoles(roles);
                        return newUser;
                    });

            if(!user.isVerifyEmail()){
                // [BẢO MẬT] Vá lỗ hổng Pre-Account Takeover:
                // Nếu email này từng được đăng ký qua Form nhưng chưa xác thực (có thể do hacker chiếm chỗ trước),
                // ta phải xóa trắng mật khẩu cũ. Hành động này ép tài khoản từ nay chỉ được phép đăng nhập
                // qua nền tảng OAuth2 (Google) này, chặn đứng đường lùi của hacker.
                user.setPassword(null);

                // Mặc định đăng nhập qua Google là email đã xác thực
                // Sau này làm đăng nhập facebook, Github,.... cần xác thực email_verified == true thì mới tin tưởng
                user.setVerifyEmail(true);
            }

            user = userRepository.save(user);

            UserOauth2Account newAccount = new UserOauth2Account();
            newAccount.setProvider(authProvider);
            newAccount.setProviderId(oAuth2UserInfo.getId());
            newAccount.setUser(user);

            userOauth2AccountRepository.save(newAccount);
        }

        return new CustomOAuth2User(user);
    }
}
