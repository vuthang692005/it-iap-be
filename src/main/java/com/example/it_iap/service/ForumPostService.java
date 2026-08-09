package com.example.it_iap.service;

import com.example.it_iap.dto.forumPost.response.ForumPostSliceResponse;
import com.example.it_iap.dto.forumPost.request.ReactPostRequest;
import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;
import com.example.it_iap.dto.forumPost.response.StreakLeaderBoardResponse;

import java.util.List;

public interface ForumPostService {

    void shareStreakPost();

    void shareGradePost(Long profileId);

    ForumPostSliceResponse<GetForumPostDTO> getPosts(int page, int seed);

    ForumPostSliceResponse<GetForumPostDTO> getMyPosts(int page, Boolean visible);

    void changePostVisible(Long postId);

    GetForumPostDTO reactPost(Long postId, ReactPostRequest request);

    List<StreakLeaderBoardResponse> getStreakLeaderBoard();

    void deleteForumPost(Long forumPostId);
}
