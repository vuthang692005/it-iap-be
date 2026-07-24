package com.example.it_iap.dto;

import java.util.List;

public record NotificationSliceResponse<T>(
    List<T> notifications,
    int unread,
    boolean hasNext
) {}