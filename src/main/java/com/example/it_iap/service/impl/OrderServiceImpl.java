package com.example.it_iap.service.impl;

import com.example.it_iap.dto.order.request.OrderPreviewRequest;
import com.example.it_iap.dto.order.request.OrderRequest;
import com.example.it_iap.dto.order.response.OrderHistoryResponse;
import com.example.it_iap.dto.order.response.OrderResponse;
import com.example.it_iap.dto.order.response.OrderPreviewResponse;
import com.example.it_iap.entity.Order;
import com.example.it_iap.entity.Promotion;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.UserSubscription;
import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.entity.enums.OrderStatus;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.OrderRepository;
import com.example.it_iap.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.WebhookData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PayOSService payOSService;
    private final UserService userService;
    private final PromotionService promotionService;
    private final UserSubscriptionService userSubscriptionService;

    public OrderResponse createOrder(OrderRequest request) {
        User user = userService.getCurrentUser();
        AccountTier targetTier = AccountTier.from(request.getAccountTier());

        // 🌟 BỔ SUNG: KIỂM TRA LEVEL NÂNG CẤP (Siêu ngắn gọn nhờ hàm @Transient)
        AccountTier currentTier = user.getActiveTier();

        // Bắt buộc gói mới phải có cấp bậc (level) cao hơn gói hiện tại đang dùng
        if (targetTier.getLevel() <= currentTier.getLevel()) {
            throw new AppException(ErrorCode.INVALID_UPGRADE_TIER);
        }

        int quantity = request.getQuantity() != null ? request.getQuantity() : 1;

        // Lấy lại Promotion Entity để lưu khóa ngoại vào Order
        Promotion appliedPromotion = promotionService.getActivePromotionByTier(targetTier);

        // 1. GỌI HÀM PREVIEW ĐỂ TÍNH TIỀN CHUẨN XÁC
        OrderPreviewResponse preview = previewOrder(user, targetTier, quantity, appliedPromotion);

        if (preview.getOriginalPrice() <= 0) {
            throw new AppException(ErrorCode.PAYMENT_NOT_REQUIRED);
        }

        long orderCode = Long.parseLong(System.currentTimeMillis() + String.valueOf((int) (Math.random() * 100)));
        int expiredInMinutes = 10;

        // 2. KHỞI TẠO ĐƠN HÀNG
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setUser(user);
        order.setAccountTier(targetTier);
        order.setQuantity(quantity);
        order.setOriginalPrice(preview.getOriginalPrice());
        order.setDiscountAmount(preview.getPromotionDiscount());
        order.setUpgradeDiscount(preview.getRemainingValue());
        order.setAmount(preview.getFinalAmount());
        order.setPromotion(appliedPromotion);
        order.setStatus(OrderStatus.PENDING);
        order.setExpiredAt(LocalDateTime.now().plusMinutes(expiredInMinutes));

        // 3. XỬ LÝ THANH TOÁN 0 ĐỒNG
        if (preview.getFinalAmount() == 0) {
            order.setStatus(OrderStatus.PAID);
            order = orderRepository.save(order);

            userSubscriptionService.handleSuccessfulPayment(order);

            return new OrderResponse(
                    order.getOrderCode(),
                    request.getReturnUrl(), // Redirect thẳng
                    null,
                    targetTier.getProductName(),
                    0L
            );
        }

        // 4. GỌI PAYOS NHƯ BÌNH THƯỜNG
        order = orderRepository.save(order);

        CreatePaymentLinkResponse payosResponse = payOSService.createPaymentLink(
                orderCode,
                targetTier.getProductName(),
                targetTier.getDescription(),
                request.getReturnUrl(),
                request.getCancelUrl(),
                preview.getFinalAmount().intValue(), // Lấy từ finalAmount của Preview
                quantity,
                expiredInMinutes
        );

        order.setCheckoutUrl(payosResponse.getCheckoutUrl());
        order.setQRCode(payosResponse.getQrCode());
        order = orderRepository.save(order);

        return new OrderResponse(
                order.getOrderCode(),
                order.getCheckoutUrl(),
                order.getQRCode(),
                order.getAccountTier().getProductName(),
                order.getAmount()
        );
    }

    @Transactional
    public void handlePayOSWebhook(Object webhookBody) {
        // 1. Dùng PayOS SDK để verify chữ ký (Checksum), đảm bảo request chuẩn từ PayOS
        WebhookData data = payOSService.verifyWebhook(webhookBody);

        if (data == null) {
            return; // Dữ liệu không hợp lệ
        }

        // 2. Tìm đơn hàng trong DB
        Order order = orderRepository.findByOrderCode(data.getOrderCode()).orElse(null);

        if (order == null) {
            log.warn("Nhận webhook PayOS nhưng không tìm thấy mã đơn hàng: {}", data.getOrderCode());
            return; // KHÔNG ném Exception. Trả về 200 OK để PayOS ngừng bắn lại (retry)
        }

        // 3. Nếu đơn đã xử lý thì bỏ qua (Idempotent - Tránh xử lý đúp)
        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }

        // 4. Nếu PayOS báo thanh toán thành công (Mã "00")
        if ("00".equals(data.getCode()) || data.getAmount() >= order.getAmount()) {
            // Cập nhật trạng thái Order
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            // 5. GỌI SERVICE CẤP QUYỀN LỢI CHO USER
            userSubscriptionService.handleSuccessfulPayment(order);
        }
    }

    public List<OrderHistoryResponse> getMyOrders() {
        User user = userService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(order -> {
                    String displayStatus = order.getStatus().name();

                    if (order.getStatus() == OrderStatus.PENDING
                            && order.getExpiredAt() != null
                            && order.getExpiredAt().isBefore(now)) {
                        displayStatus = OrderStatus.EXPIRED.name();
                    }

                    return new OrderHistoryResponse(
                            order.getOrderCode(),
                            order.getAccountTier().getProductName(),
                            order.getQuantity(),
                            order.getOriginalPrice(),
                            order.getDiscountAmount(),
                            order.getUpgradeDiscount(),
                            order.getAmount(),
                            displayStatus,
                            order.getCreatedAt()
                    );
                })
                .toList();
    }

    public OrderPreviewResponse previewOrder(OrderPreviewRequest request) {
        User user = userService.getCurrentUser();
        AccountTier targetTier = AccountTier.from(request.getAccountTier());

        // BẮT BUỘC KIỂM TRA LEVEL NÂNG CẤP (Để chặn user preview gói thấp hơn)
        AccountTier currentTier = user.getActiveTier();
        if (targetTier.getLevel() <= currentTier.getLevel()) {
            throw new AppException(ErrorCode.INVALID_UPGRADE_TIER);
        }

        int quantity = request.getQuantity() != null ? request.getQuantity() : 1;

        // Lấy Promotion Entity
        Promotion appliedPromotion = promotionService.getActivePromotionByTier(targetTier);

        return previewOrder(user, targetTier, quantity, appliedPromotion);
    }

    private OrderPreviewResponse previewOrder(User user, AccountTier targetTier, int quantity, Promotion appliedPromotion) {
        // 1. Tính giá gốc
        long originalPrice = targetTier.getPrice() * quantity;

        // 2. Tính tiền dư từ gói cũ (Sử dụng hàm calculateRemainingValue đã viết)
        long remainingValue = calculateRemainingValue(user);

        // 3. Tính tiền khuyến mãi (Nếu có truyền mã)
        long promotionDiscount = promotionService.calculateDiscount(appliedPromotion, targetTier.getPrice()) * quantity;

        // 4. CHUẨN HÓA KẾ TOÁN (Đảm bảo tổng trừ không lố giá gốc)
        if (remainingValue + promotionDiscount > originalPrice) {
            if (remainingValue >= originalPrice) {
                // Nếu riêng tiền dư đã đủ cover toàn bộ giá trị gói mới
                remainingValue = originalPrice;
                promotionDiscount = 0L; // Không cộng thêm chi phí Voucher
            } else {
                // Nếu tiền dư chưa đủ cover, thì voucher chỉ được giảm phần còn lại
                promotionDiscount = originalPrice - remainingValue;
            }
        }

        // 5. Chốt số tiền cuối cùng (Lúc này chắc chắn >= 0 và toán học khớp 100%)
        long finalAmount = originalPrice - remainingValue - promotionDiscount;

        return new OrderPreviewResponse(
                originalPrice,
                remainingValue,
                promotionDiscount,
                finalAmount
        );
    }

    private long calculateRemainingValue(User user) {
        UserSubscription currentSub = user.getUserSubscription();

        // Nếu chưa có gói, đang xài BASIC, hoặc gói vô thời hạn (endDate = null) -> Không có tiền thừa
        if (currentSub == null || currentSub.getActiveTier() == AccountTier.BASIC
                || currentSub.getPlanValue() <= 0 || currentSub.getEndDate() == null) {
            return 0L;
        }

        long totalDays = ChronoUnit.DAYS.between(currentSub.getStartDate(), currentSub.getEndDate());
        if (totalDays <= 0) {
            return 0L; // Tránh lỗi chia cho 0
        }

        // Số ngày đã dùng
        long usedDays = ChronoUnit.DAYS.between(currentSub.getStartDate(), LocalDateTime.now());

        // Số ngày còn thừa
        long remainingDays = Math.max(0, totalDays - usedDays);

        // Tính tiền thừa (Giá trị thực / Tổng số ngày * Số ngày thừa)
        double dailyRate = (double) currentSub.getPlanValue() / totalDays;
        return (long) (remainingDays * dailyRate);
    }
}
