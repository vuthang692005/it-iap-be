package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.order.request.OrderPreviewRequest;
import com.example.it_iap.dto.order.request.OrderRequest;
import com.example.it_iap.dto.order.response.OrderHistoryResponse;
import com.example.it_iap.dto.order.response.OrderPreviewResponse;
import com.example.it_iap.dto.order.response.OrderResponse;
import com.example.it_iap.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Tạo đơn hàng nâng cấp gói [USER]", description = "User tạo đơn hàng để nhận link thanh toán/mã QR PayOS")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Webhook PayOS [SYSTEM]", description = "API công khai để PayOS gọi về khi khách hàng thanh toán thành công hoặc thất bại")
    @PostMapping("/webhook/payos")
    public ResponseEntity<?> handlePayosWebhook(@RequestBody Object webhookBody) {
        orderService.handlePayOSWebhook(webhookBody);

        // Trả về đúng format JSON đơn giản mà hệ thống PayOS mong đợi
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Ok"
        ));
    }

    @Operation(summary = "Xem lịch sử giao dịch [USER]", description = "User xem danh sách các đơn hàng đã tạo")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderHistoryResponse>>> getMyOrders() {
        return ResponseEntity.ok(
                ApiResponse.<List<OrderHistoryResponse>>builder()
                        .data(orderService.getMyOrders())
                        .build()
        );
    }

    @Operation(summary = "Xem trước giá trị đơn hàng [USER]", description = "User xem trước số tiền cần thanh toán, tiền được giảm (từ voucher và gói cũ) trước khi tạo đơn hàng chính thức")
    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<OrderPreviewResponse>> previewOrder(@RequestBody @Valid OrderPreviewRequest request) {
        OrderPreviewResponse response = orderService.previewOrder(request);
        return ResponseEntity.ok(
                ApiResponse.<OrderPreviewResponse>builder()
                        .data(response)
                        .build()
        );
    }
}
