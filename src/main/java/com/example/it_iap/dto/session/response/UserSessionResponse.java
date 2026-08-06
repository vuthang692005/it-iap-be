package com.example.it_iap.dto.session.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    private String id;
    private String deviceType;
    private String osName;
    private String browserName;
    private String ipAddress;
    private String location;
    private LocalDateTime lastActiveAt;
    private Boolean isCurrent;
}
