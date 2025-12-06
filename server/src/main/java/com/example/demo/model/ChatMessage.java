package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatMessage {
    private String content;
    private String sender;
    private String roomId;
    private LocalDateTime timestamp;
}
