package com.example.it_iap.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchUserRequest {
    private String email;
    private String phoneNumber;
    private String fullName;
    private int pages = 1;
}
