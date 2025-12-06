package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenCache {
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

    public void store(String token, String username) {
        tokenToUser.put(token, username);
    }

    public String getUser(String token) {
        return tokenToUser.get(token);
    }

    public boolean isValid(String token, String username) {
        return username.equals(tokenToUser.get(token));
    }
}
