package com.example.it_iap.dto.auth.response;

import com.example.it_iap.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Nếu null là sẽ không hiển thị ở response
public class RoleResponse {
    private Set<Role> roles;
    private boolean enable2fa;
}
