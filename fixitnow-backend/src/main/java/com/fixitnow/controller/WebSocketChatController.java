package com.fixitnow.controller;

import com.fixitnow.dto.ChatMessageDTO;
import com.fixitnow.dto.SendMessageRequest;
import com.fixitnow.model.User;
import com.fixitnow.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {
    
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Handle incoming WebSocket messages from clients
     * Client sends message to: /app/chat.send
     * Message will be broadcast to: /topic/room/{roomId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get authenticated user from WebSocket session
            Authentication auth = (Authentication) headerAccessor.getSessionAttributes().get("auth");
            
            if (auth == null || !(auth.getPrincipal() instanceof User)) {
                // For development/testing, you can extract user info from the request
                // In production, ensure proper WebSocket authentication
                return;
            }
            
            User sender = (User) auth.getPrincipal();
            
            // Save message to database
            ChatMessageDTO message = chatService.sendMessage(request.getRoomId(), sender.getId(), request);
            
            // Broadcast message to all subscribers of this room
            messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(), message);
            
        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                headerAccessor.getUser().getName(),
                "/queue/errors",
                "Error sending message: " + e.getMessage()
            );
        }
    }
    
    /**
     * Handle user joining a chat room
     * Client sends join request to: /app/chat.join
     */
    @MessageMapping("/chat.join")
    public void joinChatRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Add user to room session
            headerAccessor.getSessionAttributes().put("roomId", roomId);
            
            // Optionally send a system message that user joined
            ChatMessageDTO systemMessage = ChatMessageDTO.builder()
                .roomId(roomId)
                .message("User joined the chat")
                .messageType("SYSTEM")
                .build();
            
            messagingTemplate.convertAndSend("/topic/room/" + roomId, systemMessage);
            
        } catch (Exception e) {
            // Handle error
        }
    }
    
    /**
     * Handle typing indicator
     * Client sends typing event to: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getSessionAttributes().get("auth");
            if (auth != null && auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                
                // Broadcast typing indicator to room (exclude sender)
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/typing", 
                    user.getName() + " is typing...");
            }
        } catch (Exception e) {
            // Handle error silently
        }
    }
}
