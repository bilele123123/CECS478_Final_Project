package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
public class MetricsController {

    private final LoggingService loggingService;

    @GetMapping("/messages")
    public Map<String, Object> metricsJson() {
        return Map.of(
                "totalMessages", loggingService.getTotalMessages(),
                "messagesPerUser", loggingService.getMessageMetrics()
        );
    }

    @GetMapping(value = "/messages.csv", produces = "text/csv")
    public String metricsCsv() {
        StringBuilder sb = new StringBuilder("user,messages\n");
        loggingService.getMessageMetrics().forEach((user, count) ->
                sb.append(user).append(",").append(count).append("\n")
        );
        return sb.toString();
    }
}
