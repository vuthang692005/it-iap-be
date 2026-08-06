package com.example.it_iap.dto;

import java.util.List;

public record ForumPostSliceResponse<T>(
    List<T> posts,
    boolean hasNext
) {}