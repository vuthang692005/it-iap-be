package com.example.it_iap.dto.forumPost.response;

import java.util.List;

public record ForumPostSliceResponse<T>(
    List<T> posts,
    boolean hasNext
) {}