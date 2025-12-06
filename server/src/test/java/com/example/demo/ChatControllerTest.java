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
        // Create mocks for dependent services
        JwtService jwtService = mock(JwtService.class);
        TokenCache tokenCache = mock(TokenCache.class);
        loggingService = mock(LoggingService.class);

        // Inject mocks into controller
        chatController = new ChatController(jwtService, tokenCache, loggingService);

        // Mocks behavior
        when(jwtService.extractUsername(TEST_JWT)).thenReturn(TEST_USER);
        when(tokenCache.isValid(TEST_JWT, TEST_USER)).thenReturn(true);
    }

    // --- Happy Path 1 ---
    @Test
    void happyPathMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setContent("Hello, world!");

        // Call controller method
        ChatMessage result = chatController.handle(msg, TEST_JWT);

        // Assert correct sender and content
        assertEquals(TEST_USER, result.getSender());
        assertEquals("Hello, world!", result.getContent());

        // Verify logging was recorded
        verify(loggingService, times(1)).recordMessage(TEST_USER);
    }

    // --- Happy Path 2: shorter message ---
    @Test
    void happyPathShortMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setContent("Hi!");

        ChatMessage result = chatController.handle(msg, TEST_JWT);

        assertEquals(TEST_USER, result.getSender());
        assertEquals("Hi!", result.getContent());

        verify(loggingService, times(1)).recordMessage(TEST_USER);
    }

    // --- Negative 1: empty message ---
    @Test
    void negativeEmptyMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setContent("");

        // Expect an exception for empty content
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> chatController.handle(msg, TEST_JWT)
        );

        assertEquals("Message cannot be empty.", ex.getMessage());

        verify(loggingService, never()).recordMessage(TEST_USER);
    }

    // --- Negative 2: oversized message ---
    @Test
    void negativeOversizedMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setContent("A".repeat(201)); // longer than 200 chars
        
        // Expect exception for long messages
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> chatController.handle(msg, TEST_JWT)
        );

        assertEquals("Message too long.", ex.getMessage());

        verify(loggingService, never()).recordMessage(TEST_USER);
    }
}
