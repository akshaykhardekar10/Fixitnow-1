package com.fixitnow.service;

import com.fixitnow.dto.ChatMessageDTO;
import com.fixitnow.dto.ChatRoomDTO;
import com.fixitnow.dto.SendMessageRequest;
import com.fixitnow.model.*;
import com.fixitnow.repository.BookingRepository;
import com.fixitnow.repository.ChatMessageRepository;
import com.fixitnow.repository.ChatRoomRepository;
import com.fixitnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ChatRoomDTO createOrGetChatRoom(Long bookingId, Long userId) {
        // Verify booking exists and user is part of it
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getCustomer().getId().equals(userId) && 
            !booking.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to access this chat");
        }
        
        // Check if chat room already exists
        ChatRoom chatRoom = chatRoomRepository.findByBookingId(bookingId)
            .orElseGet(() -> {
                // Create new chat room
                String roomId = String.format("booking_%d_customer_%d_provider_%d",
                    bookingId, booking.getCustomer().getId(), booking.getProvider().getId());
                
                ChatRoom newRoom = ChatRoom.builder()
                    .booking(booking)
                    .customer(booking.getCustomer())
                    .provider(booking.getProvider())
                    .roomId(roomId)
                    .isActive(true)
                    .build();
                
                return chatRoomRepository.save(newRoom);
            });
        
        return convertToChatRoomDTO(chatRoom, userId);
    }
    
    @Transactional
    public ChatMessageDTO sendMessage(String roomId, Long senderId, SendMessageRequest request) {
        // Get chat room
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        // Verify sender is part of the chat
        if (!chatRoom.getCustomer().getId().equals(senderId) && 
            !chatRoom.getProvider().getId().equals(senderId)) {
            throw new RuntimeException("Unauthorized to send message in this chat");
        }
        
        // Get sender
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        // Create message
        ChatMessage message = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .message(request.getMessage())
            .messageType(request.getMessageType())
            .isRead(false)
            .build();
        
        message = chatMessageRepository.save(message);
        
        // Update chat room last message time
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        return convertToChatMessageDTO(message);
    }
    
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatMessages(String roomId, Long userId) {
        // Get chat room
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        // Verify user is part of the chat
        if (!chatRoom.getCustomer().getId().equals(userId) && 
            !chatRoom.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to view this chat");
        }
        
        // Get recent messages
        List<ChatMessage> messages = chatMessageRepository.findTop50ByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId());
        
        return messages.stream()
            .map(this::convertToChatMessageDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getChatMessagesPaginated(String roomId, Long userId, Pageable pageable) {
        // Get chat room
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        // Verify user is part of the chat
        if (!chatRoom.getCustomer().getId().equals(userId) && 
            !chatRoom.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to view this chat");
        }
        
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId(), pageable);
        
        return messages.map(this::convertToChatMessageDTO);
    }
    
    @Transactional
    public void markMessagesAsRead(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        // Verify user is part of the chat
        if (!chatRoom.getCustomer().getId().equals(userId) && 
            !chatRoom.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to access this chat");
        }
        
        chatMessageRepository.markMessagesAsRead(chatRoom.getId(), userId);
    }
    
    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getUserChatRooms(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<ChatRoom> chatRooms;
        
        if (user.getRole() == Role.CUSTOMER) {
            chatRooms = chatRoomRepository.findActiveRoomsByCustomerId(userId);
        } else if (user.getRole() == Role.PROVIDER) {
            chatRooms = chatRoomRepository.findActiveRoomsByProviderId(userId);
        } else {
            throw new RuntimeException("Invalid user role for chat");
        }
        
        return chatRooms.stream()
            .map(room -> convertToChatRoomDTO(room, userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long getUnreadCount(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        return chatMessageRepository.countUnreadMessages(chatRoom.getId(), userId);
    }
    
    private ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom, Long currentUserId) {
        long unreadCount = chatMessageRepository.countUnreadMessages(chatRoom.getId(), currentUserId);
        
        // Get last message
        List<ChatMessage> recentMessages = chatMessageRepository.findTop50ByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId());
        String lastMessage = recentMessages.isEmpty() ? null : recentMessages.get(0).getMessage();
        
        return ChatRoomDTO.builder()
            .id(chatRoom.getId())
            .roomId(chatRoom.getRoomId())
            .bookingId(chatRoom.getBooking().getId())
            .customerId(chatRoom.getCustomer().getId())
            .customerName(chatRoom.getCustomer().getName())
            .providerId(chatRoom.getProvider().getId())
            .providerName(chatRoom.getProvider().getName())
            .isActive(chatRoom.getIsActive())
            .unreadCount(unreadCount)
            .lastMessage(lastMessage)
            .lastMessageAt(chatRoom.getLastMessageAt())
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }
    
    private ChatMessageDTO convertToChatMessageDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
            .id(message.getId())
            .chatRoomId(message.getChatRoom().getId())
            .roomId(message.getChatRoom().getRoomId())
            .senderId(message.getSender().getId())
            .senderName(message.getSender().getName())
            .message(message.getMessage())
            .isRead(message.getIsRead())
            .messageType(message.getMessageType())
            .createdAt(message.getCreatedAt())
            .build();
    }
}
