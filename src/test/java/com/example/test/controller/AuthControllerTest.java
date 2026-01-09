package com.example.test.controller;

import com.example.test.dto.auth.request.LoginRequest;
import com.example.test.dto.auth.request.RefreshTokenRequest;
import com.example.test.dto.auth.request.RegisterRequest;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(AuthController.class)
@TestPropertySource("/test.yaml")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private TokenResponse tokenResponse;
    private RegisterRequest registerRequest;
    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void initData(){
        loginRequest = LoginRequest.builder()
                .identifier("vumitha2005@gmail.com")
                .password("12345678")
                .build();

        tokenResponse = TokenResponse.builder()
                .accessToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0ZXN0IiwiaZWZyZXNoVG9rZW4iOdwd4")
                .refreshToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0ZXN0IiwiaXNSZWZyZXNoVG9rZW4iO")
                .build();

        registerRequest = RegisterRequest.builder()
                .email("vumitha2005@gmail.com")
                .password("12345678")
                .username("thang12345678")
                .fullName("Vũ Minh Thắng")
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MioIJ0ZXN0IiwiaXNSZWZyZXMoVD9rZW4iO")
                .build();
    }

    @Test
    void login_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(loginRequest);

        Mockito.when(authService.login(any()))
                        .thenReturn(tokenResponse);

        mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(request)
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data.accessToken")
                        .value("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0ZXN0IiwiaZWZyZXNoVG9rZW4iOdwd4"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.refreshToken")
                        .value("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0ZXN0IiwiaXNSZWZyZXNoVG9rZW4iO"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void login_identifierInvalid_fail(String identifier) throws Exception {
        loginRequest.setIdentifier(identifier);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "1234567", // password < 8 kí tự
            "123456789012345678901234567890123456789012345678901" // password > 50 kí tự
    })
    void login_passwordInvalid_fail(String password) throws Exception {
        loginRequest.setPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003));
    }

    @Test
    void register_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "1234567890123456789012345678901") // user name > 30 kí tự
    void register_usernameInvalid_fail(String username) throws Exception {
        registerRequest.setUsername(username);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1004));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "hello@sss.ff") // email ko có @gmail.com ở cuối
    void register_emailInvalid_fail(String email) throws Exception {
        registerRequest.setEmail(email);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1005));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "1234567", // password < 8 kí tự
            "123456789012345678901234567890123456789012345678901" // password > 50 kí tự
    })
    void register_passwordInvalid_fail(String password) throws Exception {
        registerRequest.setPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "1234567890123456789012345678901") // fullName > 30 kí tự
    void register_fullNameInvalid_fail(String fullName) throws Exception {
        registerRequest.setFullName(fullName);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1006));
    }

    @Test
    void refreshToken_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(refreshTokenRequest);

        Mockito.when(authService.refreshToken(any())).thenReturn(tokenResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data.accessToken")
                        .value("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0ZXN0IiwiaZWZyZXNoVG9rZW4iOdwd4"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.refreshToken")
                        .value("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0ZXN0IiwiaXNSZWZyZXNoVG9rZW4iO"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void refreshToken_refreshTokenInvalid_fail(String refreshToken) throws Exception {
        refreshTokenRequest.setRefreshToken(refreshToken);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(refreshTokenRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(4002));
    }
}
