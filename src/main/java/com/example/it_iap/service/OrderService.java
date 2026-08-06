package com.example.it_iap.service;

import com.example.it_iap.dto.order.request.OrderPreviewRequest;
import com.example.it_iap.dto.order.request.OrderRequest;
import com.example.it_iap.dto.order.response.OrderHistoryResponse;
import com.example.it_iap.dto.order.response.OrderPreviewResponse;
import com.example.it_iap.dto.order.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    void handlePayOSWebhook(Object webhookBody);
    List<OrderHistoryResponse> getMyOrders();
    OrderPreviewResponse previewOrder(OrderPreviewRequest request);
}
