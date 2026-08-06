package com.example.it_iap.service;

import com.example.it_iap.entity.Order;

public interface UserSubscriptionService {
    void handleSuccessfulPayment(Order order);
}
