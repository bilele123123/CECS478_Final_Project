package com.example.demo;

import com.example.demo.controller.ChatController;
import com.example.demo.model.ChatMessage;
import com.example.demo.service.JwtService;
import com.example.demo.service.LoggingService;
import com.example.demo.service.TokenCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    private ChatController chatController;
    private LoggingService loggingService;

    private static final String TEST_USER = "alice";
    private static final String TEST_JWT = "fake-jwt";

    @BeforeEach
    void setUp() {
        JwtService jwtService = mock(JwtService.class);
        TokenCache tokenCache = mock(TokenCache.class);
        loggingService = mock(LoggingService.class);

        chatController = new ChatController(jwtService, tokenCache, loggingService);

        // Mocks
        when(jwtService.extractUsername(TEST_JWT)).thenReturn(TEST_USER);
        when(tokenCache.isValid(TEST_JWT, TEST_USER)).thenReturn(true);
    }

    @Test
    void happyPathMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setContent("Hello, world!");

        ChatMessage result = chatController.handle(msg, TEST_JWT);

        assertEquals(TEST_USER, result.getSender());
        assertEquals("Hello, world!", result.getContent());

        // Verify loggingService called
        verify(loggingService, times(1)).recordMessage(TEST_USER);
    }

    @Test
    void negativeEmptyMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setContent(""); // empty

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> chatController.handle(msg, TEST_JWT)
        );

        assertEquals("Message cannot be empty.", ex.getMessage());

        verify(loggingService, never());
    }
}