package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;

public class ChatMessage {
    @Getter @Setter
    private String content;
    @Getter @Setter
    private String sender;
    @Getter @Setter
    private String roomId;
    @Getter @Setter
    private LocalDateTime timestamp;
}
