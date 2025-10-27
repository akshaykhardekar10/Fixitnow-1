package com.fixitnow.repository;

import com.fixitnow.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    // Find chat room by room ID
    Optional<ChatRoom> findByRoomId(String roomId);
    
    // Find chat room by booking
    Optional<ChatRoom> findByBookingId(Long bookingId);
    
    // Find all chat rooms for a customer
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.customer.id = :customerId " +
           "AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findActiveRoomsByCustomerId(@Param("customerId") Long customerId);
    
    // Find all chat rooms for a provider
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.provider.id = :providerId " +
           "AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findActiveRoomsByProviderId(@Param("providerId") Long providerId);
    
    // Find chat room by customer and provider
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.customer.id = :customerId " +
           "AND cr.provider.id = :providerId AND cr.booking.id = :bookingId")
    Optional<ChatRoom> findByCustomerIdAndProviderIdAndBookingId(
        @Param("customerId") Long customerId,
        @Param("providerId") Long providerId,
        @Param("bookingId") Long bookingId
    );
    
    // Check if chat room exists for booking
    boolean existsByBookingId(Long bookingId);
}
