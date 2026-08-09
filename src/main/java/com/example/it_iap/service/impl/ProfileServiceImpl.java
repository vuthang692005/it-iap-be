package com.example.it_iap.service.impl;

import com.example.it_iap.dto.profile.request.ProfileRequest;
import com.example.it_iap.dto.profile.request.UpdateProfileRequest;
import com.example.it_iap.dto.profile.response.ProfileResponse;
import com.example.it_iap.dto.profile.response.ProfileSummaryResponse;
import com.example.it_iap.entity.Profile;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.entity.enums.UserActionType;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.ProfileRepository;
import com.example.it_iap.service.ProfileService;
import com.example.it_iap.service.UserActivityService;
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
    private final UserActivityService userActivityService;

    @Transactional
    public ProfileResponse createProfile (ProfileRequest request){
        User currentUser = userService.getCurrentUser();

        int currentProfiles = profileRepository.countByUserIdAndDeletedAtIsNull(currentUser.getId());
        int maxProfiles = currentUser.getActiveTier().getMaxProfiles();

        if (currentProfiles >= maxProfiles) {
            throw new AppException(ErrorCode.PROFILE_LIMIT_EXCEEDED);
        }

        Profile profile = new Profile();
        profile.setUser(currentUser);

        mapRequestToProfile(request, profile);

        profile = profileRepository.save(profile);

        return buildProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile (UpdateProfileRequest request, long profileId){
        Profile profile = getValidProfileAndCheckAccess(profileId);

        profile.setTitle(request.getTitle());
        profile.setResumeData(request.getResumeData());

        profile = profileRepository.save(profile);

        return buildProfileResponse(profile);
    }

    @Transactional
    public void deleteProfile (long profileId){
        Profile profile = getValidProfileAndCheckAccess(profileId);

        profile.setDeletedAt(LocalDateTime.now());

        profileRepository.save(profile);

        User user = userService.getCurrentUser();
        userActivityService.logActivity(UserActionType.DELETE_PROFILE, "Xóa hồ sơ: " + profile.getTitle(), user);
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
    public Profile getValidProfileAndCheckAccess(long profileId) {
        Profile profile = profileRepository.findWithUserByIdAndDeletedAtIsNull(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        User user = profile.getUser();
        UUID userId = user.getId();
        UUID currentId = SecurityUtils.getCurrentUserId();

        if (!userId.equals(currentId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        int maxProfiles = user.getActiveTier().getMaxProfiles();
        int profileRank = profileRepository.countByUserIdAndIdLessThanEqualAndDeletedAtIsNull(userId, profileId);

        if (profileRank > maxProfiles) {
            throw new AppException(ErrorCode.PROFILE_LOCKED_DUE_TO_DOWNGRADE);
        }

        return profile;
    }

    // Hàm chung để đổ dữ liệu từ Request vào Entity Profile.
    private void mapRequestToProfile(ProfileRequest request, Profile profile) {
        TargetPosition targetPosition = TargetPosition.fromString(request.getTargetPosition());
        TargetLevel targetLevel = TargetLevel.fromString(request.getTargetLevel());

        profile.setTitle(request.getTitle());
        profile.setTargetLevel(targetLevel);
        profile.setTargetPosition(targetPosition);
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
