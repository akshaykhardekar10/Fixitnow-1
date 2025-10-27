package com.fixitnow.repository;

import com.fixitnow.model.Booking;
import com.fixitnow.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by customer
    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
    
    // Find bookings by provider
    Page<Booking> findByProviderId(Long providerId, Pageable pageable);
    
    // Find bookings by service listing
    Page<Booking> findByServiceListingId(Long serviceListingId, Pageable pageable);
    
    // Find bookings by customer and status
    Page<Booking> findByCustomerIdAndStatus(Long customerId, BookingStatus status, Pageable pageable);
    
    // Find bookings by provider and status
    Page<Booking> findByProviderIdAndStatus(Long providerId, BookingStatus status, Pageable pageable);
    
    // Find pending bookings for provider
    List<Booking> findByProviderIdAndStatus(Long providerId, BookingStatus status);
    
    // Find upcoming bookings for customer
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId " +
           "AND b.bookingDate >= :fromDate AND b.status IN :statuses " +
           "ORDER BY b.bookingDate ASC")
    List<Booking> findUpcomingBookingsForCustomer(
        @Param("customerId") Long customerId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("statuses") List<BookingStatus> statuses
    );
    
    // Find upcoming bookings for provider
    @Query("SELECT b FROM Booking b WHERE b.provider.id = :providerId " +
           "AND b.bookingDate >= :fromDate AND b.status IN :statuses " +
           "ORDER BY b.bookingDate ASC")
    List<Booking> findUpcomingBookingsForProvider(
        @Param("providerId") Long providerId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("statuses") List<BookingStatus> statuses
    );
    
    // Check for conflicting bookings (same provider, date, time slot)
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.provider.id = :providerId " +
           "AND b.bookingDate = :bookingDate AND b.timeSlot = :timeSlot " +
           "AND b.status IN ('PENDING', 'CONFIRMED')")
    boolean existsConflictingBooking(
        @Param("providerId") Long providerId,
        @Param("bookingDate") LocalDateTime bookingDate,
        @Param("timeSlot") String timeSlot
    );
    
    // Count bookings by status
    long countByProviderIdAndStatus(Long providerId, BookingStatus status);
    long countByCustomerIdAndStatus(Long customerId, BookingStatus status);
    
    // Find booking by ID and customer
    Optional<Booking> findByIdAndCustomerId(Long id, Long customerId);
    
    // Find booking by ID and provider
    Optional<Booking> findByIdAndProviderId(Long id, Long providerId);
}
