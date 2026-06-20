package com.example.it_iap.service;

public interface ChatMessageService {
    public void rollbackLatestMessages(long sessionId);
}
