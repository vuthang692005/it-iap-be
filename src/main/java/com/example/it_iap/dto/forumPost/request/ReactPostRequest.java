package com.example.it_iap.dto.forumPost.request;

import com.example.it_iap.entity.enums.ReactionType;
import com.example.it_iap.validator.annotation.EnumValue;

import lombok.Getter;

@Getter
public class ReactPostRequest {
    @EnumValue(enumClass = ReactionType.class, message = "REACTYPE_INVALID")
    private String reactType;
}
