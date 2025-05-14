package com.example.YummyDaily.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{

//    private final NotificationHandler notificationHandler;
//
//    public WebSocketConfig(NotificationHandler notificationHandler) {
//        this.notificationHandler = notificationHandler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(notificationHandler, "/ws/notify")
//                .setAllowedOrigins("*"); // Cho phép tất cả các nguồn
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // ✅ client SUBSCRIBE vào /topic
        config.setApplicationDestinationPrefixes("/app"); // ✅ client SEND vào /app
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ endpoint client kết nối WebSocket
                .setAllowedOriginPatterns("*") // ✅ chấp nhận mọi origin (localhost, domain khác,...)
                .withSockJS(); // ✅ fallback nếu trình duyệt không hỗ trợ WebSocket
    }
}
