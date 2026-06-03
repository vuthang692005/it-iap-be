package com.example.it_iap.dto.auth.response;

import com.example.it_iap.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class RoleResponse {
    private Set<Role> roles;
}
