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
public class ChatRoomDTO {
    
    private Long id;
    private String roomId;
    private Long bookingId;
    private Long customerId;
    private String customerName;
    private Long providerId;
    private String providerName;
    private Boolean isActive;
    private Long unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
}
