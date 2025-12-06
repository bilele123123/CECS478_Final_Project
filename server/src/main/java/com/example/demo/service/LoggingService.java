package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LoggingService {

    private final List<String> inMemoryLogs = new ArrayList<>();
    private final AtomicInteger totalMessages = new AtomicInteger();
    private final Map<String, AtomicInteger> messagesPerUser = new ConcurrentHashMap<>();
    private static final Path LOG_DIR = Paths.get("../evidence/logs");
    private static final Path LOG_FILE =
            LOG_DIR.resolve(Instant.now().toString().replaceAll(":", ".") + ".log");

    public LoggingService() {
        try {
            Files.createDirectories(LOG_DIR);
            if (!Files.exists(LOG_FILE)) {
                Files.createFile(LOG_FILE);
            }

            append("=== Server started at " + Instant.now() + " ===\n");

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize logging directory", e);
        }
    }

    /**
     * Append a single line to the log file safely.
     */
    private synchronized void append(String line) {
        try {
            Files.write(
                    LOG_FILE,
                    (line + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Public log method used by controllers and services.
     * Writes to memory + console + file.
     */
    public synchronized void log(String msg) {
        String line = "[" + Instant.now() + "] " + msg;
        inMemoryLogs.add(line);
        System.out.println(line);

        // Write ALL log events to file
        append(line);
    }

    /**
     * Called when a valid message is received.
     * Updates metrics and logs to file.
     */
    public synchronized void recordMessage(String username) {
        totalMessages.incrementAndGet();
        messagesPerUser
                .computeIfAbsent(username, u -> new AtomicInteger(0))
                .incrementAndGet();

        String line = "[" + Instant.now() + "] Message received from user: " + username;
        inMemoryLogs.add(line);
        append(line);
    }

    public List<String> getLogs() {
        return inMemoryLogs;
    }

    public Map<String, Integer> getMessageMetrics() {
        return messagesPerUser.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }

    public int getTotalMessages() {
        return totalMessages.get();
    }
}
