package com.fixitnow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    
    @NotNull(message = "Room ID is required")
    private String roomId;
    
    @NotBlank(message = "Message cannot be empty")
    private String message;
    
    private String messageType = "TEXT"; // TEXT, IMAGE, FILE, SYSTEM
}
