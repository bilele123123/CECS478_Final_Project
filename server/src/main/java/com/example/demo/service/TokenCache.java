package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenCache {
    // Thread-safe map storing token → username
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

    // Save a token associated with a username
    public void store(String token, String username) {
        tokenToUser.put(token, username);
    }

    // Retrieve the username for a given token
    public String getUser(String token) {
        return tokenToUser.get(token);
    }

    // Check if the token matches the expected username
    public boolean isValid(String token, String username) {
        return username.equals(tokenToUser.get(token));
    }
}
