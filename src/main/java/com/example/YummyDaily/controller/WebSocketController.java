package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notification/" + userId, notification);
    }
}
