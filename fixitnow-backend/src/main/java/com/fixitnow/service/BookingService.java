package com.fixitnow.service;

import com.fixitnow.dto.BookingRequest;
import com.fixitnow.dto.BookingResponse;
import com.fixitnow.dto.BookingStatusUpdate;
import com.fixitnow.model.*;
import com.fixitnow.repository.BookingRepository;
import com.fixitnow.repository.ServiceListingRepository;
import com.fixitnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public BookingResponse createBooking(Long customerId, BookingRequest request) {
        // Validate service listing
        ServiceListing serviceListing = serviceListingRepository.findById(request.getServiceListingId())
            .orElseThrow(() -> new RuntimeException("Service listing not found"));
        
        if (!serviceListing.isActive()) {
            throw new RuntimeException("Service is not available for booking");
        }
        
        // Get customer
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Get provider from service listing
        User provider = serviceListing.getProviderProfile().getUser();
        
        // Check for conflicting bookings
        boolean hasConflict = bookingRepository.existsConflictingBooking(
            provider.getId(),
            request.getBookingDate(),
            request.getTimeSlot()
        );
        
        if (hasConflict) {
            throw new RuntimeException("Provider is not available for the selected time slot");
        }
        
        // Create booking
        Booking booking = Booking.builder()
            .serviceListing(serviceListing)
            .customer(customer)
            .provider(provider)
            .bookingDate(request.getBookingDate())
            .timeSlot(request.getTimeSlot())
            .durationHours(request.getDurationHours())
            .totalPrice(request.getTotalPrice())
            .serviceLocation(request.getServiceLocation())
            .customerNotes(request.getCustomerNotes())
            .status(BookingStatus.PENDING)
            .build();
        
        booking = bookingRepository.save(booking);
        
        // Increment booking count for service listing
        serviceListing.setBookingCount(serviceListing.getBookingCount() + 1);
        serviceListingRepository.save(serviceListing);
        
        return convertToResponse(booking);
    }
    
    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, Long userId, BookingStatusUpdate update) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Verify user is either customer or provider
        if (!booking.getCustomer().getId().equals(userId) && 
            !booking.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this booking");
        }
        
        // Validate status transition
        BookingStatus currentStatus = booking.getStatus();
        BookingStatus newStatus = update.getStatus();
        
        // Only provider can confirm bookings
        if (newStatus == BookingStatus.CONFIRMED && !booking.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Only provider can confirm bookings");
        }
        
        // Validate status transitions
        validateStatusTransition(currentStatus, newStatus);
        
        // Update booking
        booking.setStatus(newStatus);
        
        if (update.getProviderNotes() != null) {
            booking.setProviderNotes(update.getProviderNotes());
        }
        
        if (newStatus == BookingStatus.CANCELLED) {
            booking.setCancellationReason(update.getCancellationReason());
            booking.setCancelledBy(userId);
        }
        
        booking = bookingRepository.save(booking);
        
        return convertToResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Verify user has access to this booking
        if (!booking.getCustomer().getId().equals(userId) && 
            !booking.getProvider().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to view this booking");
        }
        
        return convertToResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingResponse> getCustomerBookings(Long customerId, BookingStatus status, Pageable pageable) {
        Page<Booking> bookings;
        
        if (status != null) {
            bookings = bookingRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else {
            bookings = bookingRepository.findByCustomerId(customerId, pageable);
        }
        
        return bookings.map(this::convertToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingResponse> getProviderBookings(Long providerId, BookingStatus status, Pageable pageable) {
        Page<Booking> bookings;
        
        if (status != null) {
            bookings = bookingRepository.findByProviderIdAndStatus(providerId, status, pageable);
        } else {
            bookings = bookingRepository.findByProviderId(providerId, pageable);
        }
        
        return bookings.map(this::convertToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getUpcomingBookingsForCustomer(Long customerId) {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        List<Booking> bookings = bookingRepository.findUpcomingBookingsForCustomer(
            customerId, LocalDateTime.now(), statuses
        );
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getUpcomingBookingsForProvider(Long providerId) {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        List<Booking> bookings = bookingRepository.findUpcomingBookingsForProvider(
            providerId, LocalDateTime.now(), statuses
        );
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    private void validateStatusTransition(BookingStatus current, BookingStatus newStatus) {
        switch (current) {
            case PENDING:
                if (newStatus != BookingStatus.CONFIRMED && newStatus != BookingStatus.CANCELLED) {
                    throw new RuntimeException("Pending booking can only be confirmed or cancelled");
                }
                break;
            case CONFIRMED:
                if (newStatus != BookingStatus.COMPLETED && newStatus != BookingStatus.CANCELLED) {
                    throw new RuntimeException("Confirmed booking can only be completed or cancelled");
                }
                break;
            case COMPLETED:
                throw new RuntimeException("Completed booking cannot be modified");
            case CANCELLED:
                throw new RuntimeException("Cancelled booking cannot be modified");
        }
    }
    
    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
            .id(booking.getId())
            .serviceListingId(booking.getServiceListing().getId())
            .serviceTitle(booking.getServiceListing().getTitle())
            .serviceCategoryName(booking.getServiceListing().getCategory().getName())
            .customerId(booking.getCustomer().getId())
            .customerName(booking.getCustomer().getName())
            .customerEmail(booking.getCustomer().getEmail())
            .providerId(booking.getProvider().getId())
            .providerName(booking.getProvider().getName())
            .providerEmail(booking.getProvider().getEmail())
            .bookingDate(booking.getBookingDate())
            .timeSlot(booking.getTimeSlot())
            .durationHours(booking.getDurationHours())
            .status(booking.getStatus())
            .totalPrice(booking.getTotalPrice())
            .serviceLocation(booking.getServiceLocation())
            .customerNotes(booking.getCustomerNotes())
            .providerNotes(booking.getProviderNotes())
            .cancellationReason(booking.getCancellationReason())
            .cancelledBy(booking.getCancelledBy())
            .createdAt(booking.getCreatedAt())
            .updatedAt(booking.getUpdatedAt())
            .build();
    }
}
