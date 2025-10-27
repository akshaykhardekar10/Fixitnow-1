package com.fixitnow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private Long id;
    private Long chatRoomId;
    private String roomId;
    private Long senderId;
    private String senderName;
    private String message;
    private Boolean isRead;
    private String messageType;
    private LocalDateTime createdAt;
}
