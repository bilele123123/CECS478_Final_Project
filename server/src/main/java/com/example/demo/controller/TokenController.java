package com.example.demo.controller;

import com.example.demo.service.JwtService;
import com.example.demo.service.TokenCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtService jwtService;
    private final TokenCache tokenCache;

    @GetMapping("/token")
    public String getToken(@RequestParam String username) {
        String token = jwtService.generateJwt(username);
        tokenCache.store(token, username);
        return token;
    }
}
