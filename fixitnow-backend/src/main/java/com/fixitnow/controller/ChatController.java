package com.fixitnow.controller;

import com.fixitnow.dto.ChatMessageDTO;
import com.fixitnow.dto.ChatRoomDTO;
import com.fixitnow.dto.SendMessageRequest;
import com.fixitnow.model.User;
import com.fixitnow.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping("/room/{bookingId}")
    public ResponseEntity<?> createOrGetChatRoom(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {
        try {
            ChatRoomDTO chatRoom = chatService.createOrGetChatRoom(bookingId, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Chat room ready");
            response.put("chatRoom", chatRoom);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/rooms")
    public ResponseEntity<?> getUserChatRooms(@AuthenticationPrincipal User user) {
        try {
            List<ChatRoomDTO> chatRooms = chatService.getUserChatRooms(user.getId());
            return ResponseEntity.ok(chatRooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(
            @AuthenticationPrincipal User user,
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            if (page == 0 && size == 50) {
                // Return recent messages without pagination for initial load
                List<ChatMessageDTO> messages = chatService.getChatMessages(roomId, user.getId());
                return ResponseEntity.ok(messages);
            } else {
                // Return paginated messages
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<ChatMessageDTO> messages = chatService.getChatMessagesPaginated(roomId, user.getId(), pageable);
                return ResponseEntity.ok(messages);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SendMessageRequest request) {
        try {
            ChatMessageDTO message = chatService.sendMessage(request.getRoomId(), user.getId(), request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message sent successfully");
            response.put("chatMessage", message);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/room/{roomId}/mark-read")
    public ResponseEntity<?> markMessagesAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable String roomId) {
        try {
            chatService.markMessagesAsRead(roomId, user.getId());
            return ResponseEntity.ok(Map.of("message", "Messages marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/room/{roomId}/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @AuthenticationPrincipal User user,
            @PathVariable String roomId) {
        try {
            long unreadCount = chatService.getUnreadCount(roomId, user.getId());
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
