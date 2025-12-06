package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class JwtService {

    // Convert objects to JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Create a simple Base64-encoded JSON token containing the username
    public String generateJwt(final String username) {
        try {
            return Base64.getEncoder().encodeToString(this.objectMapper.writeValueAsString(new Jwt(username)).getBytes(StandardCharsets.UTF_8));
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    // Decode token and read username from the JSON payload
    public String extractUsername(final String jwt) {
        try {
            return this.objectMapper.readValue(Base64.getDecoder().decode(jwt), Jwt.class).getUsername();
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    // Small inner class to represent the token structure
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Jwt {
        private String username;
    }
}
