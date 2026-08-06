package com.example.it_iap.service;

import com.example.it_iap.dto.ForumPostSliceResponse;
import com.example.it_iap.dto.forumPost.request.ReactPostRequest;
import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;

import jakarta.validation.Valid;

public interface ForumPostService {

    void shareStreakPost();

    void shareGradePost();

    ForumPostSliceResponse<GetForumPostDTO> getPosts(int page, int seed);

    ForumPostSliceResponse<GetForumPostDTO> getMyPosts(int page);

    void changePostVisible(Long postId);

    GetForumPostDTO reactPost(Long postId, ReactPostRequest request);
    
}
