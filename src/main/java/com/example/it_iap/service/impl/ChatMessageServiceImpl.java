package com.example.it_iap.service.impl;

import com.example.it_iap.entity.ChatMessage;
import com.example.it_iap.repository.ChatMessageRepository;
import com.example.it_iap.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackLatestMessages(long sessionId) {
        // Lấy 2 tin nhắn mới nhất của phiên chat này
        Pageable limit = PageRequest.of(0, 2);
        List<ChatMessage> messagesToDelete = chatMessageRepository.findLatestMessages(sessionId, limit);

        if (!messagesToDelete.isEmpty()) {
            chatMessageRepository.deleteAll(messagesToDelete);
            log.warn("Đã xóa hoàn tác {} tin nhắn do lỗi lưu Feedback cho session {}", messagesToDelete.size(), sessionId);
        }
    }
}
