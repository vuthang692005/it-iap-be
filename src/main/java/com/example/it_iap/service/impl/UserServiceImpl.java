package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.dto.user.request.*;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.dto.user.response.UserStreakResponse;
import com.example.it_iap.entity.Json.DailyStudyStat;
import com.example.it_iap.entity.Notification;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.NotificationType;
import com.example.it_iap.enums.UploadFolder;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.entity.enums.UserActionType;
import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.NotificationRepository;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.CloudinaryService;
import com.example.it_iap.service.EmailService;
import com.example.it_iap.service.UserActivityService;
import com.example.it_iap.service.UserService;
import com.example.it_iap.service.VerificationService;
import com.example.it_iap.util.RandomReplyIdentifyCode;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String PENDING_EMAIL_PREFIX = "PENDING_EMAIL:";
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final CacheRepository cacheRepository;
    private final NotificationRepository notificationRepository;
    private final UserActivityService userActivityService;

    private static final Set<Integer> STREAK_MILESTONES = Set.of(3, 7, 14, 20, 30, 40, 60, 75, 90);

    @Value("${app.user.default-password}")
    private String defaultPassword;

    public User getCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        String oldPassword = request.getOldPassword();
        String encodeNewPassword = passwordEncoder.encode(request.getNewPassword());
        if (user.getPassword() == null) {
            throw new AppException(ErrorCode.OLD_PASSWORD_MISMATCH);
        }
        boolean match = passwordEncoder.matches(oldPassword, user.getPassword());
        if (match) {
            user.setPassword(encodeNewPassword);
            userRepository.save(user);
            userActivityService.logActivity(UserActionType.CHANGE_PASSWORD, "Đổi mật khẩu thành công", user);
        } else {
            throw new AppException(ErrorCode.OLD_PASSWORD_MISMATCH);
        }
    }

    public Page<UserResponse> searchUser(SearchUserRequest request) {
        int page = Math.max(0, request.getPages() - 1);
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> users = userRepository.searchUsers(
                request.getEmail(),
                request.getFullName(),
                request.getPhoneNumber(),
                pageable);

        return users.map(this::buildProfileResponse);
    }

    public UserResponse createUser(CreateUserRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setVerifyEmail(true);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        return buildProfileResponse(userRepository.save(user));
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatarUrl(request.getAvatarUrl());
        boolean isActive = request.isActive();
        user.setActive(isActive);
        // Nếu isActive là false thì đặt thời gian deleteAt
        if (!isActive) {
            user.setDeletedAt(LocalDateTime.now());
        } else {
            user.setDeletedAt(null); // Bắt buộc phải clear khi mở khóa lại
        }

        return buildProfileResponse(userRepository.save(user));
    }

    public UserResponse getInfo() {
        User user = getCurrentUser();
        return buildProfileResponse(user);
    }


    public UserResponse updateInfo(UpdateUserInfoRequest request) {
        User user = getCurrentUser();
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        UserResponse response = buildProfileResponse(userRepository.save(user));
        userActivityService.logActivity(UserActionType.UPDATE_PROFILE, "Cập nhật thông tin cá nhân", user);
        return response;
    }

    public void changeEmail(ChangeEmailRequest request) {
        User user = getCurrentUser();
        String newEmail = request.getNewEmail();

        if (newEmail.equals(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_USED);
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        String pendingMailKey = PENDING_EMAIL_PREFIX + user.getId();

        VerificationPurpose purpose = VerificationPurpose.CHANGE_EMAIL;
        cacheRepository.save(pendingMailKey, newEmail, purpose.getTtl());
        String otp = verificationService.createOtp(user.getId().toString(), purpose);
        emailService.sendVerifyOtp(newEmail, user.getFullName(), otp, purpose);
    }

    public void verifyChangeEmail(String otpCode) {
        User user = getCurrentUser();

        VerificationPurpose purpose = VerificationPurpose.CHANGE_EMAIL;
        boolean isValid = verificationService.verifyOtp(user.getId().toString(), otpCode, purpose);
        if (!isValid) {
            throw new AppException(ErrorCode.OTP_VERIFICATION_FAILED);
        }

        String pendingEmailKey = PENDING_EMAIL_PREFIX + user.getId();
        String newEmail = cacheRepository.get(pendingEmailKey)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED));

        user.setEmail(newEmail);
        userRepository.save(user);

        cacheRepository.delete(pendingEmailKey);
    }

    public String updateAvatar(MultipartFile file) {
        User user = getCurrentUser();
        String avatarUrl = cloudinaryService.uploadImage(file, UploadFolder.USER_AVATAR);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    // Hàm chung để map từ Entity sang Response DTO.
    private UserResponse buildProfileResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAvatarUrl(),
                user.isActive(),
                user.getActiveTier(),
                user.getSubscriptionEndDate(),
                user.getCreatedAt(),
                user.getDeletedAt());
    }

    @Transactional
    public void updateInterviewStreak() {
        User user = getCurrentUser();

        // Lấy ngày hiện tại
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // Trường hợp 1: User mới hoàn thành phỏng vấn lần đầu tiên trong đời
        if (user.getLastInterviewDate() == null) {
            user.setCurrentStreak(1);
            user.setLongestStreak(1);
            user.setLastInterviewDate(now);

            userRepository.save(user);
            return;
        }

        LocalDateTime lastDateTime = user.getLastInterviewDate();
        LocalDate lastDate = lastDateTime.toLocalDate();

        // Trường hợp 2: Đã làm bài phỏng vấn hôm nay rồi -> Bỏ qua, không cộng thêm
        if (lastDate.isEqual(today)) {
            return;
        }

        // Trường hợp 3: Hôm qua có làm -> Duy trì chuỗi, cộng thêm 1
        else if (lastDate.isEqual(today.minusDays(1))) {
            int newStreak = user.getCurrentStreak() + 1;
            user.setCurrentStreak(newStreak);
            
            if (STREAK_MILESTONES.contains(newStreak)) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setIdentifyCode(RandomReplyIdentifyCode.generate());
                notification.setTitle("Chúc mừng đạt chuỗi ôn luyện " + newStreak + " ngày!");
                notification.setType(NotificationType.STREAK);
                notification.setContent("Bạn đã duy trì ôn luyện phỏng vấn liên tục trong " +
                        newStreak +
                        " ngày. Hãy tiếp tục giữ vững chuỗi để cải thiện kỹ năng và sẵn sàng chinh phục những buổi phỏng vấn sắp tới!");
                notificationRepository.save(notification);
            }
            
            // Kiểm tra xem có phá kỷ lục của chính mình không
            if (newStreak > user.getLongestStreak()) {
                user.setLongestStreak(newStreak);
                // Sau làm noti phá kỉ lục ở đây cx dc
            }
        }

        // Trường hợp 4: Đã bỏ lỡ ít nhất 1 ngày -> Đứt chuỗi, bắt đầu lại từ 1
        else {
            user.setCurrentStreak(1);
        }

        // Cuối cùng: Luôn cập nhật ngày phỏng vấn gần nhất là hôm nay
        user.setLastInterviewDate(now);

        userRepository.save(user);
    }

    public UserStreakResponse getActualCurrentStreak() {
        User user = getCurrentUser();

        // 1. Xử lý an toàn Null (phòng trường hợp data cũ)
        int current = (user.getCurrentStreak() == null) ? 0 : user.getCurrentStreak();
        int longest = (user.getLongestStreak() == null) ? 0 : user.getLongestStreak();
        LocalDateTime lastDate = user.getLastInterviewDate();

        // 2. Nếu chưa từng làm bài nào hoặc đang ở mốc 0
        if (current == 0 || lastDate == null) {
            return new UserStreakResponse(0, longest);
        }

        // 3. Lấy ngày hiện tại
        LocalDateTime today = LocalDateTime.now();

        // 4. Lazy Evaluation: Kiểm tra xem chuỗi đã "nguội" chưa
        // Nếu ngày cuối cùng làm phỏng vấn diễn ra TRƯỚC HÔM QUA (cách đây >= 2 ngày)
        if (lastDate.isBefore(today.minusDays(1))) {
            // Đứt chuỗi: currentStreak trả về 0, nhưng longestStreak vẫn giữ nguyên
            return new UserStreakResponse(0, longest);
        }

        // 5. Nếu vừa làm hôm nay, hoặc làm hôm qua -> Chuỗi vẫn đang sống
        return new UserStreakResponse(current, longest);
    }

    public void updateStudyStats() {
        User user = getCurrentUser();

        LocalDate today = LocalDate.now();
        List<DailyStudyStat> dailyStudyStats = user.getDailyStudyStats();

        // Tìm xem ngày hôm nay đã có trong list chưa
        Optional<DailyStudyStat> todayStatOpt = dailyStudyStats.stream()
                .filter(stat -> stat.getDate().equals(today))
                .findFirst();

        if (todayStatOpt.isPresent()) {
            // Nếu đã có, chỉ cần cộng dồn số câu hỏi
            DailyStudyStat todayStat = todayStatOpt.get();
            todayStat.setTotalQuestions(todayStat.getTotalQuestions() + 1);
        } else {
            // Nếu chưa có, thêm mới record cho ngày hôm nay
            dailyStudyStats.add(new DailyStudyStat(today, 1));

            // Kiểm tra giới hạn 91 ngày
            if (dailyStudyStats.size() > 91) {
                dailyStudyStats.removeFirst();
            }
        }

        userRepository.save(user);
    }

    @Transactional
    public void updateUserRankStats(Float newInterviewScore) {
        User user = getCurrentUser();

        // 1. Lấy dữ liệu cũ (Xử lý an toàn Null)
        int oldTotal = (user.getTotalCompletedInterviews() == null) ? 0 : user.getTotalCompletedInterviews();
        double oldGpa = (user.getCurrentGpa() == null) ? 0.0 : user.getCurrentGpa();

        // 2. Tính toán số liệu mới
        int newTotal = oldTotal + 1;

        // Công thức tính GPA trung bình cộng dồn
        double newGpa = ((oldGpa * oldTotal) + newInterviewScore) / newTotal;

        // 3. Cập nhật và lưu lại
        user.setTotalCompletedInterviews(newTotal);
        user.setCurrentGpa(newGpa);
        userRepository.save(user);
    }
}
