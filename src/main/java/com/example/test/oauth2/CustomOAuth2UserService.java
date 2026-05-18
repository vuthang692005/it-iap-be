package com.example.test.oauth2;

import com.example.test.entity.User;
import com.example.test.entity.UserOauth2Account;
import com.example.test.entity.enums.AuthProvider;
import com.example.test.oauth2.userInfo.OAuth2UserInfo;
import com.example.test.repository.UserOauth2AccountRepository;
import com.example.test.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

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

        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        AuthProvider authProvider = AuthProvider.from(provider);
        OAuth2UserInfo oAuth2UserInfo = authProvider.getUserInfo(attributes);

        Optional<UserOauth2Account> userOauth2Account =
                userOauth2AccountRepository.findWithUserAndRolesByProviderAndProviderId(authProvider, oAuth2UserInfo.getId());

        User user = null;

        if(userOauth2Account.isPresent()){
            user = userOauth2Account.get().getUser();
        }
        else{
            user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(oAuth2UserInfo.getEmail());
                        newUser.setFullName(oAuth2UserInfo.getName());
                        return newUser;
                    });

            UserOauth2Account newAccount = new UserOauth2Account();
            newAccount.setProvider(authProvider);
            newAccount.setProviderId(oAuth2UserInfo.getId());
            newAccount.setUser(user);

            user.setUserOauth2Accounts(new ArrayList<>());
            user.getUserOauth2Accounts().add(newAccount);

            user = userRepository.save(user);
        }

        return new CustomOAuth2User(user);
    }
}
