package com.fixitnow.repository;

import com.fixitnow.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Find messages by chat room with pagination
    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
    
    // Find recent messages by chat room
    List<ChatMessage> findTop50ByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
    
    // Count unread messages for a user in a chat room
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId " +
           "AND m.sender.id != :userId AND m.isRead = false")
    long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    // Mark messages as read
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom.id = :chatRoomId " +
           "AND m.sender.id != :userId AND m.isRead = false")
    void markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    // Count total messages in chat room
    long countByChatRoomId(Long chatRoomId);
    
    // Delete all messages in chat room
    void deleteByChatRoomId(Long chatRoomId);
}
