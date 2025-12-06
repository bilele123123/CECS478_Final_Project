package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
public class MetricsController {

    private final LoggingService loggingService;

    /**
     * Export a full JSON view of all observability data.
     */
    @GetMapping("/all")
    public Map<String, Object> fullMetricsJson() {
        return Map.of(
                "totalMessages", loggingService.getTotalMessages(),
                "messagesPerUser", loggingService.getMessageMetrics(),
                "logs", loggingService.getLogs()
        );
    }

    /**
     * CSV version of message metrics + full log output.
     */
    @GetMapping(value = "/all.csv", produces = "text/csv")
    public String fullMetricsCsv() {
        StringBuilder sb = new StringBuilder();

        // Section 1 — Message metrics
        sb.append("=== MESSAGE METRICS ===\n");
        sb.append("user,messages\n");
        loggingService.getMessageMetrics().forEach((user, count) ->
                sb.append(user).append(",").append(count).append("\n")
        );

        sb.append("\n\n=== SERVER LOGS (Chronological) ===\n");
        sb.append("timestamp_event\n");

        // Section 2 — Full logs
        for (String log : loggingService.getLogs()) {
            sb.append(log.replace(",", " ")).append("\n");
        }

        return sb.toString();
    }
}
