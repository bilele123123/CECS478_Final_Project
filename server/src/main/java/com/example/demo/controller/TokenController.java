package com.example.demo.controller;

import com.example.demo.service.JwtService;
import com.example.demo.service.LoggingService;
import com.example.demo.service.TokenCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {
    
    // Service that creates signed JWTs
    private final JwtService jwtService;

    // Cache storing token → username for WebSocket auth
    private final TokenCache tokenCache;

    // Simple logging service
    private final LoggingService loggingService;

    @GetMapping("/token")
    public String getToken(@RequestParam String username) {

        // Generate a JWT for this username
        String token = jwtService.generateJwt(username);

        // Log that a token was created
        loggingService.log("Generated JWT for: " + username);

        // Store the token so WebSocket can validate it later
        tokenCache.store(token, username);
        return token;
    }
}
