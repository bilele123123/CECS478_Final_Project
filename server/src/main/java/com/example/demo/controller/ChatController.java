package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.service.JwtService;
import com.example.demo.service.TokenCache;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final JwtService jwtService;
    private final TokenCache tokenCache;

    @MessageMapping("/send-message")
    @SendTo("/topic/messages")
    public ChatMessage handle(ChatMessage message,
                              @Header("X-Auth-Token") String jwt) {

        String extractedUser = jwtService.extractUsername(jwt);

        // Validate token from our cache
        if (!tokenCache.isValid(jwt, extractedUser)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        message.setSender(extractedUser);
        return message;
    }
}
