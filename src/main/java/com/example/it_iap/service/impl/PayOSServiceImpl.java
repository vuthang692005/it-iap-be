package com.example.it_iap.service.impl;

import com.example.it_iap.service.PayOSService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

@Service
@RequiredArgsConstructor
public class PayOSServiceImpl implements PayOSService {
    private final PayOS payOS;

    public CreatePaymentLinkResponse createPaymentLink (
            long orderCode,
            String productName,
            String description,
            String returnUrl,
            String cancelUrl,
            long price,
            int quantity,
            long expiredInMinutes
    ){
        String displayItemName = productName + " (x" + quantity + ")";

        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(displayItemName)
                .quantity(1)
                .price(price)
                .build();

        CreatePaymentLinkRequest paymentData =
                CreatePaymentLinkRequest.builder()
                        .orderCode(orderCode)
                        .description(description)
                        .amount(price)
                        .item(item)
                        .returnUrl(returnUrl)
                        .expiredAt((System.currentTimeMillis() / 1000) + (expiredInMinutes * 60))
                        .cancelUrl(cancelUrl)
                        .build();

        return payOS.paymentRequests().create(paymentData);
    }

    public PaymentLink getPaymentLinkInformation (long orderCode){
        return payOS.paymentRequests().get(orderCode);
    }

    public PaymentLink cancelOrder (long orderCode, String cancellationReason) {
        return payOS.paymentRequests().cancel(orderCode, cancellationReason);
    }

    public WebhookData verifyWebhook(Object webhookBody) {
        return payOS.webhooks().verify(webhookBody);
    }
}
