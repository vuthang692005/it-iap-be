package com.example.it_iap.service.impl;

import com.example.it_iap.dto.profile.request.ProfileRequest;
import com.example.it_iap.dto.profile.response.ProfileResponse;
import com.example.it_iap.dto.profile.response.ProfileSummaryResponse;
import com.example.it_iap.entity.Profile;
import com.example.it_iap.entity.User;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.ProfileRepository;
import com.example.it_iap.service.ProfileService;
import com.example.it_iap.service.UserService;
import com.example.it_iap.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;

    @Transactional
    public ProfileResponse createProfile (ProfileRequest request){
        User currentUser = userService.getCurrentUser();

        Profile profile = new Profile();
        profile.setUser(currentUser);

        mapRequestToProfile(request, profile);

        profile = profileRepository.save(profile);

        return buildProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile (ProfileRequest request, long profileId){
        Profile profile = getValidProfileAndCheckAccess(profileId);

        mapRequestToProfile(request, profile);

        profile = profileRepository.save(profile);

        return buildProfileResponse(profile);
    }

    @Transactional
    public void deleteProfile (long profileId){
        Profile profile = getValidProfileAndCheckAccess(profileId);

        profile.setDeletedAt(LocalDateTime.now());

        profileRepository.save(profile);
    }

    public ProfileResponse getProfile (long profileId){
        Profile profile = getValidProfileAndCheckAccess(profileId);

        return buildProfileResponse(profile);
    }

    public List<ProfileSummaryResponse> getAllProfiles (){
        UUID userId = SecurityUtils.getCurrentUserId();
        List<Profile> activeProfiles = profileRepository.findAllByUserIdAndDeletedAtIsNull(userId);

        return activeProfiles.stream()
                .map(profile -> new ProfileSummaryResponse(
                        profile.getId(),
                        profile.getTitle()
                ))
                .toList();
    }

    // Hàm chung để tìm Profile và kiểm tra xem User hiện tại có quyền thao tác không.
    private Profile getValidProfileAndCheckAccess(long profileId) {
        Profile profile = profileRepository.findWithUserByIdAndDeletedAtIsNull(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        UUID userId = profile.getUser().getId();
        UUID currentId = SecurityUtils.getCurrentUserId();

        if (!userId.equals(currentId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        return profile;
    }

    // Hàm chung để đổ dữ liệu từ Request vào Entity Profile.
    private void mapRequestToProfile(ProfileRequest request, Profile profile) {
        profile.setTitle(request.getTitle());
        profile.setTargetLevel(request.getTargetLevel());
        profile.setTargetPosition(request.getTargetPosition());
        profile.setResumeData(request.getResumeData());
    }

    // Hàm chung để map từ Entity sang Response DTO.
    private ProfileResponse buildProfileResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getTitle(),
                profile.getTargetPosition(),
                profile.getTargetLevel(),
                profile.getResumeData()
        );
    }
}
