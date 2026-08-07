package com.example.it_iap.service;

import com.example.it_iap.dto.forumPost.response.ForumPostSliceResponse;
import com.example.it_iap.dto.forumPost.request.ReactPostRequest;
import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;

public interface ForumPostService {

    void shareStreakPost();

    void shareGradePost();

    ForumPostSliceResponse<GetForumPostDTO> getPosts(int page, int seed);

    ForumPostSliceResponse<GetForumPostDTO> getMyPosts(int page);

    void changePostVisible(Long postId);

    GetForumPostDTO reactPost(Long postId, ReactPostRequest request);
    
}
