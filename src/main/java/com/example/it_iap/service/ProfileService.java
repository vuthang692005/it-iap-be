package com.example.it_iap.service;

import com.example.it_iap.dto.profile.request.ProfileRequest;
import com.example.it_iap.dto.profile.response.ProfileResponse;
import com.example.it_iap.dto.profile.response.ProfileSummaryResponse;

import java.util.List;

public interface ProfileService {
    ProfileResponse createProfile (ProfileRequest request);
    ProfileResponse updateProfile (ProfileRequest request, long profileId);
    void deleteProfile (long profileId);
    ProfileResponse getProfile (long profileId);
    List<ProfileSummaryResponse> getAllProfiles ();
}
