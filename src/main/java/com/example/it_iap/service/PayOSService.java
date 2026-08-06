package com.example.it_iap.service;

import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.webhooks.WebhookData;

public interface PayOSService {
    CreatePaymentLinkResponse createPaymentLink (
            long orderCode,
            String productName,
            String description,
            String returnUrl,
            String cancelUrl,
            long price,
            int quantity,
            long expiredInMinutes
    );
    PaymentLink getPaymentLinkInformation (long orderCode);
    PaymentLink cancelOrder (long orderCode, String cancellationReason);
    WebhookData verifyWebhook(Object webhookBody);
}
