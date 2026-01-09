package com.example.test.service;

import com.example.test.dto.auth.request.LoginRequest;
import com.example.test.dto.auth.request.RefreshTokenRequest;
import com.example.test.dto.auth.request.RegisterRequest;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.entity.Permission;
import com.example.test.entity.Role;
import com.example.test.entity.User;
import com.example.test.exception.AppException;
import com.example.test.exception.ErrorCode;
import com.example.test.repository.RoleRepository;
import com.example.test.repository.UserRepository;
import com.example.test.service.impl.AuthServiceImpl;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authServiceImpl;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;


    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private User user;
    private Role role;

    @BeforeEach
    void initData(){
        loginRequest = LoginRequest.builder()
                .identifier("vumitha2005@gmail.com")
                .password("12345678")
                .build();

        registerRequest = RegisterRequest.builder()
                .email("vumitha2005@gmail.com")
                .password("12345678")
                .username("thang12345678")
                .fullName("Vũ Minh Thắng")
                .build();

        Permission permission = Permission.builder()
                .name("USER_CREATE")
                .build();

        role = Role.builder()
                .name("USER")
                .permissions(Set.of(permission))
                .build();

        user = User.builder()
                .id(1)
                .email("vumitha2005@gmail.com")
                .password("12345678")
                .username("thang12345678")
                .fullName("Vũ Minh Thắng")
                .isActive(true)
                .roles(Set.of(role))
                .build();
    }

    @Test
    void login_validRequest_success() throws JOSEException {
        Mockito.when(userRepository.findByUsernameOrEmail(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        ReflectionTestUtils.setField(authServiceImpl, "singerKey", "98313f9d9234f0160eb53c29c11fd411e708bb6186b24fe2298674341cbba7e4");

        TokenResponse response = authServiceImpl.login(loginRequest);

        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void login_userNotExisted_fail() throws JOSEException {
        Mockito.when(userRepository.findByUsernameOrEmail(any()))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.login(loginRequest)
        );

        assertEquals(ErrorCode.UNAUTHENTICATED, ex.getErrorCode());
    }

    @Test
    void login_wrongPassword_fail() throws JOSEException {
        Mockito.when(userRepository.findByUsernameOrEmail(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.login(loginRequest)
        );

        assertEquals(ErrorCode.UNAUTHENTICATED, ex.getErrorCode());
    }

    @Test
    void login_userDisabled_fail() throws JOSEException {
        user.setActive(false);

        Mockito.when(userRepository.findByUsernameOrEmail(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.login(loginRequest)
        );

        assertEquals(ErrorCode.ACCOUNT_DISABLED, ex.getErrorCode());
    }

    @Test
    void register_validRequest_success(){
        Mockito.when(userRepository.existsByUsername(any()))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail(any()))
                .thenReturn(false);

        Mockito.when(roleRepository.findById("USER"))
                .thenReturn(Optional.of(role));

        Mockito.when(passwordEncoder.encode(any()))
                .thenReturn("$encoded");

        assertDoesNotThrow(() -> authServiceImpl.register(registerRequest));
    }

    @Test
    void register_usernameExisted_fail(){
        Mockito.when(userRepository.existsByUsername(any()))
                .thenReturn(true);

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.register(registerRequest)
        );

        assertEquals(ErrorCode.USERNAME_EXISTED, ex.getErrorCode());
    }

    @Test
    void register_emailExisted_fail(){
        Mockito.when(userRepository.existsByUsername(any()))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail(any()))
                .thenReturn(true);

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.register(registerRequest)
        );

        assertEquals(ErrorCode.EMAIL_EXISTED, ex.getErrorCode());
    }

    @Test
    void register_roleUserMissing_fail(){
        Mockito.when(userRepository.existsByUsername(any()))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail(any()))
                .thenReturn(false);

        Mockito.when(roleRepository.findById("USER"))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.register(registerRequest)
        );

        assertEquals(ErrorCode.SYSTEM_ERROR, ex.getErrorCode());
    }

    @Test
    void register_uniqueConflict_fail(){
        Mockito.when(userRepository.existsByUsername(any()))
                .thenReturn(false);

        Mockito.when(userRepository.existsByEmail(any()))
                .thenReturn(false);

        Mockito.when(roleRepository.findById("USER"))
                .thenReturn(Optional.of(role));

        Mockito.when(passwordEncoder.encode(any()))
                .thenReturn("$encoded");

        Mockito.when(userRepository.save(any()))
                .thenThrow(DataIntegrityViolationException.class);

        AppException ex = assertThrows(
                AppException.class,
                () -> authServiceImpl.register(registerRequest)
        );

        assertEquals(ErrorCode.USERNAME_OR_EMAIL_EXISTED, ex.getErrorCode());
    }
}
