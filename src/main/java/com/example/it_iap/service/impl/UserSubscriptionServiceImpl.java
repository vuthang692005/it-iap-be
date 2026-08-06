package com.example.it_iap.service.impl;

import com.example.it_iap.entity.Order;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.UserSubscription;
import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.repository.UserSubscriptionRepository;
import com.example.it_iap.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    public void handleSuccessfulPayment(Order order) {
        User user = order.getUser();

        // 1. Tìm gói hiện tại hoặc tạo mới nếu user chưa từng có
        UserSubscription sub = userSubscriptionRepository.findByUserId(user.getId())
                .orElse(new UserSubscription());

        sub.setUser(user);
        sub.setAccountTier(order.getAccountTier());

        // 2. GÁN PLAN VALUE CHUẨN XÁC
        // Giá trị thực của gói = Giá gốc (đã nhân quantity) - Tiền Voucher
        sub.setPlanValue(order.getOriginalPrice() - order.getDiscountAmount());

        // 3. THIẾT LẬP THỜI GIAN SỬ DỤNG MỚI (Bắt đầu lại từ BÂY GIỜ)
        LocalDateTime now = LocalDateTime.now();
        sub.setStartDate(now);

        // Tận dụng vũ khí Period trong Enum và nhân với số lượng
        Period duration = order.getAccountTier().getDuration();
        if (duration != null) {
            Period totalDuration = duration.multipliedBy(order.getQuantity());
            sub.setEndDate(now.plus(totalDuration));
        } else {
            sub.setEndDate(null); // Dành cho gói vĩnh viễn (như BASIC) nếu có
        }

        // 4. LƯU VÀO DATABASE
        userSubscriptionRepository.save(sub);
    }
}
