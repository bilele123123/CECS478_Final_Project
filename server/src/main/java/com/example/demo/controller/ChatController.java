package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.service.JwtService;
import com.example.demo.service.LoggingService;
import com.example.demo.service.TokenCache;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final JwtService jwtService;
    private final TokenCache tokenCache;
    private final LoggingService loggingService;
    private final Map<String, Long> lastMessageTime = new ConcurrentHashMap<>();

    @MessageMapping("/send-message")
    @SendTo("/topic/messages")
    public ChatMessage handle(ChatMessage message,
                              @Header("X-Auth-Token") String jwt) {

        String extractedUser = jwtService.extractUsername(jwt);

        // Validate token from our cache
        if (!tokenCache.isValid(jwt, extractedUser)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        // Validate message
        if (message.getContent() == null || message.getContent().isBlank()) {
            loggingService.log("Rejected empty message from " + extractedUser);
            throw new IllegalArgumentException("Message cannot be empty.");
        }

        if (message.getContent().length() > 200) {
            loggingService.log("Rejected oversized message from " + extractedUser);
            throw new IllegalArgumentException("Message too long.");
        }

        // Simple per-user rate limit (200ms)
        long now = System.currentTimeMillis();
        long last = lastMessageTime.getOrDefault(extractedUser, 0L);

        if (now - last < 200) {
            loggingService.log("Rate limit hit by " + extractedUser);
            throw new IllegalArgumentException("Rate limit exceeded.");
        }

        lastMessageTime.put(extractedUser, now);
        loggingService.recordMessage(extractedUser);
        message.setSender(extractedUser);
        return message;
    }
}
